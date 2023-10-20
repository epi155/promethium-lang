package io.github.epi155.test;


import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.NoneBuilder;
import io.github.epi155.pm.lang.Some;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestWarning {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestWarning.class);
    private None good() {
        return None.none();
    }

    private None fail() {
        return None.fault(PmCustMsg.of("E01", "Errore"));
    }

    private None warn() {
        return None.alert(PmCustMsg.of("W01", "Allarme"));
    }

    private None wArn() {
        @NotNull NoneBuilder bld = None.builder();
        bld.alert(PmCustMsg.of("W02", "Attenzione"))
                .setProperty("altezza", 3.14F)
                .setProperty("larghezza", 1.41F)
                .setProperty("profondità", "N/A");
        return bld.build();
    }

    @Test
    void test111() {
        @NotNull None result = good().ergo(() -> good().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeSuccess());
    }
    @Test
    void test112() {
        @NotNull None result = good().ergo(() -> good().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
    }
    @Test
    void test113() {
        @NotNull None result = good().ergo(() -> good().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
    }
    @Test
    void test121() {
        @NotNull None result = good().ergo(() -> warn().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
    }
    @Test
    void test122() {
        @NotNull None result = good().ergo(() -> warn().ergo(this::wArn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test123() {
        @NotNull None result = good().ergo(() -> warn().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test131() {
        @NotNull None result = good().ergo(() -> fail().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
    }
    @Test
    void test132() {
        @NotNull None result = good().ergo(() -> fail().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test133() {
        @NotNull None result = good().ergo(() -> fail().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }

    @Test
    void test211() {
        @NotNull None result = warn().ergo(() -> good().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
    }
    @Test
    void test212() {
        @NotNull None result = warn().ergo(() -> good().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test213() {
        @NotNull None result = warn().ergo(() -> good().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test221() {
        @NotNull None result = warn().ergo(() -> warn().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWarning());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test231() {
        @NotNull None result = warn().ergo(() -> fail().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test232() {
        @NotNull None result = warn().ergo(() -> fail().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }
    @Test
    void test233() {
        @NotNull None result = warn().ergo(() -> fail().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(2, result.signals().size());
    }

    @Test
    void test311() {
        @NotNull None result = fail().ergo(() -> good().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test312() {
        @NotNull None result = fail().ergo(() -> good().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test313() {
        @NotNull None result = fail().ergo(() -> good().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test321() {
        @NotNull None result = fail().ergo(() -> warn().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test331() {
        @NotNull None result = fail().ergo(() -> fail().ergo(this::good));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test332() {
        @NotNull None result = fail().ergo(() -> fail().ergo(this::warn));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test333() {
        @NotNull None result = fail().ergo(() -> fail().ergo(this::fail));
        log.info("result is: {}", result);
        Assertions.assertTrue(result.completeWithErrors());
        Assertions.assertEquals(1, result.signals().size());
    }
    @Test
    void test500() {
        @NotNull Some<Object> bx = Some.of(null);
        log.info("result is: {}", bx);
        @NotNull Some<Object> cx = Some.of(None.builder().fault(PmCustMsg.of("E92", "e92")));
        log.info("result is: {}", cx);
    }
}
