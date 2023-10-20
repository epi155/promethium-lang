package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * context to perform actions based on opto
 *
 * @param <T> value type of the chosen value
 */
public interface OptoContext<T> {
    /**
     * static constructor of an opto map context with an external value
     *
     * <pre>
     *     Hope&lt;R&gt; hope = OptoContext.&lt;S,R&gt;optoMap(..)
     *          .when(..).map(..)
     *          .otherwise().map(..)
     *          .end();
     * </pre>
     *
     * @param value external value
     * @param <S>   value type of the opto/external value
     * @param <R>   value type of the mapped to value
     * @return instance of {@link OptoMapInitialContext}
     * @see #optoMap()
     */
    static @NotNull <S, R> OptoMapInitialContext<S, R> optoMap(@NotNull S value) {
        return new PmOptoRawMapContext<>(value);
    }

    /**
     * static constructor of a opto value context with an external value
     *
     * <pre>
     *     Nope nope = OptoContext.opto(..)
     *          .when(..).thenApply(..)
     *          .when(..).thenAccept(..)
     *          .otherwise().fault(..)
     *          .end();
     * </pre>
     *
     * @param value external value
     * @param <S>   value type of the opto/external value
     * @return instante of {@link OptoNixInitialContext}
     * @see #opto()
     */
    static @NotNull <S> OptoNixInitialContext<S> opto(@NotNull S value) {
        return new PmOptoRawNixContext<>(value);
    }

    /**
     * create an opto map context
     *
     * <pre>
     *      Hope&lt;R&gt; value = hopeT.&lt;R&gt;optoMap()
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
     *         <td >{@code OptoContext<T>}</td>
     *         <td><code>{@link OptoContext#optoMap() &lt;R>optoMap}</code></td>
     *         <td></td>
     *         <td rowspan="2">{@code OptoMapContext<T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code static}</td>
     *         <td><code>{@link OptoContext#optoMap(Object) &lt;T,R>optoMap}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="5">{@code OptoMapContext<T,R>}</td>
     *         <td><code>{@link OptoMapContext#when(Predicate) when}</code></td>
     *         <td>{@code Predicate<T>}</td>
     *         <td rowspan="3">{@code OptoMapWhenContext<T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapContext#when(boolean) when}</code></td>
     *         <td>{@code boolean}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapContext#whenEquals(Object) whenEquals}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
     *         <td>{@code Class<U>}</td>
     *         <td>{@code OptoMapWhenAsContext<U,T,R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapContext#otherwise() otherwise}</code></td>
     *         <td></td>
     *         <td>{@code OptoMapElseContext<T,R>}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code OptoMapWhenContext<T,R>}</td>
     *         <td><code>{@link OptoMapWhenContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super T, ? extends ErrorXorValue<R>>}</td>
     *         <td rowspan="7">{@code OptoMapContext<T, R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapWhenContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super T, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapWhenContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="3"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code OptoMapWhenAsContext<U,T,R>}</td>
     *         <td><code>{@link OptoMapWhenAsContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super U, ? extends ErrorXorValue<R>>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapWhenAsContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super U, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="3">{@code OptoMapElseContext<T,R>}</td>
     *         <td><code>{@link OptoMapElseContext#map(Function) map}</code></td>
     *         <td>{@code Function<? super T, ? extends ErrorXorValue<R>>}</td>
     *         <td rowspan="3">{@code OptoMapExitContext<R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapElseContext#mapOf(Function) mapOf}</code></td>
     *         <td>{@code Function<? super T, ? extends R>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoMapElseContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td>{@code OptoMapExitContext<R>}</td>
     *         <td><code>{@link OptoMapExitContext#end() end}</code></td>
     *         <td></td>
     *         <td>{@code Hope<R>}</td>
     *     </tr>
     * </table>
     *
     * <p>
     *     if the initial value contains an error, the final result is this error.
     *     no conditional action is performed
     * </p>
     *
     * @param <R> value type of the final value
     * @return instance of {@link OptoMapInitialContext}
     */
    @SuppressWarnings("JavadocDeclaration")
    @NotNull <R> OptoMapInitialContext<T, R> optoMap();

    /**
     * create a opto value context
     *
     * <pre>
     *      Nope nope = hopeT.opto()
     *          .when(..).thenApply(..)
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
     *         <td >{@code OptoContext<T>}</td>
     *         <td><code>{@link OptoContext#opto() opto}</code></td>
     *         <td></td>
     *         <td rowspan="2">{@code OptoNixContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@code static}</td>
     *         <td>{@link OptoContext#opto(Object) &lt;T>opto}</td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="5">{@code OptoNixContext<T>}</td>
     *         <td><code>{@link OptoNixContext#when(Predicate) when}</code></td>
     *         <td>{@code Predicate<T>}</td>
     *         <td rowspan="3">{@code OptoNixWhenContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixContext#when(boolean) when}</code></td>
     *         <td>{@code boolean}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixContext#whenEquals(Object) whenEquals}</code></td>
     *         <td>{@code T}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
     *         <td>{@code Class<U>}</td>
     *         <td>{@code OptoNixWhenAsContext<U,T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixContext#otherwise() otherwise}</code></td>
     *         <td></td>
     *         <td>{@code OptoNixElseContext<T>}</td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="4">{@code OptoNixWhenContext<T>}</td>
     *         <td><code>{@link OptoNixWhenContext#thenApply(Function) thenApply}</code></td>
     *         <td>{@code Function<? super T, ? extends SingleError>}</td>
     *         <td rowspan="9">{@code OptoNixContext<T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixWhenContext#thenAccept(Consumer) thenAccept}</code></td>
     *         <td>{@code Consumer<? super T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixWhenContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixWhenContext#nop() nop}</code></td>
     *         <td></td>
     *     </tr>
     *     <tr><td colspan="3"></td></tr>
     *     <tr>
     *         <td rowspan="4">{@code OptoNixWhenAsContext<U, T>}</td>
     *         <td><code>{@link OptoNixWhenAsContext#thenApply(Function) thenApply}</code></td>
     *         <td>{@code Function<? super U, ? extends SingleError>}</td>
     *     </tr>
     *     <tr>
     *         <td>{@link OptoNixWhenAsContext#thenAccept(Consumer) thenAccept}</td>
     *         <td>{@code Consumer<? super U>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixWhenAsContext#nop() nop}</code></td>
     *         <td></td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td rowspan="4">{@code OptoNixElseContext<T>}</td>
     *         <td><code>{@link OptoNixElseContext#thenApply(Function) thenApply}</code></td>
     *         <td>{@code Function<? super T, ? extends SingleError>}</td>
     *         <td rowspan="4">{@code OptoNixExitContext}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixElseContext#thenAccept(Consumer) thenAccept}</code></td>
     *         <td>{@code Consumer<? super T>}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixElseContext#fault(CustMsg, Object...) fault}</code></td>
     *         <td>{@code CustMsg, Object...}</td>
     *     </tr>
     *     <tr>
     *         <td><code>{@link OptoNixElseContext#nop() nop}</code></td>
     *         <td></td>
     *     </tr>
     *     <tr><td colspan="4"></td></tr>
     *     <tr>
     *         <td>{@code OptoNixExitContext}</td>
     *         <td><code>{@link OptoNixExitContext#end() end}</code></td>
     *         <td></td>
     *         <td>{@code Nope}</td>
     *     </tr>
     * </table>
     * <p>
     *     if the initial value contains an error, the final result is this error.
     *     no conditional action is performed
     * </p>
     *
     * @return instance of {@link OptoNixInitialContext}
     */
    @SuppressWarnings("JavadocDeclaration")
    @NotNull OptoNixInitialContext<T> opto();
}
