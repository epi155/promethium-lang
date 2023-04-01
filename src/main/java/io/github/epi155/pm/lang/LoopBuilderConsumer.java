package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Interface to manage forEach loop over {@link AnyValue}
 *
 * @param <A> value type
 */
public interface LoopBuilderConsumer<A> {
    /**
     * It loops on fallible function and collects errors
     *
     * @param fcn fallible function to loop over
     * @return {@link NoneBuilder} instance
     */
    @NotNull NoneBuilder forEach(@NotNull Function<? super A, ? extends ItemStatus> fcn);

    /**
     * It loops in parallel on fallible function and collects errors
     *
     * @param maxThread max parallel thread
     * @param fcn       fallible function to loop over
     * @return {@link NoneBuilder} instance
     */
    @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super A, ? extends ItemStatus> fcn);

    /**
     * It loops in parallel on fallible function and collects errors
     *
     * @param executor executor that manages the multithreading
     * @param fcn      fallible function to loop over
     * @return {@link NoneBuilder} instance
     */
    @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super A, ? extends ItemStatus> fcn);
}
