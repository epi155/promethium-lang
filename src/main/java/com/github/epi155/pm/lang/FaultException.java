package com.github.epi155.pm.lang;

public class FaultException extends RuntimeException {
    private static final long serialVersionUID = -113203096713980318L;
    public final transient Failure fault;

    public FaultException(Failure fault) {
        this.fault = fault;
    }

}
