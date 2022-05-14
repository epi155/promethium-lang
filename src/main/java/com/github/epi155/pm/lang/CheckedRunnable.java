package com.github.epi155.pm.lang;

@FunctionalInterface
public interface CheckedRunnable {
	void run() throws FailureException ;
}
