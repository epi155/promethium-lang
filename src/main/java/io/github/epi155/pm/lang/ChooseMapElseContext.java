package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;

/**
 * context to perform an action if none of the other conditions are met
 *
 * @param <T> value type of the chosen value
 * @param <R> value type of the final value
 */
public interface ChooseMapElseContext<T, R> {
    /**
     * fallible function to apply to the chosen value
     *
     * @param fcn fallible function
     * @return instance of {@link ChooseMapExitContext}
     */
    @NotNull ChooseMapExitContext<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn);

    /**
     * Apply transformation to the chosen value
     *
     * @param fcn transformation function
     * @return instance of {@link ChooseMapExitContext}
     */
    @NotNull ChooseMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error arguments
     * @return instance of {@link ChooseMapExitContext}
     */
    @NotNull ChooseMapExitContext<R> fault(CustMsg ce, Object... argv);

    /**
     * Set custom error message with properties
     *
     * @param properties error properties
     * @param ce         custom error
     * @param argv       error arguments
     * @return instance of {@link ChooseMapExitContext}
     */
    @NotNull ChooseMapExitContext<R> fault(@NotNull Map<String, Object> properties, CustMsg ce, Object... argv);

}
