package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Queue;
import java.util.function.Supplier;

class PmNone extends PmAnyError implements None {

    protected PmNone() {
        super();
    }

    protected PmNone(Queue<Failure> errors) {
        super(errors);
    }

    protected PmNone(Collection<Failure> errors) {
        super(errors);
    }

    public @NotNull Glitches onSuccess(Runnable successAction) {
        if (isSuccess())
            successAction.run();
        return this;
    }

    public @NotNull None and(@NotNull Supplier<? extends AnyError> fcn) {
        if (isSuccess()) {
            val any = fcn.get();
            if (any.isSuccess()) {
                return new PmNone();
            } else {
                return new PmNone(any.errors());
            }
        } else {
            return this;
        }
    }

    public @NotNull None implies(@NotNull Runnable action) {
        if (isSuccess()) {
            action.run();
            return new PmNone();
        } else {
            return this;
        }
    }

}
