package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * context to perform an action on an instance of condition
 *
 * @param <U> value type to be tested
 * @param <T> value type of the chosen value
 * @param <R> value type of the final value
 */
public interface ChooseMapWhenAsContext<U, T, R> {
    /**
     * fallible function to apply to the chosen value
     *
     * @param fcn fallible function
     * @return instance of {@link ChooseMapContext}
     */
    @NotNull ChooseMapContext<T, R> map(@NotNull Function<? super U, ? extends AnyValue<R>> fcn);

    /**
     * Apply transformation to the chosen value
     *
     * @param fcn transformation function
     * @return instance of {@link ChooseMapContext}
     */
    @NotNull ChooseMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error arguments
     * @return instance of {@link ChooseMapContext}
     */
    @NotNull ChooseMapContext<T, R> fault(CustMsg ce, Object... argv);

}