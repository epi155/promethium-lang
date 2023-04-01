package io.github.epi155.pm.lang;

import java.util.Collection;

/**
 * Class indicating the status of the result
 */
public interface ItemStatus {
    /**
     * Indicates that there is no error and no warning
     * @return  no error and no warning
     */
    boolean completeSuccess();

    /**
     * Indicates that there is at least one error
     * @return  there is at least one error
     */
    boolean completeWithErrors();

    /**
     * Indicates that there are no errors (there may be warnings)
     * @return  there are no errors (there may be warnings)
     */
    default boolean completeWithoutErrors() {
        return ! completeWithErrors();
    }

    /**
     * Indicates that there is at least one warning and no errors
     * @return  there is at least one warning and no errors
     */
    default boolean completeWithWarnings() {
        return false;
    }

    /**
     * Signal list (error or warning)
     * @return  signal list (error or warning)
     */
    Collection<Signal> signals();
}
