package io.github.epi155.pm.lang;

import java.util.function.Function;

/**
 * interface to iterate on fallible functions and collects any errors
 *
 * @param <A> iterable value type
 */
public interface LoopBuilder<A> {
    /**
     * It cycles on values and collects errors
     *
     * @param fcn action on value returning error payload
     * @return {@link NoneBuilder} instance with the errors collected
     */
    NoneBuilder forEach(Function<? super A, ? extends AnyItem> fcn);
}
