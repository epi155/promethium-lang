package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
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
    @NotNull ChooseNixExitContext thenAccept(@NotNull Consumer<? super T> action);

    /**
     * performs a fallible function on the value
     *
     * @param fcn fallible function on the value
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext thenApply(@NotNull Function<? super T, ? extends ItemStatus> fcn);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error argument
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext fault(@NotNull CustMsg ce, Object... argv);

    /**
     * Set custom error message with properties
     *
     * @param properties error properties
     * @param ce         custom error
     * @param argv       error argument
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv);

    /**
     * Set custom warning message
     *
     * @param ce   custom warning
     * @param argv warning argument
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext alert(@NotNull CustMsg ce, Object... argv);

    /**
     * Set custom warning message with properties
     *
     * @param properties warning properties
     * @param ce         custom warning
     * @param argv       warning argument
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext alert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv);

    /**
     * no operation
     *
     * @return instance of {@link ChooseNixExitContext}
     */
    @NotNull ChooseNixExitContext nop();
}
