package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@Deprecated
public interface ElseEmptyContext {
    @NotNull ChoiceExitContext implies(@NotNull Runnable action);

    @NotNull ChoiceExitContext perform(@NotNull Supplier<? extends AnyError> action);
}
