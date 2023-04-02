package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
            .onSuccess(i -> Assertions.assertEquals(1, i))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    void test2() {
        Hope.failure(CustMsg.of("E01", "Houston we have had a problem"))
            .onSuccess(i -> log.info("All fine"))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    void test3() {
        String result = Hope.of(1)
            .mapTo(
                i -> "all fine",
                e -> String.format("oops %s", e.message()));
        Assertions.assertEquals("all fine", result);
    }

    @Test
    void test4() {
        val result = Hope.failure(CustMsg.of("E01", "Houston we have had a problem"))
            .mapTo(
                    i -> "all fine",
                    e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
        Assertions.assertEquals("oops Houston we have had a problem", result);
    }

    @Test
    void test5() {
        val hope = Hope.of(1);
        Assertions.assertDoesNotThrow(() -> hope.orThrow());
        Assertions.assertDoesNotThrow(() -> hope.onSuccess(k -> log.info("Hi")).orThrow(f -> new FailureException(new NullPointerException())));
    }

    @Test
    void test6() {
        val hope = Hope.failure(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertThrows(FailureException.class, hope::orThrow);
    }

    @Test
    void test7() {
        val hope = Hope.of(1);
        hope.peek(i -> log.info("to be continue"));
    }

    @Test
    void test8() {
        val hope = Hope.failure(CustMsg.of("E01", "Houston we have had a problem"));
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
        val hope = Hope.failure(CustMsg.of("E01", "Houston we have had a problem"));
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
        val hope = Hope.failure(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertDoesNotThrow(() -> {
            val fault = hope.fault();
        });
    }

    @Test
    void test15() {
        val hope = Hope.of(1);
        Assertions.assertDoesNotThrow(() -> hope.orThrow(fault -> new FailureException(CustMsg.of("E02", "Error is {}"), fault.message())));
    }

    @Test
    void test16() {
        val hope = Hope.failure(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertThrows(FailureException.class, () -> hope.orThrow(fault -> new FailureException(CustMsg.of("E02", "Error is {}"), fault.message())));
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
    private class A {}
    private class B {}
    private class C {}
    private class D {}

    void test501(A a) {
        val bld = Some.<C>builder();
        formalValidation(a)
            .onSuccess(() -> decode(a)
                .onSuccess(b -> meritValidation(b)
                    .onSuccess(() -> translate(b)
                        .onSuccess(bld::withValue)
                        .onFailure(bld::add))
                    .onFailure(bld::add))
                .onFailure(bld::add))
            .onFailure(bld::add);
        Some<C> sc = bld.build();
    }
    void test502(A a) {
        val bld = Some.<C>builder();
        bld.add(
            formalValidation(a)
                .ergo(() -> decode(a)
                    .ergo(b -> meritValidation(b)
                        .ergo(() -> translate(b)
                            .peek(bld::withValue))))
        );
        Some<C> au = bld.build();

        Some<C> ax = formalValidation(a)
            .<B>map(() -> decode(a))
            .<C>map(b -> meritValidation(b)
                .<C>map(() -> translate(b)));

        Some<C> av = formalValidation(a)
            .<C>map(() -> decode(a)
                .<C>map(b -> meritValidation(b)
                    .<C>map(() -> translate(b))));

    }

    private Some<C> translate(B bData) { return Some.of(new C()); }

    private None meritValidation(B bData) { return None.none();}

    private Some<B> decode(A rawdata) { return Some.of(new B());     }

    private None formalValidation(A rawdata) { return None.none();     }

    private <B, A> Some<B> a2sb(A value) {
        return null;
    }
}
