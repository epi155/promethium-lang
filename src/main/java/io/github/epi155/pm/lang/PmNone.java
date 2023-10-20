package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class PmNone extends PmManyError implements None {
    @NoBuiltInCapture
    protected static None none() {
        return NoneHelper.NONE_INSTANCE;
    }
    private PmNone() {
        super();
    }

    protected PmNone(Collection<? extends Signal> errors) {
        super(errors);
    }
    protected PmNone(PmFinalStatus status) {
        super(status);
    }

    @Override
    @NoBuiltInCapture
    public @NotNull None ergo(@NotNull Supplier<? extends ItemStatus> fcn) {
        if (completeSuccess()) {    // no errors, no warnings
            ItemStatus that = fcn.get();
            if (that.completeSuccess()) {
                return none();
            } else {
                return new PmNone(that.signals());
            }
        } else if (completeWithErrors()) {  // errors, warnings?
            return this;    // fcn not executed
        } else {    // no errors, some warnings
            ItemStatus that = fcn.get();
            if (that.completeSuccess()) {
                return this;    // keep this warnings
            } else {
                @NotNull NoneBuilder bld = None.builder();
                bld.add(signals());        // this warning
                bld.add(that.signals());   // that error OR warning
                return bld.build();
            }
        }
    }
    public @NotNull Glitches onSuccess(@NotNull Runnable successAction) {
        if (completeWithoutErrors())
            successAction.run();
        return this;
    }

    @Override
    public @NotNull Glitches onSuccess(@NotNull Consumer<Collection<Warning>> successAction) {
        if (completeWithoutErrors()) {
            successAction.accept(alerts());
        }
        return this;
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> map(@NotNull Supplier<? extends AnyValue<R>> fcn) {
        if (completeSuccess()) {
            return PmSome.of(fcn.get());
        } else if (completeWithErrors()) {
            return new PmSome<>(this);
        } else /*completeWithWarnings()*/ {
            AnyValue<R> that = fcn.get();
            return composeOnWarning(that);
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> mapOf(@NotNull Supplier<? extends R> fcn) {
        if (completeSuccess()) {
            return new PmSome<>(fcn.get());
        } else if (completeWithErrors()) {
            return new PmSome<>(signals());
        } else /*completeWithWarnings()*/ {
            return new PmSome<>(fcn.get(), signals());
        }
    }

    @NoBuiltInCapture
    public @NotNull None implies(@NotNull Runnable action) {
        if (completeWithoutErrors()) {
            action.run();
        }
        return this;
    }

    @NoBuiltInCapture
    public @NotNull None peek(
            @NotNull Runnable successAction,
            @NotNull Consumer<Collection<? extends Signal>> signalAction) {
        if (completeWithoutErrors()) {
            successAction.run();
        } else {
            signalAction.accept(signals());
        }
        return this;
    }

    @NoBuiltInCapture
    public @NotNull None peek(
            @NotNull Consumer<Collection<Warning>> successAction,
            @NotNull Consumer<Collection<? extends Signal>> signalAction) {
        if (completeWithoutErrors()) {
            successAction.accept(alerts());
        } else {
            signalAction.accept(signals());
        }
        return this;
    }

    private static class NoneHelper {
        private static final PmNone NONE_INSTANCE = new PmNone();
    }

    @Override
    public <R> R mapTo(Function<Collection<Warning>, R> onSuccess, Function<Collection<? extends Signal>, R> onFailure) {
        if (completeWithErrors()) {
            return onFailure.apply(signals());  // error & warnings?
        } else {
            return onSuccess.apply(alerts());   // warnings?
        }
    }
    @Override
    public <R> R mapTo(Supplier<R> onSuccess, Function<Collection<? extends Signal>, R> onFailure) {
        if (completeWithErrors()) {
            return onFailure.apply(signals());  // error & warnings?
        } else {
            return onSuccess.get();   // warnings?
        }
    }
}
