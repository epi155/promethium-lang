package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Unchecked exception Failure style
 */
public class FaultException extends RuntimeException {
    private static final long serialVersionUID = -113203096713980318L;
    /**
     * error payload
     */
    public final transient Failure fault;

    /**
     * Constructor
     *
     * @param fault error payload of exception
     */
    public FaultException(Failure fault) {
        this.fault = fault;
    }

    /**
     * Contructor with custom error
     *
     * @param ce    message pattern
     * @param argv  message arguments
     */
    public FaultException(@NotNull MsgError ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        this.fault = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
    }

}
