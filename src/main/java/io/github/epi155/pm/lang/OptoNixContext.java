package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * context to set the condition to the value
 *
 * @param <T> value type of the opto value
 */
public interface OptoNixContext<T> extends OptoNixInitialContext<T> {
    /**
     * context if no previous condition is met
     *
     * @return instance of {@link OptoNixElseContext}
     */
    @NotNull OptoNixElseContext<T> otherwise();
}
