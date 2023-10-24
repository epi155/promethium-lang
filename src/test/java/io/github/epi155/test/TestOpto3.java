package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.OptoContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class TestOpto3 {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestOpto3.class);

    private static final CustMsg MY_FAULT = PmCustMsg.of("CA01", "Oop error {} !!");

    @Test
    void testC0() {
        Hope<String> a = OptoContext.<String, String>choosesMap("a")
                .when(it -> it.length() == 1).maps(it -> Hope.of(it.charAt(0)).<String>choosesMap()
                        .when(Character::isAlphabetic).mapsOf(c -> String.valueOf(Character.toUpperCase(c)))
                        .otherwise().fault(MY_FAULT)
                        .end())
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Assertions.assertEquals("A", a.value());
    }

    @Test
    void testC1() {
        Hope<String> a = OptoContext.<String, String>choosesMap("1")
                .when(it -> it.length() == 1).maps(it -> Hope.of(it.charAt(0)).<String>choosesMap()
                        .when(Character::isAlphabetic).mapsOf(c -> String.valueOf(Character.toUpperCase(c)))
                        .otherwise().mapsOf(String::valueOf)
                        .end())
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Assertions.assertEquals("1", a.value());
    }

    @Test
    void testC2() {
        Hope<String> a = OptoContext.<String, String>choosesMap("a")
                .when(it -> it.length() == 1).fault(MY_FAULT)
                .whenInstanceOf(BigInteger.class).mapsOf(BigInteger::toString)
                .when(true).mapsOf(it -> "Help")
                .otherwise().maps(it -> Hope.of("Hi"))
                .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }

    @Test
    void testC3() {
        Hope<String> a = OptoContext.<Number, String>choosesMap(3.14F)
                .whenInstanceOf(BigInteger.class).mapsOf(BigInteger::toString)
                .whenInstanceOf(Double.class).mapsOf(d -> String.format("dble: %.4f", d))
                .whenInstanceOf(Float.class).mapsOf(d -> String.format("sgle: %.4f", d))
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Assertions.assertEquals("sgle: 3,1400", a.value());
    }

    @Test
    void testC4() {
        Hope<String> a = OptoContext.<Number, String>choosesMap(3.14F)
                .when(false).fault(MY_FAULT)
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }

    @Test
    void testC5() {
        Hope<String> a = OptoContext.<Number, String>choosesMap(3.14F)
                .when(false).fault(MY_FAULT)
                .otherwise().mapsOf(Object::toString)
                .end();
        Assertions.assertTrue(a.completeWithoutErrors());
    }

    @Test
    void testC6() {
        Hope<String> a = OptoContext.<Number, String>choosesMap(3.14F)
                .whenEquals(3.14F).mapsOf(it -> Float.toString(it.floatValue() * 2))
                .otherwise().mapsOf(Object::toString)
                .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        Hope<String> b = OptoContext.<Number, String>choosesMap(3.14F)
                .when(true).mapsOf(it -> Float.toString(it.floatValue() * 2))
                .otherwise().mapsOf(Object::toString)
                .end();
        Assertions.assertTrue(b.completeWithoutErrors());
        Hope<String> c = OptoContext.<Number, String>choosesMap(3.14F)
                .whenInstanceOf(Float.class).maps(it -> Hope.of("Hi"))
                .otherwise().mapsOf(Object::toString)
                .end();
        Assertions.assertTrue(c.completeWithoutErrors());
        Hope<String> d = OptoContext.<Number, String>choosesMap(3.14F)
                .whenInstanceOf(Float.class).fault(MY_FAULT)
                .otherwise().mapsOf(Object::toString)
                .end();
        Assertions.assertTrue(d.completeWithErrors());
    }


    @Test
    void testD0() {
        Hope<String> a = OptoContext.<String, String>choosesMap("aa")
                .when(it -> it.length() == 1).maps(it -> Hope.of(it.charAt(0)).<String>choosesMap()
                        .when(Character::isAlphabetic).mapsOf(c -> String.valueOf(Character.toUpperCase(c)))
                        .otherwise().mapsOf(String::valueOf)
                        .end())
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }
}
