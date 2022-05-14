package com.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collector;

public interface None extends Any, Glitches {
    static @NotNull NoneBuilder builder() {
        return new PmNoneBuilder();
    }

    static @NotNull None failure(@NotNull MsgError ce, Object... objects) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        val fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, objects);
        return new PmNone(Collections.singletonList(fail));
    }

    static @NotNull None capture(@NotNull Throwable t) {
        return new PmNone(Collections.singletonList(PmFailure.of(t)));
    }

    static @NotNull None of(@NotNull Failure fault) {
        return new PmNone(Collections.singletonList(fault));
    }

    static @NotNull Collector<AnyOne, NoneBuilder, None> collect() {
        return new PmCollector();
    }

    /**
     * Set the action on success
     * <p>
     * In the event of an error, the action is not performed.
     * </p>
     *
     * @param successAction action to be taken if successful
     * @return Glitches to set the action on failure
     * @see Glitches#onFailure(Consumer)
     */
    @NotNull Glitches onSuccess(Runnable successAction);
}
