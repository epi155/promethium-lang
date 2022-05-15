package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;

class PmSome<T> extends PmAny implements Some<T> {
    private final T value;

    protected PmSome(T value) {
        super();
        this.value = value;
    }

    protected PmSome(Queue<Failure> errors) {
        super(errors);
        this.value = null;
    }

    protected PmSome(Collection<Failure> errors) {
        super(errors);
        this.value = null;
    }

    @Override
    public @NotNull T value() {
        if (isSuccess() && value!=null)
            return value;
        else
            throw new NoSuchElementException(summary().orElse("Attempt to get value when there is an error "));
    }

    @Override
    public @NotNull Glitches onSuccess(@NotNull Consumer<? super T> successAction) {
        if (isSuccess()) {
            if (value == null) {
                val k = Some.<T>builder();
                k.captureCaller(new NoSuchElementException());
                return k.build();
            } else {
                successAction.accept(value);
            }
        }
        return this;
    }

    @Override
    public @NotNull None asNone() {
        return isSuccess() ? new PmNone() : new PmNone(errors());
    }

    @Override
    public @NotNull <R> Some<R> andThen(@NotNull Function<T, ? extends SomeOne<R>> fcn) {
        if (isSuccess()) {
            val so = fcn.apply(value);
            if (so.isSuccess()) {
                return new PmSome<>(so.value());
            } else {
                return new PmSome<>(so.errors());
            }
        } else {
            return new PmSome<>(errors());
        }
    }

    public @NotNull None and(@NotNull Function<T, ? extends Any> fcn) {
        if (isSuccess()) {
            val any = fcn.apply(value);
            if (any.isSuccess()) {
                return new PmNone();
            } else {
                return new PmNone(any.errors());
            }
        } else {
            return new PmNone(errors());
        }
    }

    @Override
    public @NotNull None implies(@NotNull Consumer<T> action) {
        if (isSuccess()) {
            action.accept(value);
            return new PmNone();
        } else {
            return new PmNone(errors());
        }
    }

}
