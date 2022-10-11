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
                iterable.forEach(u -> consume(u, fcn));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                if (maxThread < 1)
                    throw new IllegalArgumentException();
                val s = new Semaphore(maxThread);
                val p = new Phaser(1);
                iterable.forEach(u -> consumeUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                iterable.forEach(u -> consumeUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

    private <U> void consume(AnyValue<U> u, Function<? super U, ? extends AnyItem> fcn) {
        if (u.isSuccess())
            add(fcn.apply(u.value()));
        else
            add(u.errors());
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
                    throw new IllegalArgumentException();
                val s = new Semaphore(maxThread);
                val p = new Phaser(1);
                iterable.forEach(u -> consumeOfUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                iterable.forEach(u -> consumeOfUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

    private <U> void consumeOfUsingExecutor(U u, Function<? super U, ? extends AnyItem> fcn, ExecutorService executor, Phaser p) {
        p.register();
        executor.submit(() -> {
            try {
                add(fcn.apply(u));
            } finally {
                p.arriveAndDeregister();
            }
        });
    }

    private <U> void consumeOfUsingThread(U u, Function<? super U, ? extends AnyItem> fcn, Semaphore s, Phaser p) {
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
    }

    @Override
    public @NotNull <U> LoopBuilderConsumer<U> stream(@NotNull Stream<? extends AnyValue<U>> stream) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
                stream.forEach(u -> consume(u, fcn));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                if (maxThread < 1)
                    throw new IllegalArgumentException();
                val s = new Semaphore(maxThread);
                val p = new Phaser(1);
                stream.forEach(u -> consumeUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                stream.forEach(u -> consumeUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

        };
    }

    private <U> void consumeUsingExecutor(AnyValue<U> u, Function<? super U, ? extends AnyItem> fcn, ExecutorService executor, Phaser p) {
        if (u.isSuccess()) {
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
    }

    private <U> void consumeUsingThread(AnyValue<U> u, Function<? super U, ? extends AnyItem> fcn, Semaphore s, Phaser p) {
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
                    throw new IllegalArgumentException();
                val s = new Semaphore(maxThread);
                val p = new Phaser(1);
                stream.forEach(u -> consumeOfUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
                val p = new Phaser(1);
                stream.forEach(u -> consumeOfUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

}
