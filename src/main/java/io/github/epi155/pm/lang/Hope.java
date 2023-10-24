package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility interface for carrying a single error xor a value
 * <p>
 *     The interface has static constructors with value or custom error message
 *     <pre>
 *      Hope.of(T value);                                // final value
 *      Hope.fault(CustMsg ce, Object... argv);          // error message </pre>
 *     and with Exception
 *     <pre>
 *      Hope.capture(Throwable t);      // error from Exception </pre>
 * <p>
 *     The outcome can be evaluated imperatively
 *     <pre>
 *      if (hope.completeWithoutErrors()) {
 *          val value = hope.value();
 *          // ... action on value
 *      } else {
 *          val fault = hope.fault();
 *          // ... action on error
 *      } </pre>
 *     or functionally
 *     <pre>
 *      hope
 *          .onSuccess(v -> { ... })     // ... action on value
 *          .onFailure(e -> { ... });    // ... action on error
 *
 *      R r = hope.&lt;R&gt;mapTo(
 *          v -> ...R,      // function from value to R
 *          e -> ...R);     // function from error to R </pre>
 *
 * @param <T> value type
 */
public interface Hope<T> extends ErrorXorValue<T>, OptoContext<T> {
    /**
     * Create a <b> Hope </b> with value
     *
     * @param value value to set
     * @param <S>   type of value
     * @return <b>Hope</b> instance
     */
    @NoBuiltInCapture
    static <S> @NotNull Hope<S> of(@NotNull S value) {
        /*
         * the @NotNull annotation tells the IDE that the value should be not null,
         * but the inattentive user could set a null value
         */
        //noinspection ConstantValue
        if (value == null) {
            StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
            return new PmHope<>(null, PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], EnumMessage.NIL_ARG));
        } else if (value instanceof Signal) {
            StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
            @NotNull Failure fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], EnumMessage.ILL_ARG);
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
    @NoBuiltInCapture
    static <S> @NotNull Hope<S> fault(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return new PmHope<>(null, PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv));
    }

    /**
     * Create an error with properties <b> Hope </b>
     *
     * @param properties error properties
     * @param ce         custom error message
     * @param argv       custom error parameters
     * @param <S>        type value in case of success
     * @return <b>Hope</b> instance
     */
    @NoBuiltInCapture
    static <S> @NotNull Hope<S> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return new PmHope<>(null, PmFailure.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, argv));
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
    @NoBuiltInCapture
    static <S> @NotNull Hope<S> capture(@NotNull Throwable t) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        return new PmHope<>(null, PmTrouble.of(t, caller));
    }


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
     *      Hope&lt;B&gt; hb = computeA().maps(this::a2b); </pre>
     *
     * @param fcn transform value to result {@link Hope}
     * @param <R> result type
     * @return result {@link Hope} instance, if this has an error, the transformation is not called and the result has the original error
     */
    @NotNull <R> Hope<R> maps(@NotNull Function<? super T, ? extends Hope<R>> fcn);

    /**
     * Compose operator
     * <p>Hope &bull; SingleError &rarr; Nope</p>
     *
     * @param fcn fallible function
     * @return result {@link Nope} instance, if this has an error, the function is not called and the result has the original error
     */
    @NotNull Nope ergoes(@NotNull Function<? super T, ? extends SingleError> fcn);

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
    @NotNull <R> Hope<R> mapsOf(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * If there is no error and the value is present, the action on the value is performed.
     * In any case the initial class is returned.
     *
     * @param action action on value, executed if there are no errors
     * @return original {@link Hope} instance, with value/error
     */
    @NotNull Hope<T> implies(@NotNull Consumer<? super T> action);

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
