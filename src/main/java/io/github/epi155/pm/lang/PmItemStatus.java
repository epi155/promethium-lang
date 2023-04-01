package io.github.epi155.pm.lang;

import java.util.Collection;
import java.util.Collections;

abstract class PmItemStatus implements ItemStatus {

    protected abstract Collection<Signal> getSignalsQueue();

    @Override
    public boolean completeSuccess() {
        return getSignalsQueue().isEmpty();   // no error, no warning
    }

    @Override
    public boolean completeWithErrors() {
        for(Signal signal: getSignalsQueue()) {
            if (signal instanceof Failure) return true; // at least one error (any warning)
        }
        return false;
    }

    @Override
    public boolean completeWithWarnings() {
        if (completeSuccess()) return false;
        boolean warnFound = false;
        for(Signal signal: getSignalsQueue()) {
            if (signal instanceof Failure) return false;    // error
            if (signal instanceof Warning) warnFound = true;
        }
        return warnFound;
    }
    @Override
    public Collection<Signal> signals() {
        return Collections.unmodifiableCollection(getSignalsQueue());
    }
}
