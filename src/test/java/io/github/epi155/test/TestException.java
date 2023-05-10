package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.FailureException;
import io.github.epi155.pm.lang.Nope;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
class TestException {
    private static final CustMsg MY_FAULT = PmCustMsg.of("EA01", "Oop error {} !!");

    @Test
    void test01() {
        Nope a1 = null;
        try {
            a1 = dump1();
        } catch (FailureException e) {
            a1 = Nope.capture(e);
        }
        log.warn("Errore1: {}", a1);

        Nope a2 = null;
        try {
            a2 = dump2();
        } catch (IOException e) {
            a2 = Nope.capture(e);
        }
        log.warn("Errore2: {}", a2);
        Nope a3 = null;
        try {
            a3 = dump3();
        } catch (FailureException e) {
            a3 = Nope.capture(e);
        }
        log.warn("Errore3: {}", a3);
    }

    private Nope dump3() throws FailureException {
        return dump1();
    }

    private Nope dump1() throws FailureException {
        throw new FailureException(MY_FAULT);
    }

    private Nope dump2() throws IOException {
        throw new IOException("Aaa");
    }
}
