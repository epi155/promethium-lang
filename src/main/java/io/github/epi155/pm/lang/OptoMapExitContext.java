package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface OptoMapExitContext<R> {
    @NotNull Hope<R> end();
}