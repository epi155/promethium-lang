package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

class PmDummyLoopConsumer<U, V> implements LoopConsumer<U> {
    private final AnyValue<V> anyValue;

    public PmDummyLoopConsumer(AnyValue<V> anyValue) {
        this.anyValue = anyValue;
    }

    @Override
    public @NotNull None forEach(@NotNull Function<? super U, ? extends AnyItem> fcn) {
        return None.of(anyValue);
    }

    @Override
    public @NotNull None forEachParallel(int maxThread, @NotNull Function<? super U, ? extends AnyItem> fcn) {
        return None.of(anyValue);
    }

    @Override
    public @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends AnyItem> fcn) {
        return None.of(anyValue);
    }
}
