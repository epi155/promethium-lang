package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class TestLoop {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestLoop.class);


    @Test
    void testLoop1() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.iterableOf(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop2() {
        Stream<Integer> list = IntStream.rangeClosed(1, 6).boxed();
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.streamOf(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop3() {
        Iterable<Integer> list = IntStream.rangeClosed(1, 100).boxed()::iterator;
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.iterableOf(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop4() {
        Stream<Integer> list = IntStream.rangeClosed(1, 100).boxed();
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.streamOf(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop5() {
        Iterable<Integer> list = IntStream.rangeClosed(1, 100).boxed()::iterator;
        AtomicInteger sum = new AtomicInteger();
        @NotNull LoopConsumer<Integer> w1 = None.iterableOf(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop6() {
        Stream<Integer> list = IntStream.rangeClosed(1, 100).boxed();
        AtomicInteger sum = new AtomicInteger();
        @NotNull LoopConsumer<Integer> w1 = None.streamOf(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop7() {
        Iterable<Integer> list = IntStream.rangeClosed(1, 100).boxed()::iterator;
        ExecutorService exec = Executors.newFixedThreadPool(8);
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.iterableOf(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop8() {
        Stream<Integer> list = IntStream.rangeClosed(1, 100).boxed();
        ExecutorService exec = Executors.newFixedThreadPool(8);
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.streamOf(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    //____________________________________
    //
    @Test
    void testLoop11() {
        List<Hope<Integer>> list = Stream.of(1, 2, 3, 4, 5, 6).map(Hope::of).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop12() {
        Stream<Hope<Integer>> list = Stream.of(1, 2, 3, 4, 5, 6).map(Hope::of);
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.stream(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop13() {
        List<Hope<Integer>> list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop14() {
        Stream<Hope<Integer>> list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of);
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.stream(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop15() {
        List<Hope<Integer>> list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        @NotNull LoopConsumer<Integer> w1 = None.iterable(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop16() {
        @NotNull Stream<Hope<Integer>> list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of);
        AtomicInteger sum = new AtomicInteger();
        @NotNull LoopConsumer<Integer> w1 = None.stream(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop17() {
        List<Hope<Integer>> list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of).collect(Collectors.toList());
        ExecutorService exec = Executors.newFixedThreadPool(8);
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.iterable(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop18() {
        Stream<Hope<Integer>> list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of);
        ExecutorService exec = Executors.newFixedThreadPool(8);
        AtomicInteger sum = new AtomicInteger();
        @NotNull None k1 = None.stream(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop20() {
        List<Hope<Integer>> list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        None.iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        None.iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        ExecutorService es = Executors.newFixedThreadPool(5);
        None.iterable(list).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop21() {
        List<Hope<Integer>> list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        None.iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        None.iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        ExecutorService es = Executors.newFixedThreadPool(5);
        None.iterable(list).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop22() {
        List<Hope<Integer>> list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        NoneBuilder n1 = None.builder().withStatus(Nope.capture(new NullPointerException()))
                // iterable is called even if the builder is in error
                .iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        Assertions.assertTrue(n1.completeWithErrors());
        Assertions.assertEquals(2, n1.signals().size());

        None.builder().withStatus(Nope.capture(new NullPointerException()))
                .iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        ExecutorService es = Executors.newFixedThreadPool(5);
        None.builder().withStatus(Nope.capture(new NullPointerException()))
            .iterable(list).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop23() {
        List<Hope<Integer>> list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        Some.of(list).iterable(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.of(list).iterable(it -> it).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        ExecutorService es = Executors.newFixedThreadPool(5);
        Some.of(list).iterable(it -> it).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop24() {
        List<Hope<Integer>> list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        Some.<List<Hope<Integer>>>capture(new NullPointerException()).iterable(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.<List<Hope<Integer>>>capture(new NullPointerException()).iterable(it -> it).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        ExecutorService es = Executors.newFixedThreadPool(5);
        Some.<List<Hope<Integer>>>capture(new NullPointerException()).iterable(it -> it).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop25() {
        List<Integer> list = Stream.of(1, 2, 3).collect(Collectors.toList());
        AtomicInteger sum = new AtomicInteger();
        Some.of(list).iterableOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.of(Stream.of(4, 5, 6)).streamOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.of(Stream.of(Hope.of(1), Hope.of(2))).stream(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));

        Some.<List<Integer>>capture(new NullPointerException()).iterableOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.<Stream<Integer>>capture(new NullPointerException()).streamOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.<Stream<Hope<Integer>>>capture(new NullPointerException()).stream(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
    }

}
