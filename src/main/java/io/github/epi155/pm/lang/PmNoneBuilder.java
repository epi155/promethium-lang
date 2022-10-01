package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

class PmNoneBuilder extends PmAnyBuilder implements NoneBuilder {
    @Override
    public @NotNull None build() {
        return new PmNone(errors());
    }

    @Override
    public <U> LoopBuilder<U> timesOf(@NotNull Supplier<Iterable<? extends U>> fcn) {
        val list = fcn.get();
        return fcn1 -> {
            list.forEach(u -> PmNoneBuilder.this.add(fcn1.apply(u)));
            return PmNoneBuilder.this;
        };
    }

    @Override
    public <U> LoopBuilder<U> times(@NotNull Supplier<Iterable<? extends AnyValue<U>>> fcn) {
        val list = fcn.get();
        return fcn1 -> {
            list.forEach(u -> {
                if (u.isSuccess()) {
                    PmNoneBuilder.this.add(fcn1.apply(u.value()));
                } else {
                    PmNoneBuilder.this.add(u.errors());
                }
            });
            return PmNoneBuilder.this;
        };
    }

}
