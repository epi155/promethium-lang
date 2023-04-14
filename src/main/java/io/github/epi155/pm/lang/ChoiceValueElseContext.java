package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * context to perform an action if none of the other conditions are met
 * @param <T>   value type of the chosen value
 */
public interface ChoiceValueElseContext<T> {
    /**
     * performs an action on the value
     *
     * @param action action on the value
     * @return instance of {@link ChoiceValueExitContext}
     */
    @NotNull ChoiceValueExitContext peek(@NotNull Consumer<? super T> action);

    /**
     * performs a fallible function on the value
     *
     * @param fcn fallible function on the value
     * @return instance of {@link ChoiceValueExitContext}
     */
    @NotNull ChoiceValueExitContext ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn);

    @NotNull ChoiceValueExitContext fault(CustMsg ce, Object... argv);
}
