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
class TestChoice {
    private static final MsgError MY_FAULT = MsgError.of("CA01", "Oop error {} !!");
    @Test
    void testC1() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>failure(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
            .when(fst.get()).implies(v -> fst.set(false))
            .when(v -> v%2 == 0).implies(v -> log.info("{} is even", v))
            .when(v -> v%3 == 0).implies(v -> log.info("{} is 3-fold", v))
            .otherwise().implies(v -> log.info("{} is odd", v))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }
    @Test
    void testC2() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>failure(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
            .when(fst.get()).perform(v -> {
                fst.set(false);
                return Nope.nope();
            })
            .when(v -> v%2 == 0).perform(v -> randm.nextInt(v) < v/2 ? Hope.of(v/2) : Hope.failure(MY_FAULT, v))
            .when(v -> v%3 == 0).perform(v -> Nope.nope())
            .otherwise().perform(v -> randm.nextDouble()<0.5 ? Hope.of(1) : Hope.capture(new NoSuchElementException()))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }

    @Test
    void testC3() {
        val randm = new Random();
        List<Hope<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Hope.<Integer>failure(MY_FAULT, k) : Hope.of(k))
            .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> {
                it.<String>choiceTo()
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
                    .onFailure(es -> es.forEach(e -> log.warn("Error is: {}", e.message())));
            });
    }

    @Test
    void testC6() {
        val randm = new Random();
        List<Some<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>failure(MY_FAULT, k) : Some.of(k))
            .collect(Collectors.toList());
        val bld = None.builder();
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> bld.add(it.choice()
            .when(fst.get()).implies(v -> fst.set(false))
            .when(v -> v%2 == 0).implies(v -> log.info("{} is even", v))
            .when(v -> v%3 == 0).implies(v -> log.info("{} is 3-fold", v))
            .otherwise().implies(v -> log.info("{} is odd", v))
            .end()));
        val none = bld.build();
        none.onFailure(es -> es.forEach(s -> log.warn("Errore: {}", s.message())));
    }
    @Test
    void testC7() {
        val randm = new Random();
        List<Some<Integer>> values = IntStream.rangeClosed(1, 50)
            .mapToObj(k -> (randm.nextDouble() < 0.2) ? Some.<Integer>failure(MY_FAULT, k) : Some.of(k))
            .collect(Collectors.toList());
        AtomicBoolean fst = new AtomicBoolean(true);
        values.forEach(it -> {
            it.<String>choiceTo()
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
                .onFailure(es -> es.forEach(e -> log.warn("Error is: {}", e.message())));
        });
    }
    @Test
    void testC8() {
        Number[] nn = { 1, 2L, 3F, 3.1415926535_8979323846_2643383279 };
        for(Number z: nn) {
            val a = Hope.of(z).choice()
                .whenInstanceOf(Integer.class).implies(n -> n++)
                .whenInstanceOf(Double.class).perform(n -> Hope.of(Math.sqrt(n)))
                .otherwise().perform(x -> Nope.failure(MY_FAULT))
                .end();
            val b = Hope.of(z).<String>choiceTo()
                .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                .otherwise().map(x -> Hope.failure(MY_FAULT))
                .end();
            val c = ChoiceContext.choice(z)
                .whenInstanceOf(Integer.class).implies(n -> n++)
                .whenInstanceOf(Double.class).perform(n -> Hope.of(Math.sqrt(n)))
                .otherwise().perform(x -> Nope.failure(MY_FAULT))
                .end();
            val d = ChoiceContext.<Number,String>choiceTo(z)
                .whenInstanceOf(Integer.class).map(n -> Hope.of("String"))
                .whenInstanceOf(Double.class).map(n -> Hope.of("Double"))
                .otherwise().map(x -> Hope.failure(MY_FAULT))
                .end();
        }

    }
    @Test
    void testSC1() {
        val randm = new Random();
        List<Integer> values = IntStream.rangeClosed(1, 50).boxed().collect(Collectors.toList());
        values.forEach(value -> {
            None none1 = ChoiceContext.choice(value)
                .when(randm.nextDouble()<0.05).perform(v -> Nope.nope())
                .when(v -> v%2 == 0).implies(v -> log.info("{} is even", v))
                .when(randm.nextDouble()<0.1).implies(v -> log.info("{} is bingo !!", v))
                .when(randm.nextDouble()<0.1).perform(v -> Nope.nope())
                .when(v -> v%3 == 0).implies(v -> log.info("{} is 3-fold", v))
                .when(v -> v%5 == 0).perform(v -> Nope.nope())
                .otherwise().implies(v -> log.info("{} is odd", v))
                .end();

            Some<Integer> value1 = ChoiceContext.<Integer,Integer>choiceTo(value)
                .when(v -> v%2 == 0).map(v -> Hope.of(v*v))
                .when(randm.nextDouble()<0.1).map(v -> Some.of(v/2))
                .when(v -> v%3 == 0).map(v -> Hope.capture(new NullPointerException()))
                .otherwise().map(v -> Hope.of(0))
                .end();
        });
        None none = ChoiceContext.choice(1)
            .when(v -> v%2 == 0).perform(v -> Hope.capture(new NullPointerException()))
            .otherwise().perform(v -> Nope.nope())
            .end();
        None none2 = ChoiceContext.choice(1)
            .when(v -> v%2 == 0).perform(v -> Nope.nope())
            .otherwise().perform(v -> Hope.capture(new NullPointerException()))
            .end();
    }

    private AnyValue<String> funcOne(String s) {
        return Hope.of(s);
    }

    private AnyError actionOne(String s) {
        return Nope.nope();
    }

}
