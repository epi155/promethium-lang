package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * context to perform actions based on a choice
 *
 * @param <T> value type of the chosen value
 */
public interface ChoiceContext<T> {
    /**
     * static constructor of a choice value context with an external value
     *
     * <pre>
     *     None none = ChoiceContext.choice(..)
     *          .when(..).ergo(..)
     *          .when(..).peek(..)
     *          .otherwise().fault(..)
     *          .end();
     * </pre>
     *
     * @see #choice()
     *
     * @param value     external value
     * @return instance of {@link ChoiceValueContext}
     * @param <S>       value type of the chosen/external value
     */
    static @NotNull <S> ChoiceValueContext<S> choice(@NotNull S value) {
        return new PmChoiceRawValueContext<>(value);
    }

    /**
     * static constructor of a choice map context with an external value
     *
     * <pre>
     *     Some&lt;R&gt; some = ChoiceContext.&lt;S,R&gt;choiceMap(..)
     *          .when(..).map(..)
     *          .otherwise().map(..)
     *          .end();
     * </pre>
     * @see #choiceMap()
     *
     * @param value     external value
     * @return instance of {@link ChoiceMapContext}
     * @param <S>       value type of the chosen/external value
     * @param <R>       value type of the mapped to value
     */
    static @NotNull <S,R> ChoiceMapContext<S,R> choiceMap(@NotNull S value) {
        return new PmChoiceRawMapContext<>(value);
    }
    /**
     * create a choice value context
     *
     * <pre>
     *      None none = anyValue.choice()
     *          .when(..).ergo(..)
     *          .otherwise().fault(..)
     *          .end();
     * </pre>
     *
     * <table class='plain'>
     *      <caption>Composition rule</caption>
     *     <tr>
     *         <th>origin</th><th>method</th><th>argument</th><th>result</th>
     *     </tr>
     *     <tr>
     *         <td >{@code ChoiceContext<T>}</td>
     *         <td><code>{@link ChoiceContext#choice() choice}</code></td>
     *         <td></td>
     *         <td rowspan="2">{@code ChoiceValueContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code static}</td>
     *         <td>{@link ChoiceContext#choice(Object) &lt;T>choice}</td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="5">{@code ChoiceValueContext<T>}</td>
     *         <td><code>{@link ChoiceValueContext#when(Predicate) when}</code></td>
     *         <td>{@code Predicate<T>}</td>
     *         <td rowspan="3">{@code ChoiceValueWhenContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueContext#when(Object) when}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueContext#when(boolean) when}</code></td>
     *         <td>{@code boolean}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
     *         <td>{@code Class<U>}</td>
     *         <td>{@code ChoiceValueWhenAsContext<U,T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueContext#otherwise() otherwise}</code></td>
     *         <td></td>
     *         <td>{@code ChoiceValueElseContext<T>}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChoiceValueWhenContext<T>}</td>
     *         <td><code>{@link ChoiceValueWhenContext#ergo(Function) ergo}</code></td>
     *         <td>{@code Function<? super T, ? extends ItemStatus>}</td>
     *         <td rowspan="7">{@code ChoiceValueContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueWhenContext#peek(Consumer) peek}</code></td>
     *         <td>{@code Consumer<? super T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueWhenContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="3"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChoiceValueWhenAsContext<U, T>}</td>
     *         <td><code>{@link ChoiceValueWhenAsContext#ergo(Function) ergo}</code></td>
     *         <td>{@code Function<? super U, ? extends ItemStatus>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link ChoiceValueWhenAsContext#peek(Consumer) peek}</td>
     *         <td>{@code Consumer<? super U>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="4">{@code ChoiceValueElseContext<T>}</td>
     *         <td><code>{@link ChoiceValueElseContext#ergo(Function) ergo}</code></td>
     *         <td>{@code Function<? super T, ? extends ItemStatus>}</td>
     *         <td rowspan="4">{@code ChoiceValueExitContext}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueElseContext#peek(Consumer) peek}</code></td>
     *         <td>{@code Consumer<? super T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueElseContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceValueElseContext#nop() nop}</code></td>
     *         <td></td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td>{@code ChoiceValueExitContext}</td>
     *         <td><code>{@link ChoiceValueExitContext#end() end}</code></td>
     *         <td></td>
     *         <td>{@code None}</td>
     *     </tr>
     * </table>
     * <p>
     *     if the initial value contains errors, the final result is these errors. no conditional action is performed
     * </p>
     * @return instance of {@link ChoiceValueContext}
     */
    @NotNull ChoiceValueContext<T> choice();

    /**
     * create a choice map context
     *
     * <pre>
     *      Some&lt;R&gt; value = anyValue.&lt;R&gt;choiceMap()
     *          .when(..).map(..)
     *          .otherwise().fault(..)
     *          .end();
     * </pre>
     *
     * <table class='plain'>
     *      <caption>Composition rule</caption>
     *     <tr>
     *         <th>origin</th><th>method</th><th>argument</th><th>result</th>
     *     </tr>
     *     <tr>
     *         <td >{@code ChoiceContext<T>}</td>
     *         <td><code>{@link ChoiceContext#choiceMap() &lt;R>choiceMap}</code></td>
     *         <td></td>
     *         <td rowspan="2">{@code ChoiceMapContext<T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code static}</td>
     *         <td><code>{@link ChoiceContext#choiceMap(Object) &lt;T,R>choiceMap}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="5">{@code ChoiceMapContext<T,R>}</td>
     *         <td><code>{@link ChoiceMapContext#when(Predicate) when}</code></td>
     *         <td>{@code Predicate<T>}</td>
     *         <td rowspan="3">{@code ChoiceMapWhenContext<T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapContext#when(Object) when}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapContext#when(boolean) when}</code></td>
     *         <td>{@code boolean}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
     *         <td>{@code Class<U>}</td>
     *         <td>{@code ChoiceMapWhenAsContext<U,T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapContext#otherwise() otherwise}</code></td>
     *         <td></td>
     *         <td>{@code ChoiceMapElseContext<T,R>}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChoiceMapWhenContext<T,R>}</td>
     *         <td><code>{@link ChoiceMapWhenContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super T, ? extends AnyValue<R>>}</td>
     *         <td rowspan="7">{@code ChoiceMapContext<T, R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapWhenContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super T, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapWhenContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="3"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChoiceMapWhenAsContext<U,T,R>}</td>
     *         <td><code>{@link ChoiceMapWhenAsContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super U, ? extends AnyValue<R>>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapWhenAsContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super U, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChoiceMapElseContext<T,R>}</td>
     *         <td><code>{@link ChoiceMapElseContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super T, ? extends AnyValue<R>>}</td>
     *         <td rowspan="3">{@code ChoiceMapExitContext<R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapElseContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super T, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChoiceMapElseContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td>{@code ChoiceMapExitContext<R>}</td>
     *         <td><code>{@link ChoiceMapExitContext#end() end}</code></td>
     *         <td></td>
     *         <td>{@code Some<R>}</td>
     *     </tr>
     * </table>
     * <p>
     *     if the initial value contains errors, the final result is these errors. no conditional action is performed
     * </p>
     *
     * @return instance of {@link ChoiceMapContext}
     * @param <R>   value type of the final value
     */
    @NotNull <R> ChoiceMapContext<T,R> choiceMap();
}
