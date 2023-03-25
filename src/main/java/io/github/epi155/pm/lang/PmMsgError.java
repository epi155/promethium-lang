package io.github.epi155.pm.lang;

/**
 * Error messages with formatting {@link PmFormatter#format(String, Object[])}
 */
class PmMsgError implements MsgError {
    private final String code;
    private final String pattern;

    protected PmMsgError(String code, String pattern) {
        this.code = code;
        this.pattern = pattern;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message(Object... objects) {
        return PmFormatter.format(pattern, objects);
    }
}
