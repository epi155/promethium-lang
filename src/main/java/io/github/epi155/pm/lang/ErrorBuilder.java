package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Error builder interface
 */
public interface ErrorBuilder extends ItemStatus {
    /**
     * Add error (if any has one)
     *
     * @param any object with potential error payload
     */
    void add(@NotNull ItemStatus any);

    /**
     * Add failure error
     *
     * @param signal error detail
     */
    void add(@NotNull Signal signal);

    /**
     * Add many errors
     *
     * @param collection collection of error detail
     */
    void add(@NotNull Collection<? extends Signal> collection);

    /**
     * Add error from exception.
     * Stacktrace information are registered
     *
     * @param e exception
     */
    void capture(@NotNull Throwable e);

    /**
     * Add error from exception
     * Stacktrace information of caller method are registered
     *
     * @param e exception
     */
    void captureCaller(@NotNull Throwable e);

    /**
     * Add error from exception.
     * Stacktrace information are registered, detail is exception and custom error
     *
     * @param e       exception
     * @param ce      custom error
     * @param objects error parameters
     */
    void captureException(@NotNull Throwable e, @NotNull Nuntium ce, Object... objects);

    /**
     * Add error from exception
     * Stacktrace information of current method are registered
     *
     * @param e exception
     */
    void captureHere(@NotNull Throwable e);

    /**
     * Add error from exception.
     * Stacktrace information of current method are registered, detail is exception and custom error
     *
     * @param e       exception
     * @param ce      custom error
     * @param objects error parameters
     */
    void captureHereException(@NotNull Throwable e, @NotNull Nuntium ce, Object... objects);

    /**
     * Add error from exception.
     * Stacktrace information of current method are registered, detail is exception message and custom error
     *
     * @param e       exception
     * @param ce      custom error
     * @param objects error parameters
     */
    void captureHereMessage(@NotNull Throwable e, @NotNull Nuntium ce, Object... objects);

    /**
     * Add error from exception.
     * Stacktrace information are registered, detail is exception message and custom error
     *
     * @param e       exception
     * @param ce      custom error
     * @param objects error parameters
     */
    void captureMessage(@NotNull Throwable e, @NotNull Nuntium ce, Object... objects);

    /**
     * Add error from custom message
     *
     * @param ce      custom error
     * @param objects message parameters
     * @return {@link Failure} instance
     */
    @NotNull Failure fault(@NotNull Nuntium ce, Object... objects);

    /**
     * Add warning from custom message
     *
     * @param ce      custom error
     * @param objects message parameters
     * @return {@link Warning} instance
     */
    @NotNull Warning alert(@NotNull Nuntium ce, Object... objects);
    /**
     * Flat an {@link Hope} instance, adding error if present.
     * To be used with {@link Stream#flatMap(Function)}
     *
     * @param hope {@link Hope} instance.
     * @param <T>  Hope type
     * @return empty stream on error, singleton stream when no error
     */
    <T> @NotNull Stream<T> flat(@NotNull Hope<T> hope);

    /**
     * Flat an {@link Some} instance, adding errors if present.
     * To be used with {@link Stream#flatMap(Function)}
     *
     * @param some {@link Some} instance.
     * @param <T>  Hope type
     * @return empty stream on errors, singleton stream when no errors
     */
    <T> @NotNull Stream<T> flat(@NotNull Some<T> some);

}
