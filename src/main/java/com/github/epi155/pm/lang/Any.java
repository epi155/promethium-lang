package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Any extends AnyOne {

    /**
     * amount of errors
     *
     * @return errors counted
     */
    int count();

    /**
     * summary of any errors
     *
     * @return summary of errors (if any)
     */
    @NotNull Optional<String> summary();
}
