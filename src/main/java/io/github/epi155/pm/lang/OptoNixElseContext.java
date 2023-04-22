package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface OptoNixElseContext<T> {
    @NotNull OptoNixExitContext ergo(@NotNull Function<? super T, ? extends SingleError> fcn);

    @NotNull OptoNixExitContext peek(@NotNull Consumer<? super T> action);

    @NotNull OptoNixExitContext fault(CustMsg ce, Object... argv);

    @NotNull OptoNixExitContext nop();
}
