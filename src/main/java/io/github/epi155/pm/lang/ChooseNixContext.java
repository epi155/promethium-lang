package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * context to set the condition to the value
 *
 * @param <T> value type of the chosen value
 */
public interface ChooseNixContext<T> extends ChooseNixInitialContext<T> {

    /**
     * context if no previous condition is met
     *
     * @return instance of {@link ChooseNixElseContext}
     */
    @NotNull ChooseNixElseContext<T> otherwise();


}
