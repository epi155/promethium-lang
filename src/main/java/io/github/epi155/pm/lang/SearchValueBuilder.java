package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Partial value builder
 * @param <R>   value type of the final value
 */
public interface SearchValueBuilder<R> {

    /**
     * returns the final value
     * @return  instance of {@link Some} with the final value or the errors collected
     */
    @NotNull Some<R> build();

    /**
     * Partial value builder
     * @param <S>   value type of the search result
     * @param <R>   value type of the final value
     */
    interface Found<S, R> {
        /**
         * Get the value from a fallible function
         * <p>
         *     the function is called only and only if the result of the search is found
         * </p>
         * @param func  fallible function
         * @return      instance of partial builder {@link NotFound}
         */
        @NotNull SearchValueBuilder.NotFound<R> onFound(@NotNull Function<? super S, ? extends AnyValue<R>> func);

        /**
         * Gets the value from a non-fallible function
         * <p>
         *     the function is called only and only if the result of the search is found
         * </p>
         * @param func  non-fallible function
         * @return      instance of partial builder {@link NotFound}
         */
        @NotNull SearchValueBuilder.NotFound<R> onFoundOf(@NotNull Function<? super S, ? extends R> func);

        /**
         * set custom error for final value
         * <p>
         *     the error is set only and only if the result of the search is found
         * </p>
         *
         * @param ce    custom error message
         * @param argv  error parameter
         * @return      instance of partial builder {@link NotFound}
         */
        @NotNull SearchValueBuilder.NotFound<R> onFoundSetError(@NotNull CustMsg ce, Object...argv);
    }

    /**
     * Partial value builder
     * @param <R>   value type of the final value
     */
    interface NotFound<R> {
        /**
         * Get the value from a fallible function
         * <p>
         *     the function is called only and only if the result of the search is not found
         * </p>
         * @param ctor  fallible function
         * @return      instance of partial builder {@link SearchValueBuilder}
         */
        @NotNull SearchValueBuilder<R> onNotFound(@NotNull Supplier<AnyValue<R>> ctor);

        /**
         * Gets the value from a non-fallible function
         * <p>
         *     the function is called only and only if the result of the search is not found
         * </p>
         * @param ctor  non-fallible function
         * @return      instance of partial builder {@link SearchValueBuilder}
         */
        @NotNull SearchValueBuilder<R> onNotFoundOf(@NotNull Supplier<R> ctor);

        /**
         * set custom error for final value
         * <p>
         *     the error is set only and only if the result of the search is not found
         * </p>
         *
         * @param ce    custom error message
         * @param argv  error parameter
         * @return      instance of partial builder {@link SearchValueBuilder}
         */
        @NotNull SearchValueBuilder<R> onNotFoundSetError(@NotNull CustMsg ce, Object... argv);
    }
}
