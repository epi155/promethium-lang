package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Random;

@Slf4j
class TestHope {


    @Test
    void test1() {
        Hope.of(1)
            .onSuccess(i -> log.info("All fine"))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    void test2() {
        Hope.failure(MsgError.of("E01", "Houston we have had a problem"))
            .onSuccess(i -> log.info("All fine"))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    void test3() {
        val result = Hope.of(1)
            .mapTo(i -> "all fine", e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
    }

    @Test
    void test4() {
        val result = Hope.failure(MsgError.of("E01", "Houston we have had a problem"))
            .mapTo(
                    i -> "all fine",
                    e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
    }

    @Test
    void test5() {
        val hope = Hope.of(1);
        Assertions.assertDoesNotThrow(() -> hope.orThrow());
        Assertions.assertDoesNotThrow(() -> hope.onSuccess(k -> log.info("Hi")).orThrow(f -> new FailureException(new NullPointerException())));
    }

    @Test
    void test6() {
        val hope = Hope.failure(MsgError.of("E01", "Houston we have had a problem"));
        Assertions.assertThrows(FailureException.class, hope::orThrow);
    }

    @Test
    void test7() {
        val hope = Hope.of(1);
        hope.peek(i -> log.info("to be continue"));
    }

    @Test
    void test8() {
        val hope = Hope.failure(MsgError.of("E01", "Houston we have had a problem"));
        hope.peek(i -> log.info("to be continue"));
        Assertions.assertTrue(hope.completeWithErrors());
        if (hope.completeWithErrors()) {
            val fail = hope.fault();
            Assertions.assertEquals("E01", fail.code());
        }
        if (hope.completeWithoutErrors()) {
            val value = hope.value();
        } else {
            val fault = hope.fault();
        }
    }

    @Test
    void test9() {
        val hope = Hope.of(1);
        hope.ergo(Hope::of);
    }

    @Test
    void test10() {
        val hope = Hope.of(1);
        hope.ergo(i -> Hope.capture(new NullPointerException()));
    }

    @Test
    void test11() {
        val hope = Hope.of(1);
        hope.ergo(i -> Hope.captureHere(new NullPointerException()));
    }

    @Test
    void test12() {
        val hope = Hope.failure(MsgError.of("E01", "Houston we have had a problem"));
        hope.ergo(Hope::of);
    }

    @Test
    void test13() {
        val hope = Hope.of(1);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            val fault = hope.fault();
        });
    }

    @Test
    void test14() {
        val hope = Hope.failure(MsgError.of("E01", "Houston we have had a problem"));
        Assertions.assertDoesNotThrow(() -> {
            val fault = hope.fault();
        });
    }

    @Test
    void test15() {
        val hope = Hope.of(1);
        Assertions.assertDoesNotThrow(() -> hope.orThrow(fault -> new FailureException(MsgError.of("E02", "Error is {}"), fault.message())));
    }

    @Test
    void test16() {
        val hope = Hope.failure(MsgError.of("E01", "Houston we have had a problem"));
        Assertions.assertThrows(FailureException.class, () -> hope.orThrow(fault -> new FailureException(MsgError.of("E02", "Error is {}"), fault.message())));
    }

    @Test
    void test17() {
        val hope = Hope.seize(() -> 1);
        val n = hope.signals().size();
        Assertions.assertEquals(0, n);
        val o = hope.summary();
        Assertions.assertFalse(o.isPresent());
    }

    @Test
    void test18() {
        val hope = Hope.seize(() -> {
            throw new FaultException(MsgError.of("E01", "Houston we have had a problem"));
        });
        val n = hope.signals().size();
        Assertions.assertEquals(1, n);
        val o = hope.summary();
        Assertions.assertTrue(o.isPresent());
    }

    @Test
    void test19() {
        val hope = Hope.capture(new NullPointerException());
        Assertions.assertThrows(NoSuchElementException.class, hope::value);
    }

    @Test
    void test20() {
        val hope = Hope.of(1).mapOf(it -> it + 1);
        Assertions.assertTrue(hope.completeSuccess());
        Assertions.assertEquals(2, hope.value());
        val nope = hope.asNope();
        Assertions.assertTrue(nope.completeSuccess());
        val some = Hope.of(1).mapOut(it -> Some.of(it + 1));
        Assertions.assertTrue(some.completeSuccess());
        Assertions.assertEquals(2, some.value());

        val some2 = Hope.of(1).mapOut(it -> Some.<Integer>capture(new NullPointerException()));
        Assertions.assertFalse(some2.completeSuccess());
    }

    @Test
    void test21() {
        val hope = Hope.<Integer>capture(new NullPointerException()).mapOf(it -> it + 1);
        Assertions.assertFalse(hope.completeSuccess());
        val nope = hope.asNope();
        Assertions.assertFalse(nope.completeSuccess());
        val some = Hope.<Integer>capture(new NullPointerException()).mapOut(it -> Some.of(it + 1));
        Assertions.assertFalse(some.completeSuccess());
    }
    @Test
    void test22() {
        val z = Hope.of(1)
            .map(u -> Hope.of("0123456789ABCDEF".charAt(u))
                .map(v -> Hope.of(BigInteger.probablePrime(v, new Random()))));
        Assertions.assertTrue(z.completeSuccess());
        log.info("Result is {}", z.toString());
    }
    <A,B> void test500(A a) {
        Hope<A> ha = computeA();
        Hope<B> hb;
        if (ha.completeWithoutErrors()) {
            hb = a2b(ha.value());
        } else {
            hb = Hope.<B>failure(ha);
        }

        Hope<B> ka = computeA().map(this::a2b);
    }

    private <A> Hope<A> computeA() {
        return null;
    }

    private <B, A> Hope<B> a2b(A value) {
        return null;
    }
    
    <A,B> void test501(A a) {
        Some<A> sa = Some.of(a);
        Some<B> sb;
        if (sa.completeWithoutErrors()) {
            sb = a2sb(sa.value());
        } else {
            sb = Some.failure(sa);
        }
        Some<B> kb = Some.of(a).<B>map(this::a2sb);
        @NotNull Hope<A> ha = Hope.of(a);
        Some<A> ss = Some.ofHope(ha);
    }

    private <B, A> Some<B> a2sb(A value) {
        return null;
    }
}
