package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

class PmFailureBuilder implements FailureBuilder {
    private String message;
    private String code;
    private int status = 400;

    protected PmFailureBuilder() {
    }

    @Override
    public @NotNull FailureBuilder message(@NotNull String message) {
        this.message = message;
        return this;
    }

    @Override
    public @NotNull FailureBuilder code(@NotNull String code) {
        this.code = code;
        return this;
    }

    @Override
    public @NotNull FailureBuilder status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public @NotNull Failure build() {
        return new PmFailure(code, status, message, null);
    }
}
