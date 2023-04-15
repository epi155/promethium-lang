package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class TestChoice {
    private static final CustMsg MY_FAULT = CustMsg.of("CA01", "Oop error {} !!");

    @Test
    void testC0() {
        @NotNull None ee = Some.<Integer>builder()
            .withAlert(MY_FAULT)
            .buildWithValue(100)
            .choice()
            .when(it -> it > 50)
            .ergo(it -> Nope.fault(MY_FAULT))
            .otherwise().nop()
                .end();
        System.out.println(ee);
        @NotNull None res = Hope.<Integer>fault(MY_FAULT).choice()
                .when(1).peek(it -> System.out.println("one"))
                .otherwise().peek(it -> System.out.println("off"))
                .end();
//        @NotNull None z = Hope.of(1).choice()
//                .when(1).peek(k -> System.out.println("one"))
//                .when(2).ergo(k -> Nope.failure(MY_FAULT))
//                .otherwise().peek(k -> System.out.println("boh"))
//                .end();
//        @NotNull Some<String> s = Hope.of(2).<String>choiceMap()
//                .when(1).map(k -> Hope.of("one").mapOf(String::toUpperCase))
//                .when(2).mapOf(k -> "Two")
//                .when(k -> k==3).mapOf(k -> "Two")
//                .whenInstanceOf(String.class).mapOf(String::trim)
//                .otherwise().map(k -> Hope.failure(MY_FAULT))
//                .end();
    }

    @Test
    public void testC1() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>fault(MY_FAULT, k) : Hope.of(k))
                .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
                .when(fst.get()).peek(v -> fst.set(false))
                .when(v -> v % 2 == 0).peek(v -> log.info("{} is even", v))
                .when(v -> v % 3 == 0).peek(v -> log.info("{} is 3-fold", v))
                .otherwise().peek(v -> log.info("{} is odd", v))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }
    @Test
    public void testC2() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>fault(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
                .when(fst.get()).ergo(v -> {
                    fst.set(false);
                    return Nope.nope();
                })
                .when(v -> v % 2 == 0).ergo(v -> randm.nextInt(v) < v / 2 ? Hope.of(v / 2) : Hope.fault(MY_FAULT, v))
                .when(v -> v % 3 == 0).ergo(v -> Nope.nope())
                .otherwise().ergo(v -> randm.nextDouble() < 0.5 ? Hope.of(1) : Hope.capture(new NoSuchElementException()))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }

    @Test
    public void testC3() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>fault(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> it.<String>choiceMap()
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
    public void testC6() {
        val randm = new Random();
        List<Some<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>fault(MY_FAULT, k) : Some.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
                .when(fst.get()).peek(v -> fst.set(false))
                .when(v -> v % 2 == 0).peek(v -> log.info("{} is even", v))
                .when(v -> v % 3 == 0).peek(v -> log.info("{} is 3-fold", v))
                .otherwise().peek(v -> log.info("{} is odd", v))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }
    @Test
    public void testC7() {
        val randm = new Random();
        List<Some<Integer>> values = IntStream.rangeClosed(1, 50)
                .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>fault(MY_FAULT, k) : Some.of(k))
            .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> it.<String>choiceMap()
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
    public void testC8() {
        Number[] nn = { 1, 2L, 3F, 3.1415926535_8979323846_2643383279 };
        for(Number z: nn) {
            val a = Hope.of(z).choice()
                    .whenInstanceOf(Integer.class).peek(n -> n++)
                    .whenInstanceOf(Double.class).ergo(n -> Hope.of(Math.sqrt(n)))
                    .otherwise().ergo(x -> Nope.fault(MY_FAULT))
                    .end();
            val b = Hope.of(z).<String>choiceMap()
                    .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                    .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                    .otherwise().map(x -> Hope.fault(MY_FAULT))
                    .end();
            val c = ChoiceContext.choice(z)
                    .whenInstanceOf(Integer.class).peek(n -> n++)
                    .whenInstanceOf(Double.class).ergo(n -> Hope.of(Math.sqrt(n)))
                    .otherwise().ergo(x -> Nope.fault(MY_FAULT))
                    .end();
            val d = ChoiceContext.<Number, String>choiceMap(z)
                    .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                    .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                    .otherwise().map(x -> Hope.fault(MY_FAULT))
                    .end();
        }
        ChoiceContext.choice(1)
            .when(1).peek(k -> log.info("one"))
            .when(2).peek(k -> log.info("two"))
            .otherwise().nop()
            .end();
        ChoiceContext.choice(2)
            .when(1).peek(k -> log.info("one"))
            .when(2).peek(k -> log.info("two"))
            .otherwise().nop()
            .end();
        ChoiceContext.<Integer,String>choiceMap(1)
            .when(1).map(k -> Hope.of("one"))
            .when(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        ChoiceContext.<Integer,String>choiceMap(1)
            .when(1).map(k -> Hope.of("one"))
            .when(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        ChoiceContext.<Integer,String>choiceMap(2)
            .when(1).map(k -> Hope.of("one"))
            .when(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        ChoiceContext.<Integer,String>choiceMap(3)
            .when(1).map(k -> Hope.of("one"))
            .when(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();

        ChoiceContext.choice(1)
            .when(1).ergo(k -> Hope.of("a"))
            .when(2).peek(k -> Nope.nope())
            .otherwise().nop()
            .end();
        ChoiceContext.choice(2)
            .when(1).ergo(k -> Hope.of("a"))
            .when(2).peek(k -> Nope.nope())
            .otherwise().nop()
            .end();

        Hope.of(1).choice()
            .when(1).peek(k -> log.info("one"))
            .when(2).peek(k -> log.info("two"))
            .otherwise().nop()
            .end();
        Hope.of(2).choice()
            .when(1).peek(k -> log.info("one"))
            .when(2).peek(k -> log.info("two"))
            .otherwise().nop()
            .end();
        Hope.of(1).<String>choiceMap()
            .when(1).map(k -> Hope.of("one"))
            .when(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        Hope.of(2).<String>choiceMap()
            .when(1).map(k -> Hope.of("one"))
            .when(2).map(k -> Hope.of("two"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
        Hope.of(3).<String>choiceMap()
            .when(1).map(k -> Hope.of("one"))
            .when(2).map(k -> Hope.of("two"))
            .when(false).map(f -> Hope.of("dead"))
            .when(true).map(f -> Hope.of("bingo"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();

        Hope.of(1).choice()
            .when(1).ergo(k -> Hope.of("a"))
            .when(2).peek(k -> Nope.nope())
            .otherwise().nop()
            .end();
        Hope.of(2).choice()
            .when(1).ergo(k -> Hope.of("a"))
            .when(2).peek(k -> Nope.nope())
            .otherwise().nop()
            .end();

        Hope.<Integer>capture(new NullPointerException()).choice()
            .when(1).ergo(k -> Hope.of("a"))
            .when(2).peek(k -> Nope.nope())
            .otherwise().nop()
            .end();
        Hope.<Number>capture(new NullPointerException()).<String>choiceMap()
            .when(1).map(k -> Hope.of("a"))
            .whenInstanceOf(Float.class).map(f -> Hope.of("float"))
            .when(false).map(f -> Hope.of("dead"))
            .when(true).map(f -> Hope.of("bingo"))
            .otherwise().map(k -> Hope.of("boh"))
            .end();
    }
    @Test
    public void testSC1() {
        val randm = new Random();
        List<Integer> values = IntStream.rangeClosed(1, 50).boxed().collect(Collectors.toList());
        values.forEach(value -> {
            None none1 = ChoiceContext.choice(value)
                    .when(randm.nextDouble() < 0.05).ergo(v -> Nope.nope())
                    .when(v -> v % 2 == 0).peek(v -> log.info("{} is even", v))
                    .when(randm.nextDouble() < 0.1).peek(v -> log.info("{} is bingo !!", v))
                    .when(randm.nextDouble() < 0.1).ergo(v -> Nope.nope())
                    .when(v -> v % 3 == 0).peek(v -> log.info("{} is 3-fold", v))
                    .when(v -> v % 5 == 0).ergo(v -> Nope.nope())
                    .otherwise().peek(v -> log.info("{} is odd", v))
                .end();

            Some<Integer> value1 = ChoiceContext.<Integer,Integer>choiceMap(value)
                .when(v -> v%2 == 0).map(v -> Hope.of(v*v))
                .when(randm.nextDouble()<0.1).map(v -> Some.of(v/2))
                .when(v -> v%3 == 0).map(v -> Hope.capture(new NullPointerException()))
                .otherwise().map(v -> Hope.of(0))
                .end();
        });
        None none = ChoiceContext.choice(1)
                .when(v -> v % 2 == 0).ergo(v -> Hope.capture(new NullPointerException()))
                .otherwise().ergo(v -> Nope.nope())
            .end();
        None none2 = ChoiceContext.choice(1)
                .when(v -> v % 2 == 0).ergo(v -> Nope.nope())
                .otherwise().ergo(v -> Hope.capture(new NullPointerException()))
            .end();
    }

    private AnyValue<String> funcOne(String s) {
        return Hope.of(s);
    }

    private AnyError actionOne(String s) {
        return Nope.nope();
    }

}
