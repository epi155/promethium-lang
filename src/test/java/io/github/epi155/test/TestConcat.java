package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@Slf4j
class TestConcat {
    static final MsgError NEG = MsgError.of("NEG", "Negative at {}");

    @Test
    void test01() {
        val k1 = func1(3)
                .andThen(this::func2)
                .andThen(this::func3)
                .andThen(this::func4);
    }

    @Test
    void test02() {
        val k1 = func1(1)
                .andThen(this::func2)
                .andThen(this::func3)
                .andThen(this::func4)
                .asNone();
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test03() {
        val k1 = func1(1)
                .and(it -> func2(it)
                        .and(jt -> func3(jt)
                                .and(this::func4)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    void test23() {
        val k1 = func1(1)
                .and(it -> func2(it)
                        .and(jt -> fun3(jt)
                                .and(this::fun4)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    void test32() {
        val k1 = fun1(1)
                .andThen(this::fun2)
                .andThen(this::fun3)
                .andThen(this::fun4)
                .asNope();
        k1.onFailure(e -> log.warn(e.message()));
    }

    @Test
    void test33() {
        val k1 = fun1(1)
                .and(it -> fun2(it)
                        .and(jt -> fun3(jt)
                                .and(this::fun4)));
        k1.onFailure(e -> log.warn(e.message()));
    }

    @Test
    void test50() {
        val list = Arrays.asList(1, 2, 3, 4, 5, 6);
        val bld = None.builder();
        list.stream()
                .map(this::func1)
                .flatMap(bld::flat)
                .map(this::func2)
                .flatMap(bld::flat)
                .map(this::func3)
                .flatMap(bld::flat)
                .map(this::func4)
                .flatMap(bld::flat)
                .forEach(it -> {
                });
        val z = bld.build();
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test51() {
        val list = Arrays.asList(1, 2, 3, 4, 5, 6);
        None z = list.stream().map(n -> func1(n)
                .andThen(this::func2)
                .andThen(this::func3)
                .andThen(this::func4)).collect(None.collect());
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test52() {
        val list = Arrays.asList(1, 2, 3, 4, 5, 6);
        None z = list.stream().map(n -> fun1(n)
                .andThen(this::fun2)
                .andThen(this::fun3)
                .andThen(this::fun4)).collect(None.collect());
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));

    }

    @Test
    void test53() {
        val list = Arrays.asList(1, 2, 3, 4, 5, 6);
        val bld = None.builder();
        list.forEach(n -> bld.join(fun1(n)
                .and(it -> fun2(it)
                        .and(jt -> fun3(jt)
                                .and(this::fun4)))));
        val k1 = bld.build();
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }
    @Test
    void test54() {
        val list = Arrays.asList(1, 2, 3, 4, 5, 6);
        val z = list.stream().map(n -> fun1(n)
                .and(it -> fun2(it)
                        .and(jt -> fun3(jt)
                                .and(this::fun4)
                        )
                )
        ).collect(None.collect());
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    Some<Eins> func1(int k) {
        try {
            Eins value = new Eins(k);
            return Some.of(value);
        } catch (FailureException e) {
            return Some.capture(e);
        }
    }

    Hope<Eins> fun1(int k) {
        try {
            Eins value = new Eins(k);
            return Hope.of(value);
        } catch (FailureException e) {
            return Hope.capture(e);
        }
    }

    Some<Zwei> func2(Eins value) {
        try {
            val next = new Zwei(value);
            return Some.of(next);
        } catch (FailureException e) {
            return Some.capture(e);
        }
    }

    Hope<Zwei> fun2(Eins value) {
        try {
            val next = new Zwei(value);
            return Hope.of(next);
        } catch (FailureException e) {
            return Hope.capture(e);
        }
    }

    Some<Drei> func3(Zwei value) {
        try {
            val next = new Drei(value);
            return Some.of(next);
        } catch (FailureException e) {
            return Some.capture(e);
        }
    }

    Hope<Drei> fun3(Zwei value) {
        try {
            val next = new Drei(value);
            return Hope.of(next);
        } catch (FailureException e) {
            return Hope.capture(e);
        }
    }

    Some<Vier> func4(Drei value) {
        try {
            val next = new Vier(value);
            return Some.of(next);
        } catch (FailureException e) {
            return Some.capture(e);
        }
    }

    Hope<Vier> fun4(Drei value) {
        try {
            val next = new Vier(value);
            return Hope.of(next);
        } catch (FailureException e) {
            return Hope.capture(e);
        }
    }

    private static class Eins {
        private final int value;

        public Eins(int value) throws FailureException {
            this.value = value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new FailureException(NEG, 1);
        }
    }

    private static class Zwei {
        private final int value;

        public Zwei(Eins value) throws FailureException {
            this.value = value.value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new FailureException(NEG, 2);
        }
    }

    private static class Drei {
        private final int value;

        public Drei(Zwei value) throws FailureException {
            this.value = value.value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new FailureException(NEG, 3);
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static class Vier {
        private final int value;

        public Vier(Drei value) throws FailureException {
            this.value = value.value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new FailureException(NEG, 4);
        }
    }
}
