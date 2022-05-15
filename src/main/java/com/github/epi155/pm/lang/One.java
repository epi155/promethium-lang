package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Generic utility class for carrying a single error
 */
public interface One extends Any {

    /**
     * Returns the error
     *
     * @return error
     * @see Glitch#onFailure(Consumer)
     */
    @NotNull Failure fault();

    default int count() {
        return isSuccess() ? 0 : 1;
    }

    default @NotNull Optional<String> summary() {
        return isSuccess() ? Optional.empty() : Optional.of(fault().message());
    }
}
