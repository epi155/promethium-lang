package io.github.epi155.test;

import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.Some;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
class TestLoop {


    @Test
    void testLoop1() {
        val list = Arrays.asList(1, 2, 3, 4, 5, 6);
        val sum = new AtomicInteger();
        val k1 = None.iterableOf(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop2() {
        val list = IntStream.rangeClosed(1, 6).boxed();
        val sum = new AtomicInteger();
        val k1 = None.streamOf(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop3() {
        Iterable<Integer> list = IntStream.rangeClosed(1, 100).boxed()::iterator;
        val sum = new AtomicInteger();
        val k1 = None.iterableOf(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop4() {
        val list = IntStream.rangeClosed(1, 100).boxed();
        val sum = new AtomicInteger();
        val k1 = None.streamOf(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop5() {
        Iterable<Integer> list = IntStream.rangeClosed(1, 100).boxed()::iterator;
        val sum = new AtomicInteger();
        val w1 = None.iterableOf(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop6() {
        val list = IntStream.rangeClosed(1, 100).boxed();
        val sum = new AtomicInteger();
        val w1 = None.streamOf(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop7() {
        Iterable<Integer> list = IntStream.rangeClosed(1, 100).boxed()::iterator;
        val exec = Executors.newFixedThreadPool(8);
        val sum = new AtomicInteger();
        val k1 = None.iterableOf(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop8() {
        val list = IntStream.rangeClosed(1, 100).boxed();
        val exec = Executors.newFixedThreadPool(8);
        val sum = new AtomicInteger();
        val k1 = None.streamOf(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    //____________________________________
    //
    @Test
    void testLoop11() {
        val list = Stream.of(1, 2, 3, 4, 5, 6).map(Hope::of).collect(Collectors.toList());
        val sum = new AtomicInteger();
        val k1 = None.iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop12() {
        val list = Stream.of(1, 2, 3, 4, 5, 6).map(Hope::of);
        val sum = new AtomicInteger();
        val k1 = None.stream(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(21, sum.get());
    }

    @Test
    void testLoop13() {
        val list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of).collect(Collectors.toList());
        val sum = new AtomicInteger();
        val k1 = None.iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop14() {
        val list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of);
        val sum = new AtomicInteger();
        val k1 = None.stream(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop15() {
        val list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of).collect(Collectors.toList());
        val sum = new AtomicInteger();
        val w1 = None.iterable(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop16() {
        val list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of);
        val sum = new AtomicInteger();
        val w1 = None.stream(list);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            w1.forEachParallel(0, n -> Hope.of(sum.addAndGet(n)));
        });
    }

    @Test
    void testLoop17() {
        val list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of).collect(Collectors.toList());
        val exec = Executors.newFixedThreadPool(8);
        val sum = new AtomicInteger();
        val k1 = None.iterable(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop18() {
        val list = IntStream.rangeClosed(1, 100).boxed().map(Hope::of);
        val exec = Executors.newFixedThreadPool(8);
        val sum = new AtomicInteger();
        val k1 = None.stream(list).forEachParallel(exec, n -> Hope.of(sum.addAndGet(n)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
        exec.shutdown();
        Assertions.assertEquals(5050, sum.get());
    }

    @Test
    void testLoop20() {
        val list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        val sum = new AtomicInteger();
        None.iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        None.iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        val es = Executors.newFixedThreadPool(5);
        None.iterable(list).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop21() {
        val list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        val sum = new AtomicInteger();
        None.iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        None.iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        val es = Executors.newFixedThreadPool(5);
        None.iterable(list).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop22() {
        val list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        val sum = new AtomicInteger();
        None.builder().join(() -> {
            throw new NullPointerException();
        }).iterable(list).forEach(n -> Hope.of(sum.addAndGet(n)));
        None.builder().join(() -> {
            throw new NullPointerException();
        }).iterable(list).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        val es = Executors.newFixedThreadPool(5);
        None.builder().join(() -> {
            throw new NullPointerException();
        }).iterable(list).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop23() {
        val list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        val sum = new AtomicInteger();
        Some.of(list).iterable(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.of(list).iterable(it -> it).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        val es = Executors.newFixedThreadPool(5);
        Some.of(list).iterable(it -> it).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop24() {
        val list = Stream.of(Hope.of(1), Hope.<Integer>capture(new NullPointerException())).collect(Collectors.toList());
        val sum = new AtomicInteger();
        Some.<List<Hope<Integer>>>capture(new NullPointerException()).iterable(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.<List<Hope<Integer>>>capture(new NullPointerException()).iterable(it -> it).forEachParallel(5, n -> Hope.of(sum.addAndGet(n)));
        val es = Executors.newFixedThreadPool(5);
        Some.<List<Hope<Integer>>>capture(new NullPointerException()).iterable(it -> it).forEachParallel(es, n -> Hope.of(sum.addAndGet(n)));
        es.shutdown();
    }

    @Test
    void testLoop25() {
        val list = Stream.of(1, 2, 3).collect(Collectors.toList());
        val sum = new AtomicInteger();
        Some.of(list).iterableOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.of(Stream.of(4, 5, 6)).streamOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.of(Stream.of(Hope.of(1), Hope.of(2))).stream(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));

        Some.<List<Integer>>capture(new NullPointerException()).iterableOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.<Stream<Integer>>capture(new NullPointerException()).streamOf(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
        Some.<Stream<Hope<Integer>>>capture(new NullPointerException()).stream(it -> it).forEach(n -> Hope.of(sum.addAndGet(n)));
    }

}
