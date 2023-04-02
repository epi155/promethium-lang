package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.stream.Stream;

abstract class PmAnyBuilder extends PmMutableStatus implements ErrorBuilder {
    private static final int J11_LOCATE = 2;

    protected static final int J_LOCATE = J11_LOCATE;
    protected static final int J_CALLER = J_LOCATE + 1;
    protected static final int J_DEEP_CALL = J_LOCATE + 2;


    @Override
    public @NotNull Failure fault(@NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[J_LOCATE], ce, objects);
        add(fail);
        return fail;
    }
    @Override
    public @NotNull Warning alert(@NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val warn = PmWarning.of(stPtr[J_LOCATE], ce, objects);
        add(warn);
        return warn;
    }

    @Override
    public void capture(@NotNull Throwable e) {
        add(PmFailure.of(e));
    }

    @Override
    public void captureHere(@NotNull Throwable e) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        StackTraceElement[] stErr = e.getStackTrace();
        for (int i = 1; i < stErr.length; i++) {
            StackTraceElement ste = stErr[i];
            if (caller.getClassName().equals(ste.getClassName()) && caller.getMethodName().equals(ste.getMethodName())) {
                add(PmFailure.ofException(ste, e));
                return;
            }
        }
        add(PmFailure.ofException(caller, e));
    }

    @Override
    public void captureCaller(@NotNull Throwable e) {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        // 0 - Any (this)
        // 1 - Some/None
        // 2 - caller.capture...
        // 3 - caller of method call capture
        add(PmFailure.ofException(stElements[J_CALLER], e));
    }

    @Override
    public void captureMessage(@NotNull Throwable t, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        String packagePrefix = caller.getClassName().replaceFirst("(^\\w+[.]\\w+)[.].*", "$1");
        add(PmFailure.ofMessage(t, packagePrefix, ce, argv));
    }

    @Override
    public void captureException(@NotNull Throwable t, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[J_LOCATE];
        String packagePrefix = caller.getClassName().replaceFirst("(^\\w+[.]\\w+)[.].*", "$1");
        add(PmFailure.ofException(t, packagePrefix, ce, argv));
    }

    private void captureHere(StackTraceElement[] stPtr, Throwable e, Function<StackTraceElement, Failure> krn) {
        StackTraceElement caller = stPtr[J_LOCATE];
        StackTraceElement[] stErr = e.getStackTrace();
        for (int i = 1; i < stErr.length; i++) {
            StackTraceElement error = stErr[i];
            if (caller.getClassName().equals(error.getClassName()) && caller.getMethodName().equals(error.getMethodName())) {
                add(krn.apply(error));
                return;
            }
        }
        add(krn.apply(caller));
    }

    @Override
    public void captureHereMessage(@NotNull Throwable e, @NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        captureHere(stPtr, e, (StackTraceElement error) -> PmFailure.ofMessage(error, e, ce, objects));
    }

    @Override
    public void captureHereException(@NotNull Throwable e, @NotNull CustMsg ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        captureHere(stPtr, e, (StackTraceElement error) -> PmFailure.ofException(error, e, ce, objects));
    }

    @Override
    public void add(@NotNull ItemStatus any) {
        add(any.signals());
    }

    @Override
    public <T> @NotNull Stream<T> flat(@NotNull Hope<T> hope) {
        if (hope.completeWithoutErrors()) {
            return Stream.of(hope.value());
        } else {
            add(hope.fault());
            return Stream.empty();
        }
    }

    @Override
    public <T> @NotNull Stream<T> flat(@NotNull Some<T> some) {
        if (some.completeSuccess()) {
            return Stream.of(some.value());
        } else {
            add(some.signals());
            if (completeWithErrors())
                return Stream.empty();
            else
                return Stream.of(some.value());
        }
    }
}
