package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.stream.Stream;

class PmNoneBuilder extends PmAnyBuilder implements NoneBuilder {
    @Override
    @NoBuiltInCapture
    public @NotNull None build() {
        return new PmNone(signals());
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull <U> LoopBuilderConsumer<U> iterable(@NotNull Iterable<? extends AnyValue<U>> iterable) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                iterable.forEach(u -> consume(u, fcn));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (maxThread < 1)
                    throw new IllegalArgumentException();
                Semaphore s = new Semaphore(maxThread);
                Phaser p = new Phaser(1);
                iterable.forEach(u -> consumeUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                Phaser p = new Phaser(1);
                iterable.forEach(u -> consumeUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

    private <U> void consume(@NotNull AnyValue<U> u, Function<? super U, ? extends ItemStatus> fcn) {
        if (u.completeWithErrors()) {
            add(u.signals());      // add U error & warning
        } else {
            if (! u.completeSuccess()) {
                add(u.signals());       // add U warning
            }
            add(fcn.apply(u.value()));
        }
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull <U> LoopBuilderConsumer<U> iterableOf(@NotNull Iterable<? extends U> iterable) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                iterable.forEach(u -> add(fcn.apply(u)));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (maxThread < 1)
                    throw new IllegalArgumentException();
                Semaphore s = new Semaphore(maxThread);
                Phaser p = new Phaser(1);
                iterable.forEach(u -> consumeOfUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                Phaser p = new Phaser(1);
                iterable.forEach(u -> consumeOfUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

    private <U> void consumeOfUsingExecutor(U u, Function<? super U, ? extends ItemStatus> fcn, ExecutorService executor, Phaser p) {
        p.register();
        executor.submit(() -> {
            try {
                add(fcn.apply(u));
            } finally {
                p.arriveAndDeregister();
            }
        });
    }

    private <U> void consumeOfUsingThread(U u, Function<? super U, ? extends ItemStatus> fcn, Semaphore s, Phaser p) {
        try {
            p.register();
            //noinspection BlockingMethodInNonBlockingContext
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

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull <U> LoopBuilderConsumer<U> stream(@NotNull Stream<? extends AnyValue<U>> stream) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                stream.forEach(u -> consume(u, fcn));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (maxThread < 1)
                    throw new IllegalArgumentException();
                Semaphore s = new Semaphore(maxThread);
                Phaser p = new Phaser(1);
                stream.forEach(u -> consumeUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                Phaser p = new Phaser(1);
                stream.forEach(u -> consumeUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

        };
    }

    private <U> void consumeUsingExecutor(AnyValue<U> u, Function<? super U, ? extends ItemStatus> fcn, ExecutorService executor, Phaser p) {
        if (u.completeWithErrors()) {
            add(u.signals());      // add U error & warning
        } else {
            if (! u.completeSuccess()) {
                add(u.signals());       // add U warning
            }
            p.register();
            executor.submit(() -> {
                try {
                    add(fcn.apply(u.value()));
                } finally {
                    p.arriveAndDeregister();
                }
            });
        }
    }

    private <U> void consumeUsingThread(AnyValue<U> u, Function<? super U, ? extends ItemStatus> fcn, Semaphore s, Phaser p) {
        if (u.completeWithErrors()) {
            add(u.signals());      // add U error & warning
        } else {
            if (! u.completeSuccess()) {
                add(u.signals());       // add U warning
            }
            try {
                p.register();
                //noinspection BlockingMethodInNonBlockingContext
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
        }
    }

    @SuppressWarnings("Convert2Diamond")
    @Override
    public @NotNull <U> LoopBuilderConsumer<U> streamOf(@NotNull Stream<? extends U> stream) {
        return new LoopBuilderConsumer<U>() {
            @Override
            public @NotNull NoneBuilder forEach(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                stream.forEach(u -> add(fcn.apply(u)));
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(int maxThread, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (maxThread < 1)
                    throw new IllegalArgumentException();
                Semaphore s = new Semaphore(maxThread);
                Phaser p = new Phaser(1);
                stream.forEach(u -> consumeOfUsingThread(u, fcn, s, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }

            @Override
            public @NotNull NoneBuilder forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                Phaser p = new Phaser(1);
                stream.forEach(u -> consumeOfUsingExecutor(u, fcn, executor, p));
                p.arriveAndAwaitAdvance();
                return PmNoneBuilder.this;
            }
        };
    }

}
