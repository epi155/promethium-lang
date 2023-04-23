package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Generic interface for classes with error data
 */
public interface AnyError extends ItemStatus {

    /**
     * summary of any errors
     *
     * @return summary of errors (if any)
     */
    @NotNull Optional<String> summary();
}
