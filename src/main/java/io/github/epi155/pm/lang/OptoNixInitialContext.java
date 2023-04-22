package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface OptoNixInitialContext<T> {
    @NotNull OptoNixWhenContext<T> when(@NotNull Predicate<T> predicate);

    @NotNull OptoNixWhenContext<T> when(boolean test);

    @NotNull OptoNixWhenContext<T> whenEquals(@NotNull T value);

    @NotNull <U> OptoNixWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls);
}
