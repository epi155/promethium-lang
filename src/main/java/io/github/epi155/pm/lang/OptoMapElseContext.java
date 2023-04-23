package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * context to perform an action if none of the other conditions are met
 *
 * @param <T> value type of the opto value
 * @param <R> value type of the final value
 */
public interface OptoMapElseContext<T, R> {
    /**
     * fallible function to apply to the opto value
     *
     * @param fcn fallible function
     * @return instance of {@link OptoMapExitContext}
     */
    @NotNull OptoMapExitContext<R> map(@NotNull Function<? super T, ? extends ErrorXorValue<R>> fcn);

    /**
     * Apply transformation to the opto value
     *
     * @param fcn transformation function
     * @return instance of {@link OptoMapExitContext}
     */
    @NotNull OptoMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error arguments
     * @return instance of {@link OptoMapExitContext}
     */
    @NotNull OptoMapExitContext<R> fault(CustMsg ce, Object... argv);
}
