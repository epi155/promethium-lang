package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Utility class for carrying many errors
 */
public interface None extends ManyErrors, OnlyError {
    /**
     * static {@link NoneBuilder}
     *
     * @return {@link NoneBuilder} instance
     */
    static @NotNull NoneBuilder builder() {
        return new PmNoneBuilder();
    }

    /**
     * Static constructor
     *
     * @param ce      custom error
     * @param objects error parameters
     * @return instance of {@link None}
     */
    static @NotNull None failure(@NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmNone(Collections.singletonList(fail));
    }

    /**
     * Static constructor
     *
     * @param t exception
     * @return instance of {@link None}
     */
    static @NotNull None capture(@NotNull Throwable t) {
        return new PmNone(Collections.singletonList(PmFailure.of(t)));
    }

    /**
     * Static constructor
     *
     * @param fault error
     * @return instance of {@link None}
     */
    static @NotNull None of(@NotNull Failure fault) {
        return new PmNone(Collections.singletonList(fault));
    }

    /**
     * Static constructor
     *
     * @param item instance of {@link AnyValue}
     * @return instance of {@link None}
     */
    static @NotNull None of(@NotNull AnyItem item) {
        if (item instanceof None) return (None) item;
        return item.isSuccess() ? new PmNone() : new PmNone(item.errors());
    }

    /**
     * Error collector.
     *
     * <p>
     * usage:
     * <br/>
     * <pre>
     *     None none = list.stream()
     *      .map(n -> fun1(n)
     *          .map(n1 -> fun2(n,n1))
     *          .map(n2 -> fun3(n,n2))
     *        ).collect(None.collect());
     * </pre>
     * or
     * <pre>
     *      None none = list.stream()
     *          .map(n -> fun1(n)
     *              .and(n1 -> fun2(n,n1)
     *                  .and(n2 -> fun3(n,n1,n2)
     *              )
     *          ).collect(None.collect());
     * </pre>
     *
     * @return collector.
     */
    static @NotNull Collector<AnyItem, NoneBuilder, None> collect() {
        return new PmCollector();
    }

    /**
     * Set the action on success
     * <p>
     * In the event of an error, the action is not performed.
     * </p>
     *
     * @param successAction action to be taken if successful
     * @return Glitches to set the action on failure
     * @see Glitches#onFailure(Consumer)
     */
    @NotNull Glitches onSuccess(Runnable successAction);

    /**
     * Logical short-circuit and operator.
     * <p>None &and; AnyValue<sup>+</sup> &rarr; None</p>
     *
     * <p>
     * If this has errors, the producer is not called
     * and the result has the original error, else the error
     * of the producer, is any.
     * </p>
     *
     * @param fcn producer {@link AnyError}
     * @return {@link None} instance,
     */
    @NotNull None and(@NotNull Supplier<? extends AnyError> fcn);

    /**
     * Logical implies operator
     *
     * @param action action executed if there are no errors
     * @return {@link None} instance, with original error, if any
     */
    @NotNull None implies(@NotNull Runnable action);

    /**
     * constructs a result using two alternative methods depending on whether the operation completed successfully or failed
     *
     * @param onSuccess success builder
     * @param onFailure failure builder
     * @param <R>       result type
     * @return result
     */
    <R> R mapTo(Supplier<R> onSuccess, Function<Collection<Failure>, R> onFailure);

    /**
     * It generates a {@link LoopConsumer} instance to loop on
     *
     * @param iterable fallible values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> iterable(@NotNull Iterable<? extends AnyValue<U>> iterable) {
        val loop = None.builder().iterable(iterable);
        return LoopConsumer.of(loop);
    }

    /**
     * It generates a {@link LoopConsumer} instance to loop on
     *
     * @param iterable values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> iterableOf(@NotNull Iterable<? extends U> iterable) {
        val loop = None.builder().iterableOf(iterable);
        return LoopConsumer.of(loop);
    }

    /**
     * It generates a {@link LoopConsumer} instance to loop on
     *
     * @param stream fallible values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> stream(@NotNull Stream<? extends AnyValue<U>> stream) {
        val loop = None.builder().stream(stream);
        return LoopConsumer.of(loop);
    }

    /**
     * It generates a {@link LoopConsumer} instance to loop on
     *
     * @param stream values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> streamOf(@NotNull Stream<? extends U> stream) {
        val loop = None.builder().streamOf(stream);
        return LoopConsumer.of(loop);
    }
}
