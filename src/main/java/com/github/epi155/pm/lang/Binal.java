package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

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

    BinalFalse isTrue(Runnable action);

    BinalTrue isFalse(Runnable action);
}
