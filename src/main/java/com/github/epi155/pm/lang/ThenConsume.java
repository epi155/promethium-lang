package com.github.epi155.pm.lang;

import java.util.function.Consumer;

/**
 * Consumer response
 *
 * @param <U> consumer type
 */
public interface ThenConsume<U> {
    /**
     * Action on value.
     *
     * <pre>
     *     a = fun1(..)
     *     fun2(a)
     * </pre>
     * became
     * <pre>
     *     fun1(..).andThen(a -> fun2(a))
     * </pre>
     *
     * @param action action
     */
    void andThen(Consumer<U> action);
}