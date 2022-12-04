package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * interface to collect any errors that occurred in the execution of actions
 */
public interface ChoiceValueExitContext {
    /**
     * collect any errors that occurred in the execution of actions
     * @return  instance of {@link None} with collected errors
     */
    @NotNull None end();
}
