package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Extension for class with payload
 *
 * @param <T> payload type
 */
public interface SomeOne<T> extends AnyOne {
    /**
     * Returns the value
     *
     * @return value
     */
    @NotNull T value();

}
