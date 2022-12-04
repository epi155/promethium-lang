package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * context to set the condition to the value
 * @param <T>   value type of the chosen value
 */
public interface ChoiceValueContext<T> extends ChoiceValueExitContext {
    /**
     * sets condition to value as predicate
     * @param predicate condition to the value
     * @return      instance of {@link ChoiceValueWhenContext}
     */
    @NotNull ChoiceValueWhenContext<T> when(@NotNull Predicate<T> predicate);

    /**
     * set an external condition
     * @param test  external condition
     * @return      instance of {@link ChoiceValueWhenContext}
     */
    @NotNull ChoiceValueWhenContext<T> when(boolean test);

    /**
     * context if no previous condition is met
     * @return      instance of {@link ChoiceValueElseContext}
     */
    @NotNull ChoiceValueElseContext<T> otherwise();
}
