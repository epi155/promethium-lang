package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class TestConcat {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestConcat.class);

    static final CustMsg NEG = PmCustMsg.of("NEG", "Negative at {}");

    @Test
    void test01() {
        @NotNull Some<Vier> k1 = func1(3)
                .map(this::func2)
                .map(this::func3)
                .map(this::func4);
    }

    @Test
    void test02() {
        @NotNull None k1 = func1(1)
                .map(this::func2)
                .map(this::func3)
                .map(this::func4)
                .asNone();
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test03() {
        @NotNull None k1 = func1(1)
                .ergo(it -> func2(it)
                        .ergo(jt -> func3(jt)
                                .ergo(this::func4)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    public void test23() {
        @NotNull None k1 = func1(1)
                .ergo(it -> func2(it)
                        .ergo(jt -> fun3(jt)
                                .ergo(this::fun4)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    public void test32() {
        @NotNull Nope k1 = fun1(1)
                .maps(this::fun2)
                .maps(this::fun3)
                .maps(this::fun4)
                .asNope();
        k1.onFailure(e -> log.warn(e.message()));
    }

    @Test
    void test33() {
        @NotNull None k1 = fun1(1)
                .ergo(it -> fun2(it)
                        .ergo(jt -> fun3(jt)
                                .ergo(this::fun4)));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

//    @Test
//    public void test50() {
//        val list = Arrays.asList(1, 2, 3, 4, 5, 6);
//        val bld = None.builder();
//        list.stream()
//                .map(this::func1)
//                .flatMap(bld::flat)
//                .map(this::func2)
//            .flatMap(bld::flat)
//            .map(this::func3)
//            .flatMap(bld::flat)
//            .map(this::func4)
//            .flatMap(bld::flat)
//            .forEach(it -> {
//            });
//        val z = bld.build();
//        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));
//    }


    @Test
    void test51() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        None z = list.stream().map(n -> func1(n)
                .map(this::func2)
                .map(this::func3)
                .map(this::func4)).collect(None.collect());
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test52() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        None z = list.stream().map(n -> fun1(n)
                .maps(this::fun2)
                .maps(this::fun3)
                .maps(this::fun4)).collect(None.collect());
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));

    }

    @Test
    void test53() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        @NotNull NoneBuilder bld = None.builder();
        list.forEach(n -> bld.withStatus(fun1(n)
                .ergo(it -> fun2(it)
                        .ergo(jt -> fun3(jt)
                                .ergo(this::fun4)))));
        @NotNull None k1 = bld.build();
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test54() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        None z = list.stream().map(n -> fun1(n)
                .ergo(it -> fun2(it)
                        .ergo(jt -> fun3(jt)
                                .ergo(this::fun4)
                        )
                )
        ).collect(None.collect());
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test55() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        None z = list.stream()
                .map(n -> func1(n)
                        .mapOf(it -> it.value * 2)
                        .mapOf(it -> it + 2)
                )
                .collect(None.collect());
        z.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    Some<Eins> func1(int k) {
        try {
            Eins value = new Eins(k);
            return Some.of(value);
        } catch (Exception e) {
            return Some.capture(e);
        }
    }

    @Test
    void test63() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        @NotNull NoneBuilder bld = None.builder();
        @NotNull None k1 = bld.iterableOf(list).forEach(n -> fun1(n)
                        .ergo(it -> fun2(it)
                                .ergo(jt -> fun3(jt)
                                        .ergo(this::fun4))))
                .build();
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test64() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        @NotNull None k1 = None.iterableOf(list).forEach(n -> fun1(n)
                .ergo(it -> fun2(it)
                        .ergo(jt -> fun3(jt)
                                .ergo(this::fun4))));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test65() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        @NotNull None k1 = None.iterableOf(list).forEach(n -> fun1(n)
                .maps(this::fun2)
                .maps(this::fun3)
                .maps(this::fun4));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    @Test
    void test66() {
        Stream<Integer> stream = IntStream.range(0, 100).boxed();
        @NotNull None k1 = None.streamOf(stream).forEachParallel(5, n -> fun1(n)
                .maps(this::fun2)
                .maps(this::fun3)
                .maps(this::fun4));
        k1.onFailure(es -> es.forEach(e -> log.warn(e.message())));
    }

    public void test67() {
        AnyValue<Integer> anyVal = Hope.of(10);
        anyVal.iterable(n -> IntStream.range(0, n).mapToObj(Hope::of).collect(Collectors.toList()))
            .forEach(k -> Nope.nope());
    }
    Hope<Eins> fun1(int k) {
        try {
            Eins value = new Eins(k);
            return Hope.of(value);
        } catch (Exception e) {
            return Hope.capture(e);
        }
    }

    Some<Zwei> func2(Eins value) {
        try {
            Zwei next = new Zwei(value);
            return Some.of(next);
        } catch (Exception e) {
            return Some.capture(e);
        }
    }

    Hope<Zwei> fun2(Eins value) {
        try {
            Zwei next = new Zwei(value);
            return Hope.of(next);
        } catch (Exception e) {
            return Hope.capture(e);
        }
    }

    Some<Drei> func3(Zwei value) {
        try {
            Drei next = new Drei(value);
            return Some.of(next);
        } catch (Exception e) {
            return Some.capture(e);
        }
    }

    Hope<Drei> fun3(Zwei value) {
        try {
            Drei next = new Drei(value);
            return Hope.of(next);
        } catch (Exception e) {
            return Hope.capture(e);
        }
    }

    Some<Vier> func4(Drei value) {
        try {
            Vier next = new Vier(value);
            return Some.of(next);
        } catch (Exception e) {
            return Some.capture(e);
        }
    }

    Hope<Vier> fun4(Drei value) {
        try {
            Vier next = new Vier(value);
            return Hope.of(next);
        } catch (Exception e) {
            return Hope.capture(e);
        }
    }

    private static class Eins {
        private final int value;

        public Eins(int value) {
            this.value = value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new InvalidParameterException();
        }
    }

    private static class Zwei {
        private final int value;

        public Zwei(Eins value) {
            this.value = value.value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new InvalidParameterException();
        }
    }

    private static class Drei {
        private final int value;

        public Drei(Zwei value) {
            this.value = value.value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new InvalidParameterException();
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static class Vier {
        private final int value;

        public Vier(Drei value) {
            this.value = value.value - 1;
            log.debug("Current value is {}", this.value);
            if (this.value < 0) throw new InvalidParameterException();
        }
    }
}
