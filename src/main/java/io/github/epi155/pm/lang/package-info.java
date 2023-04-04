/**
 * Utility classes to manage value xor errors
 * <hr>
 * <p>The package contains a set of classes that can be used in the event that an operation can return a value or one or
 * more errors (or warnings).
 * The first class to consider is {@link io.github.epi155.pm.lang.Some Some&lt;T>}, in its simplest form it can be instantiated with
 * <pre>
 * Some.of(T value);                               // final value (and no warnings)
 * Some.failure(CustMsg ce, Object... argv);       // single error message </pre>
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
 * </ul>
 *
 * <p>Usually {@link io.github.epi155.pm.lang.Some Some&lt;T>} and {@link io.github.epi155.pm.lang.None None}
 * are used through a builder which allows to accumulate many errors (warnings)
 * <pre>
 * val bld = Some.&lt;T&gt;builder();
 * bld.fault(CustMsg ce, Object... argv);      // add single error message
 * bld.alert(CustMsg ce, Object... argv);      // add single warning message
 * bld.capture(Throwable t);                   // add error from Exception
 * bld.value(T value);                         // set final value
 * Some&lt;T&gt; some = bld.build(); </pre>
 * The outcome can be evaluated imperatively
 * <pre>
 * if (some.completeWithoutErrors()) {
 *     T v = some.value();                     // final value
 *     // ... action on value
 * } else {
 *     Collection&lt;? extends Signal&gt; e = some.signals();   // errors/warings collection
 *     // ... action on errors (and warnings)
 * } </pre>
 * or functionally
 * <pre>
 * some
 *     .onSuccess(v -> { ... })        // ... action on value (ignoring warnings)
 *     .onFailure(e -> { ... });       // ... action on errors (and warnings) </pre>
 *
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
 *     .&lt;B>map(() -> decode(a))                 // Some&lt;B>
 *     .&lt;C>map(b -> meritValidation(b)          // None
 *         .&lt;C>map(() -> translate(b)));        // Some&lt;C> </pre>
 * or, if we are not interested in value, but only in evaluating errors
 * <pre>
 * None nc = formalValidation(a)                // None
 *     .ergo(() -> decode(a)                    // Some&lt;B>
 *         .ergo(b -> meritValidation(b)        // None
 *             .ergo(() -> translate(b))));     // Some&lt;C>
 * </pre>
 */
package io.github.epi155.pm.lang;

