package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * Boolean functional style
 */
public interface Binal {
    /**
     * Static factory
     *
     * @param test value
     * @return instance
     */
    static @NotNull Binal of(boolean test) {
        return new PmBinal(test);
    }

    /**
     * define action when true
     *
     * @param action consumer
     * @return action on else
     */
    BinalFalse isTrue(Runnable action);

    /**
     * define action when false
     *
     * @param action consumer
     * @return action on else
     */
    BinalTrue isFalse(Runnable action);
}
