package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Minimal interface for classes with error data
 */
public interface AnyOne {
    /**
     * Indicates whether at least one error has occurred.
     *
     * @return <b> true </b> no error, <b> false </b> at least one error
     */
    boolean isSuccess();

    /**
     * collection of errors
     *
     * @return errors
     */
    @NotNull Collection<Failure> errors();

}
