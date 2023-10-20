package io.github.epi155.test;

import io.github.epi155.pm.lang.Failure;
import io.github.epi155.pm.lang.Nope;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

class TestNope {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestNope.class);


    @Test
    void test1() {
        Nope.nope()
            .onSuccess(() -> log.info("All fine"))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    void test2() {
        Nope.fault(PmCustMsg.of("E01", "Houston we have had a problem"))
            .onSuccess(() -> log.info("All fine"))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    void test3() {
        String result = Nope.nope()
                .mapTo(() -> "all fine", e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
    }

    @Test
    void test4() {
        String result = Nope.fault(PmCustMsg.of("E01", "Houston we have had a problem"))
                .mapTo(() -> "all fine", e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
    }

    @Test
    void test7() {
        @NotNull Nope nope = Nope.nope();
        nope.implies(() -> log.info("to be continue"));
    }

    @Test
    void test8() {
        @NotNull Nope nope = Nope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        nope.implies(() -> log.info("to be continue"));
    }

    @Test
    void test9() {
        @NotNull Nope nope = Nope.nope();
        nope.ergo(Nope::nope);
    }

    @Test
    void test10() {
        @NotNull Nope nope = Nope.nope();
        nope.ergo(() -> Nope.capture(new NullPointerException()));
    }

    @Test
    void test12() {
        @NotNull Nope nope = Nope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        nope.ergo(Nope::nope);
    }

    @Test
    void test13() {
        @NotNull Nope nope = Nope.nope();
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            @NotNull Failure fault = nope.failure();
        });
    }

    @Test
    void test14() {
        @NotNull Nope nope = Nope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertDoesNotThrow(() -> {
            @NotNull Failure fault = nope.failure();
        });
    }

    @Test
    void test15() {
        Nope n1 = Nope.nope();
        Assertions.assertFalse(n1.summary().isPresent());

        @NotNull Nope n2 = Nope.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertTrue(n2.summary().isPresent());
    }

}
