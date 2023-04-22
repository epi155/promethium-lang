package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface OptoMapInitialContext<T, R> {
    @NotNull OptoMapWhenContext<T, R> when(@NotNull Predicate<T> predicate);

    @NotNull OptoMapWhenContext<T, R> when(boolean test);

    @NotNull OptoMapWhenContext<T, R> whenEquals(@NotNull T value);

    @NotNull <U> OptoMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls);
}
