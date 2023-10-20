package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class TestChoice {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestChoice.class);

    private static final CustMsg MY_FAULT = PmCustMsg.of("CA01", "Oop error {} !!");
    private static final CustMsg MY_ALERT = PmCustMsg.of("WA01", "Mmm warning {} !!");

    @Test
    void testC0() {
        None ee = Some.<Integer>builder()
                .withAlert(MY_FAULT)
                .buildWithValue(100)
                .choose()
                .when(it -> it > 50)
                .thenApply(it -> Nope.fault(MY_FAULT))
                .otherwise().nop()
                .end();
        System.out.println(ee);
        Assertions.assertTrue(ee.completeWithErrors());
        None res = Hope.<Integer>fault(MY_FAULT).choose()
                .whenEquals(1).thenAccept(it -> System.out.println("one"))
                .otherwise().thenAccept(it -> System.out.println("off"))
                .end();
        Assertions.assertTrue(res.completeWithErrors());
//        None z = Hope.of(1).choice()
//                .when(1).peek(k -> System.out.println("one"))
//                .when(2).ergo(k -> Nope.failure(MY_FAULT))
//                .otherwise().peek(k -> System.out.println("boh"))
//                .end();
//        Some<String> s = Hope.of(2).<String>choiceMap()
//                .when(1).map(k -> Hope.of("one").mapOf(String::toUpperCase))
//                .when(2).mapOf(k -> "Two")
//                .when(k -> k==3).mapOf(k -> "Two")
//                .whenInstanceOf(String.class).mapOf(String::trim)
//                .otherwise().map(k -> Hope.failure(MY_FAULT))
//                .end();
    }

    @Test
    void testC1() {
        Random randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>fault(MY_FAULT, k) : Hope.of(k))
                .collect(Collectors.toList());
        NoneBuilder bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choose()
                .when(fst.get()).thenAccept(v -> fst.set(false))
                .when(v -> v % 2 == 0).thenAccept(v -> log.info("{} is even", v))
                .when(v -> v % 3 == 0).thenAccept(v -> log.info("{} is 3-fold", v))
                .otherwise().thenAccept(v -> log.info("{} is odd", v))
            .end()));
        None none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }

    @Test
    void testC2() {
        Random randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>fault(MY_FAULT, k) : Hope.of(k))
                .collect(Collectors.toList());
        NoneBuilder bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choose()
                .when(fst.get()).thenApply(v -> {
                    fst.set(false);
                    return Nope.nope();
                })
                .when(v -> v % 2 == 0).thenApply(v -> randm.nextInt(v) < v / 2 ? Hope.of(v / 2) : Hope.fault(MY_FAULT, v))
                .when(v -> v % 3 == 0).thenApply(v -> Nope.nope())
                .otherwise().thenApply(v -> randm.nextDouble() < 0.5 ? Hope.of(1) : Hope.capture(new NoSuchElementException()))
            .end()));
        None none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }

    @Test
    void testC3() {
        Random randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>fault(MY_FAULT, k) : Hope.of(k))
                .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> it.<String>chooseMap()
            .when(fst.get()).map(k -> {
                fst.set(false);
                return Hope.of("zero");
            })
            .when(v -> v % 4 == 0).map(v -> Hope.of("quattro"))
            .when(v -> v % 4 == 1).map(v -> Hope.fault(MY_FAULT, v))
            .when(v -> v % 3 == 0).map(v -> Hope.of("tre"))
            .otherwise().map(v -> Hope.of("altro"))
            .end()
            .onSuccess(s -> log.info("result is {}", s))
            .onFailure(es -> es.forEach(e -> log.warn("Error is: {}", e.message()))));
    }

    @Test
    void testC4() {
        Some<String> a1 = Hope.of(1).<String>chooseMap()
                .when(false).fault(MY_FAULT)
                .otherwise().fault(MY_FAULT).end();
        Assertions.assertTrue(a1.completeWithErrors());
        Some<String> a1a = Hope.of(1).<String>chooseMap()
                .when(true).mapOf(it -> "a")
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a1a.completeWithoutErrors());
        Some<String> a1b = Hope.of(1).<String>chooseMap()
                .when(true).fault(MY_FAULT)
                .otherwise().mapOf(it -> "b")
                .end();
        Assertions.assertTrue(a1b.completeWithErrors());
        Some<String> a1c = Hope.of(1).<String>chooseMap()
                .whenInstanceOf(Integer.class).fault(MY_FAULT)
                .otherwise().mapOf(it -> "b")
                .end();
        Assertions.assertTrue(a1c.completeWithErrors());
        Some<String> a1d = Hope.of(1).<String>chooseMap()
                .whenInstanceOf(Integer.class).mapOf(it -> "Hi")
                .otherwise().mapOf(it -> "b")
                .end();
        Assertions.assertTrue(a1d.completeWithoutErrors());

        None n1 = Hope.of(1).choose()
                .when(false).fault(MY_FAULT)
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(n1.completeWithErrors());
        Assertions.assertTrue(n1.completeWithErrors());
        None m1 = Hope.of(1).choose()
                .when(true)
                .fault(MY_FAULT)
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(m1.completeWithErrors());
        None o1 = Hope.of(1).choose()
                .whenInstanceOf(Integer.class).fault(MY_FAULT)
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(o1.completeWithErrors());

        Some<String> b1 = ChooseContext.<Integer, String>chooseMap(1)
                .when(false).fault(MY_FAULT)
                .otherwise().fault(MY_FAULT).end();
        Assertions.assertTrue(b1.completeWithErrors());
        Some<String> c1 = ChooseContext.<Integer, String>chooseMap(1)
                .when(false).fault(MY_FAULT)
                .otherwise().mapOf(it -> String.format("%d", it))
                .end();
        Assertions.assertTrue(c1.completeWithoutErrors());
        Some<String> d1 = ChooseContext.<Integer, String>chooseMap(1)
                .whenInstanceOf(Integer.class).fault(MY_FAULT)
                .otherwise().mapOf(it -> String.format("%d", it))
                .end();
        Assertions.assertTrue(d1.completeWithErrors());
        Some<String> d1a = ChooseContext.<Integer, String>chooseMap(1)
                .when(true).fault(MY_FAULT)
                .otherwise().mapOf(it -> String.format("%d", it))
                .end();
        Assertions.assertTrue(d1a.completeWithErrors());
        Some<String> d1b = ChooseContext.<Integer, String>chooseMap(1)
                .when(true).mapOf(it -> String.format("%d", it))
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(d1b.completeWithoutErrors());


        Some<String> e1 = ChooseContext.<Integer, String>chooseMap(1)
                .whenInstanceOf(Integer.class).mapOf(it -> Integer.toString(it))
                .otherwise().mapOf(it -> String.format("%d", it))
                .end();
        Assertions.assertTrue(e1.completeWithoutErrors());

        None p1 = ChooseContext.choose(1)
                .when(false).fault(MY_FAULT)
                .otherwise().fault(MY_FAULT).end();
        Assertions.assertTrue(p1.completeWithErrors());
        None q1 = ChooseContext.choose(1)
                .when(true).fault(MY_FAULT)
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(q1.completeWithErrors());
        None r1 = ChooseContext.choose(1)
                .whenInstanceOf(Integer.class).fault(MY_FAULT)
                .otherwise().nop()
                .end();
        Assertions.assertTrue(r1.completeWithErrors());
    }

    @Test
    void testC6() {
        Random randm = new Random();
        List<Some<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>fault(MY_FAULT, k) : Some.of(k))
                .collect(Collectors.toList());
        NoneBuilder bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choose()
                .when(fst.get()).thenAccept(v -> fst.set(false))
                .when(v -> v % 2 == 0).thenAccept(v -> log.info("{} is even", v))
                .when(v -> v % 3 == 0).thenAccept(v -> log.info("{} is 3-fold", v))
                .otherwise().thenAccept(v -> log.info("{} is odd", v))
                .end()));
        None none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }

    @Test
    void testC7() {
        Random randm = new Random();
        List<Some<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>fault(MY_FAULT, k) : Some.of(k))
                .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> it.<String>chooseMap()
            .when(fst.get()).map(k -> {
                fst.set(false);
                return Hope.of("zero");
            })
            .when(v -> v % 4 == 0).map(v -> Hope.of("quattro"))
            .when(v -> v % 4 == 1).map(v -> Hope.fault(MY_FAULT, v))
            .when(v -> v % 3 == 0).map(v -> Hope.of("tre"))
            .otherwise().map(v -> Hope.of("altro"))
            .end()
            .onSuccess(s -> log.info("result is {}", s))
            .onFailure(es -> es.forEach(e -> log.warn("Error is: {}", e.message()))));
    }

    @Test
    void testC8() {
        Number[] nn = {1, 2L, 3F, 3.1415926535_8979323846_2643383279};
        for (Number z : nn) {
            None a = Hope.of(z).choose()
                    .whenInstanceOf(Integer.class).thenAccept(n -> n++)
                    .whenInstanceOf(Double.class).thenApply(n -> Hope.of(Math.sqrt(n)))
                    .otherwise().thenApply(x -> Nope.fault(MY_FAULT))
                    .end();
            Some<String> b = Hope.of(z).<String>chooseMap()
                    .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                    .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                    .otherwise().map(x -> Hope.fault(MY_FAULT))
                    .end();
            None c = ChooseContext.choose(z)
                    .whenInstanceOf(Integer.class).thenAccept(n -> n++)
                    .whenInstanceOf(Double.class).thenApply(n -> Hope.of(Math.sqrt(n)))
                    .otherwise().thenApply(x -> Nope.fault(MY_FAULT))
                    .end();
            Some<String> d = ChooseContext.<Number, String>chooseMap(z)
                    .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                    .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                    .otherwise().map(x -> Hope.fault(MY_FAULT))
                    .end();
        }
        ChooseContext.choose(1)
                .whenEquals(1).thenAccept(k -> log.info("one"))
                .whenEquals(2).thenAccept(k -> log.info("two"))
            .otherwise().nop()
            .end();
        ChooseContext.choose(2)
                .whenEquals(1).thenAccept(k -> log.info("one"))
                .whenEquals(2).thenAccept(k -> log.info("two"))
            .otherwise().nop()
            .end();
        ChooseContext.<Integer, String>chooseMap(1)
            .whenEquals(1).map(k -> Hope.of("one"))
            .whenEquals(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        ChooseContext.<Integer, String>chooseMap(1)
            .whenEquals(1).map(k -> Hope.of("one"))
            .whenEquals(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        ChooseContext.<Integer, String>chooseMap(2)
            .whenEquals(1).map(k -> Hope.of("one"))
            .whenEquals(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        ChooseContext.<Integer, String>chooseMap(3)
            .whenEquals(1).map(k -> Hope.of("one"))
            .whenEquals(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();

        ChooseContext.choose(1)
                .whenEquals(1).thenApply(k -> Hope.of("a"))
                .whenEquals(2).thenAccept(k -> {
                })
            .otherwise().nop()
            .end();
        ChooseContext.choose(2)
                .whenEquals(1).thenApply(k -> Hope.of("a"))
                .whenEquals(2).thenAccept(k -> {
                })
            .otherwise().nop()
            .end();

        Hope.of(1).choose()
                .whenEquals(1).thenAccept(k -> log.info("one"))
                .whenEquals(2).thenAccept(k -> log.info("two"))
            .otherwise().nop()
            .end();
        Hope.of(2).choose()
                .whenEquals(1).thenAccept(k -> log.info("one"))
                .whenEquals(2).thenAccept(k -> log.info("two"))
            .otherwise().nop()
            .end();
        Hope.of(1).<String>chooseMap()
            .whenEquals(1).map(k -> Hope.of("one"))
            .whenEquals(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        Hope.of(2).<String>chooseMap()
            .whenEquals(1).map(k -> Hope.of("one"))
            .whenEquals(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        Hope.of(3).<String>chooseMap()
            .whenEquals(1).map(k -> Hope.of("one"))
            .whenEquals(2).map(k -> Hope.of("two"))
            .when(false).map(f -> Hope.of("dead"))
            .when(true).map(f -> Hope.of("bingo"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();

        Hope.of(1).choose()
                .whenEquals(1).thenApply(k -> Hope.of("a"))
            .whenEquals(2).nop()
            .otherwise().nop()
            .end();
        Hope.of(2).choose()
                .whenEquals(1).thenApply(k -> Hope.of("a"))
            .whenEquals(2).nop()
            .otherwise().nop()
            .end();

        Hope.<Integer>capture(new NullPointerException()).choose()
                .whenEquals(1).thenApply(k -> Hope.of("a"))
                .whenEquals(2).thenAccept(k -> {
                })
            .otherwise().nop()
            .end();
        Hope.<Number>capture(new NullPointerException()).<String>chooseMap()
            .whenEquals(1).map(k -> Hope.of("a"))
            .whenInstanceOf(Float.class).map(f -> Hope.of("float"))
            .when(false).map(f -> Hope.of("dead"))
            .when(true).map(f -> Hope.of("bingo"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
    }

    @Test
    void testC9() {
        None a01 = Hope.of(1).choose()
                .when(true).nop()
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a01.completeWithoutErrors());
        None a02 = Hope.of(1).choose()
                .whenInstanceOf(Integer.class).nop()
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a02.completeWithoutErrors());
    }

    @Test
    void testC10() {
        None a01 = ChooseContext.choose(1)
                .when(true).nop()
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a01.completeWithoutErrors());
        None a02 = ChooseContext.choose(1)
                .whenInstanceOf(Integer.class).nop()
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a02.completeWithoutErrors());
    }

    @Test
    void testC11() {
        Some<Integer> some = Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(1);
        Some<Integer> a01 = some.<Integer>chooseMap()
                .when(true).mapOf(it -> it * 2)
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a01.completeWithoutErrors());
        Assertions.assertFalse(a01.completeSuccess());

        Some<Integer> a02 = some.<Integer>chooseMap()
                .when(true).fault(MY_FAULT)
                .otherwise().mapOf(it -> it * 3)
                .end();
        Assertions.assertTrue(a02.completeWithErrors());

        Some<Integer> a03 = some.<Integer>chooseMap()
                .when(true).map(it -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(it + 3))
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a03.completeWithoutErrors());
        Assertions.assertFalse(a03.completeSuccess());

        Some<Integer> a04 = Some.of(1).<Integer>chooseMap()
                .when(true).map(it -> Some.<Integer>builder().withAlert(MY_ALERT).buildWithValue(it + 3))
                .otherwise().fault(MY_FAULT)
                .end();
        Assertions.assertTrue(a04.completeWithoutErrors());
        Assertions.assertFalse(a04.completeSuccess());
    }

    @Test
    void testSC1() {
        Random randm = new Random();
        List<Integer> values = IntStream.rangeClosed(1, 50).boxed().collect(Collectors.toList());
        values.forEach(value -> {
            None none1 = ChooseContext.choose(value)
                    .when(randm.nextDouble() < 0.05).thenApply(v -> Nope.nope())
                    .when(v -> v % 2 == 0).thenAccept(v -> log.info("{} is even", v))
                    .when(randm.nextDouble() < 0.1).thenAccept(v -> log.info("{} is bingo !!", v))
                    .when(randm.nextDouble() < 0.1).thenApply(v -> Nope.nope())
                    .when(v -> v % 3 == 0).thenAccept(v -> log.info("{} is 3-fold", v))
                    .when(v -> v % 5 == 0).thenApply(v -> Nope.nope())
                    .otherwise().thenAccept(v -> log.info("{} is odd", v))
                .end();

            Some<Integer> value1 = ChooseContext.<Integer, Integer>chooseMap(value)
                .when(v -> v % 2 == 0).map(v -> Hope.of(v * v))
                .when(randm.nextDouble() < 0.1).map(v -> Some.of(v / 2))
                .when(v -> v % 3 == 0).map(v -> Hope.capture(new NullPointerException()))
                .otherwise().map(v -> Hope.of(0))
                .end();
        });
        None none = ChooseContext.choose(1)
                .when(v -> v % 2 == 0).thenApply(v -> Hope.capture(new NullPointerException()))
                .otherwise().thenApply(v -> Nope.nope())
            .end();
        Assertions.assertTrue(none.completeWithoutErrors());
        None none2 = ChooseContext.choose(1)
                .when(v -> v % 2 == 0).thenApply(v -> Nope.nope())
                .otherwise().thenApply(v -> Hope.capture(new NullPointerException()))
            .end();
        Assertions.assertTrue(none2.completeWithErrors());
    }

    @Test
    void testSC2() {
        Nope a = Nope.fault(MY_FAULT, new Bad());
        Assertions.assertTrue(a.completeWithErrors());
        Failure fault = a.failure();
        System.out.println(fault.message());
    }

    @Test
    void testA01() {
        None a01 = testAlertEq(Hope.of(1).choose());
        Assertions.assertFalse(a01.completeWithErrors());
        Assertions.assertFalse(a01.completeSuccess());
        Assertions.assertTrue(a01.completeWarning());

        None a02 = testAlertEq(Hope.of(2).choose());
        Assertions.assertFalse(a02.completeWithErrors());
        Assertions.assertFalse(a02.completeWarning());
        Assertions.assertTrue(a02.completeSuccess());

        None a03 = testAlertEq(Hope.<Integer>fault(MY_FAULT).choose());
        Assertions.assertFalse(a03.completeSuccess());
        Assertions.assertFalse(a03.completeWarning());
        Assertions.assertTrue(a03.completeWithErrors());
    }

    @Test
    void testA02() {
        None a01 = testAlertAs(Hope.of(1).choose());
        Assertions.assertFalse(a01.completeWithErrors());
        Assertions.assertFalse(a01.completeSuccess());
        Assertions.assertTrue(a01.completeWarning());

        None a02 = testAlertAs(Hope.of("1").choose());
        Assertions.assertFalse(a02.completeWithErrors());
        Assertions.assertFalse(a02.completeWarning());
        Assertions.assertTrue(a02.completeSuccess());

        None a03 = testAlertAs(Hope.fault(MY_FAULT).choose());
        Assertions.assertFalse(a03.completeSuccess());
        Assertions.assertFalse(a03.completeWarning());
        Assertions.assertTrue(a03.completeWithErrors());
    }

    @Test
    void testI01() {
        Double pi = 3.14;
        Nope n = OptoContext.opto(pi)
                .whenInstanceOf(Number.class)
                .thenAccept(it -> log.info("is Number"))
                .whenInstanceOf(Double.class)
                .thenAccept(it -> log.info("isDouble"))
                .otherwise()
                .thenAccept(it -> log.info("unknown"))
                .end();
        if (pi instanceof Number) {
            log.info("is Number");
        } else if (pi instanceof Double) {
            log.info("isDouble");
        } else {
            log.info("unknown");
        }
        Hope<String> m = OptoContext.<Double, String>optoMap(pi)
                .whenInstanceOf(Number.class)
                .mapOf(it -> "Number")
                .whenInstanceOf(Double.class)
                .mapOf(it -> "Double")
                .otherwise()
                .mapOf(it -> "unknown")
                .end();
    }

    @Test
    void testA03() {
        None a01 = testAlertElse(Hope.of(1).choose());
        Assertions.assertFalse(a01.completeWithErrors());
        Assertions.assertFalse(a01.completeSuccess());
        Assertions.assertTrue(a01.completeWarning());

        None a03 = testAlertElse(Hope.<Integer>fault(MY_FAULT).choose());
        Assertions.assertFalse(a03.completeSuccess());
        Assertions.assertFalse(a03.completeWarning());
        Assertions.assertTrue(a03.completeWithErrors());
    }

    @Test
    void testR01() {
        None a01 = testAlertEq(ChooseContext.choose(1));
        Assertions.assertFalse(a01.completeWithErrors());
        Assertions.assertFalse(a01.completeSuccess());
        Assertions.assertTrue(a01.completeWarning());

        None a02 = testAlertEq(ChooseContext.choose(2));
        Assertions.assertFalse(a02.completeWithErrors());
        Assertions.assertFalse(a02.completeWarning());
        Assertions.assertTrue(a02.completeSuccess());
    }

    @Test
    void testR02() {
        None a01 = testAlertAs(ChooseContext.choose(1));
        Assertions.assertFalse(a01.completeWithErrors());
        Assertions.assertFalse(a01.completeSuccess());
        Assertions.assertTrue(a01.completeWarning());

        None a02 = testAlertAs(ChooseContext.choose("1"));
        Assertions.assertFalse(a02.completeWithErrors());
        Assertions.assertFalse(a02.completeWarning());
        Assertions.assertTrue(a02.completeSuccess());
    }

    @Test
    void testR03() {
        None a01 = testAlertElse(ChooseContext.choose(1));
        Assertions.assertFalse(a01.completeWithErrors());
        Assertions.assertFalse(a01.completeSuccess());
        Assertions.assertTrue(a01.completeWarning());
    }

    private None testAlertElse(ChooseNixInitialContext<Integer> chooseCtx) {
        return chooseCtx
            .when(false).nop()
            .otherwise().alert(MY_ALERT, "int")
            .end();
    }

    private <T> None testAlertAs(ChooseNixInitialContext<T> chooseCtx) {
        return chooseCtx
                .whenInstanceOf(Integer.class).alert(MY_ALERT, "int")
                .otherwise().nop()
                .end();
    }

    private None testAlertEq(ChooseNixInitialContext<Integer> chooseCtx) {
        return chooseCtx
            .whenEquals(1).alert(MY_ALERT, "one")
            .otherwise().nop()
            .end();
    }


    static class Bad {
        public String toString() {
            throw new RuntimeException();
        }
    }

}
