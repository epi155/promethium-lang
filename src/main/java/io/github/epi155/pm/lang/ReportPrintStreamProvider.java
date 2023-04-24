package io.github.epi155.pm.lang;

import java.io.PrintStream;
import java.util.Optional;

/**
 * interface to provide a user PrintStream to report errors when formatting the error or warning message
 *
 * <p>
 * if no ReportPrintStreamProvider is provided, no report will be produced
 * </p>
 */
public interface ReportPrintStreamProvider {
    /**
     * custom PrintStream
     *
     * @return custom PrintStream
     */
    Optional<PrintStream> getPrintStream();
}
