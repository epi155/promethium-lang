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
 *     .&lt;B>{@link io.github.epi155.pm.lang.Nope#into(java.util.function.Supplier) into}(() -> decode(a))                // Hope&lt;B>
 *     .&lt;C>{@link io.github.epi155.pm.lang.Hope#into(java.util.function.Function) into}(b -> meritValidation(b)         // Nope
 *         .&lt;C>into(() -> translate(b)));       // Hope&lt;C>
 * Nope nx = formalValidation(a)                // Nope
 *     .{@link io.github.epi155.pm.lang.Nope#thus(java.util.function.Supplier) thus}(() -> decode(a)                    // Hope&lt;B>
 *         .{@link io.github.epi155.pm.lang.Hope#thus(java.util.function.Function) thus}(b -> meritValidation(b)        // Nope
 *             .thus(() -> translate(b))));     // Hope&lt;C> </pre>
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
 *         <td><code>{@link io.github.epi155.pm.lang.AnyValue#peek(java.util.function.Consumer) peek}</code></td>
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
 *         <td><code>{@link io.github.epi155.pm.lang.OnlyError#peek(java.lang.Runnable) peek}</code></td>
 *         <td>{@code Runnable}</td>
 *         <td><i>{@code OnlyError}</i></td>
 *     </tr>
 *     <tr>
 *         <td rowspan="4">{@code Hope<T>}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#into(java.util.function.Function) into}</code></td>
 *         <td>{@code Function<? super T,? extends Hope<R>>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#intoOf(java.util.function.Function) intoOf}</code></td>
 *         <td>{@code Function<? super T,? extends R>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#thus(java.util.function.Function) thus}</code></td>
 *         <td>{@code Function<? super T,? extends SingleError>}</td>
 *         <td>{@code Nope}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Hope#peek(java.util.function.Consumer) peek}  </code></td>
 *         <td>{@code Consumer<? super T>}</td>
 *         <td>{@code Hope<T>}</td>
 *     </tr>
 *     <tr>
 *         <td rowspan="4">{@code Nope}</td>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#into(java.util.function.Supplier) into}</code></td>
 *         <td>{@code Supplier<? extends Hope<R>>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#intoOf(java.util.function.Supplier) intoOf}</code></td>
 *         <td>{@code Supplier<? extends R>}</td>
 *         <td>{@code Hope<R>}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#thus(java.util.function.Supplier) thus}</code></td>
 *         <td>{@code Supplier<? extends SingleError>}</td>
 *         <td>{@code Nope}</td>
 *     </tr>
 *     <tr>
 *         <td><code>{@link io.github.epi155.pm.lang.Nope#peek(java.lang.Runnable) peek}</code></td>
 *         <td>{@code Runnable}</td>
 *         <td>{@code Nope}</td>
 *     </tr>
 *      <caption>Composition rule</caption>
 * </table>
 * <h2>Conditional</h2>
 * The classes that have their own value (Some and Hope) have two conditional methods which,
 * if there are no errors, allow the value contained to be evaluated
 * <pre>
 * (Hope&lt;A> | Some&lt;A>) ax = ...
 * Some&lt;B> bx = ax{@link io.github.epi155.pm.lang.ChoiceContext#choiceMap() .&lt;B>choiceMap()}
 *     {@link io.github.epi155.pm.lang.ChoiceMapContext#when(java.lang.Object) .when(A)}
 *         {@link io.github.epi155.pm.lang.ChoiceMapWhenContext#map(java.util.function.Function) .map(Function&lt;? super A, ? extends AnyValue&lt;B>>)}
 *     .when(A)
 *         {@link io.github.epi155.pm.lang.ChoiceMapWhenContext#mapOf(java.util.function.Function) .mapOf(Function&lt;? super A, ? extends B>)}
 *     .when(A)
 *         {@link io.github.epi155.pm.lang.ChoiceMapWhenContext#fault(io.github.epi155.pm.lang.CustMsg, java.lang.Object...) .fault(CustMsg, Object...)}
 *     {@link io.github.epi155.pm.lang.ChoiceMapContext#when(boolean) .when(boolean)}
 *         .map(Function&lt;? super A, ? extends AnyValue&lt;B>>)       // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceMapContext#when(java.util.function.Predicate) .when(Predicate&lt;A>)}
 *         .map(Function&lt;? super A, ? extends AnyValue&lt;B>>)       // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceMapContext#whenInstanceOf(java.lang.Class) .whenInstanceOf(Class&lt;C>)}
 *         {@link io.github.epi155.pm.lang.ChoiceMapWhenAsContext#map(java.util.function.Function) .map(Function&lt;? super C, ? extends AnyValue&lt;B>>))}      // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceMapContext#otherwise() .otherwise()}
 *         {@link io.github.epi155.pm.lang.ChoiceMapElseContext#map(java.util.function.Function) .map(Function&lt;? super A, ? extends AnyValue&lt;B>>))}      // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceMapExitContext#end() .end()}; </pre>
 * and
 * <pre>
 * None bx = ax{@link io.github.epi155.pm.lang.ChoiceContext#choice() .choice()}
 *     {@link io.github.epi155.pm.lang.ChoiceValueContext#when(java.lang.Object) .when(A)}
 *         {@link io.github.epi155.pm.lang.ChoiceValueWhenContext#ergo(java.util.function.Function) .ergo(Function&lt;? super A, ? extends ItemStatus>)}
 *     .when(A)
 *         {@link io.github.epi155.pm.lang.ChoiceValueWhenContext#peek(java.util.function.Consumer) .peek(Consumer&lt;? super A>)}
 *     .when(A)
 *         {@link io.github.epi155.pm.lang.ChoiceValueWhenContext#fault(io.github.epi155.pm.lang.CustMsg, java.lang.Object...) .fault(CustMsg, Object...)}
 *     {@link io.github.epi155.pm.lang.ChoiceValueContext#when(boolean) .when(boolean)}
 *         .ergo(Function&lt;? super A, ? extends ItemStatus>)}      // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceValueContext#when(java.util.function.Predicate) .when(Predicate&lt;A>)}
 *         .ergo(Function&lt;? super A, ? extends ItemStatus>)}      // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceValueContext#whenInstanceOf(java.lang.Class) .whenInstanceOf(Class&lt;C>)}
 *         {@link io.github.epi155.pm.lang.ChoiceValueWhenAsContext#ergo(java.util.function.Function) .ergo(Function&lt;? super C, ? extends ItemStatus>)}       // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceValueContext#otherwise() .otherwise()}
 *         {@link io.github.epi155.pm.lang.ChoiceValueElseContext#ergo(java.util.function.Function) .ergo(Function&lt;? super A, ? extends ItemStatus>)}       // etc.
 *     {@link io.github.epi155.pm.lang.ChoiceValueExitContext#end() .end()}; </pre>
 * <h3>Conditional raw</h3>
 * These method structures can also be triggered directly by a value
 * <pre>
 * Some&lt;B> bx = ChoiceContext{@link io.github.epi155.pm.lang.ChoiceContext#choice(java.lang.Object) .&lt;A,B>choiceMap(A)}
 *     ...
 *     {@link io.github.epi155.pm.lang.ChoiceMapExitContext#end() .end()}; </pre>
 * and
 * <pre>
 * None bx = ChoiceContext{@link io.github.epi155.pm.lang.ChoiceContext#choice(java.lang.Object) .&lt;A>choice(A)}
 *     ...
 *     {@link io.github.epi155.pm.lang.ChoiceValueExitContext#end() .end()}; </pre>
 * although an imperative structure might be preferable in this case.
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

