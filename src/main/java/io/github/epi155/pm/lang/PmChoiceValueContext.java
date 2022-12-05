package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChoiceValueContext<T> implements ChoiceValueContext<T> {
    private final AnyValue<T> parent;
    private boolean branchExecuted = false;
    private AnyError result;

    PmChoiceValueContext(AnyValue<T> anyValue) {
        this.parent = anyValue;
        this.result = anyValue;
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> action) {
                if (parent.isSuccess() && !branchExecuted && predicate.test(parent.value())) {
                    action.accept(parent.value());
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
    public @NotNull ChoiceValueWhenContext<T> when(boolean test) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> action) {
                if (parent.isSuccess() && !branchExecuted && test) {
                    action.accept(parent.value());
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
    public @NotNull ChoiceValueElseContext<T> otherwise() {
        return new ChoiceValueElseContext<T>() {
            @Override
            public @NotNull ChoiceValueExitContext implies(@NotNull Consumer<? super T> action) {
                if (parent.isSuccess() && !branchExecuted) {
                    action.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueExitContext perform(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.isSuccess() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @Override
    public @NotNull <U> ChoiceValueWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        return new ChoiceValueWhenAsContext<U, T>() {
            @Override
            public @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super U> action) {
                if (parent.isSuccess() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    action.accept(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super U, ? extends AnyError> fcn) {
                if (parent.isSuccess() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }
}
