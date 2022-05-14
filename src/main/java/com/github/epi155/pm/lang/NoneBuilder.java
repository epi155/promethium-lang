package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface NoneBuilder extends ErrorBuilder {
    @NotNull None build();

    default @NotNull NoneBuilder join(@NotNull AnyOne any) {
        add(any);
        return this;
    }

    default @NotNull NoneBuilder join(@NotNull CheckedRunnable runnable) {
        add(runnable);
        return this;
    }

}
