package io.github.epi155.pm.lang;

import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class PmFinalStatus implements ItemStatus {
    private static final String SUCCESS = "Success";
    private static final String WARNIGS = "Warnings";
    private static final String ERRORS = "Errors";
    private final Collection<Signal> signals;
    private final Collection<Warning> alerts;
    private final Collection<Failure> errors;
    private final boolean zSuccess;
    private final boolean zErrors;
    private final boolean zAlerts;

    protected PmFinalStatus(Collection<? extends Signal> signals) {
        this.signals = Collections.unmodifiableCollection(signals);
        List<Warning> warnings = new ArrayList<>();
        List<Failure> failures = new ArrayList<>();
        for(Signal signal: getSignalsQueue()) {
            if (signal instanceof Warning) {
                warnings.add((Warning) signal);
            } else if (signal instanceof Failure) {
                failures.add((Failure) signal);
            }
        }
        this.alerts = Collections.unmodifiableCollection(warnings);
        this.errors = Collections.unmodifiableCollection(failures);
        this.zSuccess = signals.isEmpty();
        this.zErrors = !failures.isEmpty();
        this.zAlerts = failures.isEmpty() && !alerts.isEmpty();
    }

    protected PmFinalStatus() {
        this.signals = Collections.emptyList();
        this.alerts = Collections.emptyList();
        this.errors = Collections.emptyList();
        this.zSuccess = true;
        this.zAlerts = false;
        this.zErrors = false;
    }

    protected Collection<Signal> getSignalsQueue() {
        return signals;
    }
    @Override
    public Collection<Signal> signals() {
        return signals;
    }

    public Collection<Warning> alerts() {
        return alerts;
    }
    protected Collection<Failure> errors() {
        return errors;
    }

    @Override
    public boolean completeSuccess() {
        return zSuccess;
    }

    @Override
    public boolean completeWithWarnings() {
        return zAlerts;
    }

    @Override
    public boolean completeWithErrors() {
        return zErrors;
    }


    public String toString() {
        String status = zSuccess ? SUCCESS : zErrors ? ERRORS : WARNIGS;
        val sw = new StringWriter();
        val pw = new PrintWriter(sw);
        pw.printf("%n---%n");
        pw.printf("finalStatus: %s%n",status);
        extraToString(pw);
        if (!alerts.isEmpty()) {
            pw.println("warnings:");
            alerts.forEach(s -> pw.printf(s.toString()));
        }
        if (!errors.isEmpty()) {
            pw.println("errors:");
            errors.forEach(s -> pw.printf(s.toString()));
        }
        return sw.toString();
    }

    protected void extraToString(PrintWriter pw) {
    }
}
