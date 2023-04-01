package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Custom message error
 */
public interface MsgError {
    /**
     * Simple static constructor
     *
     * @param code    error code
     * @param pattern error format message
     * @return instance of {@link MsgError}
     */
    static @NotNull MsgError of(@NotNull String code, @NotNull String pattern) {
        return new PmMsgError(code, 500, pattern);
    }

    /**
     * Complete static constructor
     *
     * @param code      error code
     * @param status    status code
     * @param pattern   error format message
     * @return          instance of {@link MsgError}
     */
    static @NotNull MsgError of(@NotNull String code, int status, @NotNull String pattern) {
        return new PmMsgError(code, status, pattern);
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
    String message(Object[] objects);

    /**
     * status code
     *
     * @return  status code
     */
    int statusCode();
}
