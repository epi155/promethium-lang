package io.github.epi155.pm.lang;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor(access = lombok.AccessLevel.PACKAGE)
class PmSignal implements Signal{
    public static final int JAVA_EXCEPTION_STATUS = 500;

    @NotNull
    protected final String theCode;
    protected final int theStatus;
    @NotNull
    protected final String theMessage;
    protected final StackTraceElement theStackTraceElement;
    protected final Map<String, Object> properties = new HashMap<>();

    protected static @NotNull String lineOf(@NotNull StackTraceElement stackElem) {
        String className = stackElem.getClassName();
        String methodName = stackElem.getMethodName();
        String fileName = stackElem.getFileName();
        int lineNumber = stackElem.getLineNumber();
        return className + "->" + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    protected static StackTraceElement guessLine(Throwable ex, @NotNull StackTraceElement caller) {
        String packagePrefix = caller.getClassName().replaceFirst("(^\\w+[.]\\w+)[.].*", "$1");
        return scan(ex, packagePrefix);
    }
    protected static StackTraceElement caller() {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return stPtr[PmAnyBuilder.J_DEEP_CALL];
    }

    protected static @Nullable StackTraceElement scan(@NotNull Throwable t, String packagePrefix) {
        StackTraceElement[] stackElems = t.getStackTrace();
        for (StackTraceElement stackElem : stackElems) {
            String className = stackElem.getClassName();
            if (className.startsWith(packagePrefix)) {
                return stackElem;
            }
        }
        return null;
    }


    //___ delegate Properties___
    @Override
    public String getStrProperty(@NotNull String key) {
        val value = properties.get(key);
        return value instanceof String ? (String) value : null;
    }

    @Override
    public String getStrProperty(@NotNull String key, @NotNull String defaultValue) {
        val value = properties.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }

    @Override
    public <T> T getProperty(@NotNull String key, @NotNull Class<T> cls) {
        val value = properties.get(key);
        if (value == null) return null;
        if (cls.isAssignableFrom(value.getClass())) {
            return cls.cast(value);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        val sw = new StringWriter();
        try(val pw = new PrintWriter(sw)) {
            pw.printf("- code: %s%n", theCode);
            pw.printf("  status: %s%n",theStatus);
            pw.printf("  message: %s%n", theMessage);
            ofNullable(place()).ifPresent(place -> pw.printf("  place: %s%n", place)) ;
            if (!properties.isEmpty()) {
                pw.printf("  properties:%n");
                properties.forEach((key, value) -> pw.printf("    %s: %s%n", key, value.toString()));
            }
        }
        return sw.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(@NotNull String key, @NotNull T defaultValue) {
        val value = properties.get(key);
        if (value == null) return defaultValue;
        val cls = defaultValue.getClass();
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
    public String place() {
        return theStackTraceElement == null ? null : lineOf(theStackTraceElement);
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

    @Override
    public StackTraceElement stackTraceElement() {
        return theStackTraceElement;
    }

}
