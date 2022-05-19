package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for carrying many errors xor a value
 *
 * @param <T> value type
 */
public interface Some<T> extends Any, Glitches, SomeOne<T> {
    /**
     * {@link Some} builder
     *
     * @param <U> data type
     * @return instance of {@link SomeBuilder}
     */
    static <U> @NotNull SomeBuilder<U> builder() {
        return new PmSomeBuilder<>();
    }

    /**
     * static factory with error message
     *
     * @param ce      error message
     * @param objects message parameters
     * @return instance of {@link Some} (error)
     * @param <U>   payload type
     */
    static <U> @NotNull Some<U> failure(@NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmSome<>(Collections.singletonList(fail));
    }

    /**
     * static factory with {@link Throwable}
     * @param t throwable instance
     * @return instance of {@link Some} (error)
     * @param <U>   payload type
     */
    static <U> @NotNull Some<U> capture(@NotNull Throwable t) {
        return new PmSome<>(Collections.singletonList(PmFailure.of(t)));
    }

    /**
     * static factory with {@link Failure}
     *
     * @param fault {@link Failure} instance
     * @return instance of {@link Some} (error)
     * @param <U>   payload type
     */
    static <U> @NotNull Some<U> of(@NotNull Failure fault) {
        return new PmSome<>(Collections.singletonList(fault));
    }

    /**
     * static factory with payload value
     *
     * @param value payload value
     * @param <U>   payload type
     * @return instance of {@link Some} (success)
     */
    static <U> @NotNull Some<U> of(@NotNull U value) {
        return new PmSome<>(value);
    }

    /**
     * Set the action on success
     * <p>
     * In the event of an error, the action is not performed.
     * </p>
     *
     * @param successAction action to be taken if successful
     * @return Glitches to set the action on failure
     * @see Glitches#onFailure(Consumer)
     */
    @NotNull Glitches onSuccess(@NotNull Consumer<? super T> successAction);

    /**
     * Collapse to {@link None} instance, keeping only errors data, and lost value
     *
     * @return {@link None} instance
     */
    @NotNull None asNone();

    /**
     * Compose operator
     *
     * @param fcn transform value to result {@link Some}
     * @param <R> result type
     * @return result {@link Some} instance, if this has errors, the transformation is not called and the result has the original error
     */
    @NotNull <R> Some<R> andThen(@NotNull Function<? super T, ? extends SomeOne<R>> fcn);

    /**
     * map value
     *
     * @param fcn mapping function
     * @param <R> result type
     * @return {@link Some} instance with new value,
     * if this has errors, the transformation is not called and the result has the original error;
     * RuntimeException are caught as new error
     */
    @NotNull <R> Some<R> map(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * Logical short-circuit and operator
     *
     * @param fcn transform value to {@link Some} or {@link None}
     * @return {@link None} instance, if this has errors, the transformation is not called and the result has the original error
     */
    @NotNull None and(@NotNull Function<? super T, ? extends Any> fcn);

    /**
     * Logical implies operator
     *
     * @param action action on value, executed if there are no errors
     * @return {@link None} instance, with original error, if any
     */
    @NotNull None implies(@NotNull Consumer<? super T> action);

}
