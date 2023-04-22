package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface OptoMapWhenContext<T, R> {
    @NotNull OptoMapContext<T, R> map(@NotNull Function<? super T, ? extends ErrorXorValue<R>> fcn);

    @NotNull OptoMapContext<T, R> mapOf(@NotNull Function<? super T, ? extends R> fcn);

    @NotNull OptoMapContext<T, R> fault(CustMsg ce, Object... argv);
}
