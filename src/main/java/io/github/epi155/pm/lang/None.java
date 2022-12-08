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
     *              .ergo(n1 -> fun2(n,n1)
     *                  .ergo(n2 -> fun3(n,n1,n2)
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
     * If there are no errors, the supplier is called,
     * if this ends with errors, these errors are returned.
     * In the presence of errors, the supplier is not called, and the initial errors are returned
     *
     * @param fcn producer {@link AnyError}
     * @return {@link None} instance,
     */
    @NotNull None ergo(@NotNull Supplier<? extends AnyError> fcn);

    /**
     * If there are no errors, the action is performed.
     * In any case the initial errors are returned.
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
     * <pre>
     *     Iterable&lt;AnyValue&lt;U&gt;&gt; list = ..
     *     None none = None.iterable(list).forEach(u -&gt; ..);
     * </pre>
     * @param iterable fallible values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> iterable(@NotNull Iterable<? extends AnyValue<U>> iterable) {
        val loop = None.builder().iterable(iterable);
        return PmLoopFactory.of(loop);
    }

    /**
     * It generates a {@link LoopConsumer} instance to loop on
     * <pre>
     *     Iterable&lt;U&gt; list = ..
     *     None none = None.iterableOf(list).forEach(u -&gt; ..);
     * </pre>
     * @param iterable values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> iterableOf(@NotNull Iterable<? extends U> iterable) {
        val loop = None.builder().iterableOf(iterable);
        return PmLoopFactory.of(loop);
    }

    /**
     * It generates a {@link LoopConsumer} instance to loop on
     * <pre>
     *     Stream&lt;AnyValue&lt;U&gt;&gt; stream = ..
     *     None none = None.stream(stream).forEach(u -&gt; ..);
     * </pre>
     * @param stream fallible values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> stream(@NotNull Stream<? extends AnyValue<U>> stream) {
        val loop = None.builder().stream(stream);
        return PmLoopFactory.of(loop);
    }

    /**
     * It generates a {@link LoopConsumer} instance to loop on
     * <pre>
     *     Stream&lt;U&gt; stream = ..
     *     None none = None.streamOf(stream).forEach(u -&gt; ..);
     * </pre>
     * @param stream values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopConsumer} instance
     */
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> streamOf(@NotNull Stream<? extends U> stream) {
        val loop = None.builder().streamOf(stream);
        return PmLoopFactory.of(loop);
    }
}
