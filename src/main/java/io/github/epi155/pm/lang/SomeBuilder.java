package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Error builder extension for {@link Some}
 *
 * @param <T> payload type
 */
public interface SomeBuilder<T> extends ErrorBuilder {
    /**
     * value setter
     * <p>
     * The value cannot be set if there are errors.
     * If a value is set in these conditions, the value is ignored and a warning is added.
     * <p>
     * The value cannot be null.
     * If a null value is set, an error message is set.
     * <p>
     * The value cannot be an instance of Signal (Warning or Failure).
     * If such a value is set, it is set as a signal message (error or warning),
     * an error message is also added.
     * <p>
     * A value cannot be set more than once.
     * If a further value is set, an error message is set and all values are ignored.
     *
     * @param value payload
     */
    void value(@NotNull T value);

    /**
     * Final builder
     * <p>
     * If neither a value nor an error has been set, an error is set.
     * <p>
     * If an error was set after a value was set,
     * the value is ignored and a warning message is added
     *
     * @return {@link Some} instance
     */
    @NotNull Some<T> build();

    /**
     * set final value and return {@link Some} instance
     * <p>
     * The value cannot be set if there are errors.
     * If a value is set in these conditions, the value is ignored and a warning is added.
     * <p>
     * The value cannot be null.
     * If a null value is set, an error message is set.
     * <p>
     * The value cannot be an instance of Signal (Warning or Failure).
     * If such a value is set, it is set as a signal message (error or warning),
     * an error message is also added.
     * <p>
     * A value cannot be set more than once.
     * If a value has already been set, an error message is set and all values are ignored
     * (use {@link #build()}.
     *
     * @param value final value
     * @return {@link Some} instance
     */
    @NotNull Some<T> buildWithValue(@NotNull T value);

    /**
     * Add error(s)
     *
     * @param any object with error(s) payload
     * @return {@link SomeBuilder} instance
     */
    default @NotNull SomeBuilder<T> withStatus(@NotNull ItemStatus any) {
        add(any);
        return this;
    }

    /**
     * Add a warning message in builder style
     *
     * @param ce   warning message
     * @param argv message arguments
     * @return builder itself
     */
    default @NotNull SomeBuilder<T> withAlert(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Warning warn = PmWarning.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(warn);
        return this;
    }

    /**
     * Add a warning message with properties in builder style
     *
     * @param properties warning properties
     * @param ce         warning message
     * @param argv       message arguments
     * @return builder itself
     */
    default @NotNull SomeBuilder<T> withAlert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Warning warn = PmWarning.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(warn);
        return this;
    }

    /**
     * Add an error message in builder style
     *
     * @param ce   error message
     * @param argv message arguments
     * @return builder itself
     */
    default @NotNull SomeBuilder<T> withFault(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Failure fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(fail);
        return this;
    }

    /**
     * Add an error message with properties in builder style
     *
     * @param properties error properties
     * @param ce         error message
     * @param argv       message arguments
     * @return builder itself
     */
    default @NotNull SomeBuilder<T> withFault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Failure fail = PmFailure.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(fail);
        return this;
    }

}
