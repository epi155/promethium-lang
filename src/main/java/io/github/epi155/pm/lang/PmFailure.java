package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

class PmFailure extends PmSignal implements Failure {

    protected PmFailure(String code, int status, String text, StackTraceElement stack) {
        super(code, status, text, stack);
    }
    protected static @NotNull Failure ofException(@NotNull StackTraceElement ste, @NotNull Throwable t) {
        if (t instanceof FailureException) {
            FailureException fe = (FailureException) t;
            String text = t.getMessage();
            return new PmFailure(fe.getCode(), fe.getStatus(), text, ste);
        } else if (t instanceof FaultException) {
            return copy(((FaultException) t).fault, ste);
        } else {
            String text = t.toString();
            return new PmFailure(JAVA_EXCEPTION_CODE, JAVA_EXCEPTION_STATUS, text, ste);
        }
    }

    private static @NotNull Failure copy(@NotNull Failure source, @NotNull StackTraceElement ste) {
        PmFailure target = new PmFailure(source.code(), source.status(), source.message(), ste);
        source.forEach(target::setProperty);
        return target;
    }

    protected static @NotNull Failure of(@NotNull Throwable ex) {
        StackTraceElement ste = guessLine(ex, caller());
        if (ex instanceof FailureException) {
            FailureException fe = (FailureException) ex;
            String text = ex.getMessage();
            return new PmFailure(fe.getCode(), fe.getStatus(), text, fe.getSte() == null ? ste : fe.getSte());
        } else if (ex instanceof FaultException) {
            return ((FaultException) ex).fault;
        } else {
            String text = ex.toString();
            return new PmFailure(JAVA_EXCEPTION_CODE, JAVA_EXCEPTION_STATUS, text, ste);
        }
    }

    protected static @NotNull Failure ofMessage(@NotNull Throwable t, String packagePrefix, @NotNull Nuntium ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t.getMessage();
        StackTraceElement ste = scan(t, packagePrefix);
        return new PmFailure(code, ce.statusCode(), text, ste);
    }

    protected static @NotNull Failure ofException(@NotNull Throwable t, String packagePrefix, @NotNull Nuntium ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t;
        StackTraceElement ste = scan(t, packagePrefix);
        return new PmFailure(code, ce.statusCode(), text, ste);
    }

    protected static @NotNull Failure ofMessage(StackTraceElement ste, @NotNull Throwable t, @NotNull Nuntium ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t.getMessage();
        return new PmFailure(code, ce.statusCode(), text, ste);
    }

    protected static @NotNull Failure ofException(StackTraceElement ste, @NotNull Throwable t, @NotNull Nuntium ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t;
        return new PmFailure(code, ce.statusCode(), text, ste);
    }

    protected static @NotNull Failure of(StackTraceElement ste, @NotNull Nuntium ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmFailure(code, ce.statusCode(), text, ste);
    }

}
