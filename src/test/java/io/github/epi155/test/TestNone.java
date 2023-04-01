package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

@Slf4j
public class TestNone {
    @Test
    public void test1() {
        None.builder().build()
            .onSuccess(() -> log.info("All fine"))
            .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }

    @Test
    public void test2() {
        None.builder().join(Nope.capture(new NullPointerException())).build()
            .onSuccess(() -> log.info("All fine"))
            .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }

    @Test
    public void test3() {
        val result = None.builder().build()
            .mapTo(() -> "all fine", es -> es.stream().map(Signal::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);

        Assertions.assertDoesNotThrow(() -> {
            Some.capture(new NullPointerException()).peek(it -> { throw new NullPointerException(); });
        });
        Some.of("hello").mapOf(it -> it.charAt(0));
        Some.<String>capture(new NullPointerException()).mapOf(it -> it.charAt(0));
    }

    @Test
    public void test4() {
        val result = None.builder().join(Nope.capture(new NullPointerException())).build()
            .mapTo(() -> "all fine", es -> es.stream().map(Signal::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);
    }

    @Test
    public void test5() {
        None.builder()
            .build()
            .peek(() -> log.info("I was here"))
            .ergo(Nope::nope);
        None.builder()
            .build()
            .peek(() -> log.info("I was here"))
            .ergo(() -> Nope.capture(new NullPointerException()));
        None.builder()
            .build()
            .ergo(Nope::nope)
            .peek(() -> log.info("I was here"));
        None.builder()
            .build()
            .ergo(() -> Nope.capture(new NullPointerException()))
            .peek(() -> log.info("I was here"));
    }

    @Test
    public void test6() {
        None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build()
            .peek(() -> log.info("I was here"))
            .ergo(Nope::nope)
            .anyway(() -> log.info("In any case"))
            .anyway(Nope::nope)
            .anyway(() -> Nope.capture(new NullPointerException()))
            .anyway(Nope::nope);
        None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build()
            .peek(() -> log.info("I was here"))
            .ergo(() -> Nope.capture(new NullPointerException()));
        None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build()
            .ergo(Nope::nope)
            .peek(() -> log.info("I was here"));
        None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build()
            .ergo(() -> Nope.capture(new NullPointerException()))
            .peek(() -> log.info("I was here"));
    }

    @Test
    public void test7() {
        val some = Some.of(1);
        val none = None.of(some);
        Assertions.assertEquals(0, none.signals().size());
        Assertions.assertFalse(none.summary().isPresent());

        val n1 = None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build();
        Assertions.assertEquals(1, n1.signals().size());
        Assertions.assertTrue(n1.summary().isPresent());

        val n2 = None.builder()
            .join(Nope.capture(new NullPointerException()))
            .join(Nope.capture(new IllegalArgumentException()))
            .build();
        Assertions.assertEquals(2, n2.signals().size());
        Assertions.assertTrue(n2.summary().isPresent());
    }
}
