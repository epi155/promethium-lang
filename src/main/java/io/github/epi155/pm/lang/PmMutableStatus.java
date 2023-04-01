package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract class PmMutableStatus extends PmItemStatus {
    private final Collection<Signal> signals = new ConcurrentLinkedQueue<>();

    protected Collection<Signal> getSignalsQueue() {
        return signals;
    }
    public void add(@NotNull Signal signal) {
        signals.add(signal);
    }
    public void add(@NotNull Collection<? extends Signal> collection) {
        signals.addAll(collection);
    }
}
