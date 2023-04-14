package io.github.epi155.pm.lang;

import java.util.Collection;

/**
 * Class indicating the status of the result
 */
public interface ItemStatus {
    /**
     * Indicates that the completion status is success, that is, no errors and no warnings
     *
     * @return no error and no warning
     */
    boolean completeSuccess();

    /**
     * Indicates that there is at least one error
     *
     * @return success
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
     * Indicates that the completion status is warning, i.e. no errors and at least one warning
     *
     * @return waring
     */
    default boolean completeWarning() {
        return false;
    }

    /**
     * Signal list (error or warning)
     * @return  signal list (error or warning)
     */
    Collection<Signal> signals();
}
