package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Utility interface for class carrying only error(s) and no value
 */
public interface OnlyError extends ItemStatus {
    /**
     * If there are no errors, the supplier is called,
     * if this ends with errors, these errors are returned.
     * In the presence of errors, the supplier is not called, and the initial errors are returned
     *
     * <p>
     * The method allows to compose the None with a Some (or a Hope).
     * Using an imperative outcome evaluation we would have
     * <pre>
     *      None na = compute();
     *      Some&lt;B&gt; sb;
     *      if (na.completeWithoutErrors()) {
     *          sb = x2sb();    // Some&lt;B&gt; x2sb();
     *      } else {
     *          sb = Some.failure(na);
     *      } </pre>
     * The method simplifies it to
     * <pre>
     *      Some&lt;B&gt; kb = compute().map(this::x2sb); </pre>
     * in addition, the method also propagates any warnings
     *
     * @param fcn producer {@link AnyValue}
     * @param <R> {@link Some} type
     * @return {@link Some} instance
     */
    @NotNull <R> Some<R> map(@NotNull Supplier<? extends AnyValue<R>> fcn);

    /**
     * In the presence of errors, the producer is not called, and the initial errors are returned
     *
     * @param fcn producer value
     * @param <R> {@link Some} type
     * @return {@link Some} instance
     */
    @NotNull <R> Some<R> mapOf(@NotNull Supplier<? extends R> fcn);

    /**
     * If there are no errors, the supplier is called,
     * if this ends with errors, these errors are returned.
     * In the presence of errors, the supplier is not called, and the initial errors are returned
     *
     * @param fcn producer {@link ItemStatus}
     * @return {@link None} instance,
     */
    @NotNull None ergo(@NotNull Supplier<? extends ItemStatus> fcn);

    /**
     * If there is no error, the action is performed.
     * In any case, the initial class is returned.
     *
     * @param action action executed if there are no errors
     * @return {@link OnlyError} instance, with original error, if any
     */
    @NotNull OnlyError peek(@NotNull Runnable action);

}
