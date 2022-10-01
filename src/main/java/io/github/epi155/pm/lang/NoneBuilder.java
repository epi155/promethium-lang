package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Error builder extension for {@link None}
 */
public interface NoneBuilder extends ErrorBuilder {
    /**
     * Final builder
     *
     * @return {@link None} instance
     */
    @NotNull None build();

    /**
     * Add error(s)
     *
     * @param any object with error(s) payload
     * @return {@link NoneBuilder} instance
     */
    default @NotNull NoneBuilder join(@NotNull AnyItem any) {
        add(any);
        return this;
    }

    /**
     * null
     * Add error when runnable throw an {@link FailureException}
     *
     * @param runnable action to be executed
     * @return {@link NoneBuilder} instance
     */
    default @NotNull NoneBuilder join(@NotNull CheckedRunnable runnable) {
        add(runnable);
        return this;
    }

    /**
     * Transforms one value into many values
     *
     * @param fcn fallible function returning iterable values
     * @param <U> iterable values type
     * @return iterable values
     */
    <U> LoopBuilder<U> timesOf(@NotNull Supplier<Iterable<? extends U>> fcn);

    /**
     * Transforms one value into many values
     *
     * @param fcn fallible function returning iterable fallible values
     * @param <U> iterable values type
     * @return iterable values
     */
    <U> LoopBuilder<U> times(@NotNull Supplier<Iterable<? extends AnyValue<U>>> fcn);

}
