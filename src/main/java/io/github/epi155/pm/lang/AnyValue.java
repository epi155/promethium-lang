package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Extension for class with payload
 *
 * @param <T> payload type
 */
public interface AnyValue<T> extends AnyError, ChooseContext<T> {
    /**
     * Returns the value.
     * <p>
     * in the presence of errors and the value is not present,
     * the {@link java.util.NoSuchElementException} error is thrown
     * </p>
     *
     * @return value
     */
    @NotNull T value();

    /**
     * It generates the fallible values to loop on
     *
     * @param src function that generates the fallible values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> iterable(@NotNull Function<? super T, Iterable<? extends AnyValue<U>>> src) {
        return completeWithoutErrors() ? None.iterable(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * It generates the values to loop on
     *
     * @param src function that generates the values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> iterableOf(@NotNull Function<? super T, Iterable<? extends U>> src) {
        return completeWithoutErrors() ? None.iterableOf(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * It generates the values to loop on
     *
     * @param src function that generates the values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> streamOf(@NotNull Function<? super T, Stream<? extends U>> src) {
        return completeWithoutErrors() ? None.streamOf(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * It generates the fallible values to loop on
     *
     * @param src function that generates the fallible values to loop on
     * @param <U> type of value generated
     * @return {@link LoopConsumer} instance
     */
    default <U> LoopConsumer<U> stream(@NotNull Function<? super T, Stream<? extends AnyValue<U>>> src) {
        return completeWithoutErrors() ? None.stream(src.apply(value())) : new PmDummyLoopConsumer<>(this);
    }

    /**
     * If there are no errors and the value is present, apply the function to the value,
     * if the function ends with errors, these errors are returned.
     * In the presence of errors, the function is not called, and the initial errors are returned.
     *
     * @param fcn transform value to {@link ItemStatus}
     * @return {@link None} instance, if this has an error,
     * the transformation is not called and the result has the original error
     */
    @NotNull None ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn);

    /**
     * Compose operator
     * <p>Some &bull; AnyValue<sup>+</sup> &rarr; Some</p>
     *
     * <p>
     * The method allows to compose two Somes (or a Some and an Hope).
     * Using an imperative outcome evaluation we would have
     * <pre>
     *      Some&lt;A&gt; sa = computeA();
     *      Some&lt;B&gt; sb;
     *      if (sa.completeWithoutErrors()) {
     *          sb = a2sb(sa.value());      // Some&lt;B&gt; a2sb(A value)
     *      } else {
     *          sb = Some.failure(sa);
     *      } </pre>
     * The method simplifies it to
     * <pre>
     *      Some&lt;B&gt; kb = computeA().map(this::a2sb); </pre>
     * in addition, the method also propagates any warnings
     *
     * @param fcn transform value to result {@link Some}
     * @param <R> result type
     * @return result {@link Some} instance, if this has errors, the transformation is not called and the result has the original error
     */
    @NotNull <R> Some<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn);

    /**
     * map value
     * <p>Some &bull; <i>value</i> &rarr; Some</p>
     *
     * @param fcn mapping function
     * @param <R> result type
     * @return {@link Some} instance with new value,
     * if this has errors, the transformation is not called and the result has the original error;
     * RuntimeException are caught as new error
     */
    @NotNull <R> Some<R> mapOf(@NotNull Function<? super T, ? extends R> fcn);

    /**
     * If there are no errors and the value is present, the action on the value is performed.
     * In any case the initial class is returned.
     *
     * @param action action on value, executed if there are no errors
     * @return original {@link AnyValue} instance, with value/errors
     */
    @NotNull AnyValue<T> peek(@NotNull Consumer<? super T> action);

}
