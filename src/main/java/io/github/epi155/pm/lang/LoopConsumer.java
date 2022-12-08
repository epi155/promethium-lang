package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Interface to manage forEach loop over {@link AnyValue}
 *
 * @param <E> value type
 */
public interface LoopConsumer<E> {

    /**
     * It loops on fallible function and collects errors
     *
     * @param fcn fallible function to loop over
     * @return {@link None} instance
     */
    @NotNull None forEach(@NotNull Function<? super E, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on fallible function and collects errors
     *
     * @param maxThread max parallel thread
     * @param fcn       fallible function to loop over
     * @return {@link None} instance
     */
    @NotNull None forEachParallel(int maxThread, @NotNull Function<? super E, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on fallible function and collects errors
     *
     * @param executor executor that manages the multithreading
     * @param fcn      fallible function to loop over
     * @return {@link None} instance
     */
    @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super E, ? extends AnyItem> fcn);
}
