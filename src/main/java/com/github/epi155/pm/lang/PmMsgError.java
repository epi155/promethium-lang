package com.github.epi155.pm.lang;

import org.slf4j.helpers.MessageFormatter;

/**
 * Error messages with formatting {@link MessageFormatter#format(String, Object)}
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
        return MessageFormatter.arrayFormat(pattern, objects).getMessage();
    }
}
