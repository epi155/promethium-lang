package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * initial context to set the condition to the value
 *
 * @param <T> value type of the chosen value
 */
public interface ChooseNixInitialContext<T> {
    /**
     * sets condition to value as predicate
     *
     * @param predicate condition to the value
     * @return instance of {@link ChooseNixWhenContext}
     */
    @NotNull ChooseNixWhenContext<T> when(@NotNull Predicate<T> predicate);

    /**
     * set an external condition
     *
     * @param test external condition
     * @return instance of {@link ChooseNixWhenContext}
     */
    @NotNull ChooseNixWhenContext<T> when(boolean test);

    /**
     * context when value is instance of selected class
     *
     * @param cls class instance of
     * @param <U> instance class type
     * @return instance of {@link ChooseNixWhenAsContext}
     */
    @NotNull <U> ChooseNixWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls);

    /**
     * context when direct value is matched
     *
     * @param value value to be equals
     * @return instance of {@link ChooseNixWhenContext}
     */
    @NotNull ChooseNixWhenContext<T> whenEquals(@NotNull T value);
}
