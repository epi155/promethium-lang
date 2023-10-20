package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

class PmWarning extends PmSignal implements Warning {

    private final String place;

    protected PmWarning(String code, int status, String text, StackTraceElement stack) {
        super(code, status, text);
        this.place = lineOf(stack);
    }

    protected PmWarning(@NotNull Map<String, Object> properties, String code, int status, String text, StackTraceElement stack) {
        super(properties, code, status, text);
        this.place = lineOf(stack);
    }

    protected static @NotNull Warning of(StackTraceElement ste, @NotNull CustMsg ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmWarning(code, ce.statusCode(), text, ste);
    }

    protected static @NotNull Warning of(@NotNull Map<String, Object> properties, StackTraceElement ste, @NotNull CustMsg ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmWarning(properties, code, ce.statusCode(), text, ste);
    }

    @Override
    public @Nullable String place() {
        return place;
    }
}
