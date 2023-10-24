/**
 * Group of classes useful for alternately handling a value or one or more errors
 * <h2>Introduction</h2>
 * <p>The package contains a set of classes that can be used in the event that an operation can return a value or one or
 * more errors (or warnings).
 * The first class to consider is {@link io.github.epi155.pm.lang.Some Some&lt;T>}, in its simplest form it can be instantiated with
 * <pre>
 * Some.{@link io.github.epi155.pm.lang.Some#of(java.lang.Object) of(T value)};                               // final value (and no warnings)
 * Some.{@link io.github.epi155.pm.lang.Some#fault(io.github.epi155.pm.lang.CustMsg, java.lang.Object...)  fault(CustMsg ce, Object... argv)};         // single error message </pre>
 * then there are the derived classes:
 * <ul>
 *     <li>
 * {@link io.github.epi155.pm.lang.None None}, if no value is returned (instead of Some&lt;Void>);
 *     </li>
 *     <li>
 * {@link io.github.epi155.pm.lang.Hope Hope&lt;T>}, if at most one error is returned;
 *     </li>
 *     <li>
 * {@link io.github.epi155.pm.lang.Nope Nope}, if no value and at most an error is returned  (instead of Hope&lt;Void>).
 *     </li>
 *     <li>
 * {@link io.github.epi155.pm.lang.SearchResult SearchResult&lt;T>}, if optional value and at most an error is returned  (instead of Hope&lt;Optional&lt;T>>).
 *     </li>
 * </ul>
 *
 * <p>Usually {@link io.github.epi155.pm.lang.Some Some&lt;T>} and {@link io.github.epi155.pm.lang.None None}
 * are used through a builder which allows to accumulate many errors (warnings)
 * <pre>
 * val bld = Some.&lt;T&gt;{@link io.github.epi155.pm.lang.Some#builder() builder()};
 * bld.{@link io.github.epi155.pm.lang.SomeBuilder#fault(io.github.epi155.pm.lang.CustMsg, java.lang.Object...) fault(CustMsg ce, Object... argv)};      // add single error message
 * bld.{@link io.github.epi155.pm.lang.SomeBuilder#alert(io.github.epi155.pm.lang.CustMsg, java.lang.Object...) alert(CustMsg ce, Object... argv)};      // add single warning message
 * bld.{@link io.github.epi155.pm.lang.SomeBuilder#capture(java.lang.Throwable) capture(Throwable t)};                   // add error from Exception
 * bld.{@link io.github.epi155.pm.lang.SomeBuilder#add(io.github.epi155.pm.lang.ItemStatus) add(ItemStatus)};
 * bld.{@link io.github.epi155.pm.lang.SomeBuilder#value(java.lang.Object) value(T value)};                         // set final value
 * Some&lt;T&gt; some = bld.{@link io.github.epi155.pm.lang.SomeBuilder#build() build()}; </pre>
 * The outcome can be evaluated imperatively
 * <pre>
 * if (some.{@link io.github.epi155.pm.lang.ItemStatus#completeWithoutErrors() completeWithoutErrors()}) {
 *     T v = some.{@link io.github.epi155.pm.lang.Some#value() value()};                     // final value
 *     // ... action on value
 * } else {
 *     Collection&lt;? extends Signal&gt; e = some.{@link io.github.epi155.pm.lang.Some#signals() signals()};   // errors/warings collection
 *     // ... action on errors (and warnings)
 * } </pre>
 * or functionally
 * <pre>
 * some
 *     .{@link io.github.epi155.pm.lang.Some#onSuccess(java.util.function.Consumer) onSuccess(v -> { ... })}        // ... action on value (ignoring warnings)
 *     .{@link io.github.epi155.pm.lang.Some#onFailure(java.util.function.Consumer) onFailure(e -> { ... })};       // ... action on errors (and warnings) </pre>
 *
 * <h2>Composition</h2>
 * <p>Let's see a case where these classes interact with each other.
 * Suppose we have data in format A.
 * The datum must undergo a formal validation, if there are no errors it is possible to decode it in the B format.
 * At this point the data is subjected to a merit check and if there are no errors it can be transformed into the final format C.
 * <p>With a functional approach that follows the imperative flow we could write code like
 * <pre>
 * val bld = Some.&lt;C>builder();
 * formalValidation(a)                          // None
 *     .onSuccess(() -> decode(a)               // Some&lt;B>
 *         .onSuccess(b -> meritValidation(b)   // None
 *             .onSuccess(() -> translate(b)    // Some&lt;C>
 *                 .onSuccess(bld::value)
 *                 .onFailure(bld::add))
 *             .onFailure(bld::add))
 *         .onFailure(bld::add))
 *     .onFailure(bld::add);
 * Some&lt;C> sc = bld.build(); </pre>
 * with a more functional approach it becomes
 * <pre>
 * Some&lt;C> sc = formalValidation(a)             // None
 *     .&lt;B>{@link io.github.epi155.pm.lang.None#map(java.util.function.Supplier) map}(() -> decode(a))                 // Some&lt;B>
 *     .&lt;C>{@link io.github.epi155.pm.lang.Some#map(java.util.function.Function) map}(b -> meritValidation(b)          // None
 *         .&lt;C>map(() -> translate(b)));        // Some&lt;C> </pre>
 * or, if we are not interested in value, but only in evaluating errors
 * <pre>
 * None nc = formalValidation(a)                // None
 *     .{@link io.github.epi155.pm.lang.None#ergo(java.util.function.Supplier) ergo}(() -> decode(a)                    // Some&lt;B>
 *         .{@link io.github.epi155.pm.lang.Some#ergo(java.util.function.Function) ergo}(b -> meritValidation(b)        // None
 *             .ergo(() -> translate(b))));     // Some&lt;C> </pre>
 * <h3>Composition (light)</h3>
 * If the methods return at most one error we can use Hope instead of Some and Nope instead of None,
 * the previous code remains valid
 * <pre>
 * val bld = Some.&lt;C>builder();
 * formalValidation(a)                          // Nope
 *     .onSuccess(() -> decode(a)               // Hope&lt;B>
 *         .onSuccess(b -> meritValidation(b)   // Nope
 *             .onSuccess(() -> translate(b)    // Hope&lt;C>
 *                 .onSuccess(bld::value)
 *                 .onFailure(bld::add))
 *             .onFailure(bld::add))
 *         .onFailure(bld::add))
 *     .onFailure(bld::add);
 * Some&lt;C> sc = bld.build(); </pre>
 * with a more functional approach it becomes
 * <pre>
 * Some&lt;C> sc = formalValidation(a)             // Nope
 *     .&lt;B>{@link io.github.epi155.pm.lang.Nope#map(java.util.function.Supplier) map}(() -> decode(a))                 // Hope&lt;B>
 *     .&lt;C>{@link io.github.epi155.pm.lang.Hope#map(java.util.function.Function) map}(b -> meritValidation(b)          // Nope
 *         .&lt;C>map(() -> translate(b)));        // Hope&lt;C> </pre>
 * or, if we are not interested in value, but only in evaluating errors
 * <pre>
 * None nc = formalValidation(a)                // Nope
 *     .{@link io.github.epi155.pm.lang.Nope#ergo(java.util.function.Supplier) ergo}(() -> decode(a)                    // Hope&lt;B>
 *         .{@link io.github.epi155.pm.lang.Hope#ergo(java.util.function.Function) ergo}(b -> meritValidation(b)        // Nope
 *             .ergo(() -> translate(b))));     // Hope&lt;C>
 * </pre>
 * in this case the last two forms, purely functional,
 * can be rewritten to produce respectively a Hope and Nope, instead of Some and None
 * <pre>
 * Hope&lt;C> hx = formalValidation(a)             // Nope
 *     .&lt;B>{@link io.github.epi155.pm.lang.Nope#maps(java.util.function.Supplier) maps}(() -> decode(a))                // Hope&lt;B>
 *     .&lt;C>{@link io.github.epi155.pm.lang.Hope#maps(java.util.function.Function) maps}(b -> meritValidation(b)         // Nope
 *         .&lt;C>maps(() -> translate(b)));       // Hope&lt;C>
 * Nope nx = formalValidation(a)                // Nope
 *     .{@link io.github.epi155.pm.lang.Nope#ergoes(java.util.function.Supplier) ergoes}(() -> decode(a)                 // Hope&lt;B>
 *         .{@link io.github.epi155.pm.lang.Hope#ergoes(java.util.function.Function) ergoes}(b -> meritValidation(b)     // Nope
 *             .ergoes(() -> translate(b))));     // Hope&lt;C> </pre>
 * <h3>Composition summary</h3>
 * <table class="plain">
 *     <tr>
 *         <th>origin</th><th>method</th><th>argument</th><th>result</th>
 *     </tr>
 *     <tr>
 *         <td rowspan="4">{@code AnyValue<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.AnyValue#map(java.util.function.Function) map}</code></td>
 *         <td>{@code Function<? super T,? extends AnyValue<R>>}</td>
 *         <td>{@code Some<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.AnyValue#mapOf(java.util.function.Function) mapOf}</code></td>
 *         <td>{@code Function<? super T,? extends R>}</td>
 *         <td>{@code Some<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.AnyValue#ergo(java.util.function.Function) ergo}</code></td>
 *         <td>{@code Function<? super T,? extends ItemStatus>}</td>
 *         <td>{@code None}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.AnyValue#implies(java.util.function.Consumer) thenAccept}</code></td>
 *         <td>{@code Consumer<? super T>}</td>
 *         <td><i>{@code AnyValue<T>}</i></td>
 *     </tr>
 *     <tr>
 *         <td rowspan="4">{@code OnlyError}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OnlyError#map(java.util.function.Supplier) map}</code></td>
 *         <td>{@code Supplier<? extends AnyValue<R>>}</td>
 *         <td>{@code Some<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OnlyError#mapOf(java.util.function.Supplier) mapOf}</code></td>
 *         <td>{@code Supplier<? extends R>}</td>
 *         <td>{@code Some<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OnlyError#ergo(java.util.function.Supplier) ergo}</code></td>
 *         <td>{@code Supplier<? extends ItemStatus>}</td>
 *         <td>{@code None}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OnlyError#implies(java.lang.Runnable) thenRun}</code></td>
 *         <td>{@code Runnable}</td>
 *         <td><i>{@code OnlyError}</i></td>
 *     </tr>
 *     <tr>
 *         <td rowspan="4">{@code Hope<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#maps(java.util.function.Function) maps}</code></td>
 *         <td>{@code Function<? super T,? extends Hope<R>>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#mapsOf(java.util.function.Function) mapsOf}</code></td>
 *         <td>{@code Function<? super T,? extends R>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#ergoes(java.util.function.Function) ergoes}</code></td>
 *         <td>{@code Function<? super T,? extends SingleError>}</td>
 *         <td>{@code Nope}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#implies(java.util.function.Consumer) thenAccept}  </code></td>
 *         <td>{@code Consumer<? super T>}</td>
 *         <td>{@code Hope<T>}</td>
 *     </tr>
 *     <tr>
 *         <td rowspan="4">{@code Nope}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#maps(java.util.function.Supplier) maps}</code></td>
 *         <td>{@code Supplier<? extends Hope<R>>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#mapsOf(java.util.function.Supplier) mapsOf}</code></td>
 *         <td>{@code Supplier<? extends R>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#ergoes(java.util.function.Supplier) ergoes}</code></td>
 *         <td>{@code Supplier<? extends SingleError>}</td>
 *         <td>{@code Nope}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#implies(java.lang.Runnable) thenRun}</code></td>
 *         <td>{@code Runnable}</td>
 *         <td>{@code Nope}</td>
 *     </tr>
 *      <caption>Composition rule</caption>
 * </table>
 * <h2>Conditional</h2>
 * The classes that have their own value (Some and Hope) have two conditional methods which,
 * if there are no errors, allow the value contained to be evaluated,
 * see {@link io.github.epi155.pm.lang.ChooseContext#chooseMap()}
 * and {@link io.github.epi155.pm.lang.ChooseContext#choose()}.
 * These method structures can also be triggered directly by a value,
 * see {@link io.github.epi155.pm.lang.ChooseContext#chooseMap(java.lang.Object)}
 * and {@link io.github.epi155.pm.lang.ChooseContext#choose(java.lang.Object)},
 * although an imperative structure might be preferable in this case.
 *
 * <h3>Conditional to <code>{@link io.github.epi155.pm.lang.None}</code></h3>
 * <table class='plain'>
 *      <caption>Composition rule</caption>
 *     <tr>
 *         <th>origin</th><th>method</th><th>argument</th><th>result</th>
 *     </tr>
 *     <tr>
 *         <td ><code>{@link io.github.epi155.pm.lang.ChooseContext ChooseContext&lt;T&gt;}</code></td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseContext#choose() choose}</code></td>
 *         <td></td>
 *         <td rowspan="2">{@code ChooseNixContext<T>}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code static}</td>
 *         <td>{@link io.github.epi155.pm.lang.ChooseContext#choose(Object) &lt;T>choose}</td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="5">{@code ChooseNixContext<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixContext#when(Predicate) when}</code></td>
 *         <td>{@code Predicate<T>}</td>
 *         <td rowspan="3">{@code ChooseNixWhenContext<T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixContext#when(boolean) when}</code></td>
 *         <td>{@code boolean}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixContext#whenEquals(Object) whenEquals}</code></td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
 *         <td>{@code Class<U>}</td>
 *         <td>{@code ChooseNixWhenAsContext<U,T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixContext#otherwise() otherwise}</code></td>
 *         <td></td>
 *         <td>{@code ChooseNixElseContext<T>}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="5">{@code ChooseNixWhenContext<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenContext#thenApply(Function) thenApply}</code></td>
 *         <td>{@code Function<? super T, ? extends ItemStatus>}</td>
 *         <td rowspan="11">{@code ChooseNixContext<T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenContext#thenAccept(Consumer) thenAccept}</code></td>
 *         <td>{@code Consumer<? super T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenContext#alert(CustMsg, Object...) alert}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenContext#nop() nop}</code></td>
 *         <td></td>
 *     </tr>
 *     <tr><td colspan="3"></td></tr>
 *     <tr>
 *         <td rowspan="5">{@code ChooseNixWhenAsContext<U, T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenAsContext#thenApply(Function) thenApply}</code></td>
 *         <td>{@code Function<? super U, ? extends ItemStatus>}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link io.github.epi155.pm.lang.ChooseNixWhenAsContext#thenAccept(Consumer) thenAccept}</td>
 *         <td>{@code Consumer<? super U>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenAsContext#alert(CustMsg, Object...) alert}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixWhenAsContext#nop() nop}</code></td>
 *         <td></td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="5">{@code ChooseNixElseContext<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixElseContext#thenApply(Function) thenApply}</code></td>
 *         <td>{@code Function<? super T, ? extends ItemStatus>}</td>
 *         <td rowspan="5">{@code ChooseNixExitContext}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixElseContext#thenAccept(Consumer) thenAccept}</code></td>
 *         <td>{@code Consumer<? super T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixElseContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixElseContext#alert(CustMsg, Object...) alert}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixElseContext#nop() nop}</code></td>
 *         <td></td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td>{@code ChooseNixExitContext}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseNixExitContext#end() end}</code></td>
 *         <td></td>
 *         <td>{@code None}</td>
 *     </tr>
 * </table>
 *
 * <h3>Conditional to <code>{@link io.github.epi155.pm.lang.Some Some&lt;R&gt;}</code></h3>
 * <table class='plain'>
 *      <caption>Composition rule</caption>
 *     <tr>
 *         <th>origin</th><th>method</th><th>argument</th><th>result</th>
 *     </tr>
 *     <tr>
 *         <td ><code>{@link io.github.epi155.pm.lang.ChooseContext ChooseContext&lt;T&gt;}</code></td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseContext#chooseMap() &lt;R>chooseMap}</code></td>
 *         <td></td>
 *         <td rowspan="2">{@code ChooseMapContext<T,R>}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code static}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseContext#chooseMap(Object) &lt;T,R>chooseMap}</code></td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="5">{@code ChooseMapContext<T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapContext#when(Predicate) when}</code></td>
 *         <td>{@code Predicate<T>}</td>
 *         <td rowspan="3">{@code ChooseMapWhenContext<T,R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapContext#when(boolean) when}</code></td>
 *         <td>{@code boolean}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapContext#whenEquals(Object) whenEquals}</code></td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
 *         <td>{@code Class<U>}</td>
 *         <td>{@code ChooseMapWhenAsContext<U,T,R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapContext#otherwise() otherwise}</code></td>
 *         <td></td>
 *         <td>{@code ChooseMapElseContext<T,R>}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="3">{@code ChooseMapWhenContext<T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapWhenContext#map(Function) map}</code></td>
 *         <td>{@code Function<? super T, ? extends AnyValue<R>>}</td>
 *         <td rowspan="7">{@code ChooseMapContext<T, R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapWhenContext#mapOf(Function) mapOf}</code></td>
 *         <td>{@code Function<? super T, ? extends R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapWhenContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr><td colspan="3"></td></tr>
 *     <tr>
 *         <td rowspan="3">{@code ChooseMapWhenAsContext<U,T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapWhenAsContext#map(Function) map}</code></td>
 *         <td>{@code Function<? super U, ? extends AnyValue<R>>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapWhenAsContext#mapOf(Function) mapOf}</code></td>
 *         <td>{@code Function<? super U, ? extends R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="3">{@code ChooseMapElseContext<T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapElseContext#map(Function) map}</code></td>
 *         <td>{@code Function<? super T, ? extends AnyValue<R>>}</td>
 *         <td rowspan="3">{@code ChooseMapExitContext<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapElseContext#mapOf(Function) mapOf}</code></td>
 *         <td>{@code Function<? super T, ? extends R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapElseContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td>{@code ChooseMapExitContext<R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.ChooseMapExitContext#end() end}</code></td>
 *         <td></td>
 *         <td>{@code Some<R>}</td>
 *     </tr>
 * </table>
 *
 * <h3>Conditional to <code>{@link io.github.epi155.pm.lang.Nope}</code></h3>
 * <table class='plain'>
 *      <caption>Composition rule</caption>
 *     <tr>
 *         <th>origin</th><th>method</th><th>argument</th><th>result</th>
 *     </tr>
 *     <tr>
 *         <td ><code>{@link io.github.epi155.pm.lang.OptoContext OptoContext&lt;T&gt;}</code></td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoContext#chooses() chooses}</code></td>
 *         <td></td>
 *         <td rowspan="2">{@code OptoNixContext<T>}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code static}</td>
 *         <td>{@link io.github.epi155.pm.lang.OptoContext#chooses(Object) &lt;T>chooses}</td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="5">{@code OptoNixContext<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixContext#when(Predicate) when}</code></td>
 *         <td>{@code Predicate<T>}</td>
 *         <td rowspan="3">{@code OptoNixWhenContext<T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixContext#when(boolean) when}</code></td>
 *         <td>{@code boolean}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixContext#whenEquals(Object) whenEquals}</code></td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
 *         <td>{@code Class<U>}</td>
 *         <td>{@code OptoNixWhenAsContext<U,T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixContext#otherwise() otherwise}</code></td>
 *         <td></td>
 *         <td>{@code OptoNixElseContext<T>}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="4">{@code OptoNixWhenContext<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixWhenContext#thenApply(Function) thenApply}</code></td>
 *         <td>{@code Function<? super T, ? extends SingleError>}</td>
 *         <td rowspan="9">{@code OptoNixContext<T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixWhenContext#thenAccept(Consumer) thenAccept}</code></td>
 *         <td>{@code Consumer<? super T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixWhenContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixWhenContext#nop() nop}</code></td>
 *         <td></td>
 *     </tr>
 *     <tr><td colspan="3"></td></tr>
 *     <tr>
 *         <td rowspan="4">{@code OptoNixWhenAsContext<U, T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixWhenAsContext#thenApply(Function) thenApply}</code></td>
 *         <td>{@code Function<? super U, ? extends SingleError>}</td>
 *     </tr>
 *     <tr>
 *         <td>{@link io.github.epi155.pm.lang.OptoNixWhenAsContext#thenAccept(Consumer) thenAccept}</td>
 *         <td>{@code Consumer<? super U>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixWhenAsContext#nop() nop}</code></td>
 *         <td></td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="4">{@code OptoNixElseContext<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixElseContext#thenApply(Function) thenApply}</code></td>
 *         <td>{@code Function<? super T, ? extends SingleError>}</td>
 *         <td rowspan="4">{@code OptoNixExitContext}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixElseContext#thenAccept(Consumer) thenAccept}</code></td>
 *         <td>{@code Consumer<? super T>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixElseContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixElseContext#nop() nop}</code></td>
 *         <td></td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td>{@code OptoNixExitContext}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoNixExitContext#end() end}</code></td>
 *         <td></td>
 *         <td>{@code Nope}</td>
 *     </tr>
 * </table>
 *
 * <h3>Conditional to <code>{@link io.github.epi155.pm.lang.Hope Hope&lt;R&gt;}</code></h3>
 * <table class='plain'>
 *      <caption>Composition rule</caption>
 *     <tr>
 *         <th>origin</th><th>method</th><th>argument</th><th>result</th>
 *     </tr>
 *     <tr>
 *         <td ><code>{@link io.github.epi155.pm.lang.OptoContext OptoContext&lt;T&gt;}</code></td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoContext#choosesMap() &lt;R>choosesMap}</code></td>
 *         <td></td>
 *         <td rowspan="2">{@code OptoMapContext<T,R>}</td>
 *     </tr>
 *     <tr>
 *         <td>{@code static}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoContext#choosesMap(Object) &lt;T,R>choosesMap}</code></td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="5">{@code OptoMapContext<T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapContext#when(Predicate) when}</code></td>
 *         <td>{@code Predicate<T>}</td>
 *         <td rowspan="3">{@code OptoMapWhenContext<T,R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapContext#when(boolean) when}</code></td>
 *         <td>{@code boolean}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapContext#whenEquals(Object) whenEquals}</code></td>
 *         <td>{@code T}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapContext#whenInstanceOf(Class) &lt;U>whenInstanceOf}</code></td>
 *         <td>{@code Class<U>}</td>
 *         <td>{@code OptoMapWhenAsContext<U,T,R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapContext#otherwise() otherwise}</code></td>
 *         <td></td>
 *         <td>{@code OptoMapElseContext<T,R>}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="3">{@code OptoMapWhenContext<T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapWhenContext#maps(Function) maps}</code></td>
 *         <td>{@code Function<? super T, ? extends ErrorXorValue<R>>}</td>
 *         <td rowspan="7">{@code OptoMapContext<T, R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapWhenContext#mapsOf(Function) mapsOf}</code></td>
 *         <td>{@code Function<? super T, ? extends R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapWhenContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr><td colspan="3"></td></tr>
 *     <tr>
 *         <td rowspan="3">{@code OptoMapWhenAsContext<U,T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapWhenAsContext#maps(Function) maps}</code></td>
 *         <td>{@code Function<? super U, ? extends ErrorXorValue<R>>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapWhenAsContext#mapsOf(Function) mapsOf}</code></td>
 *         <td>{@code Function<? super U, ? extends R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapWhenAsContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td rowspan="3">{@code OptoMapElseContext<T,R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapElseContext#maps(Function) maps}</code></td>
 *         <td>{@code Function<? super T, ? extends ErrorXorValue<R>>}</td>
 *         <td rowspan="3">{@code OptoMapExitContext<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapElseContext#mapsOf(Function) mapsOf}</code></td>
 *         <td>{@code Function<? super T, ? extends R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapElseContext#fault(CustMsg, Object...) fault}</code></td>
 *         <td>{@code CustMsg, Object...}</td>
 *     </tr>
 *     <tr><td colspan="4"></td></tr>
 *     <tr>
 *         <td>{@code OptoMapExitContext<R>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.OptoMapExitContext#end() end}</code></td>
 *         <td></td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 * </table>
 *
 * <h2>Search</h2>
 * The {@link io.github.epi155.pm.lang.SearchResult} class is designed to manage the result of a search,
 * in general three cases can occur, the result is found, it is not found, an error occurs.
 * the outcome is set in the following ways
 * <ul>
 *     <li><code>SearchResult.&lt;T>{@link io.github.epi155.pm.lang.SearchResult#of(java.lang.Object) of(T)};</code>
 *     set found result
 *     <li><code>SearchResult.&lt;T>{@link io.github.epi155.pm.lang.SearchResult#empty() empty()};</code>
 *     set not found
 *     <li><code>SearchResult.&lt;T>{@link io.github.epi155.pm.lang.SearchResult#fault(io.github.epi155.pm.lang.CustMsg, java.lang.Object...) fault(CustMsg, Object...)};</code>
 *     set custom error message
 *     <li><code>SearchResult.&lt;T>{@link io.github.epi155.pm.lang.SearchResult#capture(java.lang.Throwable) capture(Throwable)};</code>
 *     catch exception
 * </ul>
 * The outcome can be evaluated imperatively
 * <pre>
 * if (result.{@link io.github.epi155.pm.lang.SearchResult#isFound() isFound()}) {
 *     val value = result.{@link io.github.epi155.pm.lang.SearchResult#value() value()};
 *     // action on found value
 * } else if (result.{@link io.github.epi155.pm.lang.SearchResult#isNotFound() isNotFound()}) {
 *     // action on not found
 * } else if (result.{@link io.github.epi155.pm.lang.SearchResult#isFailure() isFailure()}) {
 *     val fault = result.{@link io.github.epi155.pm.lang.SearchResult#failure() failure()};
 *     // action on failure
 * } </pre>
 * or functionally
 * <pre>
 * result
 * .{@link io.github.epi155.pm.lang.SearchResult#onFound(java.util.function.Consumer) onFound(s -> ...)}       // action on found value
 * .{@link io.github.epi155.pm.lang.SearchResult#onNotFound(java.lang.Runnable) onNotFound(() -> ...)}   // action on not found
 * .{@link io.github.epi155.pm.lang.SearchResult#onFailure(java.util.function.Consumer) onFailure(e -> ...)};    // action on failure
 * </pre>
 * or converted to Some&lt;R>
 * <pre>
 * SearchResult&lt;S> result = ...
 * Some&lt;R> r = result.&lt;S,R>{@link io.github.epi155.pm.lang.SearchResult#valueBuilder() valueBuilder()}
 *     .{@link io.github.epi155.pm.lang.SearchValueBuilder.Found#onFound(java.util.function.Function) onFound(Function&lt;? super S, ? extends AnyValue&lt;R>>)}
 *     <i>.{@link io.github.epi155.pm.lang.SearchValueBuilder.Found#onFoundOf(java.util.function.Function) onFoundOf(Function&lt;? super S, ? extends R>)}</i>
 *     <i>.{@link io.github.epi155.pm.lang.SearchValueBuilder.Found#onFoundSetError(io.github.epi155.pm.lang.CustMsg, java.lang.Object...) onFoundSetError(CustMsg, Object...)}</i>
 *     .{@link io.github.epi155.pm.lang.SearchValueBuilder.NotFound#onNotFound(java.util.function.Supplier) onNotFound(Supplier&lt;? extends AnyValue&lt;R>>)}
 *     .{@link io.github.epi155.pm.lang.SearchValueBuilder#build() build()}; </pre>
 * It is possible to compose two searches
 * <pre>
 * SearchResult&lt;R> result2 = result.&lt;R>{@link io.github.epi155.pm.lang.SearchResult#resultBuilder() resultBuilder()}
 *     .{@link io.github.epi155.pm.lang.SearchResultBuilder.Found#onFound(java.util.function.Function) onFound(Function&lt;? super S, SearchResult&lt;R>>)}
 *     <i>.{@link io.github.epi155.pm.lang.SearchResultBuilder.Found#onFoundSetError(io.github.epi155.pm.lang.CustMsg, java.lang.Object...) onFoundSetError(CustMsg, Object...)}</i>
 *     .{@link io.github.epi155.pm.lang.SearchResultBuilder.NotFound#onNotFound(java.util.function.Supplier) onNotFound(Supplier&lt;SearchResult&lt;R>>)}
 *     .{@link io.github.epi155.pm.lang.SearchResultBuilder#build() build()};
 * </pre>
 * <h2>Loop</h2>
 * <pre>
 *  None none = None
 *      .{@link io.github.epi155.pm.lang.None#iterable(java.lang.Iterable) iterable(Iterable&lt;? extends AnyValue&lt;E>>)}
 *      .{@link io.github.epi155.pm.lang.LoopConsumer#forEach(java.util.function.Function) forEach(Function&lt;? super E, ? extends ItemStatus>)}
 *  None none = None
 *      .{@link io.github.epi155.pm.lang.None#iterableOf(java.lang.Iterable) iterableOf(Iterable&lt;? extends E>)}
 *      .{@link io.github.epi155.pm.lang.LoopConsumer#forEach(java.util.function.Function) forEach(Function&lt;? super E, ? extends ItemStatus>)} </pre>
 * <h3>Loop (parallel)</h3>
 * <pre>
 *  None none = None
 *      .{@link io.github.epi155.pm.lang.None#iterable(java.lang.Iterable) iterable(Iterable&lt;? extends AnyValue&lt;E>>)}
 *      .{@link io.github.epi155.pm.lang.LoopConsumer#forEachParallel(int, java.util.function.Function) forEachParallel(int, Function&lt;? super E, ? extends ItemStatus>)}
 *  None none = None
 *      .{@link io.github.epi155.pm.lang.None#iterable(java.lang.Iterable) iterable(Iterable&lt;? extends AnyValue&lt;E>>)}
 *      .{@link io.github.epi155.pm.lang.LoopConsumer#forEachParallel(java.util.concurrent.ExecutorService, java.util.function.Function) forEachParallel(ExecutorService, Function&lt;? super E, ? extends ItemStatus>)}
 * </pre>
 */
package io.github.epi155.pm.lang;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;