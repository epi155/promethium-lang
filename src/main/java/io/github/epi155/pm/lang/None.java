package io.github.epi155.pm.lang;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Utility class for carrying many errors
 * <p>
 *     The interface has two main static constructors
 *     <pre>
 *      None.none();                                    // no errors and no warnings
 *      None.fault(CustMsg ce, Object... argv);         // single error message </pre>
 *     If at most an error is returned (and no warnings), the use of {@link Nope} is preferable.
 * <p>
 *     Usually the interface is used through a builder which allows to accumulate many errors (warnings)
 *     <pre>
 *      val bld = None.builder();
 *      bld.fault(CustMsg ce, Object... argv);      // add single error message
 *      bld.alert(CustMsg ce, Object... argv);      // add single warning message
 *      bld.capture(Throwable t);                   // add error from Exception
 *      None none = bld.build(); </pre>
 *     The outcome can be evaluated imperatively
 *     <pre>
 *      if (none.completeWithoutErrors()) {
 *          Collection&lt;Warning&gt; warnings = none.alerts();     // warning collection
 *          // ... action on warnings
 *      } else {
 *          Collection&lt;? extends Signal&gt; errors = none.signals();     // errors/warings collection
 *          // ... action on errors (and warnings)
 *      } </pre>
 *     or functionally
 *     <pre>
 *      none
 *          .onSuccess((Collection&lt;Warning&gt; w) -> { ... })            // ... action on warnings
 *          .onFailure((Collection&lt;? extends Signal&gt; e) -> { ... });  // ... action on errors (and warnings);
 *
 *      R r = none.&lt;R&gt;mapTo(
 *          w -> ...R,          // function from warning to R
 *          e -> ...R);         // function from error/warning to R </pre>
 *     If we know, from the start, that no warnings will be raised, or knowingly want to ignore the warnings,
 *     we can use the simpler form
 *     <pre>
 *      none
 *          .onSuccess(() -> { ... })   // ... action on success (ignoring warnings)
 *          .onFailure(e -> { ... });   // ... action on errors (and warnings);
 *
 *      R r = none.&lt;R&gt;mapTo(
 *          () -> ...R,         // supplier (ignoring warning) to R
 *          e -> ...R);         // function from error/warning to R </pre>
 *
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
     * Static constructor without errors
     * @return  instance of {@link None} without errors
     */
    @NoBuiltInCapture
    static @NotNull None none() {
        return PmNone.none();
    }

    /**
     * Static constructor with error
     *
     * @param ce      custom error
     * @param objects error parameters
     * @return instance of {@link None} with error
     */
    @NoBuiltInCapture
    static @NotNull None fault(@NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Failure fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmNone(Collections.singletonList(fail));
    }

    /**
     * Static constructor with error with properties
     *
     * @param properties error properties
     * @param ce         custom error
     * @param objects    error parameters
     * @return instance of {@link None} with error
     */
    @NoBuiltInCapture
    static @NotNull None fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Failure fail = PmFailure.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmNone(Collections.singletonList(fail));
    }

    /**
     * Static constructor with warning
     *
     * @param ce      custom warning
     * @param objects warning parameters
     * @return instance of {@link None} with warning
     */
    @NoBuiltInCapture
    static @NotNull None alert(@NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Warning warn = PmWarning.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmNone(Collections.singletonList(warn));
    }

    /**
     * Static constructor with warning with properties
     *
     * @param properties warning properties
     * @param ce         custom warning
     * @param objects    warning parameters
     * @return instance of {@link None} with warning
     */
    @NoBuiltInCapture
    static @NotNull None alert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Warning warn = PmWarning.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmNone(Collections.singletonList(warn));
    }

    /**
     * Static constructor
     *
     * @param t exception
     * @return instance of {@link None}
     */
    @NoBuiltInCapture
    static @NotNull None capture(@NotNull Throwable t) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        return new PmNone(Collections.singletonList(PmTrouble.of(t, caller)));
    }

    /**
     * Static constructor
     *
     * @param item instance of {@link ItemStatus}
     * @return instance of {@link None}
     */
    @NoBuiltInCapture
    static @NotNull None of(@NotNull ItemStatus item) {
        if (item instanceof None) return (None) item;
        if (item.completeSuccess()) {
            return PmNone.none();
        } else {
            return new PmNone(item.signals());
        }
    }

    /**
     * Build a Nope from a Nope
     *
     * @param nope {@link Nope} instance
     * @return {@link None} instance
     */
    @NoBuiltInCapture
    static @NotNull None pull(@NotNull Nope nope) {
        return new PmNone(nope.signals());
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
    static @NotNull Collector<ItemStatus, NoneBuilder, None> collect() {
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
    @NotNull Glitches onSuccess(@NotNull Runnable successAction);

    /**
     * Set the action on success
     * <p>
     * In the event of an error, the action is not performed.
     * </p>
     *
     * @param successAction     action to be taken if successful
     * @return Glitches to set the action on failure
     * @see Glitches#onFailure(Consumer)
     */
    @NotNull Glitches onSuccess(@NotNull Consumer<Collection<Warning>> successAction);

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
        @NotNull LoopBuilderConsumer<U> loop = None.builder().iterable(iterable);
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
        @NotNull LoopBuilderConsumer<? extends U> loop = None.builder().iterableOf(iterable);
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
        @NotNull LoopBuilderConsumer<U> loop = None.builder().stream(stream);
        return PmLoopFactory.of(loop);
    }

    /**
     * constructs a result using two alternative methods depending on whether the operation completed successfully or failed
     *
     * @param onSuccess success builder
     * @param onFailure failure builder
     * @param <R>       result type
     * @return result
     */
    <R> R mapTo(Function<Collection<Warning>, R> onSuccess, Function<Collection<? extends Signal>, R> onFailure);

    /**
     * constructs a result using two alternative methods depending on whether the operation completed successfully or failed
     *
     * @param onSuccess success builder
     * @param onFailure failure builder
     * @return result
     * @param <R>       result type
     */
    <R> R mapTo(Supplier<R> onSuccess, Function<Collection<? extends Signal>, R> onFailure);

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
        @NotNull LoopBuilderConsumer<? extends U> loop = None.builder().streamOf(stream);
        return PmLoopFactory.of(loop);
    }

    /**
     * If there are no errors, the action is performed.
     * In any case the initial errors are returned.
     *
     * @param action action executed if there are no errors
     * @return {@link None} instance, with original error, if any
     */
    @NotNull None implies(@NotNull Runnable action);

    @NotNull None peek(@NotNull Runnable successAction, @NotNull Consumer<Collection<? extends Signal>> failureAction);

    @NotNull None peek(@NotNull Consumer<Collection<Warning>> successAction, @NotNull Consumer<Collection<? extends Signal>> failureAction);
}
