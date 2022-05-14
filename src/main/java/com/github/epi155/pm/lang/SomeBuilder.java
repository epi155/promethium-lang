package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface SomeBuilder<T> extends ErrorBuilder {
    @NotNull SomeBuilder<T> value(@NotNull T value);

    @NotNull Some<T> build();

    default @NotNull SomeBuilder<T> join(@NotNull AnyOne any) {
        add(any);
        return this;
    }

    default @NotNull SomeBuilder<T> join(@NotNull CheckedRunnable runnable) {
        add(runnable);
        return this;
    }

}
