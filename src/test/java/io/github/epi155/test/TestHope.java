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
    private static final CustMsg MY_FAULT = PmCustMsg.of("EA01", "Oop error {} !!");


    @Test
    void test1() {
        Hope.of(1)
            .onSuccess(i -> Assertions.assertEquals(1, i))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    void test2() {
        Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"))
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
        val result = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"))
                .mapTo(
                        i -> "all fine",
                        e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
        Assertions.assertEquals("oops Houston we have had a problem", result);
    }

    @Test
    void test7() {
        val hope = Hope.of(1);
        hope.peek(i -> log.info("to be continue"));
        Assertions.assertFalse(hope.completeWarning());
    }

    @Test
    void test8() {
        val hope = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        hope.peek(i -> log.info("to be continue"));
        Assertions.assertTrue(hope.completeWithErrors());
        Assertions.assertFalse(hope.completeWarning());
        if (hope.completeWithErrors()) {
            val fail = hope.failure();
            Assertions.assertEquals("E01", fail.code());
        }
        if (hope.completeWithoutErrors()) {
            val value = hope.value();
        } else {
            val fault = hope.failure();
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
    void test12() {
        val hope = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        hope.ergo(Hope::of);
    }

    @Test
    void test13() {
        val hope = Hope.of(1);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            val fault = hope.failure();
        });
    }

    @Test
    void test14() {
        val hope = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertDoesNotThrow(() -> {
            val fault = hope.failure();
        });
    }

    @Test
        // mvn test -Dtest="TestHope#test15"
    void test15() {
        val h1 = Hope.of(null);
        Assertions.assertTrue(h1.completeWithErrors());
        log.warn("null -> {}", h1.failure());

        val h2 = Hope.of(h1.failure());
        Assertions.assertTrue(h2.completeWithErrors());
        log.warn("signal -> {}", h2.failure());
        val f2 = h2.failure();
        log.info("status: {}", f2.status());
        f2.forEach((k, v) -> log.info("{}: {}", k, v));

        @NotNull Some<@NotNull Failure> s1 = Some.of(h1.failure());
        Assertions.assertTrue(s1.completeWithErrors());
        log.warn("some -> {}", s1);

        val s2 = Some.pull(h2);
        Assertions.assertTrue(s2.completeWithErrors());
        log.warn("some -> {}", s2);
        val n1 = None.pull(Nope.nope());
        Assertions.assertTrue(n1.completeSuccess());

        val m = f2.getStrProperty("dummy", "non esiste");
    }

    @Test
    void test19() {
        val hope = Hope.capture(new NullPointerException());
        Assertions.assertThrows(NoSuchElementException.class, hope::value);
    }

    @Test
    void test20() {
        val hope = Hope.of(1).intoOf(it -> it + 1);
        Assertions.assertTrue(hope.completeSuccess());
        Assertions.assertEquals(2, hope.value());
        val nope = hope.asNope();
        Assertions.assertTrue(nope.completeSuccess());
        val some = Hope.of(1).map(it -> Some.of(it + 1));
        Assertions.assertTrue(some.completeSuccess());
        Assertions.assertEquals(2, some.value());

        val some2 = Hope.of(1).map(it -> Some.<Integer>capture(new NullPointerException()));
        Assertions.assertFalse(some2.completeSuccess());
    }

    @Test
    void test21() {
        val hope = Hope.<Integer>capture(new NullPointerException()).intoOf(it -> it + 1);
        Assertions.assertFalse(hope.completeSuccess());
        val nope = hope.asNope();
        Assertions.assertFalse(nope.completeSuccess());
        val some = Hope.<Integer>capture(new NullPointerException()).map(it -> Some.of(it + 1));
        Assertions.assertFalse(some.completeSuccess());
    }
    @Test
    void test22() {
        val z = Hope.of(1)
                .into(u -> Hope.of("0123456789ABCDEF".charAt(u))
                        .into(v -> Hope.of(BigInteger.probablePrime(v, new Random()))));
        Assertions.assertTrue(z.completeSuccess());
        log.info("Result is {}", z);
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
                            .onSuccess(bld::value)
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
                                                .peek(bld::value))))
        );
        Some<C> au = bld.build();

        Some<C> ax = formalValidation(a)
                .map(() -> decode(a))
                .map(b -> meritValidation(b)
                        .map(() -> translate(b)));

        None nn = formalValidation(a)
                .ergo(() -> decode(a)
                        .ergo(b -> meritValidation(b)
                                .ergo(() -> translate(b))));

        Hope<C> hx = formalValidation(a)
                .into(() -> decode(a))
                .into(b -> meritValidation(b)
                        .into(() -> translate(b)));
        Nope nx = formalValidation(a)
                .thus(() -> decode(a)
                        .thus(b -> meritValidation(b)
                                .thus(() -> translate(b))));

        @NotNull None nz = hx
            .choose()
                .when(false).peek(c -> {
                })
                .when(false).ergo(c -> Nope.nope())
                .when(false).fault(MY_FAULT)
                .when(c -> false).peek(c -> {
                })
                .whenInstanceOf(String.class).peek(c -> {
                })
                .whenInstanceOf(String.class).ergo(c -> Nope.nope())
                .whenInstanceOf(String.class).fault(MY_FAULT)
                .otherwise().ergo(c -> Nope.nope())
                .end();

        Some<D> iz = hx
            .<D>chooseMap()
                .when(false)
                .map(c -> fromCtoDh(c))
                .when(false)
                .mapOf(c -> fromCtoD(c))
                .when(false)
                .fault(MY_FAULT)
                .when(c -> false)
                .map(c -> fromCtoDs(c))
                .whenInstanceOf(String.class)
                .map(s -> fromStoDh(s))
                .whenInstanceOf(String.class)
                .mapOf(s -> fromStoD(s))
                .whenInstanceOf(String.class)
                .fault(MY_FAULT)
                .otherwise()
                .map(c -> fromCtoDs(c))
                .end();

        @NotNull None vz = ChooseContext.choose(1)
            .when(false)
            .peek(c -> {
            })
            .when(false).ergo(c -> Nope.nope())
            .when(false).fault(MY_FAULT)
            .when(c -> false).peek(c -> {
            })
            .whenInstanceOf(String.class).peek(c -> {
            })
            .whenInstanceOf(String.class).ergo(c -> Nope.nope())
                .whenInstanceOf(String.class).fault(MY_FAULT)
                .otherwise().ergo(c -> Nope.nope())
                .end();

        @NotNull Some<Integer> wz = ChooseContext.<Long, Integer>chooseMap(1L)
            .when(false).map(c -> Hope.of(1))
            .when(false).mapOf(c -> 1)
            .when(false).fault(MY_FAULT)
            .when(c -> false).map(c -> Hope.of(1))
            .whenInstanceOf(String.class).map(c -> Hope.of(1))
            .whenInstanceOf(String.class).mapOf(c -> 1)
            .whenInstanceOf(String.class).fault(MY_FAULT)
            .otherwise().map(c -> Hope.of(1))
            .end();
    }

    private D fromStoD(String s) {
        return new D();
    }

    private AnyValue<D> fromStoDh(String s) {
        return Hope.of(new D());
    }

    private AnyValue<D> fromCtoDs(C c) {
        return Some.of(new D());
    }

    private D fromCtoD(C c) {
        return new D();
    }

    private Hope<D> fromCtoDh(C c) {
        return Hope.of(new D());
    }

    private Hope<C> translate(B bData) {
        return Hope.of(new C());
    }

    private Nope meritValidation(B bData) {
        return Nope.nope();
    }

    private Hope<B> decode(A rawdata) {
        return Hope.of(new B());
    }

    private Nope formalValidation(A rawdata) {
        return Nope.nope();
    }

    private <B, A> Some<B> a2sb(A value) {
        return null;
    }
}
