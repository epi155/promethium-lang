package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * context to set the condition to the value
 *
 * @param <T> value type of the opto value
 * @param <R> value type of the final value
 */
public interface OptoMapContext<T, R> extends OptoMapInitialContext<T, R> {
    /**
     * context if no previous condition is met
     *
     * @return instance of {@link OptoMapElseContext}
     */
    @NotNull OptoMapElseContext<T, R> otherwise();
}
