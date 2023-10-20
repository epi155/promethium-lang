package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestErgo {
    private static final CustMsg MY_FAULT = PmCustMsg.of("EA01", "Oop error {} !!");
    private static final CustMsg MY_ALERT = PmCustMsg.of("WA01", "Oop warning {} !!");

    @Test
    void testSome1x01() {
        None a1 = Some.of(1).ergo(k -> Some.of(k + 1));
        Assertions.assertTrue(a1.completeSuccess());
        None a2 = Some.of(1).ergo(k -> None.none());
        Assertions.assertTrue(a2.completeSuccess());
        None a3 = Some.of(1).ergo(k -> Hope.of(k + 1));
        Assertions.assertTrue(a3.completeSuccess());
        None a4 = Some.of(1).ergo(k -> Nope.nope());
        Assertions.assertTrue(a4.completeSuccess());
    }

    @Test
    void testSome1x02() {
        None b1 = Some.of(1).ergo(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        None b2 = Some.of(1).ergo(k -> None.fault(MY_FAULT));
        Assertions.assertTrue(b2.completeWithErrors());
        None b3 = Some.of(1).ergo(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());
        None b4 = Some.of(1).ergo(k -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(b4.completeWithErrors());

        None b5 = Some.of(1).ergo(k -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(k + 1));
        Assertions.assertTrue(b5.completeWarning());
        None b6 = Some.of(1).ergo(k -> None.alert(MY_ALERT));
        Assertions.assertTrue(b6.completeWarning());
        None b7 = Some.of(1).ergo(k -> Some.<Integer>builder().withFault(MY_FAULT).buildWithValue(k + 1));
        Assertions.assertTrue(b7.completeWithErrors());
    }

    @Test
    void testSome1x03() {
        None c1 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> Some.of(k + 1));
        Assertions.assertTrue(c1.completeWarning());
        None c2 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> None.none());
        Assertions.assertTrue(c2.completeWarning());
        None c3 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> Hope.of(k + 1));
        Assertions.assertTrue(c3.completeWarning());
        None c4 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> Nope.nope());
        Assertions.assertTrue(c4.completeWarning());
    }

    @Test
    void testSome1x04() {
        None d1 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(d1.completeWithErrors());
        Assertions.assertEquals(2, d1.signals().size());
        None d2 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> None.fault(MY_FAULT));
        Assertions.assertTrue(d2.completeWithErrors());
        Assertions.assertEquals(2, d2.signals().size());
        None d3 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(d3.completeWithErrors());
        Assertions.assertEquals(2, d3.signals().size());
        None d4 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(d4.completeWithErrors());
        Assertions.assertEquals(2, d4.signals().size());

        None d5 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(k + 1));
        Assertions.assertTrue(d5.completeWarning());
        Assertions.assertEquals(2, d5.signals().size());
        None d6 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).ergo(k -> None.alert(MY_ALERT));
        Assertions.assertTrue(d6.completeWarning());
        Assertions.assertEquals(2, d6.signals().size());
    }

    @Test
    void testSome1x05() {
        None e1 = Some.<Integer>fault(MY_FAULT).ergo(k -> Some.of(k + 1));
        Assertions.assertTrue(e1.completeWithErrors());
        None e2 = Some.<Integer>fault(MY_FAULT).ergo(k -> None.none());
        Assertions.assertTrue(e2.completeWithErrors());
        None e3 = Some.<Integer>fault(MY_FAULT).ergo(k -> Hope.of(k + 1));
        Assertions.assertTrue(e3.completeWithErrors());
        None e4 = Some.<Integer>fault(MY_FAULT).ergo(k -> Nope.nope());
        Assertions.assertTrue(e4.completeWithErrors());
    }

    @Test
    void testSome1x06() {
        None f1 = Some.<Integer>fault(MY_FAULT).ergo(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        None f2 = Some.<Integer>fault(MY_FAULT).ergo(k -> None.fault(MY_FAULT));
        Assertions.assertTrue(f2.completeWithErrors());
        Assertions.assertEquals(1, f2.signals().size());
        None f3 = Some.<Integer>fault(MY_FAULT).ergo(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
        None f4 = Some.<Integer>fault(MY_FAULT).ergo(k -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(f4.completeWithErrors());
        Assertions.assertEquals(1, f4.signals().size());
    }

    @Test
    void testNone1x01() {
        None a1 = None.none().ergo(() -> Some.of(1));
        Assertions.assertTrue(a1.completeSuccess());
        None a2 = None.none().ergo(None::none);
        Assertions.assertTrue(a2.completeSuccess());
        None a3 = None.none().ergo(() -> Hope.of(1));
        Assertions.assertTrue(a3.completeSuccess());
        None a4 = None.none().ergo(Nope::nope);
        Assertions.assertTrue(a4.completeSuccess());
    }

    @Test
    void testNone1x02() {
        None b1 = None.none().ergo(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        None b2 = None.none().ergo(() -> None.fault(MY_FAULT));
        Assertions.assertTrue(b2.completeWithErrors());
        None b3 = None.none().ergo(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());
        None b4 = None.none().ergo(() -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(b4.completeWithErrors());

        None b5 = None.none().ergo(() -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1));
        Assertions.assertTrue(b5.completeWarning());
        None b6 = None.none().ergo(() -> None.alert(MY_ALERT));
        Assertions.assertTrue(b6.completeWarning());
    }

    @Test
    void testNone1x03() {
        None c1 = None.alert(MY_ALERT).ergo(() -> Some.of(1));
        Assertions.assertTrue(c1.completeWarning());
        None c2 = None.alert(MY_ALERT).ergo(None::none);
        Assertions.assertTrue(c2.completeWarning());
        None c3 = None.alert(MY_ALERT).ergo(() -> Hope.of(1));
        Assertions.assertTrue(c3.completeWarning());
        None c4 = None.alert(MY_ALERT).ergo(Nope::nope);
        Assertions.assertTrue(c4.completeWarning());
    }

    @Test
    void testNone1x04() {
        None d1 = None.alert(MY_ALERT).ergo(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(d1.completeWithErrors());
        Assertions.assertEquals(2, d1.signals().size());
        None d2 = None.alert(MY_ALERT).ergo(() -> None.fault(MY_FAULT));
        Assertions.assertTrue(d2.completeWithErrors());
        Assertions.assertEquals(2, d2.signals().size());
        None d3 = None.alert(MY_ALERT).ergo(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(d3.completeWithErrors());
        Assertions.assertEquals(2, d3.signals().size());
        None d4 = None.alert(MY_ALERT).ergo(() -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(d4.completeWithErrors());
        Assertions.assertEquals(2, d4.signals().size());

        None d5 = None.alert(MY_ALERT).ergo(() -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1));
        Assertions.assertTrue(d5.completeWarning());
        Assertions.assertEquals(2, d5.signals().size());
        None d6 = None.alert(MY_ALERT).ergo(() -> None.alert(MY_ALERT));
        Assertions.assertTrue(d6.completeWarning());
        Assertions.assertEquals(2, d6.signals().size());
    }

    @Test
    void testNone1x05() {
        None e1 = None.fault(MY_FAULT).ergo(() -> Some.of(1));
        Assertions.assertTrue(e1.completeWithErrors());
        None e2 = None.fault(MY_FAULT).ergo(None::none);
        Assertions.assertTrue(e2.completeWithErrors());
        None e3 = None.fault(MY_FAULT).ergo(() -> Hope.of(1));
        Assertions.assertTrue(e3.completeWithErrors());
        None e4 = None.fault(MY_FAULT).ergo(Nope::nope);
        Assertions.assertTrue(e4.completeWithErrors());
    }

    @Test
    void testNone1x06() {
        None f1 = None.fault(MY_FAULT).ergo(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        None f2 = None.fault(MY_FAULT).ergo(() -> None.fault(MY_FAULT));
        Assertions.assertTrue(f2.completeWithErrors());
        Assertions.assertEquals(1, f2.signals().size());
        None f3 = None.fault(MY_FAULT).ergo(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
        None f4 = None.fault(MY_FAULT).ergo(() -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(f4.completeWithErrors());
        Assertions.assertEquals(1, f4.signals().size());
    }

    @Test
    void testHope1() {
        None a1 = Hope.of(1).ergo(k -> Some.of(k + 1));
        Assertions.assertTrue(a1.completeSuccess());
        None a2 = Hope.of(1).ergo(k -> None.none());
        Assertions.assertTrue(a2.completeSuccess());
        None a3 = Hope.of(1).ergo(k -> Hope.of(k + 1));
        Assertions.assertTrue(a3.completeSuccess());
        None a4 = Hope.of(1).ergo(k -> Nope.nope());
        Assertions.assertTrue(a4.completeSuccess());

        None b1 = Hope.of(1).ergo(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        None b2 = Hope.of(1).ergo(k -> None.fault(MY_FAULT));
        Assertions.assertTrue(b2.completeWithErrors());
        None b3 = Hope.of(1).ergo(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());
        None b4 = Hope.of(1).ergo(k -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(b4.completeWithErrors());

        None b5 = Hope.of(1).ergo(k -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(k + 1));
        Assertions.assertTrue(b5.completeWarning());
        None b6 = Hope.of(1).ergo(k -> None.alert(MY_ALERT));
        Assertions.assertTrue(b6.completeWarning());


        None e1 = Hope.<Integer>fault(MY_FAULT).ergo(k -> Some.of(k + 1));
        Assertions.assertTrue(e1.completeWithErrors());
        None e2 = Hope.<Integer>fault(MY_FAULT).ergo(k -> None.none());
        Assertions.assertTrue(e2.completeWithErrors());
        None e3 = Hope.<Integer>fault(MY_FAULT).ergo(k -> Hope.of(k + 1));
        Assertions.assertTrue(e3.completeWithErrors());
        None e4 = Hope.<Integer>fault(MY_FAULT).ergo(k -> Nope.nope());
        Assertions.assertTrue(e4.completeWithErrors());

        None f1 = Hope.<Integer>fault(MY_FAULT).ergo(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        None f2 = Hope.<Integer>fault(MY_FAULT).ergo(k -> None.fault(MY_FAULT));
        Assertions.assertTrue(f2.completeWithErrors());
        Assertions.assertEquals(1, f2.signals().size());
        None f3 = Hope.<Integer>fault(MY_FAULT).ergo(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
        None f4 = Hope.<Integer>fault(MY_FAULT).ergo(k -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(f4.completeWithErrors());
        Assertions.assertEquals(1, f4.signals().size());
    }

    @Test
    void testNope1() {
        None a1 = Nope.nope().ergo(() -> Some.of(1));
        Assertions.assertTrue(a1.completeSuccess());
        None a2 = Nope.nope().ergo(None::none);
        Assertions.assertTrue(a2.completeSuccess());
        None a3 = Nope.nope().ergo(() -> Hope.of(1));
        Assertions.assertTrue(a3.completeSuccess());
        None a4 = Nope.nope().ergo(Nope::nope);
        Assertions.assertTrue(a4.completeSuccess());

        None b1 = Nope.nope().ergo(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        None b2 = Nope.nope().ergo(() -> None.fault(MY_FAULT));
        Assertions.assertTrue(b2.completeWithErrors());
        None b3 = Nope.nope().ergo(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());
        None b4 = Nope.nope().ergo(() -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(b4.completeWithErrors());

        None b5 = Nope.nope().ergo(() -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1));
        Assertions.assertTrue(b5.completeWarning());
        None b6 = Nope.nope().ergo(() -> None.alert(MY_ALERT));
        Assertions.assertTrue(b6.completeWarning());

        None e1 = Nope.fault(MY_FAULT).ergo(() -> Some.of(1));
        Assertions.assertTrue(e1.completeWithErrors());
        None e2 = Nope.fault(MY_FAULT).ergo(None::none);
        Assertions.assertTrue(e2.completeWithErrors());
        None e3 = Nope.fault(MY_FAULT).ergo(() -> Hope.of(1));
        Assertions.assertTrue(e3.completeWithErrors());
        None e4 = Nope.fault(MY_FAULT).ergo(Nope::nope);
        Assertions.assertTrue(e4.completeWithErrors());

        None f1 = Nope.fault(MY_FAULT).ergo(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        None f2 = Nope.fault(MY_FAULT).ergo(() -> None.fault(MY_FAULT));
        Assertions.assertTrue(f2.completeWithErrors());
        Assertions.assertEquals(1, f2.signals().size());
        None f3 = Nope.fault(MY_FAULT).ergo(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
        None f4 = Nope.fault(MY_FAULT).ergo(() -> Nope.fault(MY_FAULT));
        Assertions.assertTrue(f4.completeWithErrors());
        Assertions.assertEquals(1, f4.signals().size());

    }
}
