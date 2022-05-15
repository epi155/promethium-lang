package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Demultiplexer (switch) consumer on custom enum.
 * Only first match is executed
 *
 * @param <E> enum type
 * @param <T> consumed type
 */
public interface DemuxEnum<E extends Enum<E>, T> {
    /**
     * Static factory
     *
     * @param s   enum switch value
     * @param v   value to be consumed
     * @param <S> enum type
     * @param <V> value type
     * @return demux instance
     */
    static <S extends Enum<S>, V> @NotNull DemuxEnum<S, V> of(S s, V v) {
        return new PmDemuxEnum<>(s, v);
    }

    /**
     * case enum
     *
     * @param e      enum value to be checked
     * @param action action if check match
     * @return demux instance
     */
    DemuxEnum<E, T> on(@NotNull E e, @NotNull Consumer<T> action);

    /**
     * case enums
     *
     * @param e      enum values to be checked
     * @param action action if check match
     * @return demux instance
     */
    DemuxEnum<E, T> on(@NotNull Set<E> e, @NotNull Consumer<T> action);

    /**
     * case not enum
     *
     * @param e      enum value to be checked
     * @param action action if check does not match
     * @return demux instance
     */
    DemuxEnum<E, T> notOn(@NotNull E e, @NotNull Consumer<T> action);

    /**
     * case not enums
     *
     * @param e      enum values to be checked
     * @param action action if check does not match
     * @return demux instance
     */
    DemuxEnum<E, T> notOn(@NotNull Set<E> e, @NotNull Consumer<T> action);

    /**
     * case else
     *
     * @param action action if no previous check match
     */
    void onElse(@NotNull Consumer<T> action);
}
