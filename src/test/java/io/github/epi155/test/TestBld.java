package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
class TestBld {


    @Test
    void test1() {
        val bld = Some.<Integer>builder();
        bld.join(() -> {
        });
        bld.join(Nope.nope());
        bld.value(1);
        val some = bld.build();

        Assertions.assertTrue(some.isSuccess());
        Assertions.assertEquals(1, some.value());
        val none = some.asNone();
        Assertions.assertTrue(none.isSuccess());
    }

    @Test
    void test2() {
        val bld = None.builder();
        bld.join(() -> Some.of(1)
            .and(k -> Some.of(2 * k)
                .mapOf(l -> 3 * l)
                .implies(m -> log.info("it was {}", m))));
        bld.join(Nope.nope());
        val none = bld.build();

        Assertions.assertTrue(none.isSuccess());
    }

    @Test
    void test3() {
        val result = Some.of(2)
            .mapTo(k -> "all fine", es -> es.stream().map(Failure::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);
    }

    @Test
    void test4() {
        val bld = Some.<Integer>builder();
        val fault = bld.fault(MsgError.of("E01", "Houston we have had a problem"));
        fault.setProperty("MissionName", "Apollo");
        fault.setProperty("MissionRun", 13);
        val some = bld.build();
        val result = some
            .mapTo(k -> "all fine", es -> es.stream().map(Failure::message).collect(Collectors.joining(", ")));
        log.info("Result is {}", result);
        Assertions.assertFalse(some.isSuccess());
        Assertions.assertThrows(NoSuchElementException.class, some::value);
        some.errors().forEach(f -> {
            val name = f.getStrProperty("MissionName");
            val run = f.getProperty("MissionRun", Integer.class);
            val born = f.getStrProperty("MissionLaunch", "Cape Canaveral");
            val city = f.getProperty("MissionCity", "Cape Canaveral");
            val moon = f.getStrProperty("MoonTarget");
            val quot = f.getProperty("MoonQuote", Integer.class);
            val vers = f.getProperty("MissionRun", String.class);
            val alt = f.getProperty("MissionName", "Gemini");
            val height = f.getProperty("MissionName", 100);
            Assertions.assertEquals(13, run);
            Assertions.assertEquals("Apollo", name);
            Assertions.assertNull(moon);
            Assertions.assertNull(vers);
            log.info("dump {}", fault);
        });
        val none = some.asNone();
        Assertions.assertFalse(none.isSuccess());
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

    @Test
    void test7() {
        val bld = Some.<Integer>builder();
        val some = bld.build();
        Assertions.assertTrue(some.isSuccess());
        Assertions.assertThrows(NoSuchElementException.class, some::value);
    }

    @Test
    void test8() {
        val bld = None.builder();
        try {
            throw new FailureException(new NullPointerException());
        } catch (Exception e) {
            bld.captureHere(e);
        }
    }

    @Test
    void test9() {
        val bld = None.builder();
        try {
            throw new FaultException(Failure.of(MsgError.of("E01", "we havr had a problem")));
        } catch (Exception e) {
            bld.captureHere(e);
        }
    }
}
