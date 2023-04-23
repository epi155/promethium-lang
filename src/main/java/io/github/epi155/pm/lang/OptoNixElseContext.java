package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * context to perform an action if none of the other conditions are met
 *
 * @param <T> value type of the chosen value
 */
public interface OptoNixElseContext<T> {
    /**
     * performs a fallible function on the value
     *
     * @param fcn fallible function on the value
     * @return instance of {@link OptoNixExitContext}
     */
    @NotNull OptoNixExitContext ergo(@NotNull Function<? super T, ? extends SingleError> fcn);

    /**
     * performs an action on the value
     *
     * @param action action on the value
     * @return instance of {@link OptoNixExitContext}
     */
    @NotNull OptoNixExitContext peek(@NotNull Consumer<? super T> action);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error argument
     * @return instance of {@link OptoNixExitContext}
     */
    @NotNull OptoNixExitContext fault(CustMsg ce, Object... argv);

    /**
     * no operation
     *
     * @return instance of {@link OptoNixExitContext}
     */
    @NotNull OptoNixExitContext nop();
}
