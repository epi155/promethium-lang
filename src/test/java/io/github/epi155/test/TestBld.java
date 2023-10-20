package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

class TestBld {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestBld.class);


    @Test
    void test1() {
        @NotNull SomeBuilder<Integer> bld = Some.builder();
        @NotNull Some<Integer> some = bld
                .withStatus(Nope.nope())
                .buildWithValue(1);

        if (some.completeWithoutErrors()) {
            @NotNull Integer value = some.value();
            Collection<Warning> warnings = some.alerts();
            // ... action on value and warnings
        } else {
            Collection<Signal> errors = some.signals();
            // ... action on errors
        }
        some.onSuccess((Integer v, Collection<Warning> w) -> {
        }).onFailure((Collection<? extends Signal> e) -> {
        });
        Integer x = some.mapTo((v, w) -> 0, e -> 1);

        Assertions.assertTrue(some.completeSuccess());
        Assertions.assertEquals(1, some.value());
        @NotNull None none = some.asNone();
        Assertions.assertTrue(none.completeSuccess());
    }

    @Test
    void test2() {
        @NotNull NoneBuilder bld = None.builder();
        bld.withStatus(Some.of(1)
                .ergo(k -> Some.of(2 * k)
                        .mapOf(l -> 3 * l)
                        .implies(m -> log.info("it was {}", m))));
        bld.withStatus(Nope.nope());
        @NotNull None none = bld.build();

        Assertions.assertTrue(none.completeSuccess());
    }

    @Test
    void test3() {
        String result = Some.of(2)
                .mapTo(k -> "all fine", es -> es.stream().map(Signal::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);
    }

    @Test
    void test4() {
        @NotNull SomeBuilder<Integer> bld = Some.builder();
        @NotNull Failure fault = bld.fault(PmCustMsg.of("E01", "Houston we have had a problem"));
        fault.setProperty("MissionName", "Apollo");
        fault.setProperty("MissionRun", 13);
        @NotNull Some<Integer> some = bld.build();
        String result = some
                .mapTo(k -> "all fine", es -> es.stream().map(Signal::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);
        Assertions.assertFalse(some.completeSuccess());
        Assertions.assertThrows(NoSuchElementException.class, some::value);
        some.signals().forEach(f -> {
            String name = f.getStrProperty("MissionName");
            Integer run = f.getProperty("MissionRun", Integer.class);
            String born = f.getStrProperty("MissionLaunch", "Cape Canaveral");
            String city = f.getProperty("MissionCity", "Cape Canaveral");
            String moon = f.getStrProperty("MoonTarget");
            Integer quot = f.getProperty("MoonQuote", Integer.class);
            String vers = f.getProperty("MissionRun", String.class);
            String alt = f.getProperty("MissionName", "Gemini");
            Integer height = f.getProperty("MissionName", 100);
            Assertions.assertEquals(13, run);
            Assertions.assertEquals("Apollo", name);
            Assertions.assertNull(moon);
            Assertions.assertNull(vers);
            log.info("dump {}", fault);
        });
        @NotNull None none = some.asNone();
        Assertions.assertFalse(none.completeSuccess());
    }

    @Test
    void test5() {
        Some.of(1)
            .onSuccess(k -> log.info("All fine"))
            .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }

    @Test
    void test6() {
        Some.capture(new NullPointerException())
            .onSuccess(k -> log.info("All fine"))
            .onFailure(es -> es.forEach(e -> log.warn("Oops {}", e.message())));
    }


}
