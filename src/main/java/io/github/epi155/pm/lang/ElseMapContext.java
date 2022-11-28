package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface ElseMapContext<T, R> {
    @NotNull ExitMapContext<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn);
}
