package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.stream.Stream;

class PmNoneBuilder extends PmAnyBuilder implements NoneBuilder {
    @Override
    public @NotNull None build() {
        return new PmNone(errors());
    }

    @Override
    public @NotNull <U> NoneBuilder forEachOf(
        @NotNull Iterable<? extends U> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        iterable.forEach(u -> add(fcn.apply(u)));
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachOfParallel(
        int maxThread,
        @NotNull Iterable<? extends U> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        if (maxThread < 1)
            throw new java.lang.IllegalArgumentException();
        val s = new Semaphore(maxThread);
        val p = new Phaser(1);
        iterable.forEach(u -> {
            try {
                p.register();
                s.acquire();
                new Thread(() -> {
                    try {
                        add(fcn.apply(u));
                    } finally {
                        s.release();
                        p.arriveAndDeregister();
                    }
                }).start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachOfParallel(
        @NotNull ExecutorService executorService,
        @NotNull Iterable<? extends U> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        val p = new Phaser(1);
        iterable.forEach(u -> {
            p.register();
            executorService.submit(() -> {
                try {
                    add(fcn.apply(u));
                } finally {
                    p.arriveAndDeregister();
                }
            });
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachOfParallel(
        int maxThread,
        @NotNull Stream<? extends U> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        if (maxThread < 1)
            throw new java.lang.IllegalArgumentException();
        val s = new Semaphore(maxThread);
        val p = new Phaser(1);
        stream.forEach(u -> {
            try {
                p.register();
                s.acquire();
                new Thread(() -> {
                    try {
                        add(fcn.apply(u));
                    } finally {
                        s.release();
                        p.arriveAndDeregister();
                    }
                }).start();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachOfParallel(
        @NotNull ExecutorService executorService,
        @NotNull Stream<? extends U> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        val p = new Phaser(1);
        stream.forEach(u -> {
            p.register();
            executorService.submit(() -> {
                try {
                    add(fcn.apply(u));
                } finally {
                    p.arriveAndDeregister();
                }
            });
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachOf(
        @NotNull Stream<? extends U> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        stream.forEach(u -> add(fcn.apply(u)));
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEach(
        @NotNull Iterable<? extends AnyValue<U>> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        iterable.forEach(u -> {
            if (isSuccess())
                add(fcn.apply(u.value()));
            else
                add(u.errors());
        });
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEach(
        @NotNull Stream<? extends AnyValue<U>> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        stream.forEach(u -> {
            if (isSuccess())
                add(fcn.apply(u.value()));
            else
                add(u.errors());
        });
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachParallel(
        int maxThread,
        @NotNull Iterable<? extends AnyValue<U>> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        if (maxThread < 1)
            throw new java.lang.IllegalArgumentException();
        val s = new Semaphore(maxThread);
        val p = new Phaser(1);
        iterable.forEach(u -> {
            if (u.isSuccess()) {
                try {
                    p.register();
                    s.acquire();
                    new Thread(() -> {
                        try {
                            add(fcn.apply(u.value()));
                        } finally {
                            s.release();
                            p.arriveAndDeregister();
                        }
                    }).start();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else
                add(u.errors());
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachParallel(
        int maxThread,
        @NotNull Stream<? extends AnyValue<U>> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        if (maxThread < 1)
            throw new java.lang.IllegalArgumentException();
        val s = new Semaphore(maxThread);
        val p = new Phaser(1);
        stream.forEach(u -> {
            if (u.isSuccess()) {
                try {
                    p.register();
                    s.acquire();
                    new Thread(() -> {
                        try {
                            add(fcn.apply(u.value()));
                        } finally {
                            s.release();
                            p.arriveAndDeregister();
                        }
                    }).start();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else
                add(u.errors());
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachParallel(
        @NotNull ExecutorService executorService,
        @NotNull Iterable<? extends AnyValue<U>> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        val p = new Phaser(1);
        iterable.forEach(u -> {
            if (isSuccess()) {
                p.register();
                executorService.submit(() -> {
                    try {
                        add(fcn.apply(u.value()));
                    } finally {
                        p.arriveAndDeregister();
                    }
                });
            } else
                add(u.errors());
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEachParallel(
        @NotNull ExecutorService executorService,
        @NotNull Stream<? extends AnyValue<U>> stream,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        val p = new Phaser(1);
        stream.forEach(u -> {
            if (isSuccess()) {
                p.register();
                executorService.submit(() -> {
                    try {
                        add(fcn.apply(u.value()));
                    } finally {
                        p.arriveAndDeregister();
                    }
                });
            } else
                add(u.errors());
        });
        p.arriveAndAwaitAdvance();
        return this;
    }

}
