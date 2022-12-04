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
    public @NotNull WhenValueContext<T> when(@NotNull Predicate<T> predicate) {
        return new WhenValueContext<T>() {
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
    public @NotNull WhenValueContext<T> when(boolean test) {
        return new WhenValueContext<T>() {
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
    public @NotNull ElseValueContext<T> otherwise() {
        return new ElseValueContext<T>() {
            @Override
            public @NotNull ChoiceExitContext implies(@NotNull Consumer<? super T> action) {
                if (!branchExecuted) {
                    action.accept(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }

            @Override
            public @NotNull ChoiceExitContext perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (!branchExecuted) {
                    result = fcn.apply(origin);
                    branchExecuted = true;
                }
                return PmChoiceRawValueContext.this;
            }
        };
    }
}
