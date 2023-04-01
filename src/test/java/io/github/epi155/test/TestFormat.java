package io.github.epi155.test;

import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.Nope;
import io.github.epi155.pm.lang.Nuntium;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

@Slf4j
public class TestFormat {

    @Test
    void testFormatZero() {
        val mesg = Nope.failure(Nuntium.of("T001", "Messagge zero")).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge zero", mesg);
    }
    @Test
    void testFormatArgTwo() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagges: {}, {}"), "Hola", "Mundo").fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagges: Hola, Mundo", mesg);
    }
    @Test
    void testFormatEscape() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge hot \\{}{}"), '*').fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge hot {}*", mesg);
    }
    @Test
    void testFormatNoEscape() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge hot {{}}"), '*').fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge hot {*}", mesg);
    }
    @Test
    void testFormatMissingArgs() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagges: {}, {}"), 1).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagges: 1, {}", mesg);
    }
    @Test
    void testFormatArgsOberflow() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagges: {}"), 1, 2).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagges: 1", mesg);
    }
    @Test
    void testFormatOject() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge obj->{}"), BigInteger.ZERO).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge obj->0", mesg);
    }
    @Test
    void testFormatBoolean() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge bool->{}"), true).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge bool->true", mesg);
    }
    @Test
    void testFormatByte() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge byte->{}"), (byte)0xff).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge byte->-1", mesg);
    }
    @Test
    void testFormatChar() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge char->{}"), (char)65).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge char->A", mesg);
    }
    @Test
    void testFormatShort() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge short->{}"), (short)0xffff).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge short->-1", mesg);
    }
    @Test
    void testFormatInt() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge int->{}"), 0xffffffff).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge int->-1", mesg);
    }
    @Test
    void testFormatLong() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge long->{}"), 0xffffffffffffffffL).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge long->-1", mesg);
    }
    @Test
    void testFormatFloat() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge float->{}"), 3.14F).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge float->3.14", mesg);
    }
    @Test
    void testFormatDouble() {
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge dble->{}"), 3.14159265897932384626433832).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge dble->3.1415926589793237", mesg);
    }

    @Test
    void testFormatOjectArray() {
        Object arg = new BigInteger[]{BigInteger.ZERO, BigInteger.TEN};
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge obj->{}"), arg ).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge obj->[0, 10]", mesg);
    }
    @Test
    void testFormatBooleanArray() {
        Object arg = new boolean[]{true, false};
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge bool->{}"), arg ).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge bool->[true, false]", mesg);
    }
    @Test
    void testFormatByteArray() {
        Object arg = new byte[]{1, 2};
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge byte->{}"), arg ).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge byte->[1, 2]", mesg);
    }
    @Test
    void testFormatCharArray() {
        Object arg = new char[]{'a', 'z'};
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge char->{}"), arg ).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge char->[a, z]", mesg);
    }
    @Test
    void testFormatShortArray() {
        Object arg = new short[]{1, (short) 0xffff};
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge short->{}"), arg ).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge short->[1, -1]", mesg);
    }
    @Test
    void testFormatIntArray() {
        Object arg = new int[]{1, 2};
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge int->{}"), arg ).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge int->[1, 2]", mesg);
    }
    @Test
    void testFormatLongArray() {
        Object arg = new long[]{1L, 2L};
        val mesg = Nope.failure(Nuntium.of("T002", "Messagge long->{}"), arg ).fault().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge long->[1, 2]", mesg);
    }
    @Test
    void testFormatFloatArray() {
        Object arg = new float[]{3.14F, 1.41F};
        val mesg = None.builder().withAlert(Nuntium.of("T002", "Messagge float->{}"), arg).build().signals().iterator().next().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge float->[3.14, 1.41]", mesg);
    }
    @Test
    void testFormatDoubleArray() {
        Object arg = new double[]{3.1415, 1.4142};
        val mesg = None.builder().withAlert(Nuntium.of("T002", "Messagge dble->{}"), arg).build().signals().iterator().next().message();
        log.info("Message: {}", mesg);
        Assertions.assertEquals("Messagge dble->[3.1415, 1.4142]", mesg);
    }

}
