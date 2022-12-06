package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChoiceRawValueContext<T> implements ChoiceValueContext<T> {
    private final T origin;
    private boolean branchExecuted = false;
    private AnyError result;

    PmChoiceRawValueContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull None end() {
        if (result == null || result.isSuccess()) {
            return new PmNone();
        } else {
            return new PmNone(result.errors());
        }
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> action) {
                if (!branchExecuted && predicate.test(origin)) {
                    action.accept(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (!branchExecuted && predicate.test(origin)) {
                    result = fcn.apply(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }
        };
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(boolean test) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> action) {
                if (!branchExecuted && test) {
                    action.accept(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (!branchExecuted && test) {
                    result = fcn.apply(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }
        };
    }

    @Override
    public @NotNull ChoiceValueElseContext<T> otherwise() {
        return new ChoiceValueElseContext<T>() {
            @Override
            public @NotNull ChoiceValueExitContext implies(@NotNull Consumer<? super T> action) {
                if (!branchExecuted) {
                    action.accept(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueExitContext perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
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
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super U> action) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    action.accept(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super U, ? extends AnyError> fcn) {
                if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(origin));
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }
        };
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull T t) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> action) {
                if (!branchExecuted && origin.equals(t)) {
                    action.accept(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (!branchExecuted && origin.equals(t)) {
                    result = fcn.apply(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }
        };
    }
}
