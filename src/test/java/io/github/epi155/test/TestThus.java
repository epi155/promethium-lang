package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.Nope;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestThus {
    private static final CustMsg MY_FAULT = PmCustMsg.of("EA01", "Oop error {} !!");

    @Test
    void testHope1() {
        @NotNull Nope a3 = Hope.of(1).ergoes(k -> Hope.of(k + 1));
        Assertions.assertTrue(a3.completeSuccess());
        @NotNull Nope a4 = Hope.of(1).ergoes(k -> Nope.nope());
        Assertions.assertTrue(a4.completeSuccess());

        @NotNull Nope b3 = Hope.of(1).ergoes(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());
        @NotNull Nope b4 = Hope.of(1).ergoes(k -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(b4.completeWithErrors());

        @NotNull Nope e3 = Hope.<Integer>fault(MY_FAULT).ergoes(k -> Hope.of(k + 1));
        Assertions.assertTrue(e3.completeWithErrors());
        @NotNull Nope e4 = Hope.<Integer>fault(MY_FAULT).ergoes(k -> Nope.nope());
        Assertions.assertTrue(e4.completeWithErrors());

        @NotNull Nope f3 = Hope.<Integer>fault(MY_FAULT).ergoes(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
        @NotNull Nope f4 = Hope.<Integer>fault(MY_FAULT).ergoes(k -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(f4.completeWithErrors());
        Assertions.assertEquals(1, f4.signals().size());
    }

    @Test
    void testNope1() {
        @NotNull Nope a3 = Nope.nope().ergoes(() -> Hope.of(1));
        Assertions.assertTrue(a3.completeSuccess());
        @NotNull Nope a4 = Nope.nope().ergoes(Nope::nope);
        Assertions.assertTrue(a4.completeSuccess());

        @NotNull Nope b3 = Nope.nope().ergoes(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());
        @NotNull Nope b4 = Nope.nope().ergoes(() -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(b4.completeWithErrors());

        @NotNull Nope e3 = Nope.fault(MY_FAULT).ergoes(() -> Hope.of(1));
        Assertions.assertTrue(e3.completeWithErrors());
        @NotNull Nope e4 = Nope.fault(MY_FAULT).ergoes(Nope::nope);
        Assertions.assertTrue(e4.completeWithErrors());

        @NotNull Nope f3 = Nope.fault(MY_FAULT).ergoes(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
        @NotNull Nope f4 = Nope.fault(MY_FAULT).ergoes(() -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(f4.completeWithErrors());
        Assertions.assertEquals(1, f4.signals().size());
    }
}
