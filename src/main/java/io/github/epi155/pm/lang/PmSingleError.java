package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

abstract class PmSingleError implements SingleError {
    private static final String L_SUCCESS = "Success";
    private static final String L_ERRORS = "Errors";
    private final Failure fault;

    protected PmSingleError(Failure fault) {
        this.fault = fault;
    }

    @Override
    public boolean completeSuccess() {
        return fault == null;
    }

    @Override
    public boolean completeWithErrors() {
        return !completeSuccess();
    }

    @Override
    public boolean completeWithoutErrors() {
        return completeSuccess();
    }

    @Override
    public Collection<Signal> signals() {
        return completeSuccess() ? Collections.emptyList() : Collections.singletonList(fault);
    }

    @Override
    public @NotNull Failure failure() {
        if (fault != null) {
            return fault;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void onFailure(@NotNull Consumer<Failure> errorAction) {
        if (fault != null) errorAction.accept(fault);
    }

    public String toString() {
        String status = fault == null ? L_SUCCESS : L_ERRORS;
        val sw = new StringWriter();
        try (val pw = new PrintWriter(sw)) {
            pw.printf("{ finalStatus: %s, ", status);
            extraToString(pw);
            if (fault != null) {
                pw.printf("error: %s", fault);
            }
            pw.print(" }");
        }
        return sw.toString();
    }

    protected void extraToString(PrintWriter pw) {
    }

}
