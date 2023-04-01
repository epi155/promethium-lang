package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

class PmNope extends PmSingleError implements Nope {

    protected PmNope() {
        super(null);
    }

    protected PmNope(@NotNull Failure fault) {
        super(fault);
    }

    @Override
    public @NotNull Glitch onSuccess(Runnable action) {
        if (completeSuccess()) {
            action.run();
        }
        return this;
    }

    public @NotNull Nope ergo(@NotNull Supplier<? extends SingleError> fcn) {
        if (completeSuccess()) {
            val one = fcn.get();
            if (one.completeSuccess()) {
                return new PmNope();
            } else {
                return new PmNope(one.fault());
            }
        } else {
            return this;
        }
    }

    @Override
    public @NotNull <R> Hope<R> map(@NotNull Supplier<Hope<R>> fcn) {
        if (completeSuccess()) {
            return fcn.get();
        } else {
            return new PmHope<>(null, fault());
        }
    }

    public @NotNull Nope peek(@NotNull Runnable action) {
        if (completeSuccess()) {
            action.run();
            return Nope.nope();
        } else {
            return this;
        }
    }

    @Override
    public <R> R mapTo(Supplier<R> onSuccess, Function<Failure, R> onFailure) {
        return completeSuccess() ? onSuccess.get() : onFailure.apply(fault());
    }

    @Override
    public void orThrow() throws FailureException {
        if (completeWithErrors()) {
            throw new FailureException(fault());
        }
    }

}
