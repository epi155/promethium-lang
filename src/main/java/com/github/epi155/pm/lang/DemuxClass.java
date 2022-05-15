package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Demultiplexer (switch) consumer on class instanceof
 * Only first match is executed
 */
public interface DemuxClass {
    /**
     * Static factory
     *
     * @param v   value to be consumed
     * @param <V> value typr
     * @return demux instance
     */
    static <V> @NotNull DemuxClass of(@NotNull V v) {
        return new PmDemuxClass(v);
    }

    /**
     * case instanceof
     *
     * @param cls    class instanceof
     * @param action action if class match
     * @param <W>    class type
     * @return demux instance
     */
    <W> DemuxClass when(@NotNull Class<W> cls, @NotNull Consumer<W> action);

    /**
     * @param action action if no when has matched
     * @param <W>    consumer action type
     * @deprecated no check demux-type consumer-type
     */
    @Deprecated
    <W> void whenOther(@NotNull Consumer<W> action);
}
