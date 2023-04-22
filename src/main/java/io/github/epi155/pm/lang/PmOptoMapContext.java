package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

class PmOptoMapContext<T, R> implements OptoMapContext<T, R> {
    private final ErrorXorValue<T> parent;
    private boolean branchExecuted = false;
    private @Nullable ErrorXorValue<R> result;

    PmOptoMapContext(@NotNull ErrorXorValue<T> errorXorValue) {
        this.parent = errorXorValue;
    }

    @Override
    public @NotNull OptoMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return new OptoMapWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(parent.value());
            }
        };
    }

    @Override
    public @NotNull OptoMapWhenContext<T, R> when(boolean test) {
        return new OptoMapWhenContextBase() {

            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull OptoMapWhenContext<T, R> whenEquals(@NotNull T value) {
        return new OptoMapWhenContextBase() {

            @Override
            protected boolean test() {
                return parent.value().equals(value);
            }
        };
    }

    @Override
    public @NotNull <U> OptoMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        //noinspection Convert2Diamond
        return new OptoMapWhenAsContext<U, T, R>() {

            @Override
            public @NotNull OptoMapContext<T, R> map(@NotNull Function<? super U, ? extends ErrorXorValue<R>> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmOptoMapContext.this;
            }

            @Override
            public @NotNull OptoMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    R value = fcn.apply(cls.cast(parent.value()));
                    result = Hope.of(value);
                    branchExecuted = true;
                }
                return PmOptoMapContext.this;
            }

            @Override
            public @NotNull OptoMapContext<T, R> fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = Hope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmOptoMapContext.this;
            }
        };
    }

    @Override
    public @NotNull OptoMapElseContext<T, R> otherwise() {
        //noinspection Convert2Diamond
        return new OptoMapElseContext<T, R>() {

            @Override
            public @NotNull OptoMapExitContext<R> map(@NotNull Function<? super T, ? extends ErrorXorValue<R>> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                }
                return new OptoMapExitContextBase();
            }

            @Override
            public @NotNull OptoMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Hope.of(fcn.apply(parent.value()));
                }
                return new OptoMapExitContextBase();
            }

            @Override
            public @NotNull OptoMapExitContext<R> fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Hope.fault(ce, argv);
                }
                return new OptoMapExitContextBase();
            }
        };
    }

    private abstract class OptoMapWhenContextBase implements OptoMapWhenContext<T, R> {
        protected abstract boolean test();

        @Override
        public @NotNull OptoMapContext<T, R> map(@NotNull Function<? super T, ? extends ErrorXorValue<R>> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return PmOptoMapContext.this;
        }

        @Override
        public @NotNull OptoMapContext<T, R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                R value = fcn.apply(parent.value());
                result = Hope.of(value);
                branchExecuted = true;
            }
            return PmOptoMapContext.this;
        }

        @Override
        public @NotNull OptoMapContext<T, R> fault(CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Hope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmOptoMapContext.this;
        }
    }

    private class OptoMapExitContextBase implements OptoMapExitContext<R> {
        @Override
        public @NotNull Hope<R> end() {
            if (parent.completeSuccess()) {
                //noinspection DataFlowIssue
                if (result.completeSuccess()) {
                    return new PmHope<>(result.value(), null);
                } else {
                    return new PmHope<>(null, result.failure());
                }
            } else {
                return new PmHope<>(null, parent.failure());
            }
        }
    }
}
