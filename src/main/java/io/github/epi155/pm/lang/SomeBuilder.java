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
    @NotNull SomeBuilder<T> value(@NotNull T value);

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
    default @NotNull SomeBuilder<T> join(@NotNull ItemStatus any) {
        add(any);
        return this;
    }
    default @NotNull SomeBuilder<T> withAlert(@NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val warn = PmWarning.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        add(warn);
        return this;
    }
    default @NotNull SomeBuilder<T> withFault(@NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        add(fail);
        return this;
    }

    /**
     * Add error when runnable throw an {@link FailureException}
     *
     * @param runnable action to be executed
     * @return {@link SomeBuilder} instance
     */
    default @NotNull SomeBuilder<T> join(@NotNull CheckedRunnable runnable) {
        add(runnable);
        return this;
    }

}
