package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

class PmWarning extends PmSignal implements Warning {

    protected PmWarning(String code, int status, String text, StackTraceElement stack) {
        super(code, status, text, stack);
    }
    protected static @NotNull Warning of(StackTraceElement ste, @NotNull Nuntium ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmWarning(code, ce.statusCode(), text, ste);
    }

}
