package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface OptoNixContext<T> extends OptoNixInitialContext<T> {
    @NotNull OptoNixElseContext<T> otherwise();
}
