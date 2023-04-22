package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface OptoMapElseContext<T, R> {
    @NotNull OptoMapExitContext<R> map(@NotNull Function<? super T, ? extends ErrorXorValue<R>> fcn);

    @NotNull OptoMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn);

    @NotNull OptoMapExitContext<R> fault(CustMsg ce, Object... argv);
}
