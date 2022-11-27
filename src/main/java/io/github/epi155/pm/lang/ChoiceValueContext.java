package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface ChoiceValueContext<T> extends ChoiceExitContext {
    @NotNull WhenValueContext<T> when(@NotNull Predicate<T> predicate);

    @NotNull WhenValueContext<T> when(boolean test);

    @NotNull ElseValueContext<T> otherwise();
}
