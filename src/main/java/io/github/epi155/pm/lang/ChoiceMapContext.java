package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface ChoiceMapContext<T, R> {
    @NotNull WhenMapContext<T,R> when(@NotNull Predicate<T> predicate);

    @NotNull WhenMapContext<T,R> when(boolean test);

    @NotNull ElseMapContext<T,R> otherwise();
}
