package io.github.epi155.test;

import io.github.epi155.pm.lang.Nope;
import io.github.epi155.pm.lang.Nuntium;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class TestCustom1 {
    static final Nuntium CUST_ERR = Nuntium.of("AZ15", "Errore non Gestito: {}");
    //@Test
    void hello() {
        Nope nope = Nope.failure(CUST_ERR, "ops", new NullPointerException("Azz"));
        val s = nope.fault().message();
        System.out.println(s);
    }
    void example1(CustomReader rd, CustomWriter wr) {
        List<CustomError> errors = new ArrayList<>();
        Iterable<CustomInput> iterable = rd.Iterable();
        for (CustomInput input : iterable) {
            try {
                TempData tempData = firstStep(input);
                try {
                    CustomOutput output = secondStep(tempData);
                    wr.write(output);
                } catch (SecondException e) {
                    errors.add(CustomError.of(tempData, e));
                }
            } catch (FirstException e) {
                errors.add(CustomError.of(input, e));
            }
        }
        report(errors);
    }

    private void report(List<CustomError> errors) {
    }

    private CustomOutput secondStep(TempData tempData) throws SecondException {
        return null;
    }

    private TempData firstStep(CustomInput input) throws FirstException {
        return null;
    }

    private interface CustomReader {
        Iterable<CustomInput> Iterable();
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
