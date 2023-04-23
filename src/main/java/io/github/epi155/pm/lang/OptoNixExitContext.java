package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * interface to collect any error that occurred in the execution of actions
 */
public interface OptoNixExitContext {
    /**
     * collects any error occurred in the execution of the actions
     *
     * @return instance of {@link Nope} with the error, if any
     */
    @NotNull Nope end();
}
