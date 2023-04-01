package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

/**
 * Error builder extension for {@link Some}
 *
 * @param <T> payload type
 */
public interface SomeBuilder<T> extends ErrorBuilder {
    /**
     * value setter
     *
     * @param value payload
     * @return {@link SomeBuilder} instance
     */
    @NotNull SomeBuilder<T> withValue(@NotNull T value);

    /**
     * Final builder
     *
     * @return {@link Some} instance
     */
    @NotNull Some<T> build();

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
     * @param ce        warning message
     * @param argv      message arguments
     * @return          builder itself
     */
    default @NotNull SomeBuilder<T> withAlert(@NotNull Nuntium ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val warn = PmWarning.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(warn);
        return this;
    }

    /**
     * Add an error message in builder style
     *
     * @param ce        error message
     * @param argv      message arguments
     * @return          builder itself
     */
    default @NotNull SomeBuilder<T> withFault(@NotNull Nuntium ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(fail);
        return this;
    }

}
