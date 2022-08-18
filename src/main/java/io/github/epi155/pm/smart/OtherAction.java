package io.github.epi155.pm.smart;

/**
 * interface to safely manage null payload
 */
public interface OtherAction {

    /**
     * indicates what action to take if the payload is null
     *
     * @param action action to be performed if the payload is null
     */
    void otherwise(Runnable action);

}
