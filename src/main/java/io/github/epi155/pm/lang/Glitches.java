package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Custom action in case of errors
 */
public interface Glitches {
    /**
     * Set the action in case of failure (and alert)
     * <p>
     * If there are no errors, the action is not performed
     * </p>
     *
     * @param signalAction action to be taken in case of failure
     */
    void onFailure(@NotNull Consumer<Collection<? extends Signal>> signalAction);

}
