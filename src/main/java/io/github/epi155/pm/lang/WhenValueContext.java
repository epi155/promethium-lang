package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface WhenValueContext<T> {
    @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super T> consumer);

    @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super T, ? extends AnyError> fcn);
}
