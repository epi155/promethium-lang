package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * context to raise error, warning or no-operation on a condition
 *
 * @param <T> value type of the chosen value
 */
public interface ChooseNixWhenStdContext<T> {
    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error argument
     * @return instance of {@link ChooseNixContext}
     */
    @NotNull ChooseNixContext<T> fault(@NotNull CustMsg ce, Object... argv);

    /**
     * Set custom error message with properties
     *
     * @param properties error properties
     * @param ce         custom error
     * @param argv       error argument
     * @return instance of {@link ChooseNixContext}
     */
    @NotNull ChooseNixContext<T> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv);

    /**
     * Set custom warning message
     *
     * @param ce   custom warning
     * @param argv warning argument
     * @return instance of {@link ChooseNixContext}
     */
    @NotNull ChooseNixContext<T> alert(@NotNull CustMsg ce, Object... argv);

    /**
     * Set custom warning message with properties
     *
     * @param properties warning properties
     * @param ce         custom warning
     * @param argv       warning argument
     * @return instance of {@link ChooseNixContext}
     */
    @NotNull ChooseNixContext<T> alert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv);

    /**
     * no operation
     *
     * @return instance of {@link ChooseNixContext}
     */
    @NotNull ChooseNixContext<T> nop();
}
