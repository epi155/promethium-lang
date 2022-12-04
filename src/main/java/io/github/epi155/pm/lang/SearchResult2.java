package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * interface to handle actions based on possible other search outcomes
 */
public interface SearchResult2 extends SearchResult1 {
    /**
     * action to perform when the object is not found
     * @param action    action to perform
     * @return          instance of {@link SearchResult1} to handle actions based on possible other search outcomes
     */
    @NotNull SearchResult1 onNotFound(@NotNull Runnable action);
}
