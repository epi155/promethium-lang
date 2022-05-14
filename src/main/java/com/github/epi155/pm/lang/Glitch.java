package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Glitch {
    /**
     * Set the action in case of failure
     * <p>
     * If there are no errors, the action is not performed
     * </p>
     *
     * @param errorAction action to be taken in case of failure
     */
    void onFailure(@NotNull Consumer<Failure> errorAction);

    void orThrow(@NotNull Function<Failure, FailureException> fcn) throws FailureException;
}
