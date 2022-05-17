package io.github.epi155.pm.lang;


import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Utility class for carrying a single error
 */
public interface Nope extends One, Glitch {
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
     * @param fault errore
     * @return Nope con errore
     */
    static @NotNull Nope of(@NotNull Failure fault) {
        return new PmNope(fault);
    }

    /**
     * Crea un <b>Nope</b> con errore
     *
     * @param ce   riferimento formattazione errore
     * @param argv parametri per dettaglio errore
     * @return Nope con errore
     */
    static @NotNull Nope failure(@NotNull MsgError ce, Object... argv) {
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

}
