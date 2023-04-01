package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Builder of {@link Failure}
 */
public interface WarningBuilder {
    /**
     * message setter
     *
     * @param message error message
     * @return {@link WarningBuilder} instance
     */
    @NotNull WarningBuilder message(@NotNull String message);

    /**
     * code setter
     *
     * @param code error code
     * @return {@link WarningBuilder} instance
     */
    @NotNull WarningBuilder code(@NotNull String code);

    /**
     * status setter
     *
     * @param status error status
     * @return {@link WarningBuilder} instance
     */
    @NotNull WarningBuilder status(int status);

    /**
     * {@link Warning} final builder
     *
     * @return {@link Warning} instance
     */
    @NotNull Warning build();
}
