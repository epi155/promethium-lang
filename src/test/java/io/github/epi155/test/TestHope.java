package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

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
        Hope.of(Failure.of(MsgError.of("E01", "Houston we have had a problem")))
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
        val result = Hope.of(Failure.of(MsgError.of("E01", "Houston we have had a problem")))
            .mapTo(i -> "all fine", e -> String.format("oops %s", e.message()));
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
        val hope = Hope.of(Failure.of(MsgError.of("E01", "Houston we have had a problem")));
        Assertions.assertThrows(FailureException.class, hope::orThrow);
    }

    @Test
    void test7() {
        val hope = Hope.of(1);
        hope.implies(i -> log.info("to be continue"));
    }

    @Test
    void test8() {
        val hope = Hope.of(Failure.of(MsgError.of("E01", "Houston we have had a problem")));
        hope.implies(i -> log.info("to be continue"));
    }

    @Test
    void test9() {
        val hope = Hope.of(1);
        hope.and(Hope::of);
    }

    @Test
    void test10() {
        val hope = Hope.of(1);
        hope.and(i -> Hope.capture(new NullPointerException()));
    }

    @Test
    void test11() {
        val hope = Hope.of(1);
        hope.and(i -> Hope.captureHere(new NullPointerException()));
    }

    @Test
    void test12() {
        val hope = Hope.of(Failure.of(MsgError.of("E01", "Houston we have had a problem")));
        hope.and(Hope::of);
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
        val hope = Hope.of(Failure.of(MsgError.of("E01", "Houston we have had a problem")));
        Assertions.assertDoesNotThrow(() -> {
            val fault = hope.fault();
        });
    }

    @Test
    void test15() {
        val hope = Hope.of(1);
        Assertions.assertDoesNotThrow(() -> {
            hope.orThrow(fault -> new FailureException(MsgError.of("E02", "Error is {}"), fault.message()));
        });
    }

    @Test
    void test16() {
        val hope = Hope.of(Failure.of(MsgError.of("E01", "Houston we have had a problem")));
        Assertions.assertThrows(FailureException.class, () -> {
            hope.orThrow(fault -> new FailureException(MsgError.of("E02", "Error is {}"), fault.message()));
        });
    }

    @Test
    void test17() {
        val hope = Hope.seize(() -> 1);
        val n = hope.count();
        Assertions.assertEquals(0, n);
        val o = hope.summary();
        Assertions.assertFalse(o.isPresent());
    }

    @Test
    void test18() {
        val hope = Hope.seize(() -> {
            throw new FaultException(Failure.of(MsgError.of("E01", "Houston we have had a problem")));
        });
        val n = hope.count();
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
        Assertions.assertTrue(hope.isSuccess());
        Assertions.assertEquals(2, hope.value());
        val nope = hope.asNope();
        Assertions.assertTrue(nope.isSuccess());
        val some = Hope.of(1).mapOut(it -> Some.of(it + 1));
        Assertions.assertTrue(some.isSuccess());
        Assertions.assertEquals(2, some.value());

        val some2 = Hope.of(1).mapOut(it -> Some.<Integer>capture(new NullPointerException()));
        Assertions.assertFalse(some2.isSuccess());
    }

    @Test
    void test21() {
        val hope = Hope.<Integer>capture(new NullPointerException()).mapOf(it -> it + 1);
        Assertions.assertFalse(hope.isSuccess());
        val nope = hope.asNope();
        Assertions.assertFalse(nope.isSuccess());
        val some = Hope.<Integer>capture(new NullPointerException()).mapOut(it -> Some.of(it + 1));
        Assertions.assertFalse(some.isSuccess());
    }
}
