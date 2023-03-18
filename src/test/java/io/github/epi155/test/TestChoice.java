package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class TestChoice {
    private static final MsgError MY_FAULT = MsgError.of("CA01", "Oop error {} !!");
    @Test
    public void testC1() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>failure(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
            .when(fst.get()).accept(v -> fst.set(false))
            .when(v -> v%2 == 0).accept(v -> log.info("{} is even", v))
            .when(v -> v%3 == 0).accept(v -> log.info("{} is 3-fold", v))
            .otherwise().accept(v -> log.info("{} is odd", v))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }
    @Test
    public void testC2() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>failure(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
            .when(fst.get()).apply(v -> {
                fst.set(false);
                return Nope.nope();
            })
            .when(v -> v%2 == 0).apply(v -> randm.nextInt(v) < v/2 ? Hope.of(v/2) : Hope.failure(MY_FAULT, v))
            .when(v -> v%3 == 0).apply(v -> Nope.nope())
            .otherwise().apply(v -> randm.nextDouble()<0.5 ? Hope.of(1) : Hope.capture(new NoSuchElementException()))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }

    @Test
    public void testC3() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>failure(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> it.<String>choiceMap()
            .when(fst.get()).map(k -> {
                fst.set(false);
                return Hope.of("zero");
            })
            .when(v -> v % 4 == 0).map(v -> Hope.of("quattro"))
            .when(v -> v % 4 == 1).map(v -> Hope.failure(MY_FAULT, v))
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
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>failure(MY_FAULT, k) : Some.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
            .when(fst.get()).accept(v -> fst.set(false))
            .when(v -> v%2 == 0).accept(v -> log.info("{} is even", v))
            .when(v -> v%3 == 0).accept(v -> log.info("{} is 3-fold", v))
            .otherwise().accept(v -> log.info("{} is odd", v))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }
    @Test
    public void testC7() {
        val randm = new Random();
        List<Some<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>failure(MY_FAULT, k) : Some.of(k))
            .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> it.<String>choiceMap()
            .when(fst.get()).map(k -> {
                fst.set(false);
                return Hope.of("zero");
            })
            .when(v -> v % 4 == 0).map(v -> Hope.of("quattro"))
            .when(v -> v % 4 == 1).map(v -> Hope.failure(MY_FAULT, v))
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
                .whenInstanceOf(Integer.class).accept(n -> n++)
                .whenInstanceOf(Double.class).apply(n -> Hope.of(Math.sqrt(n)))
                .otherwise().apply(x -> Nope.failure(MY_FAULT))
                .end();
            val b = Hope.of(z).<String>choiceMap()
                .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                .otherwise().map(x -> Hope.failure(MY_FAULT))
                .end();
            val c = ChoiceContext.choice(z)
                .whenInstanceOf(Integer.class).accept(n -> n++)
                .whenInstanceOf(Double.class).apply(n -> Hope.of(Math.sqrt(n)))
                .otherwise().apply(x -> Nope.failure(MY_FAULT))
                .end();
            val d = ChoiceContext.<Number,String>choiceMap(z)
                .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                .otherwise().map(x -> Hope.failure(MY_FAULT))
                .end();
        }
        ChoiceContext.choice(1)
            .when(1).accept(k -> log.info("one"))
            .when(2).accept(k -> log.info("two"))
            .end();
        ChoiceContext.choice(2)
            .when(1).accept(k -> log.info("one"))
            .when(2).accept(k -> log.info("two"))
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
            .when(1).apply(k -> Hope.of("a"))
            .when(2).accept(k -> Nope.nope())
            .end();
        ChoiceContext.choice(2)
            .when(1).apply(k -> Hope.of("a"))
            .when(2).accept(k -> Nope.nope())
            .end();

        Hope.of(1).choice()
            .when(1).accept(k -> log.info("one"))
            .when(2).accept(k -> log.info("two"))
            .end();
        Hope.of(2).choice()
            .when(1).accept(k -> log.info("one"))
            .when(2).accept(k -> log.info("two"))
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
            .when(1).apply(k -> Hope.of("a"))
            .when(2).accept(k -> Nope.nope())
            .end();
        Hope.of(2).choice()
            .when(1).apply(k -> Hope.of("a"))
            .when(2).accept(k -> Nope.nope())
            .end();

        Hope.<Integer>capture(new NullPointerException()).choice()
            .when(1).apply(k -> Hope.of("a"))
            .when(2).accept(k -> Nope.nope())
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
                .when(randm.nextDouble()<0.05).apply(v -> Nope.nope())
                .when(v -> v%2 == 0).accept(v -> log.info("{} is even", v))
                .when(randm.nextDouble()<0.1).accept(v -> log.info("{} is bingo !!", v))
                .when(randm.nextDouble()<0.1).apply(v -> Nope.nope())
                .when(v -> v%3 == 0).accept(v -> log.info("{} is 3-fold", v))
                .when(v -> v%5 == 0).apply(v -> Nope.nope())
                .otherwise().accept(v -> log.info("{} is odd", v))
                .end();

            Some<Integer> value1 = ChoiceContext.<Integer,Integer>choiceMap(value)
                .when(v -> v%2 == 0).map(v -> Hope.of(v*v))
                .when(randm.nextDouble()<0.1).map(v -> Some.of(v/2))
                .when(v -> v%3 == 0).map(v -> Hope.capture(new NullPointerException()))
                .otherwise().map(v -> Hope.of(0))
                .end();
        });
        None none = ChoiceContext.choice(1)
            .when(v -> v%2 == 0).apply(v -> Hope.capture(new NullPointerException()))
            .otherwise().apply(v -> Nope.nope())
            .end();
        None none2 = ChoiceContext.choice(1)
            .when(v -> v%2 == 0).apply(v -> Nope.nope())
            .otherwise().apply(v -> Hope.capture(new NullPointerException()))
            .end();
    }

    private AnyValue<String> funcOne(String s) {
        return Hope.of(s);
    }

    private AnyError actionOne(String s) {
        return Nope.nope();
    }

}
