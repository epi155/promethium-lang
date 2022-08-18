package io.github.epi155.pm.smart;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmEnsure<A> implements Ensure<A> {
    private final A value;

    PmEnsure(@Nullable A value) {
        this.value = value;
    }

    @Override
    public @Nullable A value() {
        return value;
    }

    @Override
    public @NotNull A otherwise(@NotNull A other) {
        return value == null ? other : value;
    }

    @Override
    public OtherAction accept(Consumer<A> action) {
        if (value != null) {
            action.accept(value);
        }
        return this;
    }

    @Override
    public void otherwise(Runnable action) {
        if (value == null)
            action.run();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Ensure<B> apply(Function<A, B> function) {
        if (value == null) {
            return (Ensure<B>) PmEnsureHelper.NULL_INSTANCE;
        } else {
            return new PmEnsure<>(function.apply(value));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Ensure<A> filter(Predicate<A> predicate) {
        if (value != null && predicate.test(value)) {
            return this;
        } else {
            return (Ensure<A>) PmEnsureHelper.NULL_INSTANCE;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Ensure<B> join(Function<A, Ensure<B>> function) {
        if (value == null) {
            return (Ensure<B>) PmEnsureHelper.NULL_INSTANCE;
        } else {
            return function.apply(value);
        }
    }

    private static class PmEnsureHelper {
        private static final PmEnsure<?> NULL_INSTANCE = new PmEnsure<>(null);
    }

}
