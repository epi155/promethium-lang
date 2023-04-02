package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility interface for carrying a single error xor a value
 * <p>
 *     The interface has two static constructors with value or custom error message
 *     <pre>
 *      Hope.of(T value);                                // final value
 *      Hope.failure(CustMsg ce, Object... argv);       // error message
 *     </pre>
 *     and with Exception
 *     <pre>
 *      Hope.capture(Throwable t);      // error from Exception (package level)
 *      Hope.captureHere(Throwable t);  // error from Exception (method level)
 *     </pre>
 * <p>
 *     The outcome of the interface can be evaluated imperatively
 *     <pre>
 *      if (hope.completeWithoutErrors()) {
 *          val value = hope.value();
 *          // ... action on value
 *      } else {
 *          val fault = hope.fault();
 *          // ... action on error
 *      }
 *     </pre>
 *     or functionally
 *     <pre>
 *      hope
 *          .onSuccess(v -> { ... })     // ... action on value
 *          .onFailure(e -> { ... });    // ... action on error
 *
 *      R r = hope.&lt;R&gt;mapTo(
 *          v -> ...R,      // function from value to R
 *          e -> ...R);     // function from error to R
 *     </pre>
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
    static <S> @NotNull Hope<S> of(@NotNull S value) {
        if (value == null) {
            StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
            val fail = PmFailure.ofException(stPtr[PmAnyBuilder.J_LOCATE], new IllegalArgumentException("the argument cannot be null"));
            return new PmHope<>(null, fail);
        } else if (value instanceof Signal) {
            StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
            val fail = PmFailure.ofException(stPtr[PmAnyBuilder.J_LOCATE], new IllegalArgumentException("the argument cannot be an instanceof Signal"));
            fail.setProperty("cause", value);
            return new PmHope<>(null, fail);
        }
        return new PmHope<>(value, null);
    }

    /**
     * Create an error <b> Hope </b>
     *
     * @param ce   custom error message
     * @param argv custom error parameters
     * @param <S>  type value in case of success
     * @return <b>Hope</b> instance
     */
    static <S> @NotNull Hope<S> failure(@NotNull CustMsg ce, Object... argv) {
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
     * Static constructor with {@link SingleError} ({@link Hope} or {@link Nope} completeWithErrors)
     * @param se    {@link SingleError} instance
     * @return      {@link Hope} instance in error status
     * @param <S>   {@link Hope} data type (dummy)
     */
    static <S> Hope<S> failure(SingleError se) {
        return new PmHope<>(null, se.fault());
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
     * <p>Hope &bull; Hope &rarr; Hope</p>
     *
     * <p>
     *     The method allows to compose two hopes.
     *     Using an imperative outcome evaluation we would have
     * <pre>
     *      Hope&lt;A&gt; ha = computeA();
     *      Hope&lt;B&gt; hb;
     *      if (ha.completeWithoutErrors()) {
     *          hb = a2b(ha.value());           // Hope&lt;B&gt; a2b(A value);
     *      } else {
     *          hb = Hope.&lt;B&gt;failure(ha);
     *      } </pre>
     *      The method simplifies it to
     * <pre>
     *      Hope&lt;B&gt; hb = computeA().map(this::a2b); </pre>
     *
     * @param fcn transform value to result {@link Hope}
     * @param <R> result type
     * @return result {@link Hope} instance, if this has an error, the transformation is not called and the result has the original error
     */
    @NotNull <R> Hope<R> map(@NotNull Function<? super T, Hope<R>> fcn);

    /**
     * External compose operator to {@link Some}
     * <p>Hope &bull; AnyValue<sup>+</sup> &rarr; Some</p>
     *
     * @param fcn transform value to result {@link AnyValue}
     * @param <R> result type
     * @return result {@link Some} instance, if this has an error, the transformation is not called and the result has the original error
     */
    @NotNull <R> Some<R> mapOut(@NotNull Function<? super T, ? extends AnyValue<R>> fcn);

    /**
     * map value
     * <p>Hope &bull; <i>value</i> &rarr; Hope</p>
     *
     * @param fcn mapping function
     * @param <R> result type
     * @return {@link Hope} instance with new value,
     * if this has errors, the transformation is not called and the result has the original error;
     * RuntimeException are caught as new error
     */
    @NotNull <R> Hope<R> mapOf(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * If there is no error and the value is present, the action on the value is performed.
     * In any case the initial class is returned.
     *
     * @param action action on value, executed if there are no errors
     * @return original {@link Hope} instance, with value/error
     */
    @NotNull Hope<T> peek(@NotNull Consumer<? super T> action);

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
