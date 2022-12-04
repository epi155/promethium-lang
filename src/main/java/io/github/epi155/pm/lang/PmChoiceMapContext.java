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
    public @NotNull WhenMapContext<T, R> when(@NotNull Predicate<T> predicate) {
        return fcn -> {
            if (parent.isSuccess() && !branchExecuted && predicate.test(parent.value())) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return this;
        };
    }

    @Override
    public @NotNull WhenMapContext<T, R> when(boolean test) {
        return fcn -> {
            if (parent.isSuccess() && !branchExecuted && test) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return this;
        };
    }

    @Override
    public @NotNull ElseMapContext<T, R> otherwise() {
        return fcn -> {
            if (parent.isSuccess() && !branchExecuted) {
                result = fcn.apply(parent.value());
            }
            return () -> {
                if (parent.isSuccess()) {
                    if (result.isSuccess()) {
                        return new PmSome<>(result.value());
                    } else {
                        return new PmSome<>(result.errors());
                    }
                } else {
                    return new PmSome<>(parent.errors());
                }
            };
        };
    }
}
