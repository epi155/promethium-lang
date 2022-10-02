package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

class PmNoneBuilder extends PmAnyBuilder implements NoneBuilder {
    @Override
    public @NotNull None build() {
        return new PmNone(errors());
    }

    @Override
    public @NotNull <U> NoneBuilder forEachOf(
        @NotNull Iterable<? extends U> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        iterable.forEach(u -> add(fcn.apply(u)));
        return this;
    }

    @Override
    public @NotNull <U> NoneBuilder forEach(
        @NotNull Iterable<? extends AnyValue<U>> iterable,
        @NotNull Function<? super U, ? extends AnyItem> fcn) {
        iterable.forEach(u -> {
            if (isSuccess())
                add(fcn.apply(u.value()));
            else
                add(u.errors());
        });
        return this;
    }

}
