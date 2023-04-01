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

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> accept(@NotNull Consumer<? super T> action) {
                if (parent.completeWithoutErrors() && !branchExecuted && predicate.test(parent.value())) {
                    action.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> apply(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && predicate.test(parent.value())) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull ChoiceValueWhenContext<T> when(boolean test) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> accept(@NotNull Consumer<? super T> action) {
                if (parent.completeWithoutErrors() && !branchExecuted && test) {
                    action.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> apply(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && test) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @Override
    public @NotNull None end() {
        if (parent.completeSuccess()) {
            return result.completeSuccess() ? PmNone.none() : new PmNone(result.signals());
        } else if (parent.completeWithErrors()) {
            return new PmNone(parent.signals());    // parent errors (warnings)
        } else if (result.completeSuccess()) {
            return new PmNone(parent.signals());    // parent warnings
        } else {
            return None.builder()
                .join(parent.signals()) // parent warnings
                .join(result.signals()) // result errors/warnings
                .build();
        }
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull ChoiceValueElseContext<T> otherwise() {
        return new ChoiceValueElseContext<T>() {
            @Override
            public @NotNull ChoiceValueExitContext accept(@NotNull Consumer<? super T> action) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    action.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueExitContext apply(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull <U> ChoiceValueWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        return new ChoiceValueWhenAsContext<U, T>() {
            @Override
            public @NotNull ChoiceValueContext<T> accept(@NotNull Consumer<? super U> action) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    action.accept(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> apply(@NotNull Function<? super U, ? extends AnyError> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull T t) {
        return new ChoiceValueWhenContext<T>() {
            @Override
            public @NotNull ChoiceValueContext<T> accept(@NotNull Consumer<? super T> action) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().equals(t)) {
                    action.accept(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> apply(@NotNull Function<? super T, ? extends AnyError> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().equals(t)) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }
}
