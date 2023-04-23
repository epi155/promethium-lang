package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

abstract class PmManyError extends PmFinalStatus implements ManyErrors, Glitches {

    protected PmManyError() {
        super();
    }

    protected PmManyError(Collection<? extends Signal> signals) {
        super(signals);
    }

    protected PmManyError(PmFinalStatus status) {
        super(status);
    }

    @Override
    public void onFailure(@NotNull Consumer<Collection<? extends Signal>> signalAction) {
        if (completeWithErrors())
            signalAction.accept(signals());
    }


    @Override
    public @NotNull Optional<String> summary() {
        if (completeSuccess()) return Optional.empty();
        if (completeWithoutErrors()) {
            int nmWrn = signals().size();
            if (nmWrn > 1) return Optional.of(String.format("%d warnings found", nmWrn));
            Signal alert = signals().iterator().next();
            return Optional.of(alert.message());
        }
        val alerts = alerts();
        int nmSig = signals().size();
        int nmWrn = alerts.size();
        int nmErr = nmSig - nmWrn;
        if (nmWrn == 0) {
            if (nmErr > 1) return Optional.of(String.format("%d errors found", nmErr));
            Signal error = signals().iterator().next();
            return Optional.of(error.message());
        } else {
            return Optional.of(String.format("%d error(s), %d warning(s) found", nmErr, nmWrn));
        }
    }

    protected  <R> Some<R> composeOnWarning(@NotNull AnyValue<R> that) {
        if (that.completeSuccess()) {
            return new PmSome<>(that.value(), signals());     // this warning
        } else if (that.completeWithErrors()) {
            val bld = Some.<R>builder();
            bld.add(signals());        // this warning
            bld.add(that.signals());   // that error (warning)
            return bld.build();
        } else /*that.completeWithWarnings()*/{
            val bld = Some.<R>builder();
            bld.add(signals());        // this warning
            bld.add(that.signals());   // that warning
            return bld.buildWithValue(that.value());
        }
    }
}
