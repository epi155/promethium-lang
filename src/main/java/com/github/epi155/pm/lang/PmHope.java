package com.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Class to handle a &lt;T&gt; value as an alternative to an error {@link Failure}.
 *
 * <p>
 * Do not use <i> Hope &lt;Void&gt; </i>,
 * use {@link PmNope}
 * </p>
 *
 * @param <T> type of value to manage
 */
class PmHope<T> implements Hope<T> {
    private final T value;
    private final Failure fault;

    protected PmHope(T value, Failure fault) {
        this.value = value;
        this.fault = fault;
    }

    @Override
    public boolean isSuccess() {
        return fault == null;
    }

    @Override
    public @NotNull T value() {
        if (fault == null) {
            return value;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public T orThrow() throws FailureException {
        if (fault == null) {
            return value;
        } else {
            throw new FailureException(fault);
        }
    }

    @Override
    public @NotNull <R> Hope<R> andThen(@NotNull Function<T, Hope<R>> fcn) {
        if (isSuccess()) {
            return fcn.apply(value);
        } else {
            return Hope.of(fault);
        }
    }

    public @NotNull Nope implies(@NotNull Consumer<T> action) {
        if (isSuccess()) {
            action.accept(value);
            return Nope.nope();
        } else {
            return Nope.of(fault);
        }
    }

    @Override
    public @NotNull Nope and(@NotNull Function<T, ? extends One> fcn) {
        if (isSuccess()) {
            val one = fcn.apply(value);
            if (one.isSuccess()) {
                return new PmNope();
            } else {
                return new PmNope(one.fault());
            }
        } else {
            return new PmNope(fault);
        }
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
    public @NotNull Glitch onSuccess(@NotNull Consumer<T> action) {
        if (fault == null) {
            action.accept(value);
        }
        return this.new GlitchImpl();
    }

    @Override
    public void onFailure(@NotNull Consumer<Failure> errorAction) {
        if (fault != null) errorAction.accept(fault);
    }

    @Override
    public void orThrow(@NotNull Function<Failure, FailureException> fcn) throws FailureException {
        if (fault != null) throw fcn.apply(fault);
    }

    @Override
    @NotNull
    public Nope asNope() {
        return isSuccess() ? new PmNope() : new PmNope(fault);
    }

    class GlitchImpl implements Glitch {

        @Override
        public void onFailure(@NotNull Consumer<Failure> errorAction) {
            PmHope.this.onFailure(errorAction);
        }

        @Override
        public void orThrow(@NotNull Function<Failure, FailureException> fcn) throws FailureException {
            PmHope.this.orThrow(fcn);
        }

    }

}
