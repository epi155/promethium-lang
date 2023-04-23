package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
class TestSome {
    private static final CustMsg MY_FAULT = CustMsg.of("EA01", "Oop error {} !!");
    private static final CustMsg MY_ALERT = CustMsg.of("WA01", "Oop warning {} !!");

    @Test
        // mvn test -Dtest="TestSome#test01"
    void test00() {
        val h1 = Some.of(null);
        Assertions.assertTrue(h1.completeWithErrors());
        log.warn("null -> {}", h1);

        val h2 = Some.of(h1.signals().iterator().next());
        Assertions.assertTrue(h2.completeWithErrors());
        log.warn("signals -> {}", h2);
    }

    @Test
        // mvn test -Dtest="TestSome#test01"
    void test01() {
        val bld = Some.<Integer>builder();
        Assertions.assertTrue(bld.completeSuccess());
        Assertions.assertFalse(bld.completeWarning());
        bld.alert(MY_ALERT);
        Assertions.assertFalse(bld.completeSuccess());
        Assertions.assertTrue(bld.completeWarning());
        Assertions.assertFalse(bld.completeWithErrors());
        bld.fault(MY_FAULT);
        Assertions.assertFalse(bld.completeWarning());
        Assertions.assertTrue(bld.completeWithErrors());
        val v = bld.buildWithValue(1);
        Assertions.assertTrue(v.completeWithErrors());
        Assertions.assertThrows(NoSuchElementException.class, v::value);
        val w = v.mapOf(k -> k + 1);
        log.info("w: {}", w);
    }

    @Test
        // mvn test -Dtest="TestSome#test02"
    void test02() {
        val bld = Some.<Integer>builder();
        bld.alert(MY_ALERT);
        val v = bld.buildWithValue(1);
        Assertions.assertTrue(v.completeWarning());
        Assertions.assertDoesNotThrow(v::value);
        val w = v.mapOf(k -> k + 1);
        log.info("w: {}", w);
    }

    @Test
        // mvn test -Dtest="TestSome#test03"
    void test03() {
        val bld = Some.<Integer>builder();
        val v = bld.buildWithValue(1);
        Assertions.assertFalse(v.completeWarning());
        Assertions.assertTrue(v.completeSuccess());
        Assertions.assertDoesNotThrow(v::value);
        val w = v.mapOf(k -> k + 1);
        log.info("w: {}", w);
    }

    @Test
        // mvn test -Dtest="TestSome#test04"
    void test04() {
        val v = Some
                .<Integer>builder()
                .build();
        Assertions.assertFalse(v.completeSuccess());
        Assertions.assertFalse(v.completeWarning());
        Assertions.assertTrue(v.completeWithErrors());
        Assertions.assertThrows(NoSuchElementException.class, v::value);
        log.info("v: {}", v);

        val b1 = Some.<Integer>builder();
        val v1 = b1.buildWithValue(null);
        Assertions.assertFalse(v1.completeSuccess());
        Assertions.assertFalse(v1.completeWarning());
        Assertions.assertTrue(v1.completeWithErrors());
        Assertions.assertThrows(NoSuchElementException.class, v1::value);
        log.info("v1: {}", v1);

        val h1 = Nope.fault(MY_FAULT);
        val v3 = Some
                .<Failure>builder()
                .buildWithValue(h1.failure());
        Assertions.assertFalse(v3.completeSuccess());
        Assertions.assertFalse(v3.completeWarning());
        Assertions.assertTrue(v3.completeWithErrors());
        Assertions.assertThrows(NoSuchElementException.class, v3::value);
        log.info("v3: {}", v3);
    }

    @Test
        // mvn test -Dtest="TestSome#test05"
    void test05() {
        val bld = Some.<Integer>builder();
        bld.value(1);
        bld.value(2);
        bld.alert(MY_ALERT);
        bld.fault(MY_FAULT);
        val v = bld.build();
        log.info("v: {}", v);

    }

    @Test
        // mvn test -Dtest="TestSome#test06"
    void test06() {
        val a = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1);
        val b = Some.of(a);
        Assertions.assertTrue(b.completeWithoutErrors());
        Assertions.assertFalse(b.completeSuccess());
        Assertions.assertEquals(1, b.value());

        @NotNull None c = a.choose().when(true).nop().otherwise().fault(MY_FAULT).end();
        Assertions.assertTrue(c.completeWithoutErrors());
        Assertions.assertFalse(c.completeSuccess());

    }

    @Test
        // mvn test -Dtest="TestSome#test07"
    void test07() {
        val bld = Some.<Integer>builder();
        bld.alert(MY_ALERT);
        bld.fault(MY_FAULT);
        Collection<Signal> za = bld.signals();
        Assertions.assertThrows(UnsupportedOperationException.class, za::clear);
    }

    @Test
        // mvn test -Dtest="TestSome#test08"
    void test08() {
        val bld = Some.<Integer>builder();
        bld.value(1);
        bld.fault(MY_FAULT);
        val a = bld.build();
        Assertions.assertTrue(a.completeWithErrors());
        Assertions.assertEquals(2, a.signals().size());
        System.out.println(a);

        val b = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1);
        b.summary().ifPresent(System.out::println);
        val c = Some.<Integer>builder().withAlert(MY_ALERT).withAlert(MY_FAULT).buildWithValue(1);
        c.summary().ifPresent(System.out::println);
    }

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

    }

    private Some<C> translate(B bData) {
        return Some.of(new C());
    }

    private None meritValidation(B bData) {
        return None.none();
    }

    private Some<B> decode(A rawdata) {
        return Some.of(new B());
    }

    private None formalValidation(A rawdata) {
        return None.none();
    }

    private class A {
    }

    private class B {
    }

    private class C {
    }

    private class D {
    }

}
