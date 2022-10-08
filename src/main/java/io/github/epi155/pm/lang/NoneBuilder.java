package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Stream;

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
     * It loops in parallel on iterable and collects errors
     *
     * @param maxThread max parallel thread
     * @param iterable  iterable to be cycled
     * @param fcn       fallible function to apply
     * @param <U>       iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachOfParallel(
        int maxThread,
        @NotNull Iterable<? extends U> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on iterable and collects errors
     *
     * @param executorService executor that manages the multithreading
     * @param iterable        iterable to be cycled
     * @param fcn             fallible function to apply
     * @param <U>             iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachOfParallel(
        @NotNull ExecutorService executorService,
        @NotNull Iterable<? extends U> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on stream and collects errors
     *
     * @param maxThread max parallel thread
     * @param stream    stream to be cycled
     * @param fcn       fallible function to apply
     * @param <U>       iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachOfParallel(
        int maxThread,
        @NotNull Stream<? extends U> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on stream and collects errors
     *
     * @param executorService executor that manages the multithreading
     * @param stream          stream to be cycled
     * @param fcn             fallible function to apply
     * @param <U>             iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachOfParallel(
        @NotNull ExecutorService executorService,
        @NotNull Stream<? extends U> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It cycles on stream and collects errors
     *
     * @param stream stream to be cycled
     * @param fcn    fallible function to apply
     * @param <U>    iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachOf(
        @NotNull Stream<? extends U> stream,
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

    /**
     * It cycles on fallible stream and collects errors
     *
     * @param stream fallible stream
     * @param fcn    fallible function to apply
     * @param <U>    iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEach(
        @NotNull Stream<? extends AnyValue<U>> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on fallible iterable and collects errors
     *
     * @param maxThread max parallel thread
     * @param iterable  fallible iterable
     * @param fcn       fallible function to apply
     * @param <U>       iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachParallel(
        int maxThread,
        @NotNull Iterable<? extends AnyValue<U>> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on fallible stream and collects errors
     *
     * @param maxThread max parallel thread
     * @param stream    fallible stream
     * @param fcn       fallible function to apply
     * @param <U>       iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachParallel(
        int maxThread,
        @NotNull Stream<? extends AnyValue<U>> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on fallible iterable and collects errors
     *
     * @param executorService executor that manages the multithreading
     * @param iterable        fallible iterable
     * @param fcn             fallible function to apply
     * @param <U>             iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachParallel(
        @NotNull ExecutorService executorService,
        @NotNull Iterable<? extends AnyValue<U>> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn);

    /**
     * It loops in parallel on fallible stream and collects errors
     *
     * @param executorService executor that manages the multithreading
     * @param stream          fallible stream
     * @param fcn             fallible function to apply
     * @param <U>             iterable type/function argument type
     * @return this
     */
    @NotNull <U> NoneBuilder forEachParallel(
        @NotNull ExecutorService executorService,
        @NotNull Stream<? extends AnyValue<U>> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn);
}
