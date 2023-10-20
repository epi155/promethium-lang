package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class TestFaultMap {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestChoice.class);

    private static final CustMsg MY_FAULT = PmCustMsg.of("CA01", "Oop error {} !!");
    private static final CustMsg MY_ALERT = PmCustMsg.of("WA01", "Mmm warning {} !!");

    @Test
    void testA0() {
        @NotNull None a1 = ChooseContext.choose("a")
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull None a2 = ChooseContext.choose("a")
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeWarning());

        @NotNull None a3 = ChooseContext.choose(0)
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a3.completeWarning());

        @NotNull None a4 = ChooseContext.choose(0)
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());

        @NotNull None a5 = ChooseContext.choose("a")
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a5.completeWithErrors());

        @NotNull None a6 = ChooseContext.choose("a")
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a6.completeWarning());

        @NotNull None a7 = ChooseContext.choose(0)
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a7.completeWarning());

        @NotNull None a8 = ChooseContext.choose(0)
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a8.completeWithErrors());
    }

    @Test
    void testA1() {
        @NotNull None a1 = Hope.of("a").choose()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull None a2 = Hope.of("a").choose()
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeWarning());

        @NotNull None a3 = Hope.of(0).choose()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a3.completeWarning());

        @NotNull None a4 = Hope.of(0).choose()
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());

        @NotNull None a5 = Hope.of("a").choose()
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a5.completeWithErrors());

        @NotNull None a6 = Hope.of("a").choose()
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a6.completeWarning());

        @NotNull None a7 = Hope.of(0).choose()
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a7.completeWarning());

        @NotNull None a8 = Hope.of(0).choose()
                .whenInstanceOf(Double.class).nop()
                .whenInstanceOf(String.class)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a8.completeWithErrors());
    }

    @Test
    void testB0() {
        @NotNull None a1 = ChooseContext.choose("a")
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull None a2 = ChooseContext.choose("a")
                .when(true)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeWarning());

        @NotNull None a3 = ChooseContext.choose(0)
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a3.completeWarning());

        @NotNull None a4 = ChooseContext.choose(0)
                .when(false)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testB1() {
        @NotNull None a1 = Hope.of(0).choose()
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull None a2 = Hope.of(0).choose()
                .when(true)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeWarning());

        @NotNull None a3 = Hope.of(0).choose()
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().alert(Map.of("cause", "just a test"), MY_ALERT)
                .end();
        Assertions.assertTrue(a3.completeWarning());

        @NotNull None a4 = Hope.of(0).choose()
                .when(false)
                .alert(Map.of("cause", "just a test"), MY_ALERT)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testC1() {
        @NotNull Nope a1 = Hope.of(0).opto()
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Nope a2 = Hope.of(0).opto()
                .when(true).nop()
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Nope a3 = Hope.of(0).opto()
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Nope a4 = Hope.of(0).opto()
                .when(false).nop()
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testC2() {
        @NotNull Nope a1 = OptoContext.opto(0)
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Nope a2 = OptoContext.opto(0)
                .when(true).nop()
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Nope a3 = OptoContext.opto(0)
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Nope a4 = OptoContext.opto(0)
                .when(false).nop()
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testD1() {
        @NotNull Hope<Integer> a1 = Hope.of(0).<Integer>optoMap()
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Hope<Integer> a2 = Hope.of(0).<Integer>optoMap()
                .when(true).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Hope<Integer> a3 = Hope.of(0).<Integer>optoMap()
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Hope<Integer> a4 = Hope.of(0).<Integer>optoMap()
                .when(false).mapOf(it -> it + 1)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testD2() {
        @NotNull Hope<Integer> a1 = OptoContext.<Integer, Integer>optoMap(0)
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Hope<Integer> a2 = OptoContext.<Integer, Integer>optoMap(0)
                .when(true).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Hope<Integer> a3 = OptoContext.<Integer, Integer>optoMap(0)
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Hope<Integer> a4 = OptoContext.<Integer, Integer>optoMap(0)
                .when(false).mapOf(it -> it + 1)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testE1() {
        @NotNull Some<Integer> a1 = Hope.of(0).<Integer>chooseMap()
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Some<Integer> a2 = Hope.of(0).<Integer>chooseMap()
                .when(true).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Some<Integer> a3 = Hope.of(0).<Integer>chooseMap()
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Some<Integer> a4 = Hope.of(0).<Integer>chooseMap()
                .when(false).mapOf(it -> it + 1)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testE2() {
        @NotNull Some<Integer> a1 = ChooseContext.<Integer, Integer>chooseMap(0)
                .when(true).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Some<Integer> a2 = ChooseContext.<Integer, Integer>chooseMap(0)
                .when(true).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Some<Integer> a3 = ChooseContext.<Integer, Integer>chooseMap(0)
                .when(false).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Some<Integer> a4 = ChooseContext.<Integer, Integer>chooseMap(0)
                .when(false).mapOf(it -> it + 1)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testF1() {
        @NotNull Nope a1 = Hope.of(0).opto()
                .whenInstanceOf(Integer.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Nope a2 = Hope.of(0).opto()
                .whenInstanceOf(Integer.class).nop()
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Nope a3 = Hope.of(0).opto()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Nope a4 = Hope.of(0).opto()
                .whenInstanceOf(String.class).nop()
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testF2() {
        @NotNull Nope a1 = OptoContext.opto(0)
                .whenInstanceOf(Integer.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Nope a2 = OptoContext.opto(0)
                .whenInstanceOf(Integer.class).nop()
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Nope a3 = OptoContext.opto(0)
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Nope a4 = OptoContext.opto(0)
                .whenInstanceOf(String.class).nop()
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testG1() {
        @NotNull Some<Integer> a1 = Hope.of(0).<Integer>chooseMap()
                .whenInstanceOf(Integer.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Some<Integer> a2 = Hope.of(0).<Integer>chooseMap()
                .whenInstanceOf(Integer.class).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Some<Integer> a3 = Hope.of(0).<Integer>chooseMap()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Some<Integer> a4 = Hope.of(0).<Integer>chooseMap()
                .whenInstanceOf(String.class).mapOf(String::length)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testG2() {
        @NotNull Some<Integer> a1 = ChooseContext.<Integer, Integer>chooseMap(0)
                .whenInstanceOf(Integer.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Some<Integer> a2 = ChooseContext.<Integer, Integer>chooseMap(0)
                .whenInstanceOf(Integer.class).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Some<Integer> a3 = ChooseContext.<Integer, Integer>chooseMap(0)
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Some<Integer> a4 = ChooseContext.<Integer, Integer>chooseMap(0)
                .whenInstanceOf(String.class).mapOf(String::length)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testH1() {
        @NotNull Hope<Integer> a1 = Hope.of(0).<Integer>optoMap()
                .whenInstanceOf(Integer.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Hope<Integer> a2 = Hope.of(0).<Integer>optoMap()
                .whenInstanceOf(Integer.class).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Hope<Integer> a3 = Hope.of(0).<Integer>optoMap()
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Hope<Integer> a4 = Hope.of(0).<Integer>optoMap()
                .whenInstanceOf(String.class).mapOf(String::length)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testH2() {
        @NotNull Hope<Integer> a1 = OptoContext.<Integer, Integer>optoMap(0)
                .whenInstanceOf(Integer.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a1.completeWithErrors());

        @NotNull Hope<Integer> a2 = OptoContext.<Integer, Integer>optoMap(0)
                .whenInstanceOf(Integer.class).mapOf(it -> it + 1)
                .otherwise().fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a2.completeSuccess());

        @NotNull Hope<Integer> a3 = OptoContext.<Integer, Integer>optoMap(0)
                .whenInstanceOf(String.class).fault(Map.of("cause", "just a test"), MY_FAULT)
                .otherwise().mapOf(it -> it + 1)
                .end();
        Assertions.assertTrue(a3.completeSuccess());

        @NotNull Hope<Integer> a4 = OptoContext.<Integer, Integer>optoMap(0)
                .whenInstanceOf(String.class).mapOf(String::length)
                .otherwise()
                .fault(Map.of("cause", "just a test"), MY_FAULT)
                .end();
        Assertions.assertTrue(a4.completeWithErrors());
    }

    @Test
    void testI1() {
        @NotNull Some<Object> some = Some.builder()
                .withFault(Map.of("cause", "just a test"), MY_FAULT)
                .withAlert(Map.of("cause", "just a test"), MY_ALERT)
                .build();
        @NotNull None none = None.builder()
                .withFault(Map.of("cause", "just a test"), MY_FAULT)
                .withAlert(Map.of("cause", "just a test"), MY_ALERT)
                .build();
        none.peek(() -> log.info("All fine"), es -> es.forEach(it -> log.error(it.message())));
        none.peek(ws -> ws.forEach(it -> log.warn(it.message())), es -> es.forEach(it -> log.error(it.message())));

        @NotNull Some<Object> xSome = Some.fault(Map.of("cause", "just a test"), MY_FAULT);
        @NotNull None xNome = None.fault(Map.of("cause", "just a test"), MY_FAULT);

        @NotNull None zNone = None.none();
        zNone.peek(() -> log.info("All fine"), es -> es.forEach(it -> log.error(it.message())));
        zNone.peek(ws -> ws.forEach(it -> log.warn(it.message())), es -> es.forEach(it -> log.error(it.message())));
    }
}