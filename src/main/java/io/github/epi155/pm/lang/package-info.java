/**
 * Utility classes to manage value xor errors
 *
 * <hr>
 * <p>
 * Suppose we have data in format A.
 * The datum must undergo a formal validation, if there are no errors it is possible to decode it in the B format.
 * At this point the data is subjected to a merit check and if there are no errors it can be transformed into the final format C.
 * <p>
 * With a functional approach that follows the imperative flow we could write code like
 * <pre>
 * val bld = Some.&lt;C>builder();
 * formalValidation(a)
 *     .onSuccess(() -> decode(a)
 *         .onSuccess(b -> meritValidation(b)
 *             .onSuccess(() -> translate(b)
 *                 .onSuccess(bld::withValue)
 *                 .onFailure(bld::add))
 *             .onFailure(bld::add))
 *         .onFailure(bld::add))
 *     .onFailure(bld::add);
 * Some&lt;C> sc = bld.build(); </pre>
 * with a more functional approach it becomes
 * <pre>
 * Some&lt;C> sc = formalValidation(a)
 *     .&lt;B>map(() -> decode(a))
 *     .&lt;C>map(b -> meritValidation(b)
 *         .&lt;C>map(() -> translate(b))); </pre>
 *
 * <hr>
 * monitus
 * defectum
 */
package io.github.epi155.pm.lang;