package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface OptoMapWhenAsContext<U, T, R> {
    @NotNull OptoMapContext<T, R> map(@NotNull Function<? super U, ? extends ErrorXorValue<R>> fcn);

    @NotNull OptoMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn);

    @NotNull OptoMapContext<T, R> fault(CustMsg ce, Object... argv);
}
