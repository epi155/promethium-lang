package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

class PmSingleError implements SingleError {
    private final Failure fault;

    public PmSingleError(Failure fault) {
        this.fault = fault;
    }

    @Override
    public boolean isSuccess() {
        return fault == null;
    }

    @Override
    public @NotNull Failure fault() {
        if (fault != null) {
            return fault;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void onFailure(@NotNull Consumer<Failure> errorAction) {
        if (fault != null) errorAction.accept(fault);
    }

    @Override
    public void orThrow(@NotNull Function<Failure, FailureException> fcn) throws FailureException {
        if (fault != null) throw fcn.apply(fault);
    }

}
