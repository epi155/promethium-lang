package com.github.epi155.pm.lang;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

class PmCollector implements Collector<AnyOne, NoneBuilder, None> {
    @Override
    public Supplier<NoneBuilder> supplier() {
        return None::builder;
    }

    @Override
    public BiConsumer<NoneBuilder, AnyOne> accumulator() {
        return ErrorBuilder::add;
    }

    @Override
    public BinaryOperator<NoneBuilder> combiner() {
        return NoneBuilder::join;
    }

    @Override
    public Function<NoneBuilder, None> finisher() {
        return NoneBuilder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
