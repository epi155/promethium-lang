package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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
class PmHope<T> extends PmSingleError implements Hope<T> {
    private final T value;

    protected PmHope(T value, Failure fault) {
        super(fault);
        this.value = value;
    }

    @Override
    public @NotNull T value() {
        if (completeSuccess()) {
            return value;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public T orThrow() throws FailureException {
        if (completeSuccess()) {
            return value;
        } else {
            throw new FailureException(fault());
        }
    }

    @Override
    public @NotNull <R> Hope<R> map(@NotNull Function<? super T, Hope<R>> fcn) {
        if (completeSuccess()) {
            return fcn.apply(value);
        } else {
            return new PmHope<>(null, fault());
        }
    }

    @Override
    public @NotNull <R> Some<R> mapOut(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
        if (completeSuccess()) {
            return PmSome.of(fcn.apply(value));
        } else {
            return new PmSome<>(Collections.singletonList(fault()));
        }
    }

    @Override
    public @NotNull <R> Hope<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
        return completeSuccess() ? Hope.of(fcn.apply(value)) : new PmHope<>(null, fault());
    }

    @Override
    public @NotNull Hope<T> peek(@NotNull Consumer<? super T> action) {
        if (completeSuccess()) {
            action.accept(value);
        }
        return this;
    }

    @Override
    public @NotNull None ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
        if (completeSuccess()) {
            val many = fcn.apply(value);
            if (many.completeSuccess()) {
                return PmNone.none();
            } else {
                return new PmNone(many.signals());
            }
        } else {
            return new PmNone(Collections.singletonList(fault()));
        }
    }

    @Override
    public @NotNull <R> Some<R> ergoSome(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
        if (completeSuccess()) {
            return PmSome.of(fcn.apply(value));
        } else /*completeWithErrors()*/ {   // completeWithWarnings is always false
            return new PmSome<>(signals());
        }
    }

    @Override
    public @NotNull Glitch onSuccess(@NotNull Consumer<? super T> action) {
        if (completeSuccess()) {
            action.accept(value);
        }
        return this.new GlitchImpl();
    }

    @Override
    @NotNull
    public Nope asNope() {
        return completeSuccess() ? new PmNope() : new PmNope(fault());
    }

    @Override
    public <R> R mapTo(Function<T, R> onSuccess, Function<Failure, R> onFailure) {
        return completeSuccess() ? onSuccess.apply(value) : onFailure.apply(fault());
    }

    @Override
    public @NotNull ChoiceValueContext<T> choice() {
        return new PmChoiceValueContext<>(this);
    }

    @Override
    public @NotNull <R> ChoiceMapContext<T, R> choiceMap() {
        return new PmChoiceMapContext<>(this);
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
