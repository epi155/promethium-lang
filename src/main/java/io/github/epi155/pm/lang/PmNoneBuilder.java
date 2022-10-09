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
    public @NotNull <U> LoopBuilderConsumer<U> iterable(@NotNull Iterable<? extends AnyValue<U>> iterable) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                iterable.forEach(u -> {
                    if (isSuccess())
                        add(fcn.apply(u.value()));
                    else
                        add(u.errors());
                });
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
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
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                iterable.forEach(u -> {
                    if (isSuccess()) {
                        p.register();
                        executor.submit(() -> {
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
                return PmNoneBuilder.this;
            }
        };
    }

    @Override
    public @NotNull <U> LoopBuilderConsumer<U> iterableOf(@NotNull Iterable<? extends U> iterable) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                iterable.forEach(u -> add(fcn.apply(u)));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
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
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                iterable.forEach(u -> {
                    p.register();
                    executor.submit(() -> {
                        try {
                            add(fcn.apply(u));
                        } finally {
                            p.arriveAndDeregister();
                        }
                    });
                });
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

    @Override
    public @NotNull <U> LoopBuilderConsumer<U> stream(@NotNull Stream<? extends AnyValue<U>> stream) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                stream.forEach(u -> {
                    if (isSuccess())
                        add(fcn.apply(u.value()));
                    else
                        add(u.errors());
                });
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
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
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                stream.forEach(u -> {
                    if (isSuccess()) {
                        p.register();
                        executor.submit(() -> {
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
                return PmNoneBuilder.this;
            }
        };
    }

    @Override
    public @NotNull <U> LoopBuilderConsumer<U> streamOf(@NotNull Stream<? extends U> stream) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                stream.forEach(u -> add(fcn.apply(u)));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
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
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                stream.forEach(u -> {
                    p.register();
                    executor.submit(() -> {
                        try {
                            add(fcn.apply(u));
                        } finally {
                            p.arriveAndDeregister();
                        }
                    });
                });
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

}
