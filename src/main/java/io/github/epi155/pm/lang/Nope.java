package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class for carrying a single error
 */
public interface Nope extends SingleError, OnlyError {
    /**
     * No error
     *
     * @return Nope senza errori
     */
    static @NotNull Nope nope() {
        return new PmNope();
    }

    /**
     * Crea un <b>Nope</b> con errore
     *
     * @param ce   riferimento formattazione errore
     * @param argv parametri per dettaglio errore
     * @return Nope con errore
     */
    static @NotNull Nope failure(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return new PmNope(PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv));
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
    static @NotNull Nope capture(@NotNull Throwable t) {
        return new PmNope(PmFailure.of(t));
    }

    /**
     * Create an error <b> Nope </b>
     * <p>
     * The <i> method / line </i> of the stacktrace,
     * which threw the class-internal exception calling <i> capture </i>,
     * is identified as a detail of the error
     * </p>
     *
     * @param t exception to catch
     * @return instance of {@link PmNope} with error
     */
    static @NotNull Nope captureHere(@NotNull Throwable t) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        StackTraceElement[] stErr = t.getStackTrace();
        for (int i = 1; i < stErr.length; i++) {
            StackTraceElement error = stErr[i];
            if (caller.getClassName().equals(error.getClassName()) && caller.getMethodName().equals(error.getMethodName())) {
                return new PmNope(PmFailure.ofException(error, t));
            }
        }
        return new PmNope(PmFailure.of(t));
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
     * If there is an error throw an exception
     *
     * @throws FailureException exception with error payload
     */
    void orThrow() throws FailureException;

    /**
     * If there is no error, the supplier is called,
     * if this ends with an error, this error is returned.
     * In the presence of an error, the supplier is not called, and the initial error is returned
     *
     * @param fcn producer {@link Hope} or {@link Nope}
     * @return {@link Nope} instance, if this has an error,
     * the producer is not called and the result has the original error
     */
    @NotNull Nope ergo(@NotNull Supplier<? extends SingleError> fcn);

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
     *      Hope&lt;B&gt; kb = compute().&lt;B&gt;map(this::n2sb); </pre>
     *
     * @param fcn   producer {@link Hope}
     * @return      {@link Hope} instance
     * @param <R>   {@link Hope} type
     */
    @NotNull <R> Hope<R> map(@NotNull Supplier<Hope<R>> fcn);

    /**
     * If there is no error, the action is performed.
     * In any case, the initial error is returned.
     *
     * @param action action executed if there are no errors
     * @return {@link Nope} instance, with original error, if any
     */
    @NotNull Nope peek(@NotNull Runnable action);

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
