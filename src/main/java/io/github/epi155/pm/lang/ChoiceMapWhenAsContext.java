package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ChoiceMapWhenAsContext<U, T, R> {
    @NotNull ChoiceMapContext<T,R> map(@NotNull Function<? super U, ? extends AnyValue<R>> fcn);
}
