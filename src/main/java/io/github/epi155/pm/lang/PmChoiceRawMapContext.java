package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

class PmChoiceRawMapContext<T, R> implements ChoiceMapContext<T, R> {
    private final T origin;
    private boolean branchExecuted = false;
    private AnyValue<R> result;

    PmChoiceRawMapContext(@NotNull T value) {
        this.origin = value;
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return fcn -> {
            if (!branchExecuted && predicate.test(origin)) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return this;
        };
    }

    @Override
    public @NotNull ChoiceMapWhenContext<T, R> when(boolean test) {
        return fcn -> {
            if (!branchExecuted && test) {
                result = fcn.apply(origin);
                branchExecuted = true;
            }
            return this;
        };
    }

    @Override
    public @NotNull ChoiceMapElseContext<T, R> otherwise() {
        return fcn -> {
            if (!branchExecuted) {
                result = fcn.apply(origin);
            }
            return () -> {
                if (result.isSuccess()) {
                    return new PmSome<>(result.value());
                } else {
                    return new PmSome<>(result.errors());
                }
            };
        };
    }

    @Override
    public @NotNull <U> ChoiceMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        return fcn -> {
            if (!branchExecuted && origin.getClass().isAssignableFrom(cls)) {
                result = fcn.apply(cls.cast(origin));
                branchExecuted = true;
            }
            return this;
        };
    }
}
