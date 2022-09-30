package io.github.epi155.pm.lang;

import java.util.function.Function;

/**
 * interface to iterate on fallible functions collecting any errors
 *
 * @param <A> itarable value type
 */
public interface LoopValue<A> {
    /**
     * It cycles on values by collecting errors
     *
     * @param fcn action on value returning error payload
     * @return {@link None} instance with the errors collected
     */
    None forEach(Function<? super A, ? extends AnyItem> fcn);
}
