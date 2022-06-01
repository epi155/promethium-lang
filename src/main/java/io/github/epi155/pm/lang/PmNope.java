package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

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

    public @NotNull Nope and(@NotNull Supplier<? extends SingleError> fcn) {
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
    public void orThrow() throws FailureException {
        if (!isSuccess()) {
            throw new FailureException(fault());
        }
    }
}
