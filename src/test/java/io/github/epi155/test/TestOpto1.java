package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.Hope;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

@Slf4j
class TestOpto1 {
    private static final CustMsg MY_FAULT = CustMsg.of("CA01", "Oop error {} !!");

    @Test
    void testC0() {
        Hope<String> a = Hope.of("a").<String>optoMap()
            .when(it -> it.length() == 1).map(it -> Hope.of(it.charAt(0)).<String>optoMap()
                .when(Character::isAlphabetic).mapOf(c -> String.valueOf(Character.toUpperCase(c)))
                .otherwise().fault(MY_FAULT)
                .end())
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Assertions.assertEquals("A", a.value());
    }

    @Test
    void testC1() {
        Hope<String> a = Hope.of("1").<String>optoMap()
            .when(it -> it.length() == 1).map(it -> Hope.of(it.charAt(0)).<String>optoMap()
                .when(Character::isAlphabetic).mapOf(c -> String.valueOf(Character.toUpperCase(c)))
                .otherwise().mapOf(String::valueOf)
                .end())
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Assertions.assertEquals("1", a.value());
    }

    @Test
    void testC2() {
        Hope<String> a = Hope.of("a").<String>optoMap()
            .when(it -> it.length() == 1).fault(MY_FAULT)
            .whenInstanceOf(BigInteger.class).mapOf(BigInteger::toString)
            .when(true).mapOf(it -> "Help")
            .otherwise().map(it -> Hope.of("Hi"))
            .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }

    @Test
    void testC3() {
        Hope<String> a = Hope.<Number>of(3.14F).<String>optoMap()
            .whenInstanceOf(BigInteger.class).mapOf(BigInteger::toString)
            .whenInstanceOf(Double.class).mapOf(d -> String.format("dble: %.4f", d))
            .whenInstanceOf(Float.class).mapOf(d -> String.format("sgle: %.4f", d))
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Assertions.assertEquals("sgle: 3,1400", a.value());
    }

    @Test
    void testC4() {
        Hope<String> a = Hope.<Number>of(3.14F).<String>optoMap()
            .when(false).fault(MY_FAULT)
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }

    @Test
    void testC5() {
        Hope<String> a = Hope.<Number>of(3.14F).<String>optoMap()
            .when(false).fault(MY_FAULT)
            .otherwise().mapOf(Object::toString)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
    }

    @Test
    void testC6() {
        Hope<String> a = Hope.<Number>of(3.14F).<String>optoMap()
            .whenEquals(3.14F).mapOf(it -> Float.toString(it.floatValue() * 2))
            .otherwise().mapOf(Object::toString)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Hope<String> b = Hope.<Number>of(3.14F).<String>optoMap()
            .when(true).mapOf(it -> Float.toString(it.floatValue() * 2))
            .otherwise().mapOf(Object::toString)
            .end();
        Assertions.assertTrue(b.completeWithoutErrors());
        Hope<String> c = Hope.<Number>of(3.14F).<String>optoMap()
            .whenInstanceOf(Float.class).map(it -> Hope.of("Hi"))
            .otherwise().mapOf(Object::toString)
            .end();
        Assertions.assertTrue(c.completeWithoutErrors());
        Hope<String> d = Hope.<Number>of(3.14F).<String>optoMap()
            .whenInstanceOf(Float.class).fault(MY_FAULT)
            .otherwise().mapOf(Object::toString)
            .end();
        Assertions.assertTrue(d.completeWithErrors());
    }

    @Test
    void testC7() {
        Hope<String> a = Hope.<Number>fault(MY_FAULT).<String>optoMap()
            .when(true).mapOf(it -> "hi")
            .otherwise().mapOf(it -> "lo")
            .end();
        Assertions.assertTrue(a.completeWithErrors());
    }

    @Test
    void testD0() {
        Hope<String> a = Hope.of("aa").<String>optoMap()
            .when(it -> it.length() == 1).map(it -> Hope.of(it.charAt(0)).<String>optoMap()
                .when(Character::isAlphabetic).mapOf(c -> String.valueOf(Character.toUpperCase(c)))
                .otherwise().mapOf(String::valueOf)
                .end())
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }
}
