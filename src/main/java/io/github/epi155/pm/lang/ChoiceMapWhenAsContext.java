package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * context to perform an action on an instance of condition
 * @param <U>   value type to be tested
 * @param <T>   value type of the chosen value
 * @param <R>   value type of the final value
 */
public interface ChoiceMapWhenAsContext<U, T, R> {
    /**
     * fallible function to apply to the chosen value
     *
     * @param fcn fallible function
     * @return instance of {@link ChoiceMapContext}
     */
    @NotNull ChoiceMapContext<T, R> map(@NotNull Function<? super U, ? extends AnyValue<R>> fcn);

    @NotNull ChoiceMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn);

    @NotNull ChoiceMapContext<T, R> fault(CustMsg ce, Object... argv);

}
