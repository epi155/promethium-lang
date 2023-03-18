package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * context to perform actions based on a choice
 * @param <T>   value type of the chosen value
 */
public interface ChoiceContext<T> {
    /**
     * static constructor of a choice value context with an external value
     * <pre>
     *     None none = ChoiceContext.choice(..)
     *          .when(..).apply(..)
     *          .when(..).accept(..)
     *          .otherwise().apply(..)
     *          .end();
     * </pre>
     * @param value     external value
     * @return          instance of {@link ChoiceValueContext}
     * @param <S>       value type of the chosen/external value
     */
    static @NotNull <S> ChoiceValueContext<S> choice(@NotNull S value) {
        return new PmChoiceRawValueContext<>(value);
    }

    /**
     * static constructor of a choice map context with an external value
     * <pre>
     *     Some&lt;R&gt; some = ChoiceContext.&lt;S,R&gt;choiceMap(..)
     *          .when(..).map(..)
     *          .otherwise().map(..)
     *          .end();
     * </pre>
     * @param value     external value
     * @return          instance of {@link ChoiceMapContext}
     * @param <S>       value type of the chosen/external value
     * @param <R>       value type of the mapped to value
     */
    static @NotNull <S,R> ChoiceMapContext<S,R> choiceMap(@NotNull S value) {
        return new PmChoiceRawMapContext<>(value);
    }
    /**
     * create a choice value context
     * <p>
     *     if the initial value contains errors, the final result is these errors. no conditional action is performed
     * </p>
     * <pre>
     *      None none = anyValue.choice()
     *          .when(..).apply(..)
     *          .otherwise().apply(..)
     *          .end();
     * </pre>
     * @return instance of {@link ChoiceValueContext}
     */
    @NotNull ChoiceValueContext<T> choice();

    /**
     * create a choice map context
     * <p>
     *     if the initial value contains errors, the final result is these errors. no conditional action is performed
     * </p>
     * <pre>
     *      Some&lt;R&gt; value = anyValue.&lt;R&gt;choiceMap()
     *          .when(..).map(..)
     *          .otherwise().map(..)
     *          .end();
     * </pre>
     * @return instance of {@link ChoiceMapContext}
     * @param <R>   value type of the final value
     */
    @NotNull <R> ChoiceMapContext<T,R> choiceMap();
}
