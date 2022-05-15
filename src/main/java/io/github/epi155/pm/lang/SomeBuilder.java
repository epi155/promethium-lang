package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

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
    default @NotNull SomeBuilder<T> join(@NotNull AnyOne any) {
        add(any);
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
