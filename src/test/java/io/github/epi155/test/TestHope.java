package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Random;

class TestHope {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestHope.class);

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
        String result = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"))
                .mapTo(
                        i -> "all fine",
                        e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
        Assertions.assertEquals("oops Houston we have had a problem", result);
    }

    @Test
    void test7() {
        @NotNull Hope<Integer> hope = Hope.of(1);
        hope.implies(i -> log.info("to be continue"));
        Assertions.assertFalse(hope.completeWarning());
    }

    @Test
    void test8() {
        @NotNull Hope<Object> hope = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        hope.implies(i -> log.info("to be continue"));
        Assertions.assertTrue(hope.completeWithErrors());
        Assertions.assertFalse(hope.completeWarning());
        if (hope.completeWithErrors()) {
            @NotNull Failure fail = hope.failure();
            Assertions.assertEquals("E01", fail.code());
        }
        if (hope.completeWithoutErrors()) {
            @NotNull Object value = hope.value();
        } else {
            @NotNull Failure fault = hope.failure();
        }
    }

    @Test
    void test9() {
        @NotNull Hope<Integer> hope = Hope.of(1);
        hope.ergo(Hope::of);
    }

    @Test
    void test10() {
        @NotNull Hope<Integer> hope = Hope.of(1);
        hope.ergo(i -> Hope.capture(new NullPointerException()));
    }

    @Test
    void test12() {
        @NotNull Hope<Object> hope = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        hope.ergo(Hope::of);
    }

    @Test
    void test13() {
        @NotNull Hope<Integer> hope = Hope.of(1);
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            @NotNull Failure fault = hope.failure();
        });
    }

    @Test
    void test14() {
        @NotNull Hope<Object> hope = Hope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertDoesNotThrow(() -> {
            @NotNull Failure fault = hope.failure();
        });
    }

    @Test
        // mvn test -Dtest="TestHope#test15"
    void test15() {
        @NotNull Hope<Object> h1 = Hope.of(null);
        Assertions.assertTrue(h1.completeWithErrors());
        log.warn("null -> {}", h1.failure());

        @NotNull Hope<Failure> h2 = Hope.of(h1.failure());
        Assertions.assertTrue(h2.completeWithErrors());
        log.warn("signal -> {}", h2.failure());
        @NotNull Failure f2 = h2.failure();
        log.info("status: {}", f2.status());
        f2.forEach((k, v) -> log.info("{}: {}", k, v));

        @NotNull Some<@NotNull Failure> s1 = Some.of(h1.failure());
        Assertions.assertTrue(s1.completeWithErrors());
        log.warn("some -> {}", s1);

        @NotNull Some<Failure> s2 = Some.pull(h2);
        Assertions.assertTrue(s2.completeWithErrors());
        log.warn("some -> {}", s2);
        @NotNull None n1 = None.pull(Nope.nope());
        Assertions.assertTrue(n1.completeSuccess());

        String m = f2.getStrProperty("dummy", "non esiste");
    }

    @Test
    void test19() {
        @NotNull Hope<Object> hope = Hope.capture(new NullPointerException());
        Assertions.assertThrows(NoSuchElementException.class, hope::value);
    }

    @Test
    void test20() {
        @NotNull Hope<Integer> hope = Hope.of(1).mapsOf(it -> it + 1);
        Assertions.assertTrue(hope.completeSuccess());
        Assertions.assertEquals(2, hope.value());
        @NotNull Nope nope = hope.asNope();
        Assertions.assertTrue(nope.completeSuccess());
        @NotNull Some<Integer> some = Hope.of(1).map(it -> Some.of(it + 1));
        Assertions.assertTrue(some.completeSuccess());
        Assertions.assertEquals(2, some.value());

        @NotNull Some<Integer> some2 = Hope.of(1).map(it -> Some.capture(new NullPointerException()));
        Assertions.assertFalse(some2.completeSuccess());
    }

    @Test
    void test21() {
        @NotNull Hope<Integer> hope = Hope.<Integer>capture(new NullPointerException()).mapsOf(it -> it + 1);
        Assertions.assertFalse(hope.completeSuccess());
        @NotNull Nope nope = hope.asNope();
        Assertions.assertFalse(nope.completeSuccess());
        @NotNull Some<Integer> some = Hope.<Integer>capture(new NullPointerException()).map(it -> Some.of(it + 1));
        Assertions.assertFalse(some.completeSuccess());
    }
    @Test
    void test22() {
        @NotNull Hope<BigInteger> z = Hope.of(1)
                .maps(u -> Hope.of("0123456789ABCDEF".charAt(u))
                        .maps(v -> Hope.of(BigInteger.probablePrime(v, new Random()))));
        Assertions.assertTrue(z.completeSuccess());
        log.info("Result is {}", z);
    }
    private class A {}
    private class B {}
    private class C {}
    private class D {}
    void test501(A a) {
        @NotNull SomeBuilder<C> bld = Some.builder();
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
        @NotNull SomeBuilder<C> bld = Some.builder();
        bld.add(
                formalValidation(a)
                        .ergo(() -> decode(a)
                                .ergo(b -> meritValidation(b)
                                        .ergo(() -> translate(b)
                                                .implies(bld::value))))
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
                .maps(() -> decode(a))
                .maps(b -> meritValidation(b)
                        .maps(() -> translate(b)));
        Nope nx = formalValidation(a)
                .ergoes(() -> decode(a)
                        .ergoes(b -> meritValidation(b)
                                .ergoes(() -> translate(b))));

        @NotNull None nz = hx
                .choose()
                .when(false).thenAccept(c -> {
                })
                .when(false).thenApply(c -> Nope.nope())
                .when(false).fault(MY_FAULT)
                .when(c -> false).thenAccept(c -> {
                })
                .whenInstanceOf(String.class).thenAccept(c -> {
                })
                .whenInstanceOf(String.class).thenApply(c -> Nope.nope())
                .whenInstanceOf(String.class).fault(MY_FAULT)
                .otherwise().thenApply(c -> Nope.nope())
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
                .thenAccept(c -> {
                })
                .when(false).thenApply(c -> Nope.nope())
                .when(false).fault(MY_FAULT)
                .when(c -> false).thenAccept(c -> {
                })
                .whenInstanceOf(String.class).thenAccept(c -> {
                })
                .whenInstanceOf(String.class).thenApply(c -> Nope.nope())
                .whenInstanceOf(String.class).fault(MY_FAULT)
                .otherwise().thenApply(c -> Nope.nope())
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
