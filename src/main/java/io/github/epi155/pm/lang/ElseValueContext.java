package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ElseValueContext<T> {
    @NotNull ChoiceExitContext implies(@NotNull Consumer<? super T> consumer);

    @NotNull ChoiceExitContext perform(@NotNull Function<? super T, ? extends AnyError> fcn);
}
