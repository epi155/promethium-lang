package io.github.epi155.pm.smart;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmVoidable<A> implements Voidable<A> {
    private final A value;

    PmVoidable(@Nullable A value) {
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
    public <B> Voidable<B> apply(Function<A, B> function) {
        if (value == null) {
            return (Voidable<B>) PmNullHelper.NULL_INSTANCE;
        } else {
            return new PmVoidable<>(function.apply(value));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Voidable<A> filter(Predicate<A> predicate) {
        if (value != null && predicate.test(value)) {
            return this;
        } else {
            return (Voidable<A>) PmNullHelper.NULL_INSTANCE;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <B> Voidable<B> join(Function<A, Voidable<B>> function) {
        if (value == null) {
            return (Voidable<B>) PmNullHelper.NULL_INSTANCE;
        } else {
            return function.apply(value);
        }
    }

    private static class PmNullHelper {
        private static final PmVoidable<?> NULL_INSTANCE = new PmVoidable<>(null);
    }

}
