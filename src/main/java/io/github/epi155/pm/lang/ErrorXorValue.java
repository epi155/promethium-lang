package io.github.epi155.pm.lang;

/**
 * interface to indicate the exclusive presence of a value or an error
 *
 * @param <T> value type
 */
public interface ErrorXorValue<T> extends SingleError, AnyValue<T> {
}
