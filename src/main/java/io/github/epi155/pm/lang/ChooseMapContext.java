package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * context to set the condition to the value
 *
 * @param <T> value type of the chosen value
 * @param <R> value type of the final value
 */
public interface ChooseMapContext<T, R> extends ChooseMapInitialContext<T, R> {

    /**
     * context if no previous condition is met
     *
     * @return instance of {@link ChooseMapElseContext}
     */
    @NotNull ChooseMapElseContext<T, R> otherwise();

}
