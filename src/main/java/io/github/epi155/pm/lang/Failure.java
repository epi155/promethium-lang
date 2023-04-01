package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Error message
 */
public interface Failure extends Signal {
    /**
     * Java exception code value.
     */
    String JAVA_EXCEPTION_CODE = "999J";

    /**
     * static {@link FailureBuilder}
     *
     * @return {@link FailureBuilder} instance
     */
    static @NotNull FailureBuilder builder() {
        return new PmFailureBuilder();
    }

    /**
     * Static error message constructor
     *
     * @param ce      custom error
     * @param objects error parameters
     * @return {@link Failure} instance
     * @deprecated stack info missing
     */
    @Deprecated
    static @NotNull Failure of(@NotNull MsgError ce, Object... objects) {
        String code = ce.code();
        String message = ce.message(objects);
        return new PmFailure(code, ce, message);
    }

    /**
     * Static error message constructor
     *
     * @param ex exception
     * @return {@link Failure} instance
     */
    static @NotNull Failure of(@NotNull Throwable ex) {
        return PmFailure.of(ex);
    }

}
