package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

class PmSomeBuilder<T> extends PmAnyBuilder implements SomeBuilder<T> {
    private T value;

    public @NotNull SomeBuilder<T> value(@NotNull T value) {
        this.value = value;
        return this;
    }

    public @NotNull Some<T> build() {
        if (completeSuccess()) {
            return new PmSome<>(value);
        } else if (completeWithErrors()) {
            return new PmSome<>(signals());
        } else /*completeWithWarnings()*/ {
            return new PmSome<>(value, signals());
        }
    }
}
