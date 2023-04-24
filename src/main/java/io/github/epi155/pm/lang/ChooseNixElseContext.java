package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * context to perform an action if none of the other conditions are met
 *
 * @param <T> value type of the chosen value
 */
public interface ChooseNixElseContext<T> {
    /**
     * performs an action on the value
     *
     * @param action action on the value
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext peek(@NotNull Consumer<? super T> action);

    /**
     * performs a fallible function on the value
     *
     * @param fcn fallible function on the value
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error argument
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext fault(CustMsg ce, Object... argv);

    /**
     * Set custom warning message
     *
     * @param ce   custom warning
     * @param argv warning argument
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext alert(CustMsg ce, Object... argv);

    /**
     * no operation
     *
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext nop();
}
