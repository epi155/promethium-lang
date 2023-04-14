package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

class PmChoiceRawMapContext<T, R> implements ChoiceMapContext<T, R> {
    private final T origin;
    private boolean branchExecuted = false;
    private AnyValue<R> result;

    PmChoiceRawMapContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return new ChoiceMapWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(origin);
            }
        };
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(boolean test) {
        return new ChoiceMapWhenContextBase() {
            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull ChoiceMapElseContext<T, R> otherwise() {
        return new ChoiceMapElseContext<T, R>() {
            @Override
            public @NotNull ChoiceMapExitContext<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
                }
                return () -> {
                    if (result.completeSuccess()) {
                        return new PmSome<>(result.value());
                    } else if (result.completeWithErrors()) {
                        return new PmSome<>(result.signals());
                    } else /*result.completeWithWarnings()*/ {
                        return new PmSome<>(result.value(), result.signals());
                    }
                };
            }

            @Override
            public @NotNull ChoiceMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
                if (!branchExecuted) {
                    result = Hope.of(fcn.apply(origin));
                }
                return () -> {
                    if (result.completeSuccess()) {
                        return new PmSome<>(result.value());
                    } else if (result.completeWithErrors()) {
                        return new PmSome<>(result.signals());
                    } else /*result.completeWithWarnings()*/ {
                        return new PmSome<>(result.value(), result.signals());
                    }
                };
            }

            @Override
            public @NotNull ChoiceMapExitContext<R> fault(CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Hope.fault(ce, argv);
                }
                return () -> {
                    if (result.completeSuccess()) {
                        return new PmSome<>(result.value());
                    } else if (result.completeWithErrors()) {
                        return new PmSome<>(result.signals());
                    } else /*result.completeWithWarnings()*/ {
                        return new PmSome<>(result.value(), result.signals());
                    }
                };
            }
        };
    }

    @Override
    public @NotNull <U> ChoiceMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        return new ChoiceMapWhenAsContext<U, T, R>() {
            @Override
            public @NotNull ChoiceMapContext<T, R> map(@NotNull Function<? super U, ? extends AnyValue<R>> fcn) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChoiceRawMapContext.this;
            }

            @Override
            public @NotNull ChoiceMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    R value = fcn.apply(cls.cast(origin));
                    result = Hope.of(value);
                    branchExecuted = true;
                }
                return PmChoiceRawMapContext.this;
            }

            @Override
            public @NotNull ChoiceMapContext<T, R> fault(CustMsg ce, Object... argv) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = Hope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChoiceRawMapContext.this;
            }
        };
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(@NotNull T t) {
        return new ChoiceMapWhenContextBase() {
            @Override
            protected boolean test() {
                return origin.equals(t);
            }
        };
    }

    abstract class ChoiceMapWhenContextBase implements ChoiceMapWhenContext<T, R> {
        protected abstract boolean test();

        @Override
        public @NotNull ChoiceMapContext<T, R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
            if (!branchExecuted && test()) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return PmChoiceRawMapContext.this;
        }

        @Override
        public @NotNull ChoiceMapContext<T, R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
            if (!branchExecuted && test()) {
                R value = fcn.apply(origin);
                result = Hope.of(value);
                branchExecuted = true;
            }
            return PmChoiceRawMapContext.this;
        }

        @Override
        public @NotNull ChoiceMapContext<T, R> fault(CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Hope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChoiceRawMapContext.this;
        }
    }
}
