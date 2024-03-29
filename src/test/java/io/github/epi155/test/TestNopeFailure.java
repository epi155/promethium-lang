package io.github.epi155.test;


import io.github.epi155.pm.lang.Failure;
import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.Nope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestNopeFailure {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestNopeFailure.class);

    private void crashMethod() {
        throw new NullPointerException();
    }

    @Test
    @Order(10)
    void testCapture() {
        log.info("1.0 Cattura eccezione (package) ...");
        Nope nope;
        try {
            crashMethod();
            nope = Nope.nope();
        } catch (Exception e) {
            nope = Nope.capture(e);
        }
        Assertions.assertFalse(nope.completeSuccess());
        @NotNull Failure fault = nope.failure();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        @Nullable String reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        log.error("* Errore: [{}] - ({},{})", fault.message(), fault.code(), fault.place());
    }

    @Test
    @Order(11)
    void testCaptureHere() {
        log.info("1.1 Cattura eccezione (class) ...");
        Nope nope;
        try {
            crashMethod();
            nope = Nope.nope();
        } catch (Exception e) {
            nope = Nope.capture(e);
        }
        Assertions.assertFalse(nope.completeSuccess());
        @NotNull Failure fault = nope.failure();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        @Nullable String reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("testCaptureHere"));
    }

    @Test
    @Order(20)
    void testCaptureH() {
        log.info("2.0 Cattura eccezione (package) ...");
        Hope<Integer> hope;
        try {
            crashMethod();
            hope = Hope.of(1);
        } catch (Exception e) {
            hope = Hope.capture(e);
        }
        Assertions.assertFalse(hope.completeSuccess());
        @NotNull Failure fault = hope.failure();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        @Nullable String reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        log.error("* Errore: [{}] - ({},{})", fault.message(), fault.code(), fault.place());
    }

    @Test
    @Order(21)
    void testCaptureHereH() {
        log.info("2.1 Cattura eccezione (class) ...");
        Hope<Integer> hope;
        try {
            crashMethod();
            hope = Hope.of(1);
        } catch (Exception e) {
            hope = Hope.capture(e);
        }
        Assertions.assertFalse(hope.completeSuccess());
        @NotNull Failure fault = hope.failure();
        Assertions.assertEquals("java.lang.NullPointerException", fault.message());
        @Nullable String reason = fault.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestNopeFailure"));
        Assertions.assertTrue(reason.contains("testCaptureHereH"));
        log.error("* Errore: [{}] - ({},{})", fault.message(), fault.code(), fault.place());
    }

}
