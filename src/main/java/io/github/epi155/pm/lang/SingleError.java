package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Generic utility class for carrying a single error
 */
public interface SingleError extends AnyError, Glitch {

    /**
     * Returns the error
     *
     * @return error
     * @see Glitch#onFailure(Consumer)
     */
    @NotNull Failure fault();

    default @NotNull Optional<String> summary() {
        return completeSuccess() ? Optional.empty() : Optional.of(fault().message());
    }

}
