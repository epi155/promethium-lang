package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

class PmChooseRawMapContext<T, R> implements ChooseMapContext<T, R> {
    private final @NotNull T origin;
    private boolean branchExecuted = false;
    private @Nullable AnyValue<R> result;

    PmChooseRawMapContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull ChooseMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return new ChooseMapWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(origin);
            }
        };
    }

    @Override
    public @NotNull ChooseMapWhenContext<T, R> when(boolean test) {
        return new ChooseMapWhenContextBase() {
            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull ChooseMapWhenContext<T, R> whenEquals(@NotNull T t) {
        return new ChooseMapWhenContextBase() {
            @Override
            protected boolean test() {
                return origin.equals(t);
            }
        };
    }

    @Override
    public @NotNull <U> ChooseMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        //noinspection Convert2Diamond
        return new ChooseMapWhenAsContext<U, T, R>() {
            @Override
            public @NotNull ChooseMapContext<T, R> map(@NotNull Function<? super U, ? extends AnyValue<R>> fcn) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChooseRawMapContext.this;
            }

            @Override
            public @NotNull ChooseMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    R value = fcn.apply(cls.cast(origin));
                    result = Hope.of(value);
                    branchExecuted = true;
                }
                return PmChooseRawMapContext.this;
            }

            @Override
            public @NotNull ChooseMapContext<T, R> fault(CustMsg ce, Object... argv) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = Hope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChooseRawMapContext.this;
            }
        };
    }

    @Override
    public @NotNull ChooseMapElseContext<T, R> otherwise() {
        //noinspection Convert2Diamond
        return new ChooseMapElseContext<T, R>() {

            @Override
            public @NotNull ChooseMapExitContext<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
                }
                return new ChooseMapElseContextBase();
            }

            @Override
            public @NotNull ChooseMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
                if (!branchExecuted) {
                    result = Hope.of(fcn.apply(origin));
                }
                return new ChooseMapElseContextBase();
            }

            @Override
            public @NotNull ChooseMapExitContext<R> fault(CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Hope.fault(ce, argv);
                }
                return new ChooseMapElseContextBase();
            }
        };
    }

    private abstract class ChooseMapWhenContextBase implements ChooseMapWhenContext<T, R> {
        protected abstract boolean test();

        @Override
        public @NotNull ChooseMapContext<T, R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
            if (!branchExecuted && test()) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return PmChooseRawMapContext.this;
        }

        @Override
        public @NotNull ChooseMapContext<T, R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
            if (!branchExecuted && test()) {
                R value = fcn.apply(origin);
                result = Hope.of(value);
                branchExecuted = true;
            }
            return PmChooseRawMapContext.this;
        }

        @Override
        public @NotNull ChooseMapContext<T, R> fault(CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Hope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChooseRawMapContext.this;
        }
    }

    private class ChooseMapElseContextBase implements ChooseMapExitContext<R> {
        @Override
        public @NotNull Some<R> end() {
            //noinspection DataFlowIssue
            if (result.completeSuccess()) {
                return new PmSome<>(result.value());
            } else if (result.completeWithErrors()) {
                return new PmSome<>(result.signals());
            } else /*result.completeWithWarnings()*/ {
                return new PmSome<>(result.value(), result.signals());
            }
        }
    }
}
