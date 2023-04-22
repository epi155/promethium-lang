package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChooseRawNixContext<T> implements ChooseNixContext<T> {
    private final @NotNull T origin;
    private boolean branchExecuted = false;
    private ItemStatus result;

    PmChooseRawNixContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull ChooseNixWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new ChooseNixWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(origin);
            }
        };
    }

    @Override
    public @NotNull ChooseNixWhenContext<T> when(boolean test) {
        return new ChooseNixWhenContextBase() {
            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull ChooseNixWhenContext<T> whenEquals(@NotNull T t) {
        return new ChooseNixWhenContextBase() {
            @Override
            protected boolean test() {
                return origin.equals(t);
            }
        };
    }

    @Override
    public @NotNull <U> ChooseNixWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        //noinspection Convert2Diamond
        return new ChooseNixWhenAsContext<U, T>() {
            @Override
            public @NotNull ChooseNixContext<T> peek(@NotNull Consumer<? super U> action) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    action.accept(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChooseRawNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> ergo(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChooseRawNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> fault(CustMsg ce, Object... argv) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChooseRawNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> nop() {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    branchExecuted = true;
                }
                return PmChooseRawNixContext.this;
            }
        };
    }

    @Override
    public @NotNull ChooseNixElseContext<T> otherwise() {
        //noinspection Convert2Diamond
        return new ChooseNixElseContext<T>() {
            @Override
            public @NotNull ChooseNixExitContext peek(@NotNull Consumer<? super T> action) {
                if (!branchExecuted) {
                    action.accept(origin);
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext fault(CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Nope.fault(ce, argv);
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext nop() {
                return new ChooseNixElseContextBase();
            }
        };
    }

    abstract class ChooseNixWhenContextBase implements ChooseNixWhenContext<T> {
        protected abstract boolean test();

        @Override
        public @NotNull ChooseNixContext<T> peek(@NotNull Consumer<? super T> action) {
            if (!branchExecuted && test()) {
                action.accept(origin);
                branchExecuted = true;
            }
            return PmChooseRawNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
            if (!branchExecuted && test()) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return PmChooseRawNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> fault(CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Nope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChooseRawNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> nop() {
            if (!branchExecuted && test()) {
                branchExecuted = true;
            }
            return PmChooseRawNixContext.this;
        }
    }

    private class ChooseNixElseContextBase implements ChooseNixExitContext {
        @Override
        public @NotNull None end() {
            if (result == null || result.completeSuccess()) {
                return PmNone.none();
            } else {
                return new PmNone(result.signals());
            }
        }
    }
}
