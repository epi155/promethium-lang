package io.github.epi155.test;

import io.github.epi155.pm.lang.Failure;
import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.MsgError;
import io.github.epi155.pm.lang.None;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
public class TestCustom2 {
    void example2(CustomReader rd, CustomWriter wr) {
        val bld = None.builder();
        Iterable<CustomInput> iterable = rd.Iterable();
        for (CustomInput input : iterable) {
            firstStep(input)
                .onSuccess(temp -> secondStep(temp)
                    .onSuccess(wr::write)
                    .onFailure(bld::add))
                .onFailure(bld::add);
        }
        None none = bld.build();
        report(none.errors());
    }

    void example3(CustomReader rd, CustomWriter wr) {
        Iterable<CustomInput> iterable = rd.Iterable();
        None none = None.iterableOf(iterable)
            .forEach(input -> firstStep(input)
                .and(temp -> secondStep(temp)
                    .implies(wr::write)));
        report(none.errors());
    }

    void example4(CustomReader rd, CustomWriter wr) {
        Iterable<CustomInput> iterable = rd.Iterable();
        None none = None.iterableOf(iterable)
            .forEach(input -> firstStep(input)
                .map(this::secondStep)
                .implies(wr::write));
        report(none.errors());
    }

    void example5(CustomReader rd, CustomWriter wr) {
        Stream<CustomInput> stream = rd.stream();
        None none = stream
            .map(input -> firstStep(input)
                .map(this::secondStep)
                .implies(wr::write))
            .collect(None.collect());
        report(none.errors());
    }

    void example6(CustomReader rd, CustomWriter wr) {
        Stream<CustomInput> stream = rd.stream();
        None none = None.streamOf(stream)
            .forEach(input -> firstStep(input)
                .map(this::secondStep)
                .implies(wr::write));
        report(none.errors());
    }

    private void report(@NotNull Collection<Failure> errors) {
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
                .choice()
                .when(it -> it.equals("r"))
                .implies(it -> {
                })
                .when(System.nanoTime() > 1_000_000_000)
                .implies(it -> log.info("hrrlo2"))
                .otherwise()
                .perform(temp -> secondStep(temp)
                    .implies(wr::write))
                .end()
            );
        searchFor(1)
            .choice()
            .when(Optional::isPresent).implies(it -> log.info("match at {}", it.get()))
            .otherwise().implies(it -> log.info("None match"))
            .end().onFailure(es -> es.forEach(e -> log.warn("Error: {}", e.message())));
        searchFor(1)
            .<Integer>choiceTo()
                .when(Optional::isPresent)
                    .map(it -> Hope.of(it.get()+1))
                .otherwise()
                    .map(it->Hope.of(0))
            .end()
            .onSuccess(it -> log.info("Found {}", it));
        report(none.errors());
    }

    private @NotNull Hope<Optional<Integer>> searchFor(int i) {
        int k = new Random(1).nextInt(5);
        if (i<k) return Hope.of(Optional.of(k));
        if (i>k) return Hope.of(Optional.empty());
        return Hope.failure(MsgError.of("NF100P", "Collision at {}"), i);
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