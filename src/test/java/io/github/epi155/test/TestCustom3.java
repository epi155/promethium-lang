package io.github.epi155.test;

import io.github.epi155.pm.lang.None;
import io.github.epi155.pm.lang.Nuntium;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestCustom3 {

    private static final Nuntium BAD_N = Nuntium.of("E01", "Valore nullo o negativo: {}");
    private static final Nuntium FAC_N = Nuntium.of("W01", "Valore {} scomponibile in fattori: {}");

    @Test
    void test1() {
        int[] v = new int[]{2, 3, 5, 7, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 0};
        None none = testPrime(v);
        none
            .onSuccess(
                w -> {
                    log.info("Nessun errore");
                    w.forEach(it -> log.warn(it.message()));
                })
            .onFailure(
                s -> s.forEach(it -> log.error(it.message())));
    }

    private None testPrime(int[] vs) {
        val bld = None.builder();
        for(int v: vs) {
            if (v<1) {
                bld.fault(BAD_N, v);
            } else {
                Integer[] factors = scomponi(v);
                if (factors.length > 0) {
                    bld.alert(FAC_N, v, factors);
                }
            }
        }
        return bld.build();
    }

    private Integer[] scomponi(int v) {
        if (v == 2) {
            return new Integer[0];  // 2 è primo.
        }
        List<Integer> factors = new ArrayList<>();
        while (v % 2 == 0 && v > 2) {
            factors.add(2);
            v = v / 2;
        }
        int divider = 3;
        int soglia = (int) Math.floor(Math.sqrt(v));
        while (v>1 && divider<=soglia) {
            while (v % divider == 0 && v > divider) {
                factors.add(divider);
                v = v / divider;
            }
            divider += 2;
        }
        if (! factors.isEmpty()) {
            factors.add(v);
        }
        return factors.toArray(new Integer[0]);
    }

}
