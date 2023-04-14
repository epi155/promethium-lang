package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChoiceRawValueContext<T> implements ChoiceValueContext<T> {
    private final T origin;
    private boolean branchExecuted = false;
    private ItemStatus result;

    PmChoiceRawValueContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull None end() {
        if (result == null || result.completeSuccess()) {
            return PmNone.none();
        } else {
            return new PmNone(result.signals());
        }
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new ChoiceValueWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(origin);
            }
        };
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(boolean test) {
        return new ChoiceValueWhenContextBase() {
            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull ChoiceValueElseContext<T> otherwise() {
        return new ChoiceValueElseContext<T>() {
            @Override
            public @NotNull ChoiceValueExitContext peek(@NotNull Consumer<? super T> action) {
                if (!branchExecuted) {
                    action.accept(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueExitContext ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueExitContext fault(CustMsg ce, Object... argv) {
                if (!branchExecuted) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }
        };
    }

    @Override
    public @NotNull <U> ChoiceValueWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        return new ChoiceValueWhenAsContext<U, T>() {
            @Override
            public @NotNull ChoiceValueContext<T> peek(@NotNull Consumer<? super U> action) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    action.accept(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> ergo(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> fault(CustMsg ce, Object... argv) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }
        };
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull T t) {
        return new ChoiceValueWhenContextBase() {
            @Override
            protected boolean test() {
                return origin.equals(t);
            }
        };
    }

    abstract class ChoiceValueWhenContextBase implements ChoiceValueWhenContext<T> {
        protected abstract boolean test();

        @Override
        public @NotNull ChoiceValueContext<T> peek(@NotNull Consumer<? super T> action) {
            if (!branchExecuted && test()) {
                action.accept(origin);
                branchExecuted = true;
            }
            return PmChoiceRawValueContext.this;
        }

        @Override
        public @NotNull ChoiceValueContext<T> ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
            if (!branchExecuted && test()) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return PmChoiceRawValueContext.this;
        }

        @Override
        public @NotNull ChoiceValueContext<T> fault(CustMsg ce, Object... argv) {
            if (!branchExecuted && test()) {
                result = Nope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChoiceRawValueContext.this;
        }
    }
}
