package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface WhenEmptyContext {
    @NotNull ChoiceEmptyContext implies(@NotNull Runnable action);

    @NotNull ChoiceEmptyContext perform(@NotNull Supplier<? extends AnyError> action);
}
