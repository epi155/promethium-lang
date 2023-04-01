package io.github.epi155.pm.lang;

/**
 * Error messages with formatting {@link PmFormatter#format(String, Object[])}
 */
class PmNuntium implements Nuntium {
    private final String code;
    private final int status;
    private final String pattern;

    protected PmNuntium(String code, int status, String pattern) {
        this.code = code;
        this.status = status;
        this.pattern = pattern;
    }

    @Override
    public String code() {
        return code;
    }
    @Override
    public String message(Object[] objects) {
        return PmFormatter.format(pattern, objects);
    }

    @Override
    public int statusCode() {
        return status;
    }
}
