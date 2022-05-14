package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface DemuxClass {
    static <V> @NotNull DemuxClass of(@NotNull V v) {
        return new PmDemuxClass(v);
    }

    <W> DemuxClass when(@NotNull Class<W> cls, Consumer<W> action);

    <W> void whenOther(Consumer<W> action);
}
