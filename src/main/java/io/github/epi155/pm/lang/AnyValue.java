package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
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
     * @return {@link LoopItemConsumer} instance
     */
    default <U> LoopItemConsumer<U> iterable(@NotNull Function<? super T, Iterable<? extends AnyValue<U>>> src) {
        return new LoopItemConsumer<U>() {
            @Override
            public @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachParallel(executor, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachParallel(maxThread, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEach(src.apply(value()), fcn) : None.of(AnyValue.this);
            }
        };
    }

    /**
     * It generates the values to loop on
     *
     * @param src function that generates the values to loop on
     * @param <U> type of value generated
     * @return {@link LoopItemConsumer} instance
     */
    default <U> LoopItemConsumer<U> iterableValues(@NotNull Function<? super T, Iterable<? extends U>> src) {
        return new LoopItemConsumer<U>() {
            @Override
            public @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachOfParallel(executor, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachOfParallel(maxThread, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachOf(src.apply(value()), fcn) : None.of(AnyValue.this);
            }
        };
    }

    /**
     * It generates the values to loop on
     *
     * @param src function that generates the values to loop on
     * @param <U> type of value generated
     * @return {@link LoopItemConsumer} instance
     */
    default <U> LoopItemConsumer<U> streamValues(@NotNull Function<? super T, Stream<? extends U>> src) {
        return new LoopItemConsumer<U>() {
            @Override
            public @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachOfParallel(executor, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachOfParallel(maxThread, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachOf(src.apply(value()), fcn) : None.of(AnyValue.this);
            }
        };
    }

    /**
     * It generates the fallible values to loop on
     *
     * @param src function that generates the fallible values to loop on
     * @param <U> type of value generated
     * @return {@link LoopItemConsumer} instance
     */
    default <U> LoopItemConsumer<U> stream(@NotNull Function<? super T, Stream<? extends AnyValue<U>>> src) {
        return new LoopItemConsumer<U>() {
            @Override
            public @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachParallel(executor, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEachParallel(maxThread, src.apply(value()), fcn) : None.of(AnyValue.this);
            }

            @Override
            public @NotNull None forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                return isSuccess() ? None.forEach(src.apply(value()), fcn) : None.of(AnyValue.this);
            }
        };
    }

    /**
     * Interface to manage forEach loop over {@link AnyValue}
     *
     * @param <A> value type
     */
    interface LoopItemConsumer<A> {
        /**
         * It loops on fallible function and collects errors
         *
         * @param fcn fallible function to loop over
         * @return {@link None} instance
         */
        @NotNull None forEach(@NotNull Function<? super A, ? extends AnyItem> fcn);

        /**
         * It loops in parallel on fallible function and collects errors
         *
         * @param maxThread max parallel thread
         * @param fcn       fallible function to loop over
         * @return {@link None} instance
         */
        @NotNull None forEachParallel(int maxThread, @NotNull Function<? super A, ? extends AnyItem> fcn);

        /**
         * It loops in parallel on fallible function and collects errors
         *
         * @param executor executor that manages the multithreading
         * @param fcn      fallible function to loop over
         * @return {@link None} instance
         */
        @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super A, ? extends AnyItem> fcn);
    }
}
