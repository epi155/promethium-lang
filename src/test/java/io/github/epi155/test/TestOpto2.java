package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.Nope;
import io.github.epi155.pm.lang.OptoContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

@Slf4j
class TestOpto2 {
    private static final CustMsg MY_FAULT = PmCustMsg.of("CA01", "Oop error {} !!");

    @Test
    void testC0() {
        @NotNull Nope a = OptoContext.opto("a")
            .when(it -> it.length() == 1).ergo(it -> Hope.of(it.charAt(0)).<String>optoMap()
                .when(Character::isAlphabetic).mapOf(c -> String.valueOf(Character.toUpperCase(c)))
                .otherwise().fault(MY_FAULT)
                .end())
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
    }

    @Test
    void testC1() {
        @NotNull Nope a = OptoContext.opto("1")
            .when(it -> it.length() == 1).ergo(it -> Hope.of(it.charAt(0)).<String>optoMap()
                .when(Character::isAlphabetic).mapOf(c -> String.valueOf(Character.toUpperCase(c)))
                .otherwise().mapOf(String::valueOf)
                .end())
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
    }

    @Test
    void testC2() {
        @NotNull Nope a = OptoContext.opto("a")
            .when(it -> it.length() == 1).fault(MY_FAULT)
            .whenInstanceOf(BigInteger.class).peek(BigInteger::toString)
            .when(true).ergo(it -> Hope.of("Help"))
            .otherwise().ergo(it -> Hope.of("Hi"))
            .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }

    @Test
    void testC3() {
        @NotNull Nope a = OptoContext.opto(3.14F)
            .whenInstanceOf(BigInteger.class).peek(BigInteger::toString)
            .whenInstanceOf(Double.class).peek(d -> String.format("dble: %.4f", d))
            .whenInstanceOf(Float.class).peek(d -> String.format("sgle: %.4f", d))
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
    }

    @Test
    void testC4() {
        @NotNull Nope a = OptoContext.opto(3.14F)
            .when(false).fault(MY_FAULT)
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }

    @Test
    void testC5() {
        @NotNull Nope a = OptoContext.opto(3.14F)
            .when(false).fault(MY_FAULT)
            .otherwise().peek(Object::toString)
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
    }

    @Test
    void testC6() {
        @NotNull Nope a = OptoContext.opto(3.14F)
            .whenEquals(3.14F).peek(it -> System.out.println("match"))
            .otherwise().peek(it -> System.out.println("error"))
            .end();
        Assertions.assertTrue(a.completeWithoutErrors());
        @NotNull Nope b = OptoContext.opto(3.14F)
            .when(true).peek(it -> System.out.println("match"))
            .otherwise().peek(it -> System.out.println("error"))
            .end();
        Assertions.assertTrue(b.completeWithoutErrors());
        @NotNull Nope c = OptoContext.<Number>opto(3.14F)
            .when(false).fault(MY_FAULT)
            .otherwise().nop()
            .end();
        Assertions.assertTrue(c.completeWithoutErrors());
        @NotNull Nope d = OptoContext.<Number>opto(3.14F)
            .whenInstanceOf(Float.class).fault(MY_FAULT)
            .otherwise().nop()
            .end();
        Assertions.assertTrue(d.completeWithErrors());
        @NotNull Nope e = OptoContext.<Number>opto(3.14F)
            .whenInstanceOf(Float.class).ergo(f -> Hope.of(f * f))
            .otherwise().nop()
            .end();
        Assertions.assertTrue(e.completeWithoutErrors());
        @NotNull Nope f = OptoContext.<Number>opto(3.14F)
            .whenInstanceOf(Double.class).ergo(it -> Hope.of(it * it))
            .otherwise().nop()
            .end();
        Assertions.assertTrue(f.completeWithoutErrors());
        @NotNull Nope g = OptoContext.opto(3.14F)
            .when(true).nop()
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(g.completeWithoutErrors());
        @NotNull Nope h = OptoContext.opto(3.14F)
            .whenInstanceOf(Float.class).nop()
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertTrue(h.completeWithoutErrors());
    }

    @Test
    void testD0() {
        @NotNull Nope a = OptoContext.opto("aa")
            .when(it -> it.length() == 1).ergo(it -> Hope.of(it.charAt(0)).<String>optoMap()
                .when(Character::isAlphabetic).mapOf(c -> String.valueOf(Character.toUpperCase(c)))
                .otherwise().mapOf(String::valueOf)
                .end())
            .otherwise().fault(MY_FAULT)
            .end();
        Assertions.assertFalse(a.completeWithoutErrors());
        Assertions.assertEquals("CA01", a.failure().code());
    }
}
