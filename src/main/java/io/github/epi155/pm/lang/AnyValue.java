package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension for class with payload
 *
 * @param <T> payload type
 */
public interface AnyValue<T> extends AnyError, ChoiceContext<T> {
    /**
     * Returns the value.
     * <p>
     *     in the presence of errors and the value is not present,
     *     the {@link java.util.NoSuchElementException} error is thrown
     * </p>
     *
     * @return value
     */
    @NotNull T value();

    /**
     * It generates the fallible values to loop on
     *
     * @param src function that generates the fallible values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> iterable(@NotNull Function<? super T, Iterable<? extends AnyValue<U>>> src) {
        return isSuccess() ? None.iterable(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * It generates the values to loop on
     *
     * @param src function that generates the values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> iterableOf(@NotNull Function<? super T, Iterable<? extends U>> src) {
        return isSuccess() ? None.iterableOf(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * It generates the values to loop on
     *
     * @param src function that generates the values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> streamOf(@NotNull Function<? super T, Stream<? extends U>> src) {
        return isSuccess() ? None.streamOf(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * It generates the fallible values to loop on
     *
     * @param src function that generates the fallible values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> stream(@NotNull Function<? super T, Stream<? extends AnyValue<U>>> src) {
        return isSuccess() ? None.stream(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * If there are no errors and the value is present, apply the function to the value,
     * if the function ends with errors, these errors are returned.
     * In the presence of errors, the function is not called, and the initial errors are returned.
     *
     * @param fcn transform value to {@link AnyItem}
     * @return {@link None} instance, if this has an error,
     * the transformation is not called and the result has the original error
     */
    @NotNull None ergo(@NotNull Function<? super T, ? extends AnyItem> fcn);
}
