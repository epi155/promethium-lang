package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Checked exception Failure style
 */
public class FailureException extends Exception {
    private static final long serialVersionUID = -2344173422785699743L;
    /**
     * error code
     */
    protected final String code;
    /**
     * error status
     */
    protected final int status;

    /**
     * Constructor
     *
     * @param ce   custom error
     * @param argv error parameter
     */
    public FailureException(@NotNull CustMsg ce, Object... argv) {
        super(ce.message(argv));
        this.code = ce.code();
        this.status = ce.statusCode();
    }

}
