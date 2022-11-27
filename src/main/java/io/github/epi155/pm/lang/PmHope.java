package io.github.epi155.pm.lang;

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
class PmHope<T> extends PmSingleError implements Hope<T> {
    private final T value;

    protected PmHope(T value, Failure fault) {
        super(fault);
        this.value = value;
    }

    @Override
    public @NotNull T value() {
        if (isSuccess()) {
            return value;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public T orThrow() throws FailureException {
        if (isSuccess()) {
            return value;
        } else {
            throw new FailureException(fault());
        }
    }

    @Override
    public @NotNull <R> Hope<R> map(@NotNull Function<? super T, Hope<R>> fcn) {
        if (isSuccess()) {
            return fcn.apply(value);
        } else {
            return Hope.of(fault());
        }
    }

    @Override
    public @NotNull <R> Some<R> mapOut(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
        if (isSuccess()) {
            val so = fcn.apply(value);
            if (so.isSuccess()) {
                return new PmSome<>(so.value());
            } else {
                return new PmSome<>(so.errors());
            }
        } else {
            return Some.of(fault());
        }
    }

    @Override
    public @NotNull <R> Hope<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
        return isSuccess() ? Hope.of(fcn.apply(value)) : Hope.of(fault());
    }

    @Override
    public @NotNull Nope implies(@NotNull Consumer<? super T> action) {
        if (isSuccess()) {
            action.accept(value);
            return Nope.nope();
        } else {
            return Nope.of(fault());
        }
    }

    @Override
    public @NotNull None and(@NotNull Function<? super T, ? extends AnyItem> fcn) {
        if (isSuccess()) {
            val many = fcn.apply(value);
            if (many.isSuccess()) {
                return new PmNone();
            } else {
                return new PmNone(many.errors());
            }
        } else {
            return None.of(fault());
        }
    }

    @Override
    public @NotNull Glitch onSuccess(@NotNull Consumer<? super T> action) {
        if (isSuccess()) {
            action.accept(value);
        }
        return this.new GlitchImpl();
    }

    @Override
    @NotNull
    public Nope asNope() {
        return isSuccess() ? new PmNope() : new PmNope(fault());
    }

    @Override
    public <R> R mapTo(Function<T, R> onSuccess, Function<Failure, R> onFailure) {
        return isSuccess() ? onSuccess.apply(value) : onFailure.apply(fault());
    }

    @Override
    public @NotNull ChoiceValueContext<T> choice() {
        return new PmChoiceValueContext<>(this);
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
