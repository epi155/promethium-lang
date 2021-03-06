package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Custom action in case of errors
 */
public interface Glitches {
    /**
     * Set the action in case of failure
     * <p>
     * If there are no errors, the action is not performed
     * </p>
     *
     * @param errorAction action to be taken in case of failure
     */
    void onFailure(@NotNull Consumer<Stream<Failure>> errorAction);
}
