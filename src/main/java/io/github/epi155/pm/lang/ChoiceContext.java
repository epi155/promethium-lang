package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

/**
 * context to perform actions based on a choice
 * @param <T>   value type of the chosen value
 */
public interface ChoiceContext<T> {
    static @NotNull <S> ChoiceValueContext<S> choice(@NotNull S value) {
        return new PmChoiceRawValueContext<>(value);
    }
    static @NotNull <S,R> ChoiceMapContext<S,R> choiceTo(@NotNull S value) {
        return new PmChoiceRawMapContext<>(value);
    }
    /**
     * create a choice value context
     * <p>
     *     if the initial value contains errors, the final result is these errors. no conditional action is performed
     * </p>
     * <pre>
     *      None none = anyValue.choice()
     *          .when(..).perform(..)
     *          .otherwise().perform(..)
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
     *      Some&lt;R&gt; value = anyValue.&lt;R&gt;choiceTo()
     *          .when(..).map(..)
     *          .otherwise().map(..)
     *          .end();
     * </pre>
     * @return instance of {@link ChoiceMapContext}
     * @param <R>   value type of the final value
     */
    @NotNull <R> ChoiceMapContext<T,R> choiceTo();
}
