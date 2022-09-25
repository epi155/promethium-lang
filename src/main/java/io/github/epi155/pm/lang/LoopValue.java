package io.github.epi155.pm.lang;

import java.util.function.Function;

public interface LoopValue<A> {
    None forEach(Function<? super A, ? extends AnyItem> fcn);
}
