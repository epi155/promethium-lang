package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestMap {
    private static final CustMsg MY_FAULT = CustMsg.of("EA01", "Oop error {} !!");
    private static final CustMsg MY_ALERT = CustMsg.of("WA01", "Oop warning {} !!");

    @Test
    void testSome1() {
        Some<Integer> a1 = Some.of(1).map(k -> Some.of(k + 1));
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(2, a1.value());
        Some<Integer> a3 = Some.of(1).map(k -> Hope.of(k + 1));
        Assertions.assertTrue(a3.completeSuccess());
        Assertions.assertEquals(2, a3.value());

        Some<Integer> b1 = Some.of(1).map(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        Some<Integer> b3 = Some.of(1).map(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());

        Some<Integer> b5 = Some.of(1).map(k -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(k + 1));
        Assertions.assertTrue(b5.completeWarning());
        Assertions.assertEquals(2, b5.value());

        Some<Integer> c1 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).map(k -> Some.of(k + 1));
        Assertions.assertTrue(c1.completeWarning());
        Assertions.assertEquals(2, c1.value());
        Some<Integer> c3 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).map(k -> Hope.of(k + 1));
        Assertions.assertTrue(c3.completeWarning());
        Assertions.assertEquals(2, c3.value());

        Some<Integer> d1 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).map(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(d1.completeWithErrors());
        Assertions.assertEquals(2, d1.signals().size());
        Some<Integer> d3 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).map(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(d3.completeWithErrors());
        Assertions.assertEquals(2, d3.signals().size());

        Some<Integer> d5 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).map(k -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(k + 1));
        Assertions.assertTrue(d5.completeWarning());
        Assertions.assertEquals(2, d5.signals().size());
        Assertions.assertEquals(2, d5.value());

        Some<Integer> e1 = Some.<Integer>fault(MY_FAULT).map(k -> Some.of(k + 1));
        Assertions.assertTrue(e1.completeWithErrors());
        Some<Integer> e3 = Some.<Integer>fault(MY_FAULT).map(k -> Hope.of(k + 1));
        Assertions.assertTrue(e3.completeWithErrors());

        Some<Integer> f1 = Some.<Integer>fault(MY_FAULT).map(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        Some<Integer> f3 = Some.<Integer>fault(MY_FAULT).map(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
    }

    @Test
    void testNone1() {
        @NotNull Some<Integer> a1 = None.none().map(() -> Some.of(1));
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(1, a1.value());
        @NotNull Some<Integer> a3 = None.none().map(() -> Hope.of(1));
        Assertions.assertTrue(a3.completeSuccess());
        Assertions.assertEquals(1, a3.value());

        @NotNull Some<Integer> b1 = None.none().map(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        @NotNull Some<Integer> b3 = None.none().map(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());

        @NotNull Some<Integer> b5 = None.none().map(() -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1));
        Assertions.assertTrue(b5.completeWarning());

        @NotNull Some<Integer> c1 = None.alert(MY_ALERT).map(() -> Some.of(1));
        Assertions.assertTrue(c1.completeWarning());
        Assertions.assertEquals(1, c1.value());
        @NotNull Some<Integer> c3 = None.alert(MY_ALERT).map(() -> Hope.of(1));
        Assertions.assertTrue(c3.completeWarning());
        Assertions.assertEquals(1, c3.value());

        @NotNull Some<Integer> d1 = None.alert(MY_ALERT).map(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(d1.completeWithErrors());
        Assertions.assertEquals(2, d1.signals().size());
        @NotNull Some<Integer> d3 = None.alert(MY_ALERT).map(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(d3.completeWithErrors());
        Assertions.assertEquals(2, d3.signals().size());

        @NotNull Some<Integer> d5 = None.alert(MY_ALERT).map(() -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1));
        Assertions.assertTrue(d5.completeWarning());
        Assertions.assertEquals(2, d5.signals().size());
        Assertions.assertEquals(1, d5.value());

        @NotNull Some<Integer> e1 = None.fault(MY_FAULT).map(() -> Some.of(1));
        Assertions.assertTrue(e1.completeWithErrors());
        @NotNull Some<Integer> e3 = None.fault(MY_FAULT).map(() -> Hope.of(1));
        Assertions.assertTrue(e3.completeWithErrors());

        @NotNull Some<Integer> f1 = None.fault(MY_FAULT).map(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        @NotNull Some<Integer> f3 = None.fault(MY_FAULT).map(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());
    }

    @Test
    void testHope1() {
        Some<Integer> a1 = Hope.of(1).map(k -> Some.of(k + 1));
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(2, a1.value());
        @NotNull Some<Integer> a3 = Hope.of(1).map(k -> Hope.of(k + 1));
        Assertions.assertTrue(a3.completeSuccess());
        Assertions.assertEquals(2, a3.value());

        @NotNull Some<Integer> b1 = Hope.of(1).map(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        @NotNull Some<Integer> b3 = Hope.of(1).map(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());

        @NotNull Some<Integer> b5 = Hope.of(1).map(k -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(k + 1));
        Assertions.assertTrue(b5.completeWarning());
        Assertions.assertEquals(2, b5.value());


        @NotNull Some<Integer> e1 = Hope.<Integer>fault(MY_FAULT).map(k -> Some.of(k + 1));
        Assertions.assertTrue(e1.completeWithErrors());
        @NotNull Some<Integer> e3 = Hope.<Integer>fault(MY_FAULT).map(k -> Hope.of(k + 1));
        Assertions.assertTrue(e3.completeWithErrors());

        @NotNull Some<Integer> f1 = Hope.<Integer>fault(MY_FAULT).map(k -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        @NotNull Some<Integer> f3 = Hope.<Integer>fault(MY_FAULT).map(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());

        @NotNull Hope<Integer> g1 = Hope.of(1).into(k -> Hope.of(k + 1));
        Assertions.assertTrue(g1.completeSuccess());
        Assertions.assertEquals(2, g1.value());
        @NotNull Hope<Integer> g2 = Hope.of(1).into(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(g2.completeWithErrors());
        @NotNull Hope<Integer> g3 = Hope.<Integer>fault(MY_FAULT).into(k -> Hope.of(k + 1));
        Assertions.assertTrue(g3.completeWithErrors());
        @NotNull Hope<Integer> g4 = Hope.<Integer>fault(MY_FAULT).into(k -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(g4.completeWithErrors());
        Assertions.assertEquals(1, g4.signals().size());

    }

    @Test
    void testNope1() {
        @NotNull Some<Integer> a1 = Nope.nope().map(() -> Some.of(1));
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(1, a1.value());
        @NotNull Some<Integer> a3 = Nope.nope().map(() -> Hope.of(1));
        Assertions.assertTrue(a3.completeSuccess());
        Assertions.assertEquals(1, a3.value());

        @NotNull Some<Integer> b1 = Nope.nope().map(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(b1.completeWithErrors());
        @NotNull Some<Integer> b3 = Nope.nope().map(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(b3.completeWithErrors());

        @NotNull Some<Integer> b5 = Nope.nope().map(() -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1));
        Assertions.assertTrue(b5.completeWarning());
        Assertions.assertEquals(1, b5.value());

        @NotNull Some<Integer> e1 = Nope.fault(MY_FAULT).map(() -> Some.of(1));
        Assertions.assertTrue(e1.completeWithErrors());
        @NotNull Some<Integer> e3 = Nope.fault(MY_FAULT).map(() -> Hope.of(1));
        Assertions.assertTrue(e3.completeWithErrors());

        @NotNull Some<Integer> f1 = Nope.fault(MY_FAULT).map(() -> Some.fault(MY_FAULT));
        Assertions.assertTrue(f1.completeWithErrors());
        Assertions.assertEquals(1, f1.signals().size());
        @NotNull Some<Integer> f3 = Nope.fault(MY_FAULT).map(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(f3.completeWithErrors());
        Assertions.assertEquals(1, f3.signals().size());

        @NotNull Hope<Integer> g1 = Nope.nope().into(() -> Hope.of(1));
        Assertions.assertTrue(g1.completeSuccess());
        Assertions.assertEquals(1, g1.value());
        @NotNull Hope<Integer> g2 = Nope.nope().into(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(g2.completeWithErrors());
        @NotNull Hope<Integer> g3 = Nope.fault(MY_FAULT).into(() -> Hope.of(1));
        Assertions.assertTrue(g3.completeWithErrors());
        @NotNull Hope<Integer> g4 = Nope.fault(MY_FAULT).into(() -> Hope.fault(MY_FAULT));
        Assertions.assertTrue(g4.completeWithErrors());
        Assertions.assertEquals(1, g4.signals().size());

    }
}
