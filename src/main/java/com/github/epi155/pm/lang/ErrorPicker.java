package com.github.epi155.pm.lang;

/**
 * Custom error message extension with another code (usually http status code)
 */
public interface ErrorPicker extends MsgError {
    /**
     * returns the status code.
     *
     * @return status code
     */
    int statusCode();
}
