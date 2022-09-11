package io.github.epi155.pm.smart;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * interface to safely manage nullable objects
 *
 * @param <A> object type
 */
public interface Voidable<A> extends OtherAction {
    /**
     * static constructor of Ensure, for the management of nullable fields
     *
     * @param <T>   value type
     * @param value nullable value
     * @return instance of {@link Voidable}
     */
    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull Voidable<T> voidable(@Nullable T value) {
        return new PmVoidable<>(value);
    }

    /**
     * value contained in the ensure
     *
     * @return contained value
     */
    @Nullable A value();

    /**
     * Alternative value if current is null, like kotlin elvis operator <code>?:</code>
     *
     * @param other value to use i current value is null
     * @return value xor other
     */
    @NotNull A otherwise(@NotNull A other);

    /**
     * action on the field if it is not null
     *
     * @param action action to be performed using the value of the field
     * @return instance of {@link OtherAction} to indicate the action to take if the field is null
     */
    OtherAction accept(Consumer<A> action);

    /**
     * Call function if value is not null, like kotlin safe call <code>?.</code>
     *
     * @param function function to apply if value is not null
     * @param <B>      final type
     * @return result of function xor null
     */
    <B> Voidable<B> apply(Function<A, B> function);

    /**
     * if the predicate is not satisfied it returns an instance of null else it returns <b>this</b>
     *
     * @param predicate predicate to apply
     * @return {@link Voidable} instance
     */
    Voidable<A> filter(Predicate<A> predicate);

    /**
     * join nullable object
     * <pre>
     *     voidable(@Nullable a)
     *       .join(ai -> voidable(@Nullable b)
     *          .apply(bi -> <i>action(@NotNull ai, @NotNull bi)</i> ))
     * </pre>
     *
     * @param function composition of next nullable object
     * @param <B>      next object type
     * @return result of function xor null
     */
    <B> Voidable<B> join(Function<A, Voidable<B>> function);

}
