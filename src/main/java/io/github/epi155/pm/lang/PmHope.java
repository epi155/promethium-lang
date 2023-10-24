package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
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
    @NoBuiltInCapture
    public @NotNull <R> Hope<R> maps(@NotNull Function<? super T, ? extends Hope<R>> fcn) {
        if (completeSuccess()) {
            return fcn.apply(value);
        } else {
            return new PmHope<>(null, failure());
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull Nope ergoes(@NotNull Function<? super T, ? extends SingleError> fcn) {
        if (completeSuccess()) {
            SingleError result = fcn.apply(value);
            if (result.completeWithoutErrors()) {
                return Nope.nope();
            } else {
                return new PmNope(result.failure());
            }
        } else {
            return new PmNope(failure());
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
        if (completeSuccess()) {
            return PmSome.of(fcn.apply(value));
        } else {
            return new PmSome<>(Collections.singletonList(failure()));
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Hope<R> mapsOf(@NotNull Function<? super T, ? extends R> fcn) {
        return completeSuccess() ? Hope.of(fcn.apply(value)) : new PmHope<>(null, failure());
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
        return completeSuccess() ? new PmSome<>(fcn.apply(value)) : new PmSome<>(Collections.singletonList(failure()));
    }

    @Override
    @NoBuiltInCapture
    public @NotNull Hope<T> implies(@NotNull Consumer<? super T> action) {
        if (completeSuccess()) {
            action.accept(value);
        }
        return this;
    }

    @Override
    @NoBuiltInCapture
    public @NotNull None ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
        if (completeSuccess()) {
            ItemStatus many = fcn.apply(value);
            if (many.completeSuccess()) {
                return PmNone.none();
            } else {
                return new PmNone(many.signals());
            }
        } else {
            return new PmNone(Collections.singletonList(failure()));
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
    @NoBuiltInCapture
    public Nope asNope() {
        return completeSuccess() ? Nope.nope() : new PmNope(failure());
    }

    @Override
    public <R> R mapTo(Function<T, R> onSuccess, Function<Failure, R> onFailure) {
        return completeSuccess() ? onSuccess.apply(value) : onFailure.apply(failure());
    }

    @Override
    public @NotNull <R> OptoMapContext<T, R> choosesMap() {
        return new PmOptoMapContext<>(this);
    }

    @Override
    public @NotNull OptoNixContext<T> chooses() {
        return new PmOptoNixContext<>(this);
    }

    @Override
    public @NotNull ChooseNixContext<T> choose() {
        return new PmChooseNixContext<>(this);
    }

    @Override
    public @NotNull <R> ChooseMapContext<T, R> chooseMap() {
        return new PmChooseMapContext<>(this);
    }

    @Override
    protected void extraToString(PrintWriter pw) {
        if (value != null) {
            pw.printf("finalValue: %s!%s%n", value.getClass().getName(), value);
        }
    }

    class GlitchImpl implements Glitch {

        @Override
        public void onFailure(@NotNull Consumer<Failure> errorAction) {
            PmHope.this.onFailure(errorAction);
        }

    }
}
