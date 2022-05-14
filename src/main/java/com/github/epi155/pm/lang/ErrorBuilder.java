package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public interface ErrorBuilder extends AnyOne {
    void add(@NotNull CheckedRunnable runnable);

    void add(@NotNull AnyOne any);

    void add(@NotNull Failure failure);

    void add(@NotNull Stream<Failure> failureStream);

    void capture(@NotNull Throwable e);

    void captureCaller(@NotNull Throwable e);

    void captureException(@NotNull Throwable e);

    void captureException(@NotNull Throwable e, @NotNull MsgError ce, Object... objects);

    void captureHere(@NotNull Throwable e);

    void captureHereException(@NotNull Throwable e, @NotNull MsgError ce, Object... objects);

    void captureHereMessage(@NotNull Throwable e, @NotNull MsgError ce, Object... objects);

    void captureMessage(@NotNull Throwable e, @NotNull MsgError ce, Object... objects);

    int count();

    @NotNull Failure fault(@NotNull MsgError ce, Object... objects);

    <T> @NotNull Stream<T> flat(@NotNull Hope<T> hope);

    <T> @NotNull Stream<T> flat(@NotNull Some<T> some);

    @NotNull ErrorBuilder join(@NotNull CheckedRunnable runnable);

    @NotNull Stream<Failure> stream();
}
