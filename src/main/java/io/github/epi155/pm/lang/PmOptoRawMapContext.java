package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

class PmOptoRawMapContext<T, R> implements OptoMapContext<T, R> {
    private final @NotNull T origin;
    private boolean branchExecuted = false;
    private @Nullable ErrorXorValue<R> result;

    public PmOptoRawMapContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull OptoMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return new OptoMapWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(origin);
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
                return origin.equals(value);
            }
        };
    }

    @Override
    public @NotNull <U> OptoMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        //noinspection Convert2Diamond
        return new OptoMapWhenAsContext<U, T, R>() {
            @Override
            public @NotNull OptoMapContext<T, R> maps(@NotNull Function<? super U, ? extends ErrorXorValue<R>> fcn) {
                if (!branchExecuted && cls.isInstance(origin)) {
                    result = fcn.apply(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmOptoRawMapContext.this;
            }

            @Override
            public @NotNull OptoMapContext<T, R> mapsOf(@NotNull Function<? super U, ? extends R> fcn) {
                if (!branchExecuted && cls.isInstance(origin)) {
                    R value = fcn.apply(cls.cast(origin));
                    result = Hope.of(value);
                    branchExecuted = true;
                }
                return PmOptoRawMapContext.this;
            }

            @Override
            public @NotNull OptoMapContext<T, R> fault(@NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted && cls.isInstance(origin)) {
                    result = Hope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmOptoRawMapContext.this;
            }

            @Override
            public @NotNull OptoMapContext<T, R> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted && cls.isInstance(origin)) {
                    result = Hope.fault(properties, ce, argv);
                    branchExecuted = true;
                }
                return PmOptoRawMapContext.this;
            }
        };
    }

    @Override
    public @NotNull OptoMapElseContext<T, R> otherwise() {
        //noinspection Convert2Diamond
        return new OptoMapElseContext<T, R>() {
            @Override
            public @NotNull OptoMapExitContext<R> maps(@NotNull Function<? super T, ? extends ErrorXorValue<R>> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
                }
                return new OptoMapExitContextBase();
            }

            @Override
            public @NotNull OptoMapExitContext<R> mapsOf(@NotNull Function<? super T, ? extends R> fcn) {
                if (!branchExecuted) {
                    result = Hope.of(fcn.apply(origin));
                }
                return new OptoMapExitContextBase();
            }

            @Override
            public @NotNull OptoMapExitContext<R> fault(@NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Hope.fault(ce, argv);
                }
                return new OptoMapExitContextBase();
            }

            @Override
            public @NotNull OptoMapExitContext<R> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Hope.fault(properties, ce, argv);
                }
                return new OptoMapExitContextBase();
            }
        };
    }

    private abstract class OptoMapWhenContextBase implements OptoMapWhenContext<T, R> {
        protected abstract boolean test();

        @Override
        public @NotNull OptoMapContext<T, R> maps(@NotNull Function<? super T, ? extends ErrorXorValue<R>> fcn) {
            if (!branchExecuted && test()) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return PmOptoRawMapContext.this;
        }

        @Override
        public @NotNull OptoMapContext<T, R> mapsOf(@NotNull Function<? super T, ? extends R> fcn) {
            if (!branchExecuted && test()) {
                R value = fcn.apply(origin);
                result = Hope.of(value);
                branchExecuted = true;
            }
            return PmOptoRawMapContext.this;
        }

        @Override
        public @NotNull OptoMapContext<T, R> fault(@NotNull CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Hope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmOptoRawMapContext.this;
        }

        @Override
        public @NotNull OptoMapContext<T, R> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Hope.fault(properties, ce, argv);
                branchExecuted = true;
            }
            return PmOptoRawMapContext.this;
        }
    }

    private class OptoMapExitContextBase implements OptoMapExitContext<R> {

        @Override
        @NoBuiltInCapture
        public @NotNull Hope<R> end() {
            //noinspection DataFlowIssue
            if (result.completeSuccess()) {
                return new PmHope<>(result.value(), null);
            } else {
                return new PmHope<>(null, result.failure());
            }
        }
    }
}
