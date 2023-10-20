package io.github.epi155.test;


import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;

import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestFailure {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestFailure.class);

    static final CustMsg CUST_ERR = PmCustMsg.of("AZ15", "Errore Gestito");

    static {
        log.info(System.getProperty("java.version"));
    }

    @Test
    @Order(10)
    void testCapture() {
        log.info("1.0 Cattura eccezione (package) ...");
        @NotNull NoneBuilder bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.capture(e);
        }
        @NotNull None esito = bld.build();
        Assertions.assertFalse(esito.completeSuccess());
        Collection<Signal> ce = esito.signals();
        Assertions.assertEquals(1, ce.size());
        Signal e = ce.iterator().next();
        Assertions.assertEquals("java.lang.NullPointerException", e.message());
        @Nullable String reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        log.error("* Errore: {}", esito);
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
        Assertions.assertFalse(hope.completeSuccess());
        @NotNull Failure e = hope.failure();
        Assertions.assertTrue(e.message().contains("NullPointerException"));
        @Nullable String reason = e.place();
        Assertions.assertNotNull(reason);
        Assertions.assertTrue(reason.startsWith("[ io.github.epi155.test.TestFailure"));
        Assertions.assertTrue(reason.contains("crashMethod"));
        Assertions.assertEquals("999J", e.code());
        log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
    }


    @Test
    @Order(50)
    void testEmpty() {
        log.info("5.0 Some non valorizzato ...");
        envelope1()
            .onSuccess(k -> log.info("Result is {}", k))
            .onFailure(le -> {
                Assertions.assertEquals(1, le.size());
                Signal e = le.iterator().next();
                Assertions.assertEquals("999B", e.code());
                @Nullable String reason = e.place();
                Assertions.assertNotNull(reason);
                Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
                    Assertions.assertTrue(reason.contains("envelope1"));
                    log.error("* Errore: [{}] - ({},{})", e.message(), e.code(), e.place());
                });
    }

    @Test
    @Order(60)
    void testCustomError0() {
        log.info("6.0 Custom Error ...");
        @NotNull CustMsg error = PmCustMsg.of("T033", "Custom Error");
        @NotNull Hope<Object> e = Hope.fault(error);
        e.onFailure(z -> {
            Assertions.assertEquals("Custom Error", z.message());
            @Nullable String reason = z.place();
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
        @NotNull CustMsg error = PmCustMsg.of("T033", "Custom Error");
        @NotNull Nope e = Nope.fault(error);
        e.onFailure(z -> {
            Assertions.assertEquals("Custom Error", z.message());
            @Nullable String reason = z.place();
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
        @NotNull CustMsg error = PmCustMsg.of("T033", "Custom Error");
        @NotNull Some<Object> e = Some.fault(error);
        e.onFailure(le -> {
            Assertions.assertEquals(1, le.size());
            Signal z = le.iterator().next();
            Assertions.assertEquals("Custom Error", z.message());
            @Nullable String reason = z.place();
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
        @NotNull CustMsg error = PmCustMsg.of("T033", "Custom Error");
        @NotNull None e = None.fault(error);
        e.onFailure(le -> {
            Assertions.assertEquals(1, le.size());
            Signal z = le.iterator().next();
            Assertions.assertEquals("Custom Error", z.message());
            @Nullable String reason = z.place();
            Assertions.assertNotNull(reason);
            Assertions.assertTrue(reason.startsWith("io.github.epi155.test.TestFailure"));
            Assertions.assertTrue(reason.contains("testCustomError"));
            log.error("* Errore: [{}] - ({},{})", z.message(), z.code(), z.place());
        });
    }

    @Test
    @Order(900)
    void test900() {
        @NotNull Some<Object> some = Some.fault(CUST_ERR);
        Assertions.assertFalse(some.completeSuccess());
    }


    @Test
    @Order(910)
    void test910() {
        @NotNull None none = None.fault(CUST_ERR);
        Assertions.assertFalse(none.completeSuccess());
    }

    @Test
    @Order(911)
    void test911() {
        try {
            crashMethod();
        } catch (Exception e) {
            @NotNull None none = None.capture(e);
            Assertions.assertFalse(none.completeSuccess());
        }
    }

    @Test
    @Order(922)
    void test922() {
        @NotNull Nope some = Nope.nope();
        Assertions.assertTrue(some.completeSuccess());
    }

    @Test
    @Order(920)
    void test920() {
        @NotNull Nope some = Nope.fault(CUST_ERR);
        Assertions.assertFalse(some.completeSuccess());
    }

    @Test
    @Order(930)
    void test930() {
        @NotNull NoneBuilder bld = None.builder();
        try {
            crashMethod();
        } catch (Exception e) {
            bld.capture(e);
        }
        @NotNull None none = bld.build();

        Assertions.assertFalse(none.completeSuccess());
    }

    @Test
    @Order(931)
    void test931() {
        Assertions.assertDoesNotThrow(() -> {
            @NotNull Signal fault = None.builder().fault(PmCustMsg.of("AZ95", 404, "Not Found"))
                    .setProperty("key1", 1).setProperty("key2", "red");
            log.info(fault.toString());
        });
    }


    private Some<Integer> envelope1() {
        @NotNull SomeBuilder<Integer> bld = Some.builder();
        return bld.build();
    }

    private void crashMethod() {
        throw new NullPointerException();
    }

}
