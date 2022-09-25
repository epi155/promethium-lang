package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
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

    default int count() {
        return isSuccess() ? 0 : 1;
    }

    default @NotNull Optional<String> summary() {
        return isSuccess() ? Optional.empty() : Optional.of(fault().message());
    }

    default @NotNull Collection<Failure> errors() {
        return isSuccess() ? Collections.emptyList() : Collections.singletonList(fault());
    }

}
