package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * interface to retrieve the final value or collect any errorsoccurred in the execution of the actions
 *
 * @param <R> value type of the final value
 */
public interface OptoMapExitContext<R> {
    /**
     * final value or collect any error occurred in the execution of the actions
     *
     * @return instance of {@link Hope} with final value or any error
     */
    @NotNull Hope<R> end();
}
