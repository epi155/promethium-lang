package io.github.epi155.pm.lang;

/**
 * Runnable with exception
 */
@FunctionalInterface
public interface CheckedRunnable {
    /**
     * execute method
     *
     * @throws FailureException exception thrown
     */
    void run() throws FailureException;
}
