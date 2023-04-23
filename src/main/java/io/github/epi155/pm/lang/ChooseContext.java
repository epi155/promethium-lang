package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * context to perform actions based on choose
 *
 * @param <T> value type of the chosen value
 */
public interface ChooseContext<T> {
    /**
     * static constructor of a choose value context with an external value
     *
     * <pre>
     *     None none = ChooseContext.choose(..)
     *          .when(..).ergo(..)
     *          .when(..).peek(..)
     *          .otherwise().fault(..)
     *          .end();
     * </pre>
     *
     * @param value external value
     * @param <S>   value type of the chosen/external value
     * @return instance of {@link ChooseNixInitialContext}
     * @see #choose()
     */
    static @NotNull <S> ChooseNixInitialContext<S> choose(@NotNull S value) {
        return new PmChooseRawNixContext<>(value);
    }

    /**
     * static constructor of a choose map context with an external value
     *
     * <pre>
     *     Some&lt;R&gt; some = ChooseContext.&lt;S,R&gt;chooseMap(..)
     *          .when(..).map(..)
     *          .otherwise().map(..)
     *          .end();
     * </pre>
     * @see #chooseMap()
     *
     * @param value     external value
     * @return instance of {@link ChooseMapContext}
     * @param <S>       value type of the chosen/external value
     * @param <R>       value type of the mapped to value
     */
    static @NotNull <S, R> ChooseMapInitialContext<S, R> chooseMap(@NotNull S value) {
        return new PmChooseRawMapContext<>(value);
    }

    /**
     * create a choose value context
     *
     * <pre>
     *      None none = anyValue.choose()
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
     *         <td >{@code ChooseContext<T>}</td>
     *         <td><code>{@link ChooseContext#choose() choose}</code></td>
     *         <td></td>
     *         <td rowspan="2">{@code ChooseNixContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code static}</td>
     *         <td>{@link ChooseContext#choose(Object) &lt;T>choose}</td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="5">{@code ChooseNixContext<T>}</td>
     *         <td><code>{@link ChooseNixContext#when(Predicate) when}</code></td>
     *         <td>{@code Predicate<T>}</td>
     *         <td rowspan="3">{@code ChooseNixWhenContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixContext#when(boolean) when}</code></td>
     *         <td>{@code boolean}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixContext#whenEquals(Object) whenEquals}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
     *         <td>{@code Class<U>}</td>
     *         <td>{@code ChooseNixWhenAsContext<U,T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixContext#otherwise() otherwise}</code></td>
     *         <td></td>
     *         <td>{@code ChooseNixElseContext<T>}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="4">{@code ChooseNixWhenContext<T>}</td>
     *         <td><code>{@link ChooseNixWhenContext#ergo(Function) ergo}</code></td>
     *         <td>{@code Function<? super T, ? extends ItemStatus>}</td>
     *         <td rowspan="9">{@code ChooseNixContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixWhenContext#peek(Consumer) peek}</code></td>
     *         <td>{@code Consumer<? super T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixWhenContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixWhenContext#nop() nop}</code></td>
     *         <td></td>
     *     </tr>
     *     <tr><td colspan="3"></td></tr>
     *     <tr>
     *         <td rowspan="4">{@code ChooseNixWhenAsContext<U, T>}</td>
     *         <td><code>{@link ChooseNixWhenAsContext#ergo(Function) ergo}</code></td>
     *         <td>{@code Function<? super U, ? extends ItemStatus>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link ChooseNixWhenAsContext#peek(Consumer) peek}</td>
     *         <td>{@code Consumer<? super U>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixWhenAsContext#nop() nop}</code></td>
     *         <td></td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="4">{@code ChooseNixElseContext<T>}</td>
     *         <td><code>{@link ChooseNixElseContext#ergo(Function) ergo}</code></td>
     *         <td>{@code Function<? super T, ? extends ItemStatus>}</td>
     *         <td rowspan="4">{@code ChooseNixExitContext}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixElseContext#peek(Consumer) peek}</code></td>
     *         <td>{@code Consumer<? super T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixElseContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseNixElseContext#nop() nop}</code></td>
     *         <td></td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td>{@code ChooseNixExitContext}</td>
     *         <td><code>{@link ChooseNixExitContext#end() end}</code></td>
     *         <td></td>
     *         <td>{@code None}</td>
     *     </tr>
     * </table>
     * <p>
     *     if the initial value contains errors, the final result is these errors. no conditional action is performed
     * </p>
     *
     * @return instance of {@link ChooseNixContext}
     */
    @SuppressWarnings("JavadocDeclaration")
    @NotNull ChooseNixInitialContext<T> choose();

    /**
     * create a choose map context
     *
     * <pre>
     *      Some&lt;R&gt; value = anyValue.&lt;R&gt;chooseMap()
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
     *         <td >{@code ChooseContext<T>}</td>
     *         <td><code>{@link ChooseContext#chooseMap() &lt;R>chooseMap}</code></td>
     *         <td></td>
     *         <td rowspan="2">{@code ChooseMapContext<T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code static}</td>
     *         <td><code>{@link ChooseContext#chooseMap(Object) &lt;T,R>chooseMap}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="5">{@code ChooseMapContext<T,R>}</td>
     *         <td><code>{@link ChooseMapContext#when(Predicate) when}</code></td>
     *         <td>{@code Predicate<T>}</td>
     *         <td rowspan="3">{@code ChooseMapWhenContext<T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapContext#when(boolean) when}</code></td>
     *         <td>{@code boolean}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapContext#whenEquals(Object) whenEquals}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
     *         <td>{@code Class<U>}</td>
     *         <td>{@code ChooseMapWhenAsContext<U,T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapContext#otherwise() otherwise}</code></td>
     *         <td></td>
     *         <td>{@code ChooseMapElseContext<T,R>}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChooseMapWhenContext<T,R>}</td>
     *         <td><code>{@link ChooseMapWhenContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super T, ? extends AnyValue<R>>}</td>
     *         <td rowspan="7">{@code ChooseMapContext<T, R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapWhenContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super T, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapWhenContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="3"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChooseMapWhenAsContext<U,T,R>}</td>
     *         <td><code>{@link ChooseMapWhenAsContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super U, ? extends AnyValue<R>>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapWhenAsContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super U, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code ChooseMapElseContext<T,R>}</td>
     *         <td><code>{@link ChooseMapElseContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super T, ? extends AnyValue<R>>}</td>
     *         <td rowspan="3">{@code ChooseMapExitContext<R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapElseContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super T, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link ChooseMapElseContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td>{@code ChooseMapExitContext<R>}</td>
     *         <td><code>{@link ChooseMapExitContext#end() end}</code></td>
     *         <td></td>
     *         <td>{@code Some<R>}</td>
     *     </tr>
     * </table>
     * <p>
     *     if the initial value contains errors, the final result is these errors. no conditional action is performed
     * </p>
     *
     * @return instance of {@link ChooseMapContext}
     * @param <R>   value type of the final value
     */
    @SuppressWarnings("JavadocDeclaration")
    @NotNull <R> ChooseMapInitialContext<T, R> chooseMap();
}
