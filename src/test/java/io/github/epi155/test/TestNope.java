package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.Nope;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

@Slf4j
public class TestNope {


    @Test
    public void test1() {
        Nope.nope()
            .onSuccess(() -> log.info("All fine"))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    public void test2() {
        Nope.fault(CustMsg.of("E01", "Houston we have had a problem"))
            .onSuccess(() -> log.info("All fine"))
            .onFailure(e -> log.warn("Oops {}", e.message()));
    }

    @Test
    public void test3() {
        val result = Nope.nope()
            .mapTo(() -> "all fine", e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
    }

    @Test
    public void test4() {
        val result = Nope.fault(CustMsg.of("E01", "Houston we have had a problem"))
                .mapTo(() -> "all fine", e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
    }

    @Test
    public void test7() {
        val nope = Nope.nope();
        nope.peek(() -> log.info("to be continue"));
    }

    @Test
    public void test8() {
        val nope = Nope.fault(CustMsg.of("E01", "Houston we have had a problem"));
        nope.peek(() -> log.info("to be continue"));
    }

    @Test
    public void test9() {
        val nope = Nope.nope();
        nope.ergo(Nope::nope);
    }

    @Test
    public void test10() {
        val nope = Nope.nope();
        nope.ergo(() -> Nope.capture(new NullPointerException()));
    }

    @Test
    public void test12() {
        val nope = Nope.fault(CustMsg.of("E01", "Houston we have had a problem"));
        nope.ergo(Nope::nope);
    }

    @Test
    public void test13() {
        val nope = Nope.nope();
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            val fault = nope.failure();
        });
    }

    @Test
    public void test14() {
        val nope = Nope.fault(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertDoesNotThrow(() -> {
            val fault = nope.failure();
        });
    }
    @Test
    public void test15() {
        Nope n1 = Nope.nope();
        Assertions.assertFalse(n1.summary().isPresent());

        val n2 = Nope.fault(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertTrue(n2.summary().isPresent());
    }

}
