package io.github.epi155.pm.lang;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class PmFinalStatus implements ItemStatus {
    private static final String L_SUCCESS = "Success";
    private static final String L_WARNIGS = "Warnings";
    private static final String L_ERRORS = "Errors";
    private final Collection<Signal> signals;
    private final Collection<Warning> alerts;
    private final boolean zSuccess;
    private final boolean zErrors;
    private final boolean zAlerts;

    protected PmFinalStatus(Collection<? extends Signal> signals) {
        this.signals = Collections.unmodifiableCollection(signals);
        List<Warning> warnings = new ArrayList<>();
        boolean kErrors = false;
        for (Signal signal : getSignalsQueue()) {
            if (signal instanceof Warning) {
                warnings.add((Warning) signal);
            } else {
                kErrors = true;
            }
        }
        this.alerts = Collections.unmodifiableCollection(warnings);
        this.zSuccess = signals.isEmpty();
        this.zErrors = kErrors;
        this.zAlerts = !kErrors && !alerts.isEmpty();
    }

    protected PmFinalStatus() {
        this.signals = Collections.emptyList();
        this.alerts = Collections.emptyList();
        this.zSuccess = true;
        this.zAlerts = false;
        this.zErrors = false;
    }

    protected PmFinalStatus(PmFinalStatus status) {
        this.signals = status.signals;
        this.alerts = status.alerts;
        this.zSuccess = status.zSuccess;
        this.zAlerts = status.zAlerts;
        this.zErrors = status.zErrors;
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
    @Override
    public boolean completeSuccess() {
        return zSuccess;
    }

    @Override
    public boolean completeWarning() {
        return zAlerts;
    }

    @Override
    public boolean completeWithErrors() {
        return zErrors;
    }


    public String toString() {
        String status = labelStatus();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.printf("{ finalStatus: %s", status);
        extraToString(pw);
        if (!alerts.isEmpty()) {
            pw.print(", warnings: [ ");
            dumpAlerts(pw);
            pw.print(" ]");
        }
        if (zErrors) {
            pw.print(", errors: [ ");
            dumpFailure(pw);
            pw.print(" ]");
        }
        pw.print(" }");
        return sw.toString();
    }

    private void dumpFailure(PrintWriter pw) {
        boolean append = false;
        for (Signal signal : signals) {
            if (signal instanceof Failure) {
                if (append) pw.print(", ");
                pw.print(signal);
                append = true;
            }
        }
    }

    private void dumpAlerts(PrintWriter pw) {
        boolean append = false;
        for (Warning alert : alerts) {
            if (append) pw.print(", ");
            pw.print(alert.toString());
            append = true;
        }
    }

    private String labelStatus() {
        if (zSuccess)
            return L_SUCCESS;
        if (zErrors)
            return L_ERRORS;
        return L_WARNIGS;
    }

    protected void extraToString(PrintWriter pw) {
    }
}
