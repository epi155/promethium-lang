package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Optional.ofNullable;

abstract class PmSignal implements Signal {
    @NotNull
    protected final String theCode;
    protected final int theStatus;
    @NotNull
    protected final String theMessage;
    protected final Map<String, Object> properties;

    protected PmSignal(@NotNull String theCode, int theStatus, @NotNull String theMessage) {
        this(new HashMap<>(), theCode, theStatus, theMessage);
    }

    protected PmSignal(@NotNull Map<String, Object> properties, @NotNull String theCode, int theStatus, @NotNull String theMessage) {
        this.theCode = theCode;
        this.theStatus = theStatus;
        this.theMessage = theMessage;
        this.properties = properties;
    }

    protected static @NotNull String lineOf(@NotNull StackTraceElement stackElem) {
        String className = stackElem.getClassName();
        String methodName = stackElem.getMethodName();
        String fileName = stackElem.getFileName();
        int lineNumber = stackElem.getLineNumber();
        return className + "->" + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    //___ delegate Properties___
    @Override
    public String getStrProperty(@NotNull String key) {
        Object value = properties.get(key);
        return value instanceof String ? (String) value : null;
    }

    @Override
    public String getStrProperty(@NotNull String key, @NotNull String defaultValue) {
        Object value = properties.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    @Override
    public <T> T getProperty(@NotNull String key, @NotNull Class<T> cls) {
        Object value = properties.get(key);
        if (value == null) return null;
        if (cls.isAssignableFrom(value.getClass())) {
            return cls.cast(value);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            pw.printf("{ code: \"%s\", ", theCode);
            pw.printf("status: %d, ", theStatus);
            pw.printf("message: \"%s\"", theMessage);
            ofNullable(place()).ifPresent(place -> pw.printf(", place: \"%s\"", place));
            if (!properties.isEmpty()) {
                pw.printf(", properties: { ");
                Set<Map.Entry<String, Object>> entries = properties.entrySet();
                boolean append = false;
                for (Map.Entry<String, Object> entry : entries) {
                    if (append) pw.printf(", ");
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        pw.printf("%s: \"%s\"", entry.getKey(), value);
                    } else {
                        pw.printf("%s: %s", entry.getKey(), value.toString());
                    }
                    append = true;
                }
                pw.printf(" }");    // end property
            }
            pw.printf(" }");    // end object
        }
        return sw.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(@NotNull String key, @NotNull T defaultValue) {
        Object value = properties.get(key);
        if (value == null) return defaultValue;
        Class<?> cls = defaultValue.getClass();
        if (cls.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public <T> @NotNull Signal setProperty(String key, T value) {
        properties.put(key, value);
        return this;
    }

    /*
    protected factory are used by AnyBuilder
     */

    @Override
    public void forEach(BiConsumer<String, ? super Object> action) {
        properties.forEach(action);
    }

    @Override
    public @NotNull String code() {
        return theCode;
    }

    @Override
    public int status() {
        return theStatus;
    }

    @Override
    public @NotNull String message() {
        return theMessage;
    }

}
