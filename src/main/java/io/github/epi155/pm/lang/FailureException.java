package io.github.epi155.pm.lang;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Checked exception Failure style
 */
public class FailureException extends Exception {
    private static final long serialVersionUID = -2344173422785699743L;
    /**
     * error code
     */
    @Getter
    private final String code;
    /**
     * error status
     */
    @Getter
    private final int status;
    /**
     * error stack trace
     */
    @Getter
    private final StackTraceElement ste;

    /**
     * Constructor
     *
     * @param e       original exception
     * @param ce      custom error
     * @param objects error parameter
     */
    public FailureException(Exception e, @NotNull MsgError ce, Object... objects) {
        super(ce.message(objects), e);
        this.code = ce.code();
        this.status = PmFailure.statusOf(ce);
        this.ste = PmFailure.guessLine(e, Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE]);
    }

    /**
     * Constructor
     *
     * @param e original exception
     */
    public FailureException(Exception e) {
        super(e);
        this.code = Failure.JAVA_EXCEPTION_CODE;
        this.status = PmFailure.JAVA_EXCEPTION_STATUS;
        this.ste = PmFailure.guessLine(e, Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE]);
    }

    /**
     * Constructor
     *
     * @param ste custom stack trace
     * @param e   original exception
     */
    public FailureException(StackTraceElement ste, Exception e) {
        super(e);
        this.code = Failure.JAVA_EXCEPTION_CODE;
        this.status = PmFailure.JAVA_EXCEPTION_STATUS;
        this.ste = ste;
    }

    /**
     * Constructor
     *
     * @param ce      custom error
     * @param objects error parameter
     */
    public FailureException(@NotNull MsgError ce, Object... objects) {
        super(ce.message(objects));
        this.code = ce.code();
        this.status = PmFailure.statusOf(ce);
        this.ste = Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE];
    }

    /**
     * Constructor
     *
     * @param fault error payload of exception
     */
    FailureException(@NotNull Failure fault) {
        super(fault.message());
        this.code = fault.code();
        this.status = fault.status();
        StackTraceElement origin = fault.stackTraceElement();
        this.ste = (origin == null) ? Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE] : origin;
    }
}
