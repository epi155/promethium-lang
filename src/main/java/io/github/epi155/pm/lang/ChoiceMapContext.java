package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * context to set the condition to the value
 * @param <T>   value type of the chosen value
 * @param <R>   value type of the final value
 */
public interface ChoiceMapContext<T, R> {
    /**
     * sets condition to value as predicate
     * @param predicate condition to the value
     * @return          instance of {@link ChoiceMapWhenContext}
     */
    @NotNull ChoiceMapWhenContext<T,R> when(@NotNull Predicate<T> predicate);

    /**
     * set an external condition
     * @param test  external condition
     * @return      instance of {@link ChoiceMapWhenContext}
     */
    @NotNull ChoiceMapWhenContext<T,R> when(boolean test);

    /**
     * context if no previous condition is met
     * @return      instance of {@link ChoiceMapElseContext}
     */
    @NotNull ChoiceMapElseContext<T,R> otherwise();

    /**
     * context when value is instance of selected class
     * @param cls       class instance of
     * @return          instance of {@link ChoiceMapWhenAsContext}
     * @param <U>       instance class type
     */
    @NotNull <U> ChoiceMapWhenAsContext<U,T,R> whenInstanceOf(Class<U> cls);

    /**
     * context when direct value is matched
     * @param value     value to be equals
     * @return          instance of {@link ChoiceMapWhenContext}
     */
    @NotNull ChoiceMapWhenContext<T,R> when(@NotNull T value);
}
