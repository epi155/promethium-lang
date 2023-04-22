package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface OptoNixWhenAsContext<U, T> {
    @NotNull OptoNixContext<T> ergo(@NotNull Function<? super U, ? extends SingleError> fcn);

    @NotNull OptoNixContext<T> peek(@NotNull Consumer<? super U> action);

    @NotNull OptoNixContext<T> fault(CustMsg ce, Object... argv);

    @NotNull OptoNixContext<T> nop();
}
