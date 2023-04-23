package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * context to perform an action on an instance of condition
 *
 * @param <U> value type to be tested
 * @param <T> value type of the opto value
 * @param <R> value type of the final value
 */
public interface OptoMapWhenAsContext<U, T, R> {
    /**
     * fallible function to apply to the opto value
     *
     * @param fcn fallible function
     * @return instance of {@link OptoMapContext}
     */
    @NotNull OptoMapContext<T, R> map(@NotNull Function<? super U, ? extends ErrorXorValue<R>> fcn);

    /**
     * Apply transformation to the opto value
     *
     * @param fcn transformation function
     * @return instance of {@link OptoMapContext}
     */
    @NotNull OptoMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error arguments
     * @return instance of {@link OptoMapContext}
     */
    @NotNull OptoMapContext<T, R> fault(CustMsg ce, Object... argv);
}
