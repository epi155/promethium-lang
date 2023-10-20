package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

class TestNone {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestNone.class);

    @Test
    void test1() {
        None.builder().build()
                .onSuccess(() -> log.info("All fine"))
                .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }

    @Test
    void test2() {
        None.builder().withStatus(Nope.capture(new NullPointerException())).build()
            .onSuccess(() -> log.info("All fine"))
            .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }

    @Test
    void test3() {
        String result = None.builder().build()
                .mapTo(() -> "all fine", es -> es.stream().map(Signal::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);

        Assertions.assertDoesNotThrow(() -> {
            Some.capture(new NullPointerException()).implies(it -> {
                throw new NullPointerException();
            });
        });
        Some.of("hello").mapOf(it -> it.charAt(0));
        Some.<String>capture(new NullPointerException()).mapOf(it -> it.charAt(0));
    }

    private static final CustMsg MY_FAULT = PmCustMsg.of("EA01", "Oop error {} !!");
    private static final CustMsg MY_ALERT = PmCustMsg.of("WA01", "Oop warning {} !!");

    @Test
    void test4() {
        String result = None.builder().withStatus(Nope.capture(new NullPointerException())).build()
                .mapTo(() -> "all fine", es -> es.stream().map(Signal::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);
    }

    @Test
    void test5() {
        None.builder()
                .build()
                .implies(() -> log.info("I was here"))
            .ergo(Nope::nope);
        None.builder()
                .build()
                .implies(() -> log.info("I was here"))
                .ergo(() -> Nope.capture(new NullPointerException()));
        None.builder()
                .build()
                .ergo(Nope::nope)
                .implies(() -> log.info("I was here"));
        None.builder()
                .build()
                .ergo(() -> Nope.capture(new NullPointerException()))
                .implies(() -> log.info("I was here"));
    }

    @Test
    void test6() {
        None.builder()
                .withStatus(Nope.capture(new NullPointerException()))
                .build()
                .implies(() -> log.info("I was here"))
            .ergo(Nope::nope)
//            .anyway(() -> log.info("In any case"))
//            .anyway(Nope::nope)
//            .anyway(() -> Nope.capture(new NullPointerException()))
//            .anyway(Nope::nope)
        ;
        None.builder()
                .withStatus(Nope.capture(new NullPointerException()))
                .build()
                .implies(() -> log.info("I was here"))
                .ergo(() -> Nope.capture(new NullPointerException()));
        None.builder()
                .withStatus(Nope.capture(new NullPointerException()))
                .build()
                .ergo(Nope::nope)
                .implies(() -> log.info("I was here"));
        None.builder()
                .withStatus(Nope.capture(new NullPointerException()))
                .build()
                .ergo(() -> Nope.capture(new NullPointerException()))
                .implies(() -> log.info("I was here"));
    }

    @Test
    void test7() {
        @NotNull Some<Integer> some = Some.of(1);
        @NotNull None none = None.of(some);
        Assertions.assertEquals(0, none.signals().size());
        Assertions.assertFalse(none.summary().isPresent());

        @NotNull None n1 = None.builder()
                .withStatus(Nope.capture(new NullPointerException()))
                .build();
        Assertions.assertEquals(1, n1.signals().size());
        Assertions.assertTrue(n1.summary().isPresent());

        @NotNull None n2 = None.builder()
                .withStatus(Nope.capture(new NullPointerException()))
                .withStatus(Nope.capture(new IllegalArgumentException()))
                .build();
        Assertions.assertEquals(2, n2.signals().size());
        Assertions.assertTrue(n2.summary().isPresent());
    }

    @Test
        //  mvn test -Dtest="TestNone#test8"
    void test8() {
        Integer a = None.none().<Integer>mapTo(w -> w.isEmpty() ? 0 : 4, e -> 12);
        Assertions.assertEquals(0, a);
        Integer b = None.alert(MY_ALERT).<Integer>mapTo(w -> w.isEmpty() ? 0 : 4, e -> 12);
        Assertions.assertEquals(4, b);
        Integer c = None.fault(MY_FAULT).<Integer>mapTo(w -> w.isEmpty() ? 0 : 4, e -> 12);
        Assertions.assertEquals(12, c);

        None.none().onSuccess(w -> w.forEach(it -> log.warn("w: {}", it)));
        None.alert(MY_ALERT).onSuccess(w -> w.forEach(it -> log.warn("w: {}", it)));
        None.fault(MY_FAULT).onSuccess(w -> w.forEach(it -> log.warn("w: {}", it)));

        @NotNull None x = None.builder()
                .withAlert(MY_ALERT)
                .withFault(MY_FAULT).build();
        Assertions.assertFalse(x.completeSuccess());
        Assertions.assertFalse(x.completeWarning());
        Assertions.assertTrue(x.completeWithErrors());
    }
}
