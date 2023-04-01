package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

class PmChoiceMapContext<T,R> implements ChoiceMapContext<T, R> {
    private final AnyValue<T> parent;
    private boolean branchExecuted = false;
    private AnyValue<R> result;

    PmChoiceMapContext(AnyValue<T> anyValue) {
        this.parent = anyValue;
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return fcn -> {
            if (parent.completeWithoutErrors() && !branchExecuted && predicate.test(parent.value())) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return this;
        };
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(boolean test) {
        return fcn -> {
            if (parent.completeWithoutErrors() && !branchExecuted && test) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return this;
        };
    }

    @Override
    public @NotNull ChoiceMapElseContext<T, R> otherwise() {
        return fcn -> {
            if (parent.completeWithoutErrors() && !branchExecuted) {
                result = fcn.apply(parent.value());
            }
            return () -> {
                if (parent.completeSuccess()) {
                    if (result.completeSuccess()) {
                        return new PmSome<>(result.value());
                    } else if (result.completeWithErrors()) {
                        return new PmSome<>(result.signals());
                    } else /*result.completeWithWarnings()*/ {
                        return new PmSome<>(result.value(), result.signals());
                    }
                } else if (parent.completeWithErrors()) {
                    return new PmSome<>(parent.signals());  // parent errors (warnings)
                } else /*parent.completeWithWarnings()*/ {
                    if (result.completeSuccess()) {
                        return new PmSome<>(result.value(), parent.signals());
                    } else if (result.completeWithErrors()) {
                        return Some.<R>builder()
                            .join(parent.signals())  // parent warnings
                            .join(result.signals()) // result errors (warnings)
                            .build();
                    } else /*result.completeWithWarnings()*/ {
                        return Some.<R>builder()
                            .join(parent.signals())  // parent warnings
                            .join(result.signals())  // result warnings
                            .value(result.value())  // result value
                            .build();
                    }
                }
            };
        };
    }

    @Override
    public @NotNull <U> ChoiceMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        return fcn -> {
            if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                result = fcn.apply(cls.cast(parent.value()));
                branchExecuted = true;
            }
            return this;
        };
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(@NotNull T t) {
        return fcn -> {
            if (parent.completeWithoutErrors() && !branchExecuted && parent.value().equals(t)) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return this;
        };
    }
}
