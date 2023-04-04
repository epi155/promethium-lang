package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility interface for carrying many errors (and warnings) xor a value (and warnings)
 * <p>
 *     The interface has two main static constructors
 *     <pre>
 *      {@link Some#of(Object) Some.of(T value)};                               // final value (and no warnings)
 *      {@link Some#failure(CustMsg, Object...) Some.failure(CustMsg ce, Object... argv)};       // single error message </pre>
 *     If at most an error is returned (and no warnings), the use of {@link Hope} is preferable.
 * <p>
 *     Usually the interface is used through a builder which allows to accumulate many errors (warnings)
 *     <pre>
 *      val bld = Some.&lt;T&gt;{@link Some#builder() builder()};
 *      bld.{@link ErrorBuilder#fault(CustMsg, Object...) fault(CustMsg ce, Object... argv)};      // add single error message
 *      bld.{@link ErrorBuilder#alert(CustMsg, Object...) alert(CustMsg ce, Object... argv)};      // add single warning message
 *      bld.{@link ErrorBuilder#capture(Throwable) capture(Throwable t)};                   // add error from Exception
 *      bld.{@link SomeBuilder#value(Object) value(T value)};                         // set final value
 *      Some&lt;T&gt; some = bld.{@link SomeBuilder#build() build()}; </pre>
 *     The outcome can be evaluated imperatively
 *     <pre>
 *      if (some.{@link ItemStatus#completeWithoutErrors() completeWithoutErrors()}) {
 *          T value = some.{@link Some#value() value()};                           // final value
 *          Collection&lt;Warning&gt; warnings = some.{@link Some#alerts() alerts()};     // warning collection
 *          // ... action on value and warnings
 *      } else {
 *          Collection&lt;? extends Signal&gt; errors = some.{@link ItemStatus#signals() signals()};     // errors/warings collection
 *          // ... action on errors (and warnings)
 *      } </pre>
 *     or functionally
 *     <pre>
 *      some
 *          .{@link Some#onSuccess(BiConsumer) onSuccess((T v, Collection&lt;Warning&gt; w) -> { ... })}         // ... action on value and warnings
 *          .{@link Some#onFailure(Consumer) onFailure((Collection&lt;? extends Signal&gt; e) -> { ... })};    // ... action on errors (and warnings);
 *
 *      R r = some.&lt;R&gt;{@link Some#mapTo(BiFunction, Function) mapTo}(
 *          (v, w) -> ...R,        // function from value and warning to R
 *          e -> ...R);            // function from error/warning to R </pre>
 *     If we know, from the start, that no warnings will be raised, or knowingly want to ignore the warnings,
 *     we can use the simpler form
 *     <pre>
 *      some
 *          .{@link Some#onSuccess(Consumer) onSuccess(v -> { ... })}        // ... action on value (ignoring warnings)
 *          .{@link Some#onFailure(Consumer) onFailure(e -> { ... })};       // ... action on errors (and warnings);
 *
 *      R r = some.&lt;R&gt;{@link Some#mapTo(Function, Function) mapTo}(
 *          v -> ...R,          // function from value (ignoring warnings) to R
 *          e -> ...R);         // function from error/warning to R </pre>
 *
 * @param <T> value type
 */
public interface Some<T> extends ManyErrors, AnyValue<T> {
    /**
     * {@link Some} builder
     *
     * @param <U> data type
     * @return instance of {@link SomeBuilder}
     */
    static <U> @NotNull SomeBuilder<U> builder() {
        return new PmSomeBuilder<>();
    }

    /**
     * static factory with error message
     *
     * @param ce      error message
     * @param argv message parameters
     * @param <U>     payload type
     * @return instance of {@link Some} (error)
     */
    static <U> @NotNull Some<U> failure(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        return new PmSome<>(Collections.singletonList(fail));
    }

    /**
     * static constructor with {@link SingleError} ({@link Hope} or {@link Nope} completeWithErrors)
     * @param se    {@link SingleError} instance
     * @return      {@link Some} instance
     * @param <U>   {@link Some} data type (dummy)
     */
    static <U> @NotNull Some<U> failure(SingleError se) {
        return new PmSome<>(Collections.singletonList(se.fault()));
    }

    /**
     * static constructor with {@link ManyErrors} ({@link Some} or {@link None} completeWithErrors)
     * @param me    {@link ManyErrors} instance
     * @return      {@link Some} instance
     * @param <U>   {@link Some} data type (dummy)
     */
    static <U> @NotNull Some<U> failure(ManyErrors me) {
        val signals = me.signals();
        if (signals.isEmpty())
            throw new NoSuchElementException();
        return new PmSome<>(signals);
    }

    /**
     * static factory with {@link Throwable}
     *
     * @param t   throwable instance
     * @param <U> payload type
     * @return instance of {@link Some} (error)
     */
    static <U> @NotNull Some<U> capture(@NotNull Throwable t) {
        return new PmSome<>(Collections.singletonList(PmFailure.of(t)));
    }

    /**
     * static factory with payload value
     *
     * @param value payload value
     * @param <U>   payload type
     * @return instance of {@link Some} (success)
     */
    static <U> @NotNull Some<U> of(@NotNull U value) {
        if (value == null) {
            StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
            val fail = PmFailure.ofException(stPtr[PmAnyBuilder.J_LOCATE], new IllegalArgumentException("the argument cannot be null"));
            return new PmSome<>(Collections.singletonList(fail));
        } else if (value instanceof Signal) {
            StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
            val fail = PmFailure.ofException(stPtr[PmAnyBuilder.J_LOCATE], new IllegalArgumentException("the argument cannot be an instanceof Signal"));
            val bld = Some.<U>builder();
            bld.add((Signal)value);
            bld.add(fail);
            return bld.build();
        }
        return new PmSome<>(value);
    }

    /**
     * Static constructor from {@link Hope}
     * @param u     {@link Hope} instance
     * @return      {@link Some} instance
     * @param <U>   {@link Some}/{@link Hope} data type
     */
    static <U> @NotNull Some<U> pull(@NotNull Hope<U> u) {
        return PmSome.of(u);
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
    @NotNull Glitches onSuccess(@NotNull Consumer<? super T> successAction);

    /**
     * Set the action on success (ignoring warnings)
     * <p>
     * In the event of an error, the action is not performed.
     * </p>
     *
     * @param successAction action to be taken if successful
     * @return              Glitches to set the action on failure
     * @see Glitches#onFailure(Consumer)
     */
    @NotNull Glitches onSuccess(@NotNull BiConsumer<? super T, Collection<Warning>> successAction);

    /**
     * Collapse to {@link None} instance, keeping only errors' data, and lost value
     *
     * @return {@link None} instance
     */
    @NotNull None asNone();

    /**
     * Compose operator
     * <p>Some &bull; AnyValue<sup>+</sup> &rarr; Some</p>
     *
     * <p>
     *     The method allows to compose two Somes (or a Some and an Hope).
     *     Using an imperative outcome evaluation we would have
     * <pre>
     *      Some&lt;A&gt; sa = computeA();
     *      Some&lt;B&gt; sb;
     *      if (sa.completeWithoutErrors()) {
     *          sb = a2sb(sa.value());      // Some&lt;B&gt; a2sb(A value)
     *      } else {
     *          sb = Some.failure(sa);
     *      } </pre>
     *      The method simplifies it to
     * <pre>
     *      Some&lt;B&gt; kb = computeA().map(this::a2sb); </pre>
     * in addition, the method also propagates any warnings
     *
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
     * @return original {@link Some} instance, with value/errors
     */
    @NotNull Some<T> peek(@NotNull Consumer<? super T> action);

    /**
     * constructs a result using two alternative methods depending on whether the operation completed successfully or failed
     *
     * @param onSuccess success builder
     * @param onFailure failure builder
     * @param <R>       result type
     * @return result
     */
    <R> R mapTo(BiFunction<T, Collection<Warning>, R> onSuccess, Function<Collection<? extends Signal>, R> onFailure);

    /**
     * constructs a result using two alternative methods depending on whether the operation completed successfully or failed,
     * warnings on success are ignored
     *
     * @param onSuccess success builder
     * @param onFailure failure builder
     * @return result
     * @param <R>       result type
     */
    <R> R mapTo(Function<T, R> onSuccess, Function<Collection<? extends Signal>, R> onFailure);

    /**
     * All warnings
     *
     * @return  collection of warning message
     */
    Collection<Warning> alerts();
}
