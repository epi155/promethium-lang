package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Error message
 */
public interface Warning extends Signal {
    /**
     * Java exception code value.
     */
    String JAVA_EXCEPTION_CODE = "999J";

    /**
     * static {@link WarningBuilder}
     *
     * @return {@link WarningBuilder} instance
     */
    static @NotNull WarningBuilder builder() {
        return new PmWarningBuilder();
    }

    /**
     * Static error message constructor
     *
     * @param ce      custom error
     * @param objects error parameters
     * @return {@link Warning} instance
     * @deprecated stack info missing
     */
    @Deprecated
    static @NotNull Warning of(@NotNull MsgError ce, Object... objects) {
        String code = ce.code();
        String message = ce.message(objects);
        return new PmWarning(code, ce, message);
    }

}
