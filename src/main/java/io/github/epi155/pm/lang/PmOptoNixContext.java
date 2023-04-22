package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmOptoNixContext<T> implements OptoNixContext<T> {
    private final ErrorXorValue<T> parent;
    private boolean branchExecuted = false;
    private @Nullable SingleError result;

    PmOptoNixContext(ErrorXorValue<T> errorXorValue) {
        this.parent = errorXorValue;
    }

    @Override
    public @NotNull OptoNixWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new OptoNixWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(parent.value());
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
                return parent.value().equals(value);
            }
        };
    }

    @Override
    public @NotNull <U> OptoNixWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        //noinspection Convert2Diamond
        return new OptoNixWhenAsContext<U, T>() {

            @Override
            public @NotNull OptoNixContext<T> ergo(@NotNull Function<? super U, ? extends SingleError> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmOptoNixContext.this;
            }

            @Override
            public @NotNull OptoNixContext<T> peek(@NotNull Consumer<? super U> action) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    action.accept(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmOptoNixContext.this;
            }

            @Override
            public @NotNull OptoNixContext<T> fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmOptoNixContext.this;
            }

            @Override
            public @NotNull OptoNixContext<T> nop() {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    branchExecuted = true;
                }
                return PmOptoNixContext.this;
            }
        };
    }

    @Override
    public @NotNull OptoNixElseContext<T> otherwise() {
        //noinspection Convert2Diamond
        return new OptoNixElseContext<T>() {

            @Override
            public @NotNull OptoNixExitContext ergo(@NotNull Function<? super T, ? extends SingleError> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                }
                return new OptoNixExitContextBase();
            }

            @Override
            public @NotNull OptoNixExitContext peek(@NotNull Consumer<? super T> action) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    action.accept(parent.value());
                }
                return new OptoNixExitContextBase();
            }

            @Override
            public @NotNull OptoNixExitContext fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Nope.fault(ce, argv);
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
        public @NotNull OptoNixContext<T> ergo(@NotNull Function<? super T, ? extends SingleError> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return PmOptoNixContext.this;
        }

        @Override
        public @NotNull OptoNixContext<T> peek(@NotNull Consumer<? super T> action) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                action.accept(parent.value());
                branchExecuted = true;
            }
            return PmOptoNixContext.this;
        }

        @Override
        public @NotNull OptoNixContext<T> fault(CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Nope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmOptoNixContext.this;
        }

        @Override
        public @NotNull OptoNixContext<T> nop() {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                branchExecuted = true;
            }
            return PmOptoNixContext.this;
        }
    }

    private class OptoNixExitContextBase implements OptoNixExitContext {

        @Override
        public @NotNull Nope end() {
            if (parent.completeSuccess()) {
                return result == null || result.completeSuccess() ? PmNope.nope() : new PmNope(result.failure());
            } else {
                return new PmNope(parent.failure());    // parent error
            }
        }
    }
}
