package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface ChoiceValue<T> {
    @NotNull ChoiceValueContext<T> choice();
}
