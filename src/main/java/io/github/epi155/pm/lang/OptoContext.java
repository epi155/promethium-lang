package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface OptoContext<T> {
    static @NotNull <S, R> OptoMapInitialContext<S, R> optoMap(@NotNull S value) {
        return new PmOptoRawMapContext<>(value);
    }

    static @NotNull <S> OptoNixInitialContext<S> opto(@NotNull S value) {
        return new PmOptoRawNixContext<>(value);
    }

    @NotNull <R> OptoMapInitialContext<T, R> optoMap();

    @NotNull OptoNixInitialContext<T> opto();
}
