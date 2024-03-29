package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * context to perform an action on an instance of condition
 *
 * @param <U> value type to be tested
 * @param <T> value type of the opto value
 */
public interface OptoNixWhenAsContext<U, T> {
    /**
     * performs a fallible function on the value
     *
     * @param fcn fallible function on the value
     * @return instance of {@link OptoNixContext}
     */
    @NotNull OptoNixContext<T> thenApply(@NotNull Function<? super U, ? extends SingleError> fcn);

    /**
     * performs an action on the value
     *
     * @param action action on the value
     * @return instance of {@link OptoNixContext}
     */
    @NotNull OptoNixContext<T> thenAccept(@NotNull Consumer<? super U> action);

    /**
     * Set custom error message
     *
     * @param ce   custom error
     * @param argv error argument
     * @return instance of {@link OptoNixContext}
     */
    @NotNull OptoNixContext<T> fault(@NotNull CustMsg ce, Object... argv);

    /**
     * Set custom error message with properties
     *
     * @param properties error properties
     * @param ce         custom error
     * @param argv       error argument
     * @return instance of {@link OptoNixContext}
     */
    @NotNull OptoNixContext<T> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv);

    /**
     * no operation
     *
     * @return instance of {@link OptoNixContext}
     */
    @NotNull OptoNixContext<T> nop();
}
