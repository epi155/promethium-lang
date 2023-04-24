import io.github.epi155.pm.lang.ReportPrintStreamProvider;

/**
 * Utility module for handling errors xor a value
 */
module promethium.lang {
    uses ReportPrintStreamProvider;
    exports io.github.epi155.pm.lang;

    requires org.jetbrains.annotations;
    requires static lombok;
}