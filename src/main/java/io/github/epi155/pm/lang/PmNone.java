package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class PmNone extends PmManyError implements None {
    private static class NoneHelper {
        private static final None NONE_INSTANCE = new PmNone();
    }
    private PmNone() {
        super();
    }

    protected PmNone(Collection<? extends Signal> errors) {
        super(errors);
    }

    protected static None none() {
        return NoneHelper.NONE_INSTANCE;
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
    public @NotNull None ergo(@NotNull Supplier<? extends ItemStatus> fcn) {
        if (completeSuccess()) {    // no errors, no warnings
            val that = fcn.get();
            if (that.completeSuccess()) {
                return none();
            } else {
                return new PmNone(that.signals());
            }
        } else if (completeWithErrors()) {  // errors, warnings?
            return this;    // fcn not executed
        } else {    // no errors, some warnings
            val that = fcn.get();
            if (that.completeSuccess()) {
                return this;    // keep this warnings
            } else {
                val bld = None.builder();
                bld.add(signals());        // this warning
                bld.add(that.signals());   // that error OR warning
                return bld.build();
            }
        }
    }
    @Override
    public @NotNull <R> Some<R> map(@NotNull Supplier<? extends AnyValue<R>> fcn) {
        if (completeSuccess()) {
            return PmSome.of(fcn.get());
        } else if (completeWithErrors()) {
            return new PmSome<>(signals());
        } else /*completeWithWarnings()*/ {
            val that = fcn.get();
            return composeOnWarning(that);
        }
    }

    public @NotNull None peek(@NotNull Runnable action) {
        if (completeWithoutErrors()) {
            action.run();
            return none();
        } else {
            return this;
        }
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
