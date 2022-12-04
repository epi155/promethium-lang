package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * context to perform an action on a condition
 * @param <T>   value type of the chosen value
 * @param <R>   value type of the final value
 */
public interface ChoiceMapWhenContext<T, R> {
    /**
     * fallible function to apply to the chosen value
     * @param fcn   fallible function
     * @return      instance of {@link ChoiceMapContext}
     */
    @NotNull ChoiceMapContext<T,R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn);
}
