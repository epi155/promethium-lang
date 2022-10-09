package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Stream;

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

}
