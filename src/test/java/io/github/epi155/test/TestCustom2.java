package io.github.epi155.test;

import io.github.epi155.pm.lang.Failure;
import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.Nope;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
        none
            .choice()
            .when(true).perform(() -> Nope.nope())
            .otherwise().implies(() -> {
            })
            .end();
        none
            .choice()
            .when(true).implies(() -> {
            })
            .otherwise().perform(() -> Nope.nope())
            .end();
        report(none.errors());
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
