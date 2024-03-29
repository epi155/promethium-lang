package io.github.epi155.test;

import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.NoneBuilder;
import io.github.epi155.pm.lang.Signal;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class TestCustom2 {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestCustom2.class);

    void example2(CustomReader rd, CustomWriter wr) {
        @NotNull NoneBuilder bld = None.builder();
        Iterable<CustomInput> iterable = rd.Iterable();
        for (CustomInput input : iterable) {
            firstStep(input)
                    .onSuccess(temp -> secondStep(temp)
                            .onSuccess(wr::write)
                            .onFailure(bld::add))
                    .onFailure(bld::add);
        }
        None none = bld.build();
        report(none.signals());
    }

    void example3(CustomReader rd, CustomWriter wr) {
        Iterable<CustomInput> iterable = rd.Iterable();
        None none = None.iterableOf(iterable)
                .forEach(input -> firstStep(input)
                        .ergo(temp -> secondStep(temp)
                                .implies(wr::write)));
        report(none.signals());
    }

    void example4(CustomReader rd, CustomWriter wr) {
        Iterable<CustomInput> iterable = rd.Iterable();
        None none = None.iterableOf(iterable)
                .forEach(input -> firstStep(input)
                        .maps(this::secondStep)
                        .implies(wr::write));
        report(none.signals());
    }

    void example5(CustomReader rd, CustomWriter wr) {
        Stream<CustomInput> stream = rd.stream();
        None none = stream
            .map(input -> firstStep(input)
                    .maps(this::secondStep)
                    .implies(wr::write))
            .collect(None.collect());
        report(none.signals());
    }

    void example6(CustomReader rd, CustomWriter wr) {
        Stream<CustomInput> stream = rd.stream();
        None none = None.streamOf(stream)
                .forEach(input -> firstStep(input)
                        .maps(this::secondStep)
                        .implies(wr::write));
        report(none.signals());
    }

    private void report(@NotNull Collection<? extends Signal> errors) {
    }

    private Hope<CustomOutput> secondStep(TempData tempData) {
        return null;
    }

    private Hope<TempData> firstStep(CustomInput input) {
        return null;
    }

    void example13(CustomReader rd, CustomWriter wr) {
        Iterable<CustomInput> iterable = rd.Iterable();
        None none = None.iterableOf(iterable)
            .forEach(input -> firstStep(input)
                    .choose()
                    .when(it -> it.equals("r"))
                    .thenAccept(it -> {
                    })
                    .when(System.nanoTime() > 1_000_000_000)
                    .thenAccept(it -> log.info("hrrlo2"))
                    .otherwise()
                    .thenApply(temp -> secondStep(temp)
                            .implies(wr::write))
                .end()
            );
        searchFor(1)
                .choose()
                .when(Optional::isPresent).thenAccept(it -> log.info("match at {}", it.get()))
                .otherwise().thenAccept(it -> log.info("None match"))
            .end().onFailure(es -> es.forEach(e -> log.warn("Error: {}", e.message())));
        searchFor(1)
            .<Integer>chooseMap()
                .when(Optional::isPresent)
                    .map(it -> Hope.of(it.get()+1))
                .otherwise()
                    .map(it->Hope.of(0))
            .end()
            .onSuccess(it -> log.info("Found {}", it));
        report(none.signals());
    }

    private @NotNull Hope<Optional<Integer>> searchFor(int i) {
        int k = new Random(1).nextInt(5);
        if (i<k) return Hope.of(Optional.of(k));
        if (i>k) return Hope.of(Optional.empty());
        return Hope.fault(PmCustMsg.of("NF100P", "Collision at {}"), i);
    }

    private interface CustomReader {
        Iterable<CustomInput> Iterable();

        Stream<CustomInput> stream();
    }

    private interface CustomWriter {
        void write(CustomOutput output);
    }

    private interface TempData {
    }

    private interface CustomOutput {
    }

    private static class CustomError {
        public static CustomError of(CustomInput input, FirstException e) {
            return null;
        }

        public static CustomError of(TempData tempData, SecondException e) {
            return null;
        }
    }

    private class CustomInput {
    }

    private class FirstException extends Exception {
    }

    private class SecondException extends Exception {
    }
}
