package io.github.epi155.pm.lang;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@RequiredArgsConstructor(access = lombok.AccessLevel.PACKAGE)
class PmFailure implements Failure {
    public static final int JAVA_EXCEPTION_STATUS = 500;

    @NotNull
    private final String theCode;
    private final int theStatus;
    @NotNull
    private final String theMessage;
    private final StackTraceElement theStackTraceElement;
    private final Map<String, Object> properties = new HashMap<>();

    protected PmFailure(String code, MsgError ce, String text) {
        this(code, statusOf(ce), text, null);
    }

    protected static @NotNull Failure ofException(@NotNull StackTraceElement ste, @NotNull Throwable t) {
        if (t instanceof FailureException) {
            FailureException fe = (FailureException) t;
            String text = t.getMessage();
            return new PmFailure(fe.getCode(), fe.getStatus(), text, ste);
        } else if (t instanceof FaultException) {
            return copy(((FaultException) t).fault, ste);
        } else {
            String text = t.toString();
            return new PmFailure(JAVA_EXCEPTION_CODE, JAVA_EXCEPTION_STATUS, text, ste);
        }
    }

    private static @NotNull Failure copy(@NotNull Failure source, @NotNull StackTraceElement ste) {
        PmFailure target = new PmFailure(source.code(), source.status(), source.message(), ste);
        source.forEach(target::setProperty);
        return target;
    }

    protected static @NotNull String lineOf(@NotNull StackTraceElement stackElem) {
        String className = stackElem.getClassName();
        String methodName = stackElem.getMethodName();
        String fileName = stackElem.getFileName();
        int lineNumber = stackElem.getLineNumber();
        return className + "->" + methodName + "(" + fileName + ":" + lineNumber + ")";
    }

    protected static @NotNull Failure of(@NotNull Throwable ex) {
        StackTraceElement ste = guessLine(ex, caller());
        if (ex instanceof FailureException) {
            FailureException fe = (FailureException) ex;
            String text = ex.getMessage();
            return new PmFailure(fe.getCode(), fe.getStatus(), text, fe.getSte() == null ? ste : fe.getSte());
        } else if (ex instanceof FaultException) {
            return ((FaultException) ex).fault;
        } else {
            String text = ex.toString();
            return new PmFailure(JAVA_EXCEPTION_CODE, JAVA_EXCEPTION_STATUS, text, ste);
        }
    }

    protected static StackTraceElement guessLine(Throwable ex, @NotNull StackTraceElement caller) {
        String packagePrefix = caller.getClassName().replaceFirst("(^\\w+[.]\\w+)[.].*", "$1");
        return scan(ex, packagePrefix);
    }

    private static StackTraceElement caller() {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        return stPtr[PmAnyBuilder.J_DEEP_CALL];
    }

    private static @Nullable StackTraceElement scan(@NotNull Throwable t, String packagePrefix) {
        StackTraceElement[] stackElems = t.getStackTrace();
        if (stackElems != null) {
            for (StackTraceElement stackElem : stackElems) {
                String className = stackElem.getClassName();
                if (className.startsWith(packagePrefix)) {
                    return stackElem;
                }
            }
        }
        return null;
    }

    protected static @NotNull Failure ofMessage(@NotNull Throwable t, String packagePrefix, @NotNull MsgError ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t.getMessage();
        StackTraceElement ste = scan(t, packagePrefix);
        return new PmFailure(code, statusOf(ce), text, ste);
    }

    protected static @NotNull Failure ofException(@NotNull Throwable t, String packagePrefix, @NotNull MsgError ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t;
        StackTraceElement ste = scan(t, packagePrefix);
        return new PmFailure(code, statusOf(ce), text, ste);
    }

    protected static @NotNull Failure ofMessage(StackTraceElement ste, @NotNull Throwable t, @NotNull MsgError ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t.getMessage();
        return new PmFailure(code, statusOf(ce), text, ste);
    }

    protected static @NotNull Failure ofException(StackTraceElement ste, @NotNull Throwable t, @NotNull MsgError ce, Object[] objects) {
        String code = ce.code();
        String text = ce.message(objects) + " >> " + t;
        return new PmFailure(code, statusOf(ce), text, ste);
    }

    static int statusOf(MsgError ce) {
        if (ce instanceof ErrorPicker) {
            return ((ErrorPicker) ce).statusCode();
        }
        return JAVA_EXCEPTION_STATUS;
    }

    protected static @NotNull Failure of(StackTraceElement ste, @NotNull MsgError ce, Object... objects) {
        String code = ce.code();
        String text = ce.message(objects);
        return new PmFailure(code, statusOf(ce), text, ste);
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
        StringBuilder sb = new StringBuilder();
        sb.append("class Failure {\n");

        sb.append("    code: ").append(theCode).append("\n");
        sb.append("    status: ").append(theStatus).append("\n");
        sb.append("    message: ").append(theMessage).append("\n");
        sb.append("    place: ").append(place()).append("\n");
        if (!properties.isEmpty()) {
            sb.append("    properties {\n");
            properties.forEach((key, value) -> sb
                .append("        ").append(key).append(": ").append(value).append("\n"));
            sb.append("    }");
        }
        sb.append("}");
        return sb.toString();
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
    public <T> @NotNull Failure setProperty(String key, T value) {
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
