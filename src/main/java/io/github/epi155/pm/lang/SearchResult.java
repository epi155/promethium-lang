package io.github.epi155.pm.lang;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Interface to manage the result of a search
 * @param <S>   value type of the object being searched for
 */
public interface SearchResult<S> extends SearchResult2 {
    /**
     * static constructor indicating that the searched object has been found
     * @param value     searched object value
     * @return      instance of {@link SearchResult}
     * @param <T>   value type of the object being searched for
     */
    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull SearchResult<T> of(@NotNull T value) {
        return new PmSearchResult<>(value, null);
    }

    /**
     * Static constructor indicating that nothing was found
     * @return      instance of {@link SearchResult}
     * @param <T>   value type of the object being searched for
     */
    @Contract(value = " -> new", pure = true)
    static <T> @NotNull SearchResult<T> empty() {
        return new PmSearchResult<>(null, null);
    }

    /**
     * static constructor indicating that a custom error occurred
     * @param ce    custom error message template
     * @param argv  custom errore message parameters
     * @return      instance of {@link SearchResult}
     * @param <T>   value type of the object being searched for
     */
    static <T> @NotNull SearchResult<T> failure(@NotNull Nuntium ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return new PmSearchResult<>(null, PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv));
    }

    /**
     * Static constructor to indicate that the search ended with an exception
     * @param t     exception that occurred
     * @return      instance of {@link SearchResult}
     * @param <T>   value type of the object being searched for
     */
    static <T> @NotNull SearchResult<T> capture(@NotNull Throwable t) {
        return new PmSearchResult<>(null, PmFailure.of(t));
    }

    /**
     * action to perform when the object is found, using its value
     * @param action    action to perform
     * @return          instance of {@link SearchResult2} to handle actions based on possible other search outcomes
     */
    @NotNull SearchResult2 onFound(@NotNull Consumer<? super S> action);

    /**
     * builder to obtain a value based on the search result
     * <p>
     *     The construction of the new value takes place in two steps:
     *     it is necessary to indicate how to construct the value if the first search ended by finding the object
     *     and not finding the object
     * </p>
     * <pre>
     *      Some&lt;R&gt; value = result
     *          .&lt;R&gt;valueBuilder()
     *          .onFound(...)
     *          .onNotFound(...)
     *          .build();
     * </pre>
     * <p>
     *     If the first search ended in error, the error is reported in the value.
     *     the <i>onFound</i> and <i>onNotFound</i> actions are not performed
     * </p>
     * @return      instance of partial builder {@link SearchValueBuilder.Found}
     * @param <R>   value type of the object to get
     */
    @NotNull <R> SearchValueBuilder.Found<S, R> valueBuilder();

    /**
     * Builder to get a new search result based on the current search result
     *
     * <p>
     *     The construction of the new result takes place in two steps:
     *     it is necessary to indicate how to construct the result if the first search ended by finding the object
     *     and not finding the object
     * </p>
     * <pre>
     *      SearchResult&lt;R&gt; newResult = oldResult
     *          .&lt;R&gt;resultBuilder()
     *          .onFound(...)
     *          .onNotFound(...)
     *          .build();
     * </pre>
     * <p>
     *     If the first search ended in error, the error is reported in the outcome of the new search.
     *     the <i>onFound</i> and <i>onNotFound</i> actions are not performed
     * </p>
     * @return      instance of partial builder {@link SearchResultBuilder.Found}
     * @param <R>   value type of the new search result
     */
    @NotNull <R> SearchResultBuilder.Found<S, R> resultBuilder();

    /**
     * indicates if an error has occurred
     * @return  true when an error has occurred
     */
    boolean isFailure();

    /**
     * indicates if the object was found
     * @return  true when object was found
     */
    boolean isFound();

    /**
     * indicates if the object was not found
     * @return  true when object was found (and no errors occurred)
     */
    boolean isNotFound();

    /**
     * value of the found object
     * <p>
     *     if the object was not found or an error occurred, {@link java.util.NoSuchElementException} is thrown
     * </p>
     * @return  instance of the found object
     */
    @NotNull S value();

    /**
     * error that has occurred.
     * <p>
     * if no error has occurred, {@link java.util.NoSuchElementException} is thrown
     * </p>
     * @return  instance of {@link Failure} with the error
     */
    @NotNull Failure  fault();
}
