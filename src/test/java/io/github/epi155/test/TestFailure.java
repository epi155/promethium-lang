package io.github.epi155.test;


import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestFailure {
    static final MsgError CUST_ERR = MsgError.of("AZ15", "Errore Gestito");

    static {
        log.info(System.getProperty("java.version"));
    }

    @Test
    @Order(10)
    void testCapture() {
        log.info("1.0 Cattura eccezione (package) ...");
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.captureException(e);
        }
        val esito = bld.build();
        Assertions.assertFalse(esito.isSuccess());
        val ce = esito.errors();
        Assertions.assertEquals(1, ce.size());
        val e = ce.iterator().next();
        Assertions.assertEquals("java.lang.NullPointerException", e.message());
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }

    @Test @Order(20)
    void testCaptureHere() {
        log.info("2.0 Cattura eccezione (class) ...");
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.captureHere(e);
        }
        val esito = bld.build();
        Assertions.assertFalse(esito.isSuccess());
        val ce = esito.errors();
        Assertions.assertEquals(1, ce.size());
        val e = ce.iterator().next();
        Assertions.assertEquals("java.lang.NullPointerException", e.message());
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("testCaptureHere"));
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }

    @Test @Order(30)
    void testCaptureCustomE() {
        log.info("3.0 Cattura eccezione (package/custom) ...");
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.captureException(e, CUST_ERR);
        }
        val esito = bld.build();
        Assertions.assertFalse(esito.isSuccess());
        val ce = esito.errors();
        Assertions.assertEquals(1, ce.size());
        val e = ce.iterator().next();
        Assertions.assertTrue(e.message().startsWith("Errore Gestito"));
        Assertions.assertTrue(e.message().contains("java.lang.NullPointerException"));
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        Assertions.assertEquals(CUST_ERR.code(), e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }
    @Test @Order(31)
    void testCaptureCustomM() {
        log.info("3.1 Cattura eccezione/message (package/custom) ...");
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.captureMessage(e, CUST_ERR);
        }
        val esito = bld.build();
        Assertions.assertFalse(esito.isSuccess());
        val ce = esito.errors();
        Assertions.assertEquals(1, ce.size());
        val e = ce.iterator().next();
        Assertions.assertTrue(e.message().startsWith("Errore Gestito"));
        Assertions.assertTrue(e.message().contains("null"));
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        Assertions.assertEquals(CUST_ERR.code(), e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }
    @Test @Order(41)
    void testCaptureCustomHM() {
        log.info("4.1 Cattura eccezione/message (class/custom) ...");
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.captureHereMessage(e, CUST_ERR);
        }
        val esito = bld.build();
        Assertions.assertFalse(esito.isSuccess());
        val ce = esito.errors();
        Assertions.assertEquals(1, ce.size());
        val e = ce.iterator().next();
        Assertions.assertTrue(e.message().startsWith("Errore Gestito"));
        Assertions.assertTrue(e.message().contains("null"));
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("testCaptureCustomHM"));
        Assertions.assertEquals(CUST_ERR.code(), e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }
    @Test @Order(40)
    void testCaptureCustomHE() {
        log.info("4.0 Cattura eccezione (class/custom) ...");
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.captureHereException(e, CUST_ERR);
        }
        val esito = bld.build();
        Assertions.assertFalse(esito.isSuccess());
        val ce = esito.errors();
        Assertions.assertEquals(1, ce.size());
        val e = ce.iterator().next();
        Assertions.assertTrue(e.message().startsWith("Errore Gestito"));
        Assertions.assertTrue(e.message().contains("java.lang.NullPointerException"));
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("testCaptureCustomHE"));
        Assertions.assertEquals(CUST_ERR.code(), e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }

    @Test
    @Order(42)
    void testCaptureHope() {
        log.info("4.2 Cattura eccezione Hope ...");
        Hope<String> hope;
        try {
            crashMethod();
            hope = Hope.of("Hola");
        } catch (Exception e) {
            hope = Hope.capture(e);
        }
        Assertions.assertFalse(hope.isSuccess());
        val e = hope.fault();
        Assertions.assertTrue(e.message().contains("NullPointerException"));
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        Assertions.assertEquals("999J", e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }

    @Test
    @Order(43)
    void testCaptureHopeHere() {
        log.info("4.3 Cattura eccezione Hope Here ...");
        Hope<String> hope;
        try {
            crashMethod();
            hope = Hope.of("Hola");
        } catch (Exception e) {
            hope = Hope.captureHere(e);
        }
        Assertions.assertFalse(hope.isSuccess());
        val e = hope.fault();
        Assertions.assertTrue(e.message().contains("NullPointerException"));
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("testCaptureHopeHere"));
        Assertions.assertEquals("999J", e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }

    @Test
    @Order(44)
    void testFailureException() {
        log.info("4.4 Cattura eccezione Hope Here ...");
        val error = MsgError.of("T034", "Custom Error");
        try {
            try {
                crashMethod();
            } catch (NullPointerException e) {
                throw new FailureException(e, error);
            }
        } catch (FailureException ex) {
            val ho = Nope.capture(ex);
            val fault = ho.fault();
            Assertions.assertTrue(fault.message().contains("Custom Error"));
            val reason = fault.place();
            Assertions.assertNotNull(reason);
            Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
            Assertions.assertTrue(reason.contains("crashMethod"));
            Assertions.assertEquals(error.code(), fault.code());
            log.error("* Errore: [{}] - ({},{})", fault.message(), fault.code(), fault.place());
        }
    }

    @Test
    @Order(50)
    void testEmpty() {
        log.info("5.0 Some non valorizzato ...");
        envelope1()
                .onSuccess(k -> log.info("Result is {}", k))
                .onFailure(es -> {
                    List<Failure> le = es.collect(Collectors.toList());
                    Assertions.assertEquals(1, le.size());
                    val e = le.get(0);
                    Assertions.assertEquals("java.util.NoSuchElementException", e.message());
                    val reason = e.place();
                    Assertions.assertNotNull(reason);
                    Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
                    Assertions.assertTrue(reason.contains("testEmpty"));
                    log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
                });
    }

    @Test
    @Order(60)
    void testCustomError0() {
        log.info("6.0 Custom Error ...");
        val error = MsgError.of("T033", "Custom Error");
        val e = Hope.failure(error);
        e.onFailure(z -> {
            Assertions.assertEquals("Custom Error", z.message());
            val reason = z.place();
            Assertions.assertNotNull(reason);
            Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
            Assertions.assertTrue(reason.contains("testCustomError"));
            log.error("* Errore: [{}] - ({},{})", z.message(), z.code(), z.place());
        });
    }

    @Test
    @Order(61)
    void testCustomError1() {
        log.info("6.1 Custom Error ...");
        val error = MsgError.of("T033", "Custom Error");
        val e = Nope.failure(error);
        e.onFailure(z -> {
            Assertions.assertEquals("Custom Error", z.message());
            val reason = z.place();
            Assertions.assertNotNull(reason);
            Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
            Assertions.assertTrue(reason.contains("testCustomError"));
            log.error("* Errore: [{}] - ({},{})", z.message(), z.code(), z.place());
        });
    }

    @Test
    @Order(62)
    void testCustomError2() {
        log.info("6.2 Custom Error ...");
        val error = MsgError.of("T033", "Custom Error");
        val e = Some.failure(error);
        e.onFailure(zs -> {
            List<Failure> le = zs.collect(Collectors.toList());
            Assertions.assertEquals(1, le.size());
            val z = le.get(0);
            Assertions.assertEquals("Custom Error", z.message());
            val reason = z.place();
            Assertions.assertNotNull(reason);
            Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
            Assertions.assertTrue(reason.contains("testCustomError"));
            log.error("* Errore: [{}] - ({},{})", z.message(), z.code(), z.place());
        });
    }

    @Test
    @Order(63)
    void testCustomError3() {
        log.info("6.3 Custom Error ...");
        val error = MsgError.of("T033", "Custom Error");
        val e = None.failure(error);
        e.onFailure(zs -> {
            List<Failure> le = zs.collect(Collectors.toList());
            Assertions.assertEquals(1, le.size());
            val z = le.get(0);
            Assertions.assertEquals("Custom Error", z.message());
            val reason = z.place();
            Assertions.assertNotNull(reason);
            Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
            Assertions.assertTrue(reason.contains("testCustomError"));
            log.error("* Errore: [{}] - ({},{})", z.message(), z.code(), z.place());
        });
    }


    private Some<Integer> envelope1() {
        val bld = Some.<Integer>builder();
        return bld.build();
    }

    private void crashMethod() {
        throw new NullPointerException();
    }

}
