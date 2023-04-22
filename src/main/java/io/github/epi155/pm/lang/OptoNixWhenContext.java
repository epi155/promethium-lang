package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface OptoNixWhenContext<T> {
    @NotNull OptoNixContext<T> ergo(@NotNull Function<? super T, ? extends SingleError> fcn);

    @NotNull OptoNixContext<T> peek(@NotNull Consumer<? super T> action);

    @NotNull OptoNixContext<T> fault(CustMsg ce, Object... argv);

    @NotNull OptoNixContext<T> nop();
}
