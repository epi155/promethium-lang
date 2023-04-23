package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

abstract class PmAnyBuilder extends PmMutableStatus implements ErrorBuilder {
    private static final int J11_LOCATE = 2;

    protected static final int J_LOCATE = J11_LOCATE;

    @Override
    public @NotNull Failure fault(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[J_LOCATE], ce, argv);
        add(fail);
        return fail;
    }

    @Override
    public @NotNull Warning alert(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val warn = PmWarning.of(stPtr[J_LOCATE], ce, argv);
        add(warn);
        return warn;
    }

    @Override
    public void capture(@NotNull Throwable t) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stPtr[PmAnyBuilder.J_LOCATE];
        add(PmTrouble.of(t, caller));
    }

    @Override
    public void add(@NotNull ItemStatus any) {
        add(any.signals());
    }
}
