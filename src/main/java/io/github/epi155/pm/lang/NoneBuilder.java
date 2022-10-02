package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

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
     * It cycles on iterable and collects errors
     *
     * @param iterable iterable to be cycled
     * @param fcn      fallible function to apply
     * @param <U>      iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachOf(
        @NotNull Iterable<? extends U> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It cycles on fallible iterable and collects errors
     *
     * @param iterable fallible iterable
     * @param fcn      fallible function to apply
     * @param <U>      iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEach(
        @NotNull Iterable<? extends AnyValue<U>> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn);
}
