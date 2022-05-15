package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Custom message error
 */
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

    /**
     * error code
     *
     * @return error code
     */
    String code();

    /**
     * message builder
     *
     * @param objects error parameters
     * @return final error message
     */
    String message(Object... objects);

}
