package io.github.epi155.pm.lang;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Interface to manage forEach loop over {@link AnyValue}
 *
 * @param <A> value type
 */
public interface LoopConsumer<A> {
    /**
     * creates a LoopConsumer from a LoopBuilderConsumer
     *
     * @param loop {@link LoopBuilderConsumer} instance
     * @param <U>  loop data type
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> of(@NotNull LoopBuilderConsumer<? extends U> loop) {
        return new LoopConsumer<U>() {

            @Override
            public @NotNull None forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                return loop.forEach(fcn).build();
            }

            @Override
            public @NotNull None forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return loop.forEachParallel(maxThread, fcn).build();
            }

            @Override
            public @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                return loop.forEachParallel(executor, fcn).build();
            }
        };
    }

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
