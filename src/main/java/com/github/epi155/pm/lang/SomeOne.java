package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface SomeOne<T> extends AnyOne {
    /**
     * Returns the value
     *
     * @return value
     */
    @NotNull T value();

}
