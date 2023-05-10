package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import org.jetbrains.annotations.NotNull;
import org.slf4j.helpers.MessageFormatter;

/**
 * Error messages with formatting {@link PmFormatter#format(String, Object[])}
 */
class PmCustMsg implements CustMsg {
    private final String code;
    private final int status;
    private final String pattern;

    protected PmCustMsg(String code, int status, String pattern) {
        this.code = code;
        this.status = status;
        this.pattern = pattern;
    }

    static @NotNull CustMsg of(@NotNull String code, @NotNull String pattern) {
        return new PmCustMsg(code, 500, pattern);
    }

    static @NotNull CustMsg of(@NotNull String code, int status, @NotNull String pattern) {
        return new PmCustMsg(code, status, pattern);
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message(Object[] objects) {
        return MessageFormatter.arrayFormat(pattern, objects).getMessage();
    }

    @Override
    public int statusCode() {
        return status;
    }
}
