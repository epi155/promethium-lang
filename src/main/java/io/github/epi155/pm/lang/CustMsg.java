package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Custom message error or warning
 */
public interface CustMsg {
    /**
     * Simple static constructor
     *
     * @param code    error(warning) code
     * @param pattern error(waring) format message
     * @return instance of {@link CustMsg}
     */
    static @NotNull CustMsg of(@NotNull String code, @NotNull String pattern) {
        return new PmCustMsg(code, 500, pattern);
    }

    /**
     * Complete static constructor
     *
     * @param code      message code
     * @param status    message status code
     * @param pattern   message format
     * @return          instance of {@link CustMsg}
     */
    static @NotNull CustMsg of(@NotNull String code, int status, @NotNull String pattern) {
        return new PmCustMsg(code, status, pattern);
    }
    /**
     * message code
     *
     * @return message code
     */
    String code();

    /**
     * message builder
     *
     * @param objects message parameters
     * @return final message
     */
    String message(Object[] objects);

    /**
     * status code
     *
     * @return  status code
     */
    int statusCode();
}
