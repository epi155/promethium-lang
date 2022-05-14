package com.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

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
     */
    static <U> @NotNull Some<U> failure(@NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmSome<>(Collections.singletonList(fail));
    }

    /**
     * static factory with {@link Throwable}
     *
     * @param t throwable instance
     * @return instance of {@link Some} (error)
     */
    static <U> @NotNull Some<U> capture(@NotNull Throwable t) {
        return new PmSome<>(Collections.singletonList(PmFailure.of(t)));
    }

    /**
     * static factory with {@link Failure}
     *
     * @param fault {@link Failure} instance
     * @return instance of {@link Some} (error)
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
    @NotNull Glitches onSuccess(Consumer<? super T> successAction);

    @NotNull None asNone();

    @NotNull <R> Some<R> andThen(@NotNull Function<T, ? extends SomeOne<R>> fcn);

    @NotNull None and(@NotNull Function<T, ? extends Any> fcn);

    @NotNull None andClose(@NotNull Consumer<T> action);


}
