package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Error builder extension for {@link None}
 */
public interface NoneBuilder extends ErrorBuilder {
    /**
     * Final builder
     *
     * @return {@link None} instance
     */
    @NotNull None build();

    /**
     * Add error(s)
     *
     * @param any object with error(s) payload
     * @return {@link NoneBuilder} instance
     */
    default @NotNull NoneBuilder join(@NotNull ItemStatus any) {
        add(any);
        return this;
    }

    /**
     * Add a collection of {@link Signal}
     * @param signals   collection of {@link Signal}
     * @return          builder itself
     */
    default @NotNull NoneBuilder join(@NotNull Collection<? extends Signal> signals) {
        add(signals);
        return this;
    }

    /**
     * Add a single {@link Signal}
     * @param signal    {@link Signal}
     * @return          builder itself
     */
    default @NotNull NoneBuilder join(@NotNull Signal signal) {
        add(signal);
        return this;
    }

    /**
     * null
     * Add error when runnable throw an {@link FailureException}
     *
     * @param runnable action to be executed
     * @return {@link NoneBuilder} instance
     */
    default @NotNull NoneBuilder join(@NotNull CheckedRunnable runnable) {
        add(runnable);
        return this;
    }

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param iterable fallible values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> iterable(@NotNull Iterable<? extends AnyValue<U>> iterable);

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param iterable values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> iterableOf(@NotNull Iterable<? extends U> iterable);

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param stream fallible values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> stream(@NotNull Stream<? extends AnyValue<U>> stream);

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param stream values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> streamOf(@NotNull Stream<? extends U> stream);
}
