package io.github.epi155.pm.lang;

import org.jetbrains.annotations.ApiStatus;

/**
 * Supplier with exception
 *
 * @param <U> type of value provided
 */
@ApiStatus.Experimental
public
interface CheckSupplier<U> {
    /**
     * value getter
     *
     * @return value
     * @throws Exception error getting value
     */
    U get() throws Exception;
}
