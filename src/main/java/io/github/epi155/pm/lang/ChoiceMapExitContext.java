package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * interface to retrieve the final value or collect any errors occurred in the execution of the actions
 * @param <R>   value type of the final value
 */
public interface ChoiceMapExitContext<R> {
    /**
     * final value or collect any errors occurred in the execution of the actions
     * @return  instance of {@link Some} with final value or collected errors
     */
    @NotNull Some<R> end();
}
