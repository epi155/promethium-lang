package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * context to perform an action if none of the other conditions are met
 * @param <T>   value type of the chosen value
 * @param <R>   value type of the final value
 */
public interface ChoiceMapElseContext<T, R> {
    /**
     * fallible function to apply to the chosen value
     * @param fcn   fallible function
     * @return      instance of {@link ChoiceMapExitContext}
     */
    @NotNull ChoiceMapExitContext<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn);
}
