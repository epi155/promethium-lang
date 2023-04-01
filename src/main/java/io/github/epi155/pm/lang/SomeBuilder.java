package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Error builder extension for {@link Some}
 *
 * @param <T> payload type
 */
public interface SomeBuilder<T> extends ErrorBuilder {
    /**
     * value setter
     *
     * @param value payload
     * @return {@link SomeBuilder} instance
     */
    @NotNull SomeBuilder<T> value(@NotNull T value);

    /**
     * Final builder
     *
     * @return {@link Some} instance
     */
    @NotNull Some<T> build();

    /**
     * Add error(s)
     *
     * @param any object with error(s) payload
     * @return {@link SomeBuilder} instance
     */
    default @NotNull SomeBuilder<T> join(@NotNull ItemStatus any) {
        add(any);
        return this;
    }
    /**
     * Add a collection of {@link Signal}
     * @param signals   collection of {@link Signal}
     * @return          builder itself
     */
    default @NotNull SomeBuilder<T> join(@NotNull Collection<? extends Signal> signals) {
        add(signals);
        return this;
    }
    /**
     * Add a single {@link Signal}
     * @param signal    {@link Signal}
     * @return          builder itself
     */
    default @NotNull SomeBuilder<T> join(@NotNull Signal signal) {
        add(signal);
        return this;
    }

    /**
     * Add error when runnable throw an {@link FailureException}
     *
     * @param runnable action to be executed
     * @return {@link SomeBuilder} instance
     */
    default @NotNull SomeBuilder<T> join(@NotNull CheckedRunnable runnable) {
        add(runnable);
        return this;
    }

}
