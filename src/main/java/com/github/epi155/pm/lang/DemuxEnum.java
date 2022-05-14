package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;

public interface DemuxEnum<E extends Enum<E>, T> {
    static <S extends Enum<S>, V> @NotNull DemuxEnum<S, V> of(S s, V v) {
        return new PmDemuxEnum<>(s, v);
    }

    DemuxEnum<E, T> on(E e, Consumer<T> action);

    DemuxEnum<E, T> on(@NotNull Set<E> e, Consumer<T> action);

    DemuxEnum<E, T> notOn(E e, Consumer<T> action);

    DemuxEnum<E, T> notOn(@NotNull Set<E> e, Consumer<T> action);

    <W> void onElse(Consumer<W> action);
}
