package io.github.epi155.test;


import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestFailure {
    static final CustMsg CUST_ERR = CustMsg.of("AZ15", "Errore Gestito");

    static {
        log.info(System.getProperty("java.version"));
    }

    @Test
    @Order(10)
    public void testCapture() {
        log.info("1.0 Cattura eccezione (package) ...");
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.capture(e);
        }
        val esito = bld.build();
        Assertions.assertFalse(esito.completeSuccess());
        val ce = esito.signals();
        Assertions.assertEquals(1, ce.size());
        val e = ce.iterator().next();
        Assertions.assertEquals("java.lang.NullPointerException", e.message());
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        log.error("* Errore: {}", esito);
    }


    @Test
    @Order(42)
    public void testCaptureHope() {
        log.info("4.2 Cattura eccezione Hope ...");
        Hope<String> hope;
        try {
            crashMethod();
            hope = Hope.of("Hola");
        } catch (Exception e) {
            hope = Hope.capture(e);
        }
        Assertions.assertFalse(hope.completeSuccess());
        val e = hope.failure();
        Assertions.assertTrue(e.message().contains("NullPointerException"));
        val reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        Assertions.assertEquals("999J", e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }


    @Test
    @Order(50)
    public void testEmpty() {
        log.info("5.0 Some non valorizzato ...");
        envelope1()
                .onSuccess(k -> log.info("Result is {}", k))
                .onFailure(le -> {
                    Assertions.assertEquals(1, le.size());
                    val e = le.iterator().next();
                    Assertions.assertEquals("999B", e.code());
                    val reason = e.place();
                    Assertions.assertNotNull(reason);
                    Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
                    Assertions.assertTrue(reason.contains("envelope1"));
                    log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
                });
    }

    @Test
    @Order(60)
    public void testCustomError0() {
        log.info("6.0 Custom Error ...");
        val error = CustMsg.of("T033", "Custom Error");
        val e = Hope.fault(error);
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
    public void testCustomError1() {
        log.info("6.1 Custom Error ...");
        val error = CustMsg.of("T033", "Custom Error");
        val e = Nope.fault(error);
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
    public void testCustomError2() {
        log.info("6.2 Custom Error ...");
        val error = CustMsg.of("T033", "Custom Error");
        val e = Some.fault(error);
        e.onFailure(le -> {
            Assertions.assertEquals(1, le.size());
            val z = le.iterator().next();
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
    public void testCustomError3() {
        log.info("6.3 Custom Error ...");
        val error = CustMsg.of("T033", "Custom Error");
        val e = None.fault(error);
        e.onFailure(le -> {
            Assertions.assertEquals(1, le.size());
            val z = le.iterator().next();
            Assertions.assertEquals("Custom Error", z.message());
            val reason = z.place();
            Assertions.assertNotNull(reason);
            Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
            Assertions.assertTrue(reason.contains("testCustomError"));
            log.error("* Errore: [{}] - ({},{})", z.message(), z.code(), z.place());
        });
    }

    @Test
    @Order(900)
    public void test900() {
        val some = Some.fault(CUST_ERR);
        Assertions.assertFalse(some.completeSuccess());
    }


    @Test
    @Order(910)
    public void test910() {
        val none = None.fault(CUST_ERR);
        Assertions.assertFalse(none.completeSuccess());
    }

    @Test
    @Order(911)
    public void test911() {
        try {
            crashMethod();
        } catch (Exception e) {
            val none = None.capture(e);
            Assertions.assertFalse(none.completeSuccess());
        }
    }

    @Test
    @Order(922)
    public void test922() {
        val some = Nope.nope();
        Assertions.assertTrue(some.completeSuccess());
    }

    @Test
    @Order(920)
    public void test920() {
        val some = Nope.fault(CUST_ERR);
        Assertions.assertFalse(some.completeSuccess());
    }

    @Test
    @Order(930)
    public void test930() {
        val bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.capture(e);
        }
        val none = bld.build();

        Assertions.assertFalse(none.completeSuccess());
    }

    @Test
    @Order(931)
    void test931() {
        Assertions.assertDoesNotThrow(() -> {
            val fault = None.builder().fault(CustMsg.of("AZ95", 404, "Not Found"))
                    .setProperty("key1", 1).setProperty("key2", "red");
            log.info(fault.toString());
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
