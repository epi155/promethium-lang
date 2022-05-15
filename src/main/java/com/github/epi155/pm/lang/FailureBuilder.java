package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Builder of {@link Failure}
 */
public interface FailureBuilder {
    /**
     * message setter
     *
     * @param message error message
     * @return {@link FailureBuilder} instance
     */
    @NotNull FailureBuilder message(@NotNull String message);

    /**
     * code setter
     *
     * @param code error code
     * @return {@link FailureBuilder} instance
     */
    @NotNull FailureBuilder code(@NotNull String code);

    /**
     * status setter
     *
     * @param status error status
     * @return {@link FailureBuilder} instance
     */
    @NotNull FailureBuilder status(int status);

    /**
     * {@link Failure} final builder
     *
     * @return {@link Failure} instance
     */
    @NotNull Failure build();
}
