package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * context to perform an action on a condition
 *
 * @param <T> value type of the chosen value
 * @param <R> value type of the final value
 */
public interface ChooseMapWhenContext<T, R> {
    /**
     * fallible function to apply to the chosen value
     *
     * @param fcn fallible function
     * @return instance of {@link ChooseMapContext}
     */
    @NotNull ChooseMapContext<T, R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn);

    /**
     * Apply transformation to the chosen value
     *
     * @param fcn transformation function
     * @return instance of {@link ChooseMapContext}
     */
    @NotNull ChooseMapContext<T, R> mapOf(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error arguments
     * @return instance of {@link ChooseMapContext}
     */
    @NotNull ChooseMapContext<T, R> fault(@NotNull CustMsg ce, Object... argv);

    /**
     * Set custom error message with properties
     *
     * @param properties error properties
     * @param ce         custom error
     * @param argv       error arguments
     * @return instance of {@link ChooseMapContext}
     */
    @NotNull ChooseMapContext<T, R> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv);
}
