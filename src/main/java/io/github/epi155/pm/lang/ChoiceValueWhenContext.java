package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * context to perform an action on a condition
 * @param <T>   value type of the chosen value
 */
public interface ChoiceValueWhenContext<T> {
    /**
     * performs an action on the value
     * @param action    action on the value
     * @return      instance of {@link ChoiceValueContext}
     */
    @NotNull ChoiceValueContext<T> accept(@NotNull Consumer<? super T> action);

    /**
     * performs a fallible function on the value
     * @param fcn       fallible function on the value
     * @return      instance of {@link ChoiceValueContext}
     */
    @NotNull ChoiceValueContext<T> apply(@NotNull Function<? super T, ? extends AnyError> fcn);
}
