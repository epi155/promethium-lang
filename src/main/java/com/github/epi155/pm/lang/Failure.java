package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Error message
 */
public interface Failure {
    /**
     * Java exception code value.
     */
    String JAVA_EXCEPTION_CODE = "999J";

    /**
     * static {@link FailureBuilder}
     *
     * @return {@link FailureBuilder} instance
     */
    static @NotNull FailureBuilder builder() {
        return new PmFailureBuilder();
    }

    /**
     * Static error message constructor
     *
     * @param ce      custom error
     * @param objects error parameters
     * @return {@link Failure} instance
     */
    static @NotNull Failure of(@NotNull MsgError ce, Object... objects) {
        String code = ce.code();
        String message = ce.message(objects);
        return new PmFailure(code, ce, message);
    }

    /**
     * Static error message constructor
     *
     * @param ex exception
     * @return {@link Failure} instance
     */
    static @NotNull Failure of(@NotNull Throwable ex) {
        return PmFailure.of(ex);
    }

    /**
     * Retrieves the value of a property associated with Failure
     *
     * @param key name that identifies the property
     * @param cls required class
     * @param <T> class type
     * @return property value, or <i> null </i> if there is no property with that name and that class
     */
    <T> T getProperty(@NotNull String key, @NotNull Class<T> cls);

    /**
     * Retrieves the value of a property associated with Failure
     *
     * @param key          name that identifies the property
     * @param defaultValue value to return if the property is not defined
     * @param <T>   property type
     * @return property value, or <i> defaultValue </i> if there is no property with that name
     */
    <T> T getProperty(@NotNull String key, @NotNull T defaultValue);

    /**
     * Retrieves the value of a property associated with Failure as {@link String}
     *
     * @param key name that identifies the property
     * @return property value
     */
    String getStrProperty(@NotNull String key);

    /**
     * Retrieves the value of a property associated with Failure as {@link String}
     *
     * @param key          name that identifies the property
     * @param defaultValue value to return if the property is not defined
     * @return property value
     */
    String getStrProperty(@NotNull String key, @NotNull String defaultValue);

    /**
     * Store the value of a property associated with Failure
     *
     * @param key   name that identifies the property
     * @param value value to be saved
     * @param <T>   value type
     * @return {@link Failure} instance
     */
    <T> @NotNull Failure setProperty(String key, T value);

    /**
     * Performs the action specified for each property
     *
     * @param action action to perform
     */
    void forEach(BiConsumer<String, ? super Object> action);

    /**
     * Returns the error message
     *
     * @return error message
     */
    @NotNull String message();

    /**
     * Returns the error code
     *
     * @return error code
     */
    @NotNull String code();

    /**
     * Returns the status code
     *
     * @return status code
     */
    int status();

    /**
     * Stack position where exception or failure has happened, if any.
     * The format is: class.method(file:line)
     *
     * @return failure position
     */
    @Nullable String place();

    /**
     * Return the stack trace element, if any
     *
     * @return stack trace element where failure has happened
     */
    @Nullable StackTraceElement stackTraceElement();

}
