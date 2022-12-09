package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

class PmNone extends PmManyError implements None {

    protected PmNone() {
        super();
    }

    protected PmNone(Collection<Failure> errors) {
        super(errors);
    }

    public @NotNull Glitches onSuccess(Runnable successAction) {
        if (isSuccess())
            successAction.run();
        return this;
    }

    public @NotNull None ergo(@NotNull Supplier<? extends AnyItem> fcn) {
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

    public @NotNull None peek(@NotNull Runnable action) {
        if (isSuccess()) {
            action.run();
            return new PmNone();
        } else {
            return this;
        }
    }

    @Override
    public <R> R mapTo(Supplier<R> onSuccess, Function<Collection<Failure>, R> onFailure) {
        return isSuccess() ? onSuccess.get() : onFailure.apply(errors());
    }

}
