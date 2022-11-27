package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChoiceValueContext<T> implements ChoiceValueContext<T> {
    private final AnyValue<T> parent;
    private boolean branchExecuted = false;
    private AnyError result;

    public PmChoiceValueContext(AnyValue<T> anyValue) {
        this.parent = anyValue;
        this.result = anyValue;
    }

    @Override
    public @NotNull WhenValueContext<T> when(@NotNull Predicate<T> predicate) {
        return new WhenValueContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> consumer) {
                if (parent.isSuccess() && !branchExecuted && predicate.test(parent.value())) {
                    consumer.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.isSuccess() && !branchExecuted && predicate.test(parent.value())) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @Override
    public @NotNull WhenValueContext<T> when(boolean test) {
        return new WhenValueContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> consumer) {
                if (parent.isSuccess() && !branchExecuted && test) {
                    consumer.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.isSuccess() && !branchExecuted && test) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @Override
    public @NotNull None end() {
        return result.isSuccess() ? new PmNone() : new PmNone(result.errors());
    }

    @Override
    public @NotNull ElseValueContext<T> otherwise() {
        return new ElseValueContext<T>() {
            @Override
            public @NotNull ChoiceExitContext implies(@NotNull Consumer<? super T> consumer) {
                if (parent.isSuccess() && !branchExecuted) {
                    consumer.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceExitContext perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.isSuccess() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }
}
