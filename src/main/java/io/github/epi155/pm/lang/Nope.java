package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class for carrying a single error
 * <p>
 *     The interface has static constructors with custom error message or without
 *     <pre>
 *      Nope.nope();                                    // no error
 *      Nope.fault(CustMsg ce, Object... argv);         // error message </pre>
 *     and with Exception
 *     <pre>
 *      Nope.capture(Throwable t);      // error from Exception (package level)</pre>
 * <p>
 *     The outcome can be evaluated imperatively
 *     <pre>
 *      if (nope.completeWithoutErrors()) {
 *          // ... action on success
 *      } else {
 *          val fault = nope.fault();
 *          // ... action on error
 *      } </pre>
 *     or functionally
 *     <pre>
 *      nope
 *          .onSuccess(() -> { ... })    // ... action on success
 *          .onFailure(e -> { ... });    // ... action on error
 *
 *      R r = nope.&lt;R&gt;mapTo(
 *          () -> ...R,     // supplier to R
 *          e -> ...R);     // function from error to R
 *     </pre>
 */
public interface Nope extends SingleError, OnlyError {
    /**
     * No error
     *
     * @return Nope senza errori
     */
    @NoBuiltInCapture
    static @NotNull Nope nope() {
        return PmNope.nope();
    }

    /**
     * Static constructor with error
     *
     * @param ce   custom error
     * @param argv error parameters
     * @return instance of {@link Nope} with error
     */
    @NoBuiltInCapture
    static @NotNull Nope fault(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return new PmNope(PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv));
    }

    /**
     * Static constructor with error with properties
     *
     * @param properties error properties
     * @param ce         custom error
     * @param argv       error parameters
     * @return instance of {@link Nope} with error
     */
    @NoBuiltInCapture
    static @NotNull Nope fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return new PmNope(PmFailure.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, argv));
    }

    /**
     * Create an error <b> Nope </b>
     * <p>
     * The first <i> class / method / line </i> of the stacktrace,
     * which has the same package as the class it calls <i> capture </i>
     * (limited to the first two namespaces),
     * is identified as a detail of the error
     * </p>
     *
     * @param t exception to catch
     * @return instance of {@link PmNope} with error
     */
    @NoBuiltInCapture
    static @NotNull Nope capture(@NotNull Throwable t) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        return new PmNope(PmTrouble.of(t, caller));
    }


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
    @NotNull Glitch onSuccess(Runnable action);

    /**
     * If there is no error, the supplier is called,
     * if this ends with an error, this error is returned.
     * In the presence of an error, the supplier is not called, and the initial error is returned
     *
     * @param fcn producer {@link Hope} or {@link Nope}
     * @return {@link Nope} instance, if this has an error,
     * the producer is not called and the result has the original error
     */
    @NotNull Nope ergoes(@NotNull Supplier<? extends SingleError> fcn);

    /**
     * If there are no errors, the supplier is called,
     * if this ends with errors, these errors are returned.
     * In the presence of errors, the supplier is not called, and the initial errors are returned
     *
     * <p>
     *     The method allows to compose the Nope with a Hope.
     *     Using an imperative outcome evaluation we would have
     * <pre>
     *      Nope na = compute();
     *      Hope&lt;B&gt; sb;
     *      if (na.completeWithoutErrors()) {
     *          sb = n2sb();    // Hope&lt;B&gt; n2sb()
     *      } else {
     *          sb = Hope.failure(na);
     *      } </pre>
     *      The method simplifies it to
     * <pre>
     *      Hope&lt;B&gt; kb = compute().&lt;B&gt;maps(this::n2sb); </pre>
     *
     * @param fcn   producer {@link Hope}
     * @return      {@link Hope} instance
     * @param <R>   {@link Hope} type
     */
    @NotNull <R> Hope<R> maps(@NotNull Supplier<? extends Hope<R>> fcn);

    /**
     * map value
     * <p>Nope &bull; <i>value</i> &rarr; Hope</p>
     *
     * @param fcn mapping function
     * @param <R> result type
     * @return {@link Hope} instance with new value,
     * if this has errors, the transformation is not called and the result has the original error;
     */
    @NotNull <R> Hope<R> mapsOf(@NotNull Supplier<? extends R> fcn);

    /**
     * If there is no error, the action is performed.
     * In any case, the initial error is returned.
     *
     * @param action action executed if there are no errors
     * @return {@link Nope} instance, with original error, if any
     */
    @NotNull Nope implies(@NotNull Runnable action);

    /**
     * constructs a result using two alternative methods depending on whether the operation completed successfully or failed
     *
     * @param onSuccess success builder
     * @param onFailure failure builder
     * @param <R>       result type
     * @return result
     */
    <R> R mapTo(Supplier<R> onSuccess, Function<Failure, R> onFailure);
}
