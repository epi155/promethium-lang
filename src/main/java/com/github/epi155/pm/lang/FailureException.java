package com.github.epi155.pm.lang;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class FailureException extends Exception {
    private static final long serialVersionUID = -2344173422785699743L;

    @Getter
    private final String code;
    @Getter
    private final int status;
    @Getter
    private final StackTraceElement ste;

    public FailureException(Exception e, @NotNull MsgError ce, Object... objects) {
        super(ce.message(objects), e);
        this.code = ce.code();
        this.status = PmFailure.statusOf(ce);
        this.ste = PmFailure.guessLine(e, Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE]);
    }

    public FailureException(Exception e) {
        super(e);
        this.code = Failure.JAVA_EXCEPTION_CODE;
        this.status = PmFailure.JAVA_EXCEPTION_STATUS;
        this.ste = PmFailure.guessLine(e, Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE]);
    }

    public FailureException(StackTraceElement ste, Exception e) {
        super(e);
        this.code = Failure.JAVA_EXCEPTION_CODE;
        this.status = PmFailure.JAVA_EXCEPTION_STATUS;
        this.ste = ste;
    }

    public FailureException(@NotNull MsgError ce, Object... objects) {
        super(ce.message(objects));
        this.code = ce.code();
        this.status = PmFailure.statusOf(ce);
        this.ste = Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE];
    }

    FailureException(@NotNull Failure fault) {
        super(fault.message());
        this.code = fault.code();
        this.status = fault.status();
        StackTraceElement origin = fault.stackTraceElement();
        this.ste = (origin == null) ? Thread.currentThread().getStackTrace()[PmAnyBuilder.J_LOCATE] : origin;
    }
}
