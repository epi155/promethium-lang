package io.github.epi155.pm.lang;

import java.util.function.Function;

/**
 * interface to iterate on fallible functions and collects any errors
 *
 * @param <A> iterable value type
 */
public interface LoopNone<A> {
    /**
     * It loops over the values and collects errors
     *
     * @param fcn action on value returning error payload
     * @return {@link None} instance with the errors collected
     */
    None forEach(Function<? super A, ? extends AnyItem> fcn);
}
