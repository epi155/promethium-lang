package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class PmFailure extends PmSignal implements Failure {

    private final String place;

    protected PmFailure(String code, int status, String text, StackTraceElement stack) {
        super(code, status, text);
        this.place = lineOf(stack);
    }

    protected static @NotNull Failure of(StackTraceElement ste, @NotNull CustMsg ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmFailure(code, ce.statusCode(), text, ste);
    }

    @Override
    public @Nullable String place() {
        return place;
    }
}
