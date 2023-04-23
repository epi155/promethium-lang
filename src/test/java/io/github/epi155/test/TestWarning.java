package io.github.epi155.test;


import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.Some;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestWarning {
    private None good() {
        return None.none();
    }

    private None fail() {
        return None.fault(CustMsg.of("E01", "Errore"));
    }

    private None warn() {
        return None.alert(CustMsg.of("W01", "Allarme"));
    }

    private None wArn() {
        val bld = None.builder();
        bld.alert(CustMsg.of("W02", "Attenzione"))
                .setProperty("altezza", 3.14F)
                .setProperty("larghezza", 1.41F)
                .setProperty("profondità", "N/A");
        return bld.build();
    }

    @Test
    void test111() {
        val result = good().ergo(() -> good().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeSuccess());
    }
    @Test
    void test112() {
        val result = good().ergo(() -> good().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
    }
    @Test
    void test113() {
        val result = good().ergo(() -> good().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
    }
    @Test
    void test121() {
        val result = good().ergo(() -> warn().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
    }
    @Test
    void test122() {
        val result = good().ergo(() -> warn().ergo(this::wArn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test123() {
        val result = good().ergo(() -> warn().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test131() {
        val result = good().ergo(() -> fail().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
    }
    @Test
    void test132() {
        val result = good().ergo(() -> fail().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test133() {
        val result = good().ergo(() -> fail().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }

    @Test
    void test211() {
        val result = warn().ergo(() -> good().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
    }
    @Test
    void test212() {
        val result = warn().ergo(() -> good().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test213() {
        val result = warn().ergo(() -> good().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test221() {
        val result = warn().ergo(() -> warn().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test231() {
        val result = warn().ergo(() -> fail().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test232() {
        val result = warn().ergo(() -> fail().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test233() {
        val result = warn().ergo(() -> fail().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }

    @Test
    void test311() {
        val result = fail().ergo(() -> good().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test312() {
        val result = fail().ergo(() -> good().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test313() {
        val result = fail().ergo(() -> good().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test321() {
        val result = fail().ergo(() -> warn().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test331() {
        val result = fail().ergo(() -> fail().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test332() {
        val result = fail().ergo(() -> fail().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test333() {
        val result = fail().ergo(() -> fail().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test500() {
        val bx = Some.of(null);
        log.info("result is: {}", bx);
        val cx = Some.of(None.builder().fault(CustMsg.of("E92", "e92")));
        log.info("result is: {}", cx);
    }
}
