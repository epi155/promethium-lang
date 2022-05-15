package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

abstract class PmAnyBuilder implements ErrorBuilder {
    private static final int J11_LOCATE = 2;

    protected static final int J_LOCATE = J11_LOCATE;
    protected static final int J_CALLER = J_LOCATE + 1;
    protected static final int J_DEEP_CALL = J_LOCATE + 2;

    private final Queue<Failure> errors = new ConcurrentLinkedQueue<>();

    @Override
    public @NotNull Failure fault(@NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[J_LOCATE], ce, objects);
        this.errors.add(fail);
        return fail;
    }

    @Override
    public void capture(@NotNull Throwable e) {
        errors.add(PmFailure.of(e));
    }
    @Override
    public void captureException(@NotNull Throwable e) {
        errors.add(PmFailure.of(e));
    }

    @Override
    public void captureHere(@NotNull Throwable e) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        StackTraceElement[] stErr = e.getStackTrace();
        for (int i = 1; i < stErr.length; i++) {
            StackTraceElement ste = stErr[i];
            if (caller.getClassName().equals(ste.getClassName()) && caller.getMethodName().equals(ste.getMethodName())) {
                errors.add(PmFailure.ofException(ste, e));
                return;
            }
        }
        errors.add(PmFailure.ofException(caller, e));
    }

    @Override
    public void captureCaller(@NotNull Throwable e) {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        // 0 - Any (this)
        // 1 - Some/None
        // 2 - caller.capture...
        // 3 - caller of method call capture
        errors.add(PmFailure.ofException(stElements[J_CALLER], e));
    }

    @Override
    public void captureMessage(@NotNull Throwable t, @NotNull MsgError ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        String packagePrefix = caller.getClassName().replaceFirst("(^\\w+[.]\\w+)[.].*", "$1");
        errors.add(PmFailure.ofMessage(t, packagePrefix, ce, argv));
    }

    @Override
    public void captureException(@NotNull Throwable t, @NotNull MsgError ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        String packagePrefix = caller.getClassName().replaceFirst("(^\\w+[.]\\w+)[.].*", "$1");
        errors.add(PmFailure.ofException(t, packagePrefix, ce, argv));
    }

    @Override
    public void captureHereMessage(@NotNull Throwable e, @NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        StackTraceElement[] stErr = e.getStackTrace();
        for (int i = 1; i < stErr.length; i++) {
            StackTraceElement error = stErr[i];
            if (caller.getClassName().equals(error.getClassName()) && caller.getMethodName().equals(error.getMethodName())) {
                errors.add(PmFailure.ofMessage(error, e, ce, objects));
                return;
            }
        }
        errors.add(PmFailure.ofMessage(caller, e, ce, objects));
    }

    @Override
    public void captureHereException(@NotNull Throwable e, @NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        StackTraceElement[] stErr = e.getStackTrace();
        for (int i = 1; i < stErr.length; i++) {
            StackTraceElement error = stErr[i];
            if (caller.getClassName().equals(error.getClassName()) && caller.getMethodName().equals(error.getMethodName())) {
                errors.add(PmFailure.ofException(error, e, ce, objects));
                return;
            }
        }
        errors.add(PmFailure.ofException(caller, e, ce, objects));
    }

    @Override
    public boolean isSuccess() {
        return errors.isEmpty();
    }

    public @NotNull Collection<Failure> errors() {
        return errors;
    }

    protected void capture(StackTraceElement ste, Throwable t) {
        errors.add(PmFailure.ofException(ste, t));
    }

    @Override
    public void add(@NotNull AnyOne any) {
        any.errors().forEach(this::add);
    }

    @Override
    public void add(@NotNull Stream<Failure> failureStream) {
        failureStream.forEach(errors::add);
    }

    @Override
    public void add(@NotNull CheckedRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            capture(e);
        }
    }

    @Override
    public void add(@NotNull Failure failure) {
        errors.add(failure);
    }


    @Override
    public <T> @NotNull Stream<T> flat(@NotNull Hope<T> hope) {
        if (hope.isSuccess()) {
            return Stream.of(hope.value());
        } else {
            add(hope.fault());
            return Stream.empty();
        }
    }

    @Override
    public <T> @NotNull Stream<T> flat(@NotNull Some<T> some) {
        if (some.isSuccess()) {
            return Stream.of(some.value());
        } else {
            add(some.errors().stream());
            return Stream.empty();
        }
    }
}
