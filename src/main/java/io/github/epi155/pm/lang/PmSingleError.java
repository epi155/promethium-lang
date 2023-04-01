package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class PmSingleError implements SingleError {
    private final Failure fault;

    protected PmSingleError(Failure fault) {
        this.fault = fault;
    }

    @Override
    public boolean completeSuccess() {
        return fault == null;
    }

    @Override
    public boolean completeWithErrors() {
        return !completeSuccess();
    }

    @Override
    public boolean completeWithoutErrors() {
        return completeSuccess();
    }

    @Override
    public Collection<Signal> signals() {
        return completeSuccess() ? Collections.emptyList() : Collections.singletonList(fault);
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
