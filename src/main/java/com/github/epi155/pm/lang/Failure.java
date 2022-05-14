package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public interface Failure {
    String JAVA_EXCEPTION_CODE = "999J";

    static @NotNull FailureBuilder builder() {
        return new PmFailureBuilder();
    }

    static @NotNull Failure of(@NotNull MsgError ce, Object... objects) {
        String code = ce.code();
        String message = ce.message(objects);
        return new PmFailure(code, ce, message);
    }

    static @NotNull Failure of(@NotNull Throwable ex) {
        return PmFailure.of(ex);
    }

    /**
     * Retrieves the value of a property associated with Failure
     *
     * @param key name that identifies the property
     * @return property value, or <i> null </i> if there is no property with that name
     */
    <T> T getProperty(@NotNull String key, @NotNull Class<T> cls);

    /**
     * Retrieves the value of a property associated with Failure
     *
     * @param key          name that identifies the property
     * @param defaultValue value to return if the property is not defined
     * @return property value, or <i> defaultValue </i> if there is no property with that name
     */
    <T> T getProperty(@NotNull String key, @NotNull T defaultValue);

    String getStrProperty(@NotNull String key);

    String getStrProperty(@NotNull String key, @NotNull String defaultValue);

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

    int status();

    @Nullable String place();

    @Nullable StackTraceElement stackTraceElement();

}
