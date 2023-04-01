package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Partial search result builder
 * @param <R>   value type of the new search result
 */
public interface SearchResultBuilder<R> {
    /**
     * returns the result of the new search
     * @return  instance of {@link SearchResult} with the result of the new search
     */
    @NotNull SearchResult<R> build();

    /**
     * Partial search result builder
     * @param <S>   value type of the old search result
     * @param <R>   value type of the new search result
     */
    interface Found<S, R> {
        /**
         * performs a new search using the value from the first search
         * <p>
         *     the function is called only and only if the result of the search is found
         * </p>
         * @param func  function that performs the new search
         * @return      instance of partial builder {@link NotFound}
         */
        @NotNull SearchResultBuilder.NotFound<R> onFound(@NotNull Function<? super S, SearchResult<R>> func);

        /**
         * sets the result of the new search with the custom error
         * <p>
         *     the error is set only and only if the result of the search is found
         *
         * @param ce    custom error message
         * @param argv  error parameter
         * @return      instance of partial builder {@link NotFound}
         */
        @NotNull SearchResultBuilder.NotFound<R> onFoundSetError(@NotNull Nuntium ce, Object...argv);
    }

    /**
     * Partial search result builder
     * @param <R>   value type of the new search result
     */
    interface NotFound<R> {
        /**
         * performs a new search
         * <p>
         *     the function is called only and only if the result of the search is not found
         * </p>
         * @param ctor  function that performs the new search
         * @return      instance of partial builder {@link SearchResultBuilder}
         */
        @NotNull SearchResultBuilder<R> onNotFound(@NotNull Supplier<SearchResult<R>> ctor);

        /**
         * sets the result of the new search with the custom error
         * <p>
         *     the error is set only and only if the result of the search is found
         * </p>
         *
         * @param ce    custom error message
         * @param argv  error parameter
         * @return      instance of partial builder {@link SearchResultBuilder}
         */
        @NotNull SearchResultBuilder<R> onNotFoundSetError(@NotNull Nuntium ce, Object... argv);
    }
}
