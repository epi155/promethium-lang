package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract class PmMutableStatus implements ItemStatus {
    private final Collection<Signal> signals = new ConcurrentLinkedQueue<>();
    private boolean noSignals = true;
    private boolean isSuccess = true;

    public void add(@NotNull Signal signal) {
        noSignals = false;
        if (isSuccess && signal instanceof Failure) isSuccess = false;
        signals.add(signal);
    }

    public void add(@NotNull Collection<? extends Signal> signals) {
        noSignals = false;
        if (isSuccess) {
            for (Signal signal : signals) {
                if (signal instanceof Failure) {
                    isSuccess = false;
                    break;
                }
            }
        }
        this.signals.addAll(signals);
    }

    public boolean completeSuccess() {
        return noSignals;
    }

    public boolean completeWithErrors() {
        return !isSuccess;
    }

    public boolean completeWarning() {
        return isSuccess && !noSignals;
    }

    public Collection<Signal> signals() {
        return Collections.unmodifiableCollection(signals);
    }
}
