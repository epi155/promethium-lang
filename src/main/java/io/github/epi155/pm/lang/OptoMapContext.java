package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface OptoMapContext<T, R> extends OptoMapInitialContext<T, R> {
    @NotNull OptoMapElseContext<T, R> otherwise();
}
