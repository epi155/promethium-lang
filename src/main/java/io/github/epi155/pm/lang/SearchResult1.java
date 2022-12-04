package io.github.epi155.pm.lang;

import java.util.function.Consumer;

/**
 * interface to handle actions based on possible other search outcomes
 */
public interface SearchResult1 {
    /**
     * Action in case of failure
     * <p>
     * If there are no errors, the action is not performed
     * </p>
     *
     * @param action action to be taken in case of failure
     */
    void onFailure(Consumer<Failure> action);
}
