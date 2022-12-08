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
        if (isSuccess()) {
            action.run();
        }
        return this;
    }

    public @NotNull Nope ergo(@NotNull Supplier<? extends SingleError> fcn) {
        if (isSuccess()) {
            val one = fcn.get();
            if (one.isSuccess()) {
                return new PmNope();
            } else {
                return new PmNope(one.fault());
            }
        } else {
            return this;
        }
    }

    public @NotNull Nope implies(@NotNull Runnable action) {
        if (isSuccess()) {
            action.run();
            return Nope.nope();
        } else {
            return this;
        }
    }

    @Override
    public <R> R mapTo(Supplier<R> onSuccess, Function<Failure, R> onFailure) {
        return isSuccess() ? onSuccess.get() : onFailure.apply(fault());
    }

    @Override
    public void orThrow() throws FailureException {
        if (!isSuccess()) {
            throw new FailureException(fault());
        }
    }

}
