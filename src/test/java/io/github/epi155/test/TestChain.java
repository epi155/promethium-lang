package io.github.epi155.test;

import com.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

@Slf4j
class TestChain {
    private static final Random gRandom = new Random();
    private static final MsgError Zero = MsgError.of("DVZ", "Zero");

    @Test
    void test00() {
        val bld = None.builder();
        val a = Arrays.asList(1, 2, 3, 4);
        a.forEach(ii -> compute0(ii)
                .onSuccess(s -> s
                        .on(Parity.Odd, o -> log.info("{} is odd", o))
                        .on(Parity.Even, e -> log.info("{} is even", e))
                )
                .onFailure(bld::add));
    }

    @Test
    void test01() {
        val bld = None.builder();
        val a = Arrays.asList(1, 2, 3, 4);
        a.forEach(ii -> compute1(ii)
                .onSuccess(s -> s
                        .when(Integer.class, it -> log.info("{} is int", it))
                        .when(Double.class, dt -> log.info("{} is dbl", dt))
                )
                .onFailure(bld::add));
    }

    @Test
    void test02() {
        val bld = None.builder();
        val a = Arrays.asList(1, 2, 3, 4);
        a.forEach(ii -> bld.add(compute1(ii)
                .implies(s -> s
                        .when(Integer.class, it -> log.info("{} is int", it))
                        .when(Double.class, dt -> log.info("{} is dbl", dt))
                )));
    }

    private Hope<DemuxEnum<Parity, Integer>> compute0(Integer ii) {
        gRandom.setSeed(ii);
        int x = gRandom.nextInt(3);
        if (x == 0) return Hope.failure(Zero, ii);
        if (x == 1) return Hope.of(DemuxEnum.of(Parity.Odd, ii));
        return Hope.of(DemuxEnum.of(Parity.Even, ii));
    }

    private Hope<DemuxClass> compute1(Integer ii) {
        gRandom.setSeed(ii);
        int x = gRandom.nextInt(3);
        if (x == 0) return Hope.failure(Zero, ii);
        if (x == 1) return Hope.of(DemuxClass.of(ii));
        return Hope.of(DemuxClass.of(0.0 + ii));
    }


    enum Parity {Even, Odd}

}
