package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ChoiceValueWhenAsContext<U, T> {
    @NotNull ChoiceValueContext<T> implies(@NotNull Consumer<? super U> action);
    @NotNull ChoiceValueContext<T> perform(@NotNull Function<? super U, ? extends AnyError> fcn);
}
