package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmOptoRawNixContext<T> implements OptoNixContext<T> {
    private final @NotNull T origin;
    private boolean branchExecuted = false;
    private @Nullable SingleError result;

    PmOptoRawNixContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull OptoNixWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new OptoNixWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(origin);
            }
        };
    }

    @Override
    public @NotNull OptoNixWhenContext<T> when(boolean test) {
        return new OptoNixWhenContextBase() {

            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull OptoNixWhenContext<T> whenEquals(@NotNull T value) {
        return new OptoNixWhenContextBase() {

            @Override
            protected boolean test() {
                return origin.equals(value);
            }
        };
    }

    @Override
    public @NotNull <U> OptoNixWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        //noinspection Convert2Diamond
        return new OptoNixWhenAsContext<U, T>() {
            @Override
            public @NotNull OptoNixContext<T> thenApply(@NotNull Function<? super U, ? extends SingleError> fcn) {
                if (!branchExecuted && cls.isInstance(origin)) {
                    result = fcn.apply(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmOptoRawNixContext.this;
            }

            @Override
            public @NotNull OptoNixContext<T> thenAccept(@NotNull Consumer<? super U> action) {
                if (!branchExecuted && cls.isInstance(origin)) {
                    action.accept(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmOptoRawNixContext.this;
            }

            @Override
            public @NotNull OptoNixContext<T> fault(@NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmOptoRawNixContext.this;
            }

            @Override
            public @NotNull OptoNixContext<T> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = Nope.fault(properties, ce, argv);
                    branchExecuted = true;
                }
                return PmOptoRawNixContext.this;
            }

            @Override
            public @NotNull OptoNixContext<T> nop() {
                if (!branchExecuted && cls.isInstance(origin)) {
                    branchExecuted = true;
                }
                return PmOptoRawNixContext.this;
            }
        };
    }

    @Override
    public @NotNull OptoNixElseContext<T> otherwise() {
        //noinspection Convert2Diamond
        return new OptoNixElseContext<T>() {
            @Override
            public @NotNull OptoNixExitContext thenApply(@NotNull Function<? super T, ? extends SingleError> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
                }
                return new OptoNixExitContextBase();
            }

            @Override
            public @NotNull OptoNixExitContext thenAccept(@NotNull Consumer<? super T> action) {
                if (!branchExecuted) {
                    action.accept(origin);
                }
                return new OptoNixExitContextBase();
            }

            @Override
            public @NotNull OptoNixExitContext fault(@NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Nope.fault(ce, argv);
                }
                return new OptoNixExitContextBase();
            }

            @Override
            public @NotNull OptoNixExitContext fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Nope.fault(properties, ce, argv);
                }
                return new OptoNixExitContextBase();
            }

            @Override
            public @NotNull OptoNixExitContext nop() {
                return new OptoNixExitContextBase();
            }
        };
    }

    private abstract class OptoNixWhenContextBase implements OptoNixWhenContext<T> {
        protected abstract boolean test();

        @Override
        public @NotNull OptoNixContext<T> thenApply(@NotNull Function<? super T, ? extends SingleError> fcn) {
            if (!branchExecuted && test()) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return PmOptoRawNixContext.this;
        }

        @Override
        public @NotNull OptoNixContext<T> thenAccept(@NotNull Consumer<? super T> action) {
            if (!branchExecuted && test()) {
                action.accept(origin);
                branchExecuted = true;
            }
            return PmOptoRawNixContext.this;
        }

        @Override
        public @NotNull OptoNixContext<T> fault(@NotNull CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Nope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmOptoRawNixContext.this;
        }

        @Override
        public @NotNull OptoNixContext<T> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Nope.fault(properties, ce, argv);
                branchExecuted = true;
            }
            return PmOptoRawNixContext.this;
        }

        @Override
        public @NotNull OptoNixContext<T> nop() {
            if (!branchExecuted && test()) {
                branchExecuted = true;
            }
            return PmOptoRawNixContext.this;
        }
    }

    private class OptoNixExitContextBase implements OptoNixExitContext {
        @Override
        @NoBuiltInCapture
        public @NotNull Nope end() {
            if (result == null || result.completeSuccess()) {
                return Nope.nope();
            } else {
                return new PmNope(result.failure());
            }
        }
    }
}
