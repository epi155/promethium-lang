package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

class PmSomeBuilder<T> extends PmAnyBuilder implements SomeBuilder<T> {
    private T value;

    public @NotNull SomeBuilder<T> value(@NotNull T value) {
        this.value = value;
        return this;
    }

    public @NotNull Some<T> build() {
        return isSuccess() ? new PmSome<>(value) : new PmSome<>(super.errors());
    }
}
