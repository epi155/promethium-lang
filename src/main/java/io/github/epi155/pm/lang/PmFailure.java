package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

class PmFailure extends PmSignal implements Failure {

    private final String place;

    protected PmFailure(String code, int status, String text, StackTraceElement stack) {
        super(code, status, text);
        this.place = lineOf(stack);
    }

    protected PmFailure(@NotNull Map<String, Object> properties, String code, int status, String text, StackTraceElement stack) {
        super(properties, code, status, text);
        this.place = lineOf(stack);
    }

    protected static @NotNull Failure of(StackTraceElement ste, @NotNull CustMsg ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmFailure(code, ce.statusCode(), text, ste);
    }

    protected static @NotNull Failure of(@NotNull Map<String, Object> properties, StackTraceElement ste, @NotNull CustMsg ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmFailure(properties, code, ce.statusCode(), text, ste);
    }

    @Override
    public @Nullable String place() {
        return place;
    }
}
