package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Custom action in case of error
 */
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

    /**
     * Generate a {@link FailureException} in case of error
     *
     * @param fcn transformation of {@link Failure} to {@link FailureException}
     * @throws FailureException throws in case of error
     */
    void orThrow(@NotNull Function<Failure, FailureException> fcn) throws FailureException;
}
