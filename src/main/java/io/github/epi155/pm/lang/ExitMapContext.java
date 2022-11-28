package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface ExitMapContext<R> {
    @NotNull Some<R> end();
}
