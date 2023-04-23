package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
     * Add error from custom message
     *
     * @param ce      custom error
     * @param objects message parameters
     * @return {@link Failure} instance
     */
    @NotNull Failure fault(@NotNull CustMsg ce, Object... objects);

    /**
     * Add warning from custom message
     *
     * @param ce      custom error
     * @param objects message parameters
     * @return {@link Warning} instance
     */
    @NotNull Warning alert(@NotNull CustMsg ce, Object... objects);
}
