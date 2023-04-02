package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.FailureException;
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
        Nope.failure(CustMsg.of("E01", "Houston we have had a problem"))
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
        val result = Nope.failure(CustMsg.of("E01", "Houston we have had a problem"))
            .mapTo(() -> "all fine", e -> String.format("oops %s", e.message()));
        log.info("Result is {}", result);
    }

    @Test
    public void test5() {
        val nope = Nope.nope();
        Assertions.assertDoesNotThrow(() -> nope.orThrow());
    }

    @Test
    public void test6() {
        val nope = Nope.failure(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertThrows(FailureException.class, nope::orThrow);
    }

    @Test
    public void test7() {
        val nope = Nope.nope();
        nope.peek(() -> log.info("to be continue"));
    }

    @Test
    public void test8() {
        val nope = Nope.failure(CustMsg.of("E01", "Houston we have had a problem"));
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
    public void test11() {
        val nope = Nope.nope();
        nope.ergo(() -> Nope.captureHere(new NullPointerException()));
    }

    @Test
    public void test12() {
        val nope = Nope.failure(CustMsg.of("E01", "Houston we have had a problem"));
        nope.ergo(Nope::nope);
    }

    @Test
    public void test13() {
        val nope = Nope.nope();
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            val fault = nope.fault();
        });
    }

    @Test
    public void test14() {
        val nope = Nope.failure(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertDoesNotThrow(() -> {
            val fault = nope.fault();
        });
    }

    @Test
    public void test15() {
        val nope = Nope.nope();
        Assertions.assertDoesNotThrow(() -> nope.orThrow(fault -> new FailureException(CustMsg.of("E02", "Error is {}"), fault.message())));
    }

    @Test
    public void test16() {
        val nope = Nope.failure(CustMsg.of("E01", "Houston we have had a problem"));
        Assertions.assertThrows(FailureException.class, () -> nope.orThrow(fault -> new FailureException(CustMsg.of("E02", "Error is {}"), fault.message())));
    }
}
