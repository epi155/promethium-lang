package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Extension for class with payload
 *
 * @param <T> payload type
 */
public interface AnyValue<T> extends AnyItem {
    /**
     * Returns the value
     *
     * @return value
     */
    @NotNull T value();

    /**
     * Loop with expansion function.
     *
     * @param mul expand function value to {@link AnyValue} collection
     * @param fcn consumer function to {@link AnyItem}
     * @param <U> consumed type
     * @return {@link None} instance with collected errors, if any
     */
    default @NotNull <U> None forAnyValues(
            Function<? super T, Iterable<? extends AnyValue<U>>> mul,
            Function<? super U, ? extends AnyItem> fcn) {
        if (isSuccess()) {
            val bld = None.builder();
            val iter = mul.apply(value());
            iter.forEach(it -> {
                if (it.isSuccess()) {
                    bld.add(fcn.apply(it.value()));
                } else {
                    bld.add(it);
                }
            });
            return bld.build();
        } else {
            return new PmNone(errors());
        }
    }

    /**
     * Loop with expansion function.
     *
     * @param mul expand function value to value collection
     * @param fcn consumer function to {@link AnyItem}
     * @param <U> consumed type
     * @return {@link None} instance with collected errors, if any
     */
    default @NotNull <U> None forValues(
            Function<? super T, Iterable<? extends U>> mul,
            Function<? super U, ? extends AnyItem> fcn) {
        if (isSuccess()) {
            val bld = None.builder();
            val iter = mul.apply(value());
            iter.forEach(it -> bld.add(fcn.apply(it)));
            return bld.build();
        } else {
            return new PmNone(errors());
        }
    }

}
