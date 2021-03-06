package io.github.epi155.test;


import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.Nope;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestNopeFailure {

    private void crashMethod() {
        throw new NullPointerException();
    }

    @Test @Order(10)
    void testCapture() {
        log.info("1.0 Cattura eccezione (package) ...");
        Nope nope;
        try {
            crashMethod();
            nope = Nope.nope();
        } catch (Exception e) {
            nope = Nope.capture(e);
        }
        Assertions.assertFalse(nope.isSuccess());
        val fault = nope.fault();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        val reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        log.error("* Errore: [{}] - ({},{})", fault.message(), fault.code(), fault.place());
    }

    @Test @Order(11)
    void testCaptureHere() {
        log.info("1.1 Cattura eccezione (class) ...");
        Nope nope;
        try {
            crashMethod();
            nope = Nope.nope();
        } catch (Exception e) {
            nope = Nope.captureHere(e);
        }
        Assertions.assertFalse(nope.isSuccess());
        val fault = nope.fault();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        val reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("testCaptureHere"));
    }

    @Test @Order(20)
    void testCaptureH() {
        log.info("2.0 Cattura eccezione (package) ...");
        Hope<Integer> hope;
        try {
            crashMethod();
            hope = Hope.of(1);
        } catch (Exception e) {
            hope = Hope.capture(e);
        }
        Assertions.assertFalse(hope.isSuccess());
        val fault = hope.fault();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        val reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        log.error("* Errore: [{}] - ({},{})", fault.message(), fault.code(), fault.place());
    }
    @Test @Order(21)
    void testCaptureHereH() {
        log.info("2.1 Cattura eccezione (class) ...");
        Hope<Integer> hope;
        try {
            crashMethod();
            hope = Hope.of(1);
        } catch (Exception e) {
            hope = Hope.captureHere(e);
        }
        Assertions.assertFalse(hope.isSuccess());
        val fault = hope.fault();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        val reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("testCaptureHereH"));
        log.error("* Errore: [{}] - ({},{})", fault.message(), fault.code(), fault.place());
    }

}
