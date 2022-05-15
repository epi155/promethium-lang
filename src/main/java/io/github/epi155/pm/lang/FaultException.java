package io.github.epi155.pm.lang;

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

}
