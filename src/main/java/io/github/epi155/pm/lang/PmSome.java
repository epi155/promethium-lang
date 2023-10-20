package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

class PmSome<T> extends PmManyError implements Some<T> {
    private final T value;

    protected PmSome(T value) {
        super();
        this.value = value;
    }
    protected PmSome(T value, @NotNull Collection<? extends Signal> warnings) {
        super(warnings);
        this.value = value;
    }

    protected PmSome(@NotNull Collection<? extends Signal> signals) {
        super(signals);
        this.value = null;
    }
    protected PmSome(@NotNull PmFinalStatus status) {
        super(status);
        this.value = null;
    }

    @NoBuiltInCapture
    protected static <U> @NotNull Some<U> of(@NotNull AnyValue<U> v) {
        if (v instanceof Some) {
            return (Some<U>) v;
        }
        if (v.completeSuccess()) {
            return new PmSome<>(v.value());
        } else if (v.completeWithErrors()) {
            return new PmSome<>(v.signals());
        } else {
            /*
             * the only class implementing AnyValue that can handle warnings is Some,
             * which is handled separately at the beginning of the method.
             */
            return new PmSome<>(v.value(), v.signals());
        }

    }
    @Override
    public @NotNull T value() {
        if (completeWithoutErrors() && value != null)
            return value;
        else
            throw new NoSuchElementException(summary().orElse("Attempt to get value when there is an error "));
    }

    @Override
    public @NotNull Glitches onSuccess(@NotNull Consumer<? super T> successAction) {
        if (completeWithoutErrors()) {
            successAction.accept(value);
        }
        return this;
    }

    @Override
    public @NotNull Glitches onSuccess(@NotNull BiConsumer<? super T, Collection<Warning>> successAction) {
        if (completeWithoutErrors()) {
            successAction.accept(value, alerts());
        }
        return this;
    }

    @Override
    @NoBuiltInCapture
    public @NotNull None asNone() {
        if (completeSuccess()) {
            return PmNone.none();
        } else {
            return new PmNone(this);
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
        if (completeSuccess()) {
            return of(fcn.apply(value));
        } else if (completeWithErrors()) {
            return new PmSome<>(this);  // this error & warning - fcn not executed
        } else /*completeWithWarnings()*/ {
            AnyValue<R> that = fcn.apply(value);
            return composeOnWarning(that);
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
        if (completeSuccess()) {
            return new PmSome<>(fcn.apply(value));
        } else if (completeWithErrors()) {
            return new PmSome<>(signals());
        } else /*completeWithWarnings()*/ {
            return new PmSome<>(fcn.apply(value), signals());
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull None ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
        if (completeSuccess()) {
            ItemStatus that = fcn.apply(value);
            if (that.completeSuccess()) {
                return PmNone.none();   // full success
            } else {
                return new PmNone(that.signals());    // that error OR warning
            }
        } else if (completeWithErrors()) {
            return new PmNone(this);
        } else /*completeWithWarnings()*/ {
            ItemStatus that = fcn.apply(value);
            if (that.completeSuccess()) {
                return new PmNone(signals());     // this warning
            } else {
                @NotNull NoneBuilder bld = None.builder();
                bld.add(signals());        // this warning
                bld.add(that.signals());   // that error OR warning
                return bld.build();
            }
        }
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
    @NoBuiltInCapture
    public @NotNull Some<T> implies(@NotNull Consumer<? super T> action) {
        if (completeWithoutErrors()) {
            action.accept(value);
        }
        return this;
    }

    @Override
    public <R> R mapTo(BiFunction<T, Collection<Warning>, R> onSuccess, Function<Collection<? extends Signal>, R> onFailure) {
        return completeWithErrors() ? onFailure.apply(signals()) : onSuccess.apply(value, alerts());
    }
    @Override
    public <R> R mapTo(Function<T, R> onSuccess, Function<Collection<? extends Signal>, R> onFailure) {
        return completeWithErrors() ? onFailure.apply(signals()) : onSuccess.apply(value);
    }
    @Override
    protected void extraToString(PrintWriter pw) {
        if (value != null) {
            pw.printf(", finalValue: %s!%s", value.getClass().getName(), value);
        }
    }

}
