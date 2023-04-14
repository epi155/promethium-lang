package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

class PmTrouble extends PmSignal implements Failure {
    private static final int JAVA_EXCEPTION_STATUS = 500;
    private static final String JAVA_EXCEPTION_CODE = "999J";


    private final String place;

    protected PmTrouble(String code, int status, String text, List<String> places) {
        super(code, status, text);
        this.place = "[ " + String.join(", ", places) + " ]";
    }


    static Failure of(@NotNull Throwable t, StackTraceElement caller) {
        StackTraceElement[] stErr = t.getStackTrace();
        LinkedList<String> places = new LinkedList<>();
        for (StackTraceElement error : stErr) {
            places.addFirst(lineOf(error));
            if (caller.getClassName().equals(error.getClassName()) && caller.getMethodName().equals(error.getMethodName())) {
                break;
            }
        }
        if (t instanceof FailureException) {
            return new PmTrouble(((FailureException) t).code, ((FailureException) t).status, t.getMessage(), places);
        } else {
            return new PmTrouble(JAVA_EXCEPTION_CODE, JAVA_EXCEPTION_STATUS, t.toString(), places);
        }
    }

    @Override
    public @Nullable String place() {
        return place;
    }
}
