package io.github.epi155.test;

import io.github.epi155.pm.lang.Failure;
import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.Nope;
import io.github.epi155.pm.lang.Some;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

@Slf4j
class TestNone {
    @Test
    void test1() {
        None.builder().build()
            .onSuccess(() -> log.info("All fine"))
            .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }

    @Test
    void test2() {
        None.builder().join(Nope.capture(new NullPointerException())).build()
            .onSuccess(() -> log.info("All fine"))
            .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }

    @Test
    void test3() {
        val result = None.builder().build()
            .mapTo(() -> "all fine", es -> es.stream().map(Failure::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);

        Assertions.assertDoesNotThrow(() -> {
            Some.capture(new NullPointerException()).implies(it -> { throw new NullPointerException(); });
        });
        Some.of("hello").mapOf(it -> it.charAt(0));
        Some.<String>capture(new NullPointerException()).mapOf(it -> it.charAt(0));
    }

    @Test
    void test4() {
        val result = None.builder().join(Nope.capture(new NullPointerException())).build()
            .mapTo(() -> "all fine", es -> es.stream().map(Failure::message).collect(Collectors.joining(", ")));
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
            .join(Nope.capture(new NullPointerException()))
            .build()
            .implies(() -> log.info("I was here"))
            .ergo(Nope::nope)
            .anyway(() -> log.info("In any case"))
            .anyway(Nope::nope)
            .anyway(() -> Nope.capture(new NullPointerException()))
            .anyway(Nope::nope);
        None.builder()
            .join(Nope.of(Failure.of(new NullPointerException())))
            .build()
            .implies(() -> log.info("I was here"))
            .ergo(() -> Nope.capture(new NullPointerException()));
        None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build()
            .ergo(Nope::nope)
            .implies(() -> log.info("I was here"));
        None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build()
            .ergo(() -> Nope.capture(new NullPointerException()))
            .implies(() -> log.info("I was here"));
    }

    @Test
    void test7() {
        val some = Some.of(1);
        val none = None.of(some);
        Assertions.assertEquals(0, none.count());
        Assertions.assertFalse(none.summary().isPresent());

        val n1 = None.builder()
            .join(Nope.capture(new NullPointerException()))
            .build();
        Assertions.assertEquals(1, n1.count());
        Assertions.assertTrue(n1.summary().isPresent());

        val n2 = None.builder()
            .join(Nope.capture(new NullPointerException()))
            .join(Nope.capture(new IllegalArgumentException()))
            .build();
        Assertions.assertEquals(2, n2.count());
        Assertions.assertTrue(n2.summary().isPresent());
    }
}
