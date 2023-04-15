package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

class PmChoiceMapContext<T, R> implements ChoiceMapContext<T, R> {
    private final @NotNull AnyValue<T> parent;
    private boolean branchExecuted = false;
    private @Nullable AnyValue<R> result;

    PmChoiceMapContext(@NotNull AnyValue<T> anyValue) {
        this.parent = anyValue;
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return new ChoiceMapWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(parent.value());
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
        //noinspection Convert2Diamond
        return new ChoiceMapElseContext<T, R>() {
            private ChoiceMapExitContext<R> exit() {
                return () -> {
                    if (parent.completeSuccess()) {
                        //noinspection DataFlowIssue
                        if (result.completeSuccess()) {
                            return new PmSome<>(result.value());
                        } else if (result.completeWithErrors()) {
                            return new PmSome<>(result.signals());
                        } else /*result.completeWithWarnings()*/ {
                            return new PmSome<>(result.value(), result.signals());
                        }
                    } else if (parent.completeWithErrors()) {
                        return new PmSome<>(parent.signals());  // parent errors (warnings)
                    } else /*parent.completeWithWarnings()*/ {
                        //noinspection DataFlowIssue
                        if (result.completeSuccess()) {
                            return new PmSome<>(result.value(), parent.signals());
                        } else if (result.completeWithErrors()) {
                            val bld = Some.<R>builder();
                            bld.add(parent.signals());  // parent warnings
                            bld.add(result.signals()); // result errors (warnings)
                            return bld.build();
                        } else /*result.completeWithWarnings()*/ {
                            val bld = Some.<R>builder();
                            bld.add(parent.signals());  // parent warnings
                            bld.add(result.signals());  // result warnings
                            return bld.buildWithValue(result.value());
                        }
                    }
                };
            }

            @Override
            public @NotNull ChoiceMapExitContext<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                }
                return exit();
            }

            @Override
            public @NotNull ChoiceMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Hope.of(fcn.apply(parent.value()));
                }
                return exit();
            }

            @Override
            public @NotNull ChoiceMapExitContext<R> fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Hope.fault(ce, argv);
                }
                return exit();
            }
        };
    }

    @Override
    public @NotNull <U> ChoiceMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        //noinspection Convert2Diamond
        return new ChoiceMapWhenAsContext<U, T, R>() {
            @Override
            public @NotNull ChoiceMapContext<T, R> map(@NotNull Function<? super U, ? extends AnyValue<R>> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChoiceMapContext.this;
            }

            @Override
            public @NotNull ChoiceMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    R value = fcn.apply(cls.cast(parent.value()));
                    result = Hope.of(value);
                    branchExecuted = true;
                }
                return PmChoiceMapContext.this;
            }

            @Override
            public @NotNull ChoiceMapContext<T, R> fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = Hope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChoiceMapContext.this;
            }
        };
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(@NotNull T t) {
        return new ChoiceMapWhenContextBase() {
            @Override
            protected boolean test() {
                return parent.value().equals(t);
            }
        };
    }

    abstract class ChoiceMapWhenContextBase implements ChoiceMapWhenContext<T, R> {
        protected abstract boolean test();

        @Override
        public @NotNull ChoiceMapContext<T, R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return PmChoiceMapContext.this;
        }

        @Override
        public @NotNull ChoiceMapContext<T, R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                R value = fcn.apply(parent.value());
                result = Hope.of(value);
                branchExecuted = true;
            }
            return PmChoiceMapContext.this;
        }

        @Override
        public @NotNull ChoiceMapContext<T, R> fault(CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Hope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChoiceMapContext.this;
        }

    }
}
