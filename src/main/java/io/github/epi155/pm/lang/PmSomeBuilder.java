package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class PmSomeBuilder<T> extends PmAnyBuilder implements SomeBuilder<T> {
    private T value;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private StackTraceElement setter;
    private boolean multipleSet = false;

    @Override
    public void value(@NotNull T value) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        valueAt(caller, value);
    }

    private void valueAt(StackTraceElement caller, T value) {
        if (completeWithErrors()) {
            @NotNull Warning warn = PmWarning.of(caller, EnumMessage.ERR_BLD);
            add(warn);
        } else if (value == null) {
            @NotNull Failure fail = PmFailure.of(caller, EnumMessage.NIL_ARG);
            add(fail);
        } else if (value instanceof Signal) {
            @NotNull Failure fail = PmFailure.of(caller, EnumMessage.ILL_ARG);
            add(fail);
            add((Signal) value);
        } else {
            try {
                lock.writeLock().lock();
                if (this.value != null) {
                    @NotNull Failure fail = PmFailure.of(caller, EnumMessage.DBL_SET, PmSignal.lineOf(setter));
                    add(fail);
                    multipleSet = true;
                } else {
                    this.setter = caller;
                    this.value = value;
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @NoBuiltInCapture
    public @NotNull Some<T> build() {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        return buildAt(caller);
    }

    @NoBuiltInCapture
    private Some<T> buildAt(StackTraceElement caller) {
        if (completeWithErrors()) {
            if (value != null && !multipleSet) {
                @NotNull Warning warn = PmWarning.of(caller, EnumMessage.OVR_BLD, PmSignal.lineOf(setter));
                add(warn);
            }
            return new PmSome<>(signals());
        } else if (value != null) {
            return new PmSome<>(value, signals());
        } else {
            @NotNull Failure fail = PmFailure.of(caller, EnumMessage.NIL_BLD);
            add(fail);
            return new PmSome<>(signals());
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull Some<T> buildWithValue(@NotNull T value) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        valueAt(caller, value);
        return build();
    }
}
