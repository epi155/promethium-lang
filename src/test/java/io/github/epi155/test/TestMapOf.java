package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestMapOf {
    private static final CustMsg MY_FAULT = PmCustMsg.of("EA01", "Oop error {} !!");
    private static final CustMsg MY_ALERT = PmCustMsg.of("WA01", "Oop warning {} !!");

    @Test
    void testSome1() {
        Some<Integer> a1 = Some.of(1).mapOf(k -> k + 1);
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(2, a1.value());

        Some<Integer> c1 = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1).mapOf(k -> k + 1);
        Assertions.assertTrue(c1.completeWarning());
        Assertions.assertEquals(2, c1.value());

        Some<Integer> e1 = Some.<Integer>fault(MY_FAULT).mapOf(k -> k + 1);
        Assertions.assertTrue(e1.completeWithErrors());
    }

    @Test
    void testNone1() {
        @NotNull Some<Integer> a1 = None.none().mapOf(() -> 1);
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(1, a1.value());

        @NotNull Some<Integer> c1 = None.alert(MY_ALERT).mapOf(() -> 1);
        Assertions.assertTrue(c1.completeWarning());
        Assertions.assertEquals(1, c1.value());

        @NotNull Some<Integer> e1 = None.fault(MY_FAULT).mapOf(() -> 1);
        Assertions.assertTrue(e1.completeWithErrors());
    }

    @Test
    void testHope1() {
        Some<Integer> a1 = Hope.of(1).mapOf(k -> k + 1);
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(2, a1.value());

        @NotNull Some<Integer> e1 = Hope.<Integer>fault(MY_FAULT).mapOf(k -> k + 1);
        Assertions.assertTrue(e1.completeWithErrors());

        @NotNull Hope<Integer> g1 = Hope.of(1).mapsOf(k -> k + 1);
        Assertions.assertTrue(g1.completeSuccess());
        Assertions.assertEquals(2, g1.value());
        @NotNull Hope<Integer> g3 = Hope.<Integer>fault(MY_FAULT).mapsOf(k -> k + 1);
        Assertions.assertTrue(g3.completeWithErrors());
    }

    @Test
    void testNope1() {
        @NotNull Some<Integer> a1 = Nope.nope().mapOf(() -> 1);
        Assertions.assertTrue(a1.completeSuccess());
        Assertions.assertEquals(1, a1.value());

        @NotNull Some<Integer> e1 = Nope.fault(MY_FAULT).mapOf(() -> 1);
        Assertions.assertTrue(e1.completeWithErrors());


        @NotNull Hope<Integer> g1 = Nope.nope().mapsOf(() -> 1);
        Assertions.assertTrue(g1.completeSuccess());
        Assertions.assertEquals(1, g1.value());
        @NotNull Hope<Integer> g3 = Nope.fault(MY_FAULT).mapsOf(() -> 1);
        Assertions.assertTrue(g3.completeWithErrors());
    }
}
