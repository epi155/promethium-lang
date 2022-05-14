package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface MsgError {
    /**
     * Factory constructor
     *
     * @param code    error code
     * @param pattern error format message
     * @return instance of {@link PmMsgError}
     */
    static @NotNull MsgError of(@NotNull String code, @NotNull String pattern) {
        return new PmMsgError(code, pattern);
    }

    String code();

    String message(Object... objects);

}
