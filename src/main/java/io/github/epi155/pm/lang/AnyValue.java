package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Extension for class with payload
 *
 * @param <T> payload type
 */
public interface AnyValue<T> extends AnyItem {
    /**
     * Returns the value
     *
     * @return value
     */
    @NotNull T value();

    /**
     * @param src function that generates the values to iterate
     * @param fcn fallible function to iterate over
     * @param <U> type of value generated
     * @return {@link None} instance
     */
    default <U> @NotNull None forEachOf(
        @NotNull Function<? super T, Iterable<? extends U>> src,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        return isSuccess() ? None.forEachOf(src.apply(value()), fcn) : None.of(this);
    }

    /**
     *
     * @param src   function that generates the fallible values to iterate
     * @param fcn   fallible function to iterate over
     * @return      {@link None} instance
     * @param <U>   type of value generated
     */
    default <U> @NotNull None forEach(
        @NotNull Function<? super T, Iterable<? extends AnyValue<U>>> src,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        return isSuccess() ? None.forEach(src.apply(value()), fcn) : None.of(this);
    }

}
