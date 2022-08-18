package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for carrying a single error xor a value
 *
 * @param <T> value type
 */
public interface Hope<T> extends SingleError, AnyValue<T> {
    /**
     * Create a <b> Hope </b> with value
     *
     * @param value value to set
     * @param <S>   type of value
     * @return <b>Hope</b> instance
     */
    static <S> @NotNull Hope<S> of(S value) {
        return new PmHope<>(value, null);
    }

    /**
     * Create an error <b> Hope </b>
     *
     * @param fault error
     * @param <S>   type value in case of success
     * @return <b>Hope</b> instance
     */
    static <S> @NotNull Hope<S> of(@NotNull Failure fault) {
        return new PmHope<>(null, fault);
    }

    /**
     * Create an error <b> Hope </b>
     *
     * @param ce   custom error message
     * @param argv custom error parameters
     * @param <S>  type value in case of success
     * @return <b>Hope</b> instance
     */
    static <S> @NotNull Hope<S> failure(@NotNull MsgError ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return new PmHope<>(null, PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv));
    }

    /**
     * Create an error <b> Hope </b>
     * <p>
     * The first <i> class / method / line </i> of the stacktrace,
     * which has the same package as the class it calls <i> capture </i>
     * (limited to the first two namespaces),
     * is identified as a detail of the error
     * </p>
     *
     * @param t   exception to catch
     * @param <S> type value in case of success
     * @return <b>Hope</b> instance
     */
    static <S> @NotNull Hope<S> capture(@NotNull Throwable t) {
        return new PmHope<>(null, PmFailure.of(t));
    }

    /**
     * Create an error <b> Hope </b>
     * <p>
     * The <i> method / line </i> of the stacktrace,
     * which threw the class-internal exception calling <i> capture </i>,
     * is identified as a detail of the error
     * </p>
     *
     * @param t   exception to catch
     * @param <S> type value in case of success
     * @return <b>Hope</b> instance
     */
    static <S> @NotNull Hope<S> captureHere(@NotNull Throwable t) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        StackTraceElement[] stErr = t.getStackTrace();
        for (int i = 1; i < stErr.length; i++) {
            StackTraceElement error = stErr[i];
            if (caller.getClassName().equals(error.getClassName()) && caller.getMethodName().equals(error.getMethodName())) {
                return new PmHope<>(null, PmFailure.ofException(error, t));
            }
        }
        return new PmHope<>(null, PmFailure.of(t));
    }

    /**
     * Retrieve the value if there is no error or throw an exception
     *
     * @return value
     * @throws FailureException exception with error payload
     */
    T orThrow() throws FailureException;

    /**
     * Compose operator
     *
     * @param fcn transform value to result {@link Hope}
     * @param <R> result type
     * @return result {@link Hope} instance, if this has an error, the transformation is not called and the result has the original error
     */
    @NotNull <R> Hope<R> andThen(@NotNull Function<? super T, Hope<R>> fcn);

    /**
     * map value
     *
     * @param fcn mapping function
     * @param <R> result type
     * @return {@link Hope} instance with new value,
     * if this has errors, the transformation is not called and the result has the original error;
     * RuntimeException are caught as new error
     */
    @NotNull <R> Hope<R> map(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * Logical implies operator
     *
     * @param action action on value, executed if there are no errors
     * @return {@link Nope} instance, with original error, if any
     */
    @NotNull Nope implies(@NotNull Consumer<? super T> action);

    /**
     * Logical short-circuit and operator
     *
     * @param fcn transform value to {@link Hope} or {@link Nope}
     * @return {@link Nope} instance, if this has an error,
     * the transformation is not called and the result has the original error
     */
    @NotNull Nope and(@NotNull Function<? super T, ? extends SingleError> fcn);

    /**
     * Set the action on success
     * <p>
     * In the event of an error, the action is not performed.
     * </p>
     *
     * @param action action to be taken if successful
     * @return Glitch to set the action on failure
     * @see Glitch#onFailure(Consumer)
     */
    @NotNull Glitch onSuccess(@NotNull Consumer<? super T> action);

    /**
     * Collapse to {@link Nope} instance, keeping only error data, and lost value
     *
     * @return {@link Nope} instance
     */
    @NotNull Nope asNope();

    /**
     * constructs a result using two alternative methods depending on whether the operation completed successfully or failed
     *
     * @param onSuccess success builder
     * @param onFailure failure builder
     * @param <R>       result type
     * @return result
     */
    <R> R mapTo(Function<T, R> onSuccess, Function<Failure, R> onFailure);
}
