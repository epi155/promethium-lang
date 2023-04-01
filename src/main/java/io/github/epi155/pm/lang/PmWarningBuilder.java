package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

class PmWarningBuilder implements WarningBuilder {
    private String message;
    private String code;
    private int status = 400;

    protected PmWarningBuilder() {
    }

    @Override
    public @NotNull WarningBuilder message(@NotNull String message) {
        this.message = message;
        return this;
    }

    @Override
    public @NotNull WarningBuilder code(@NotNull String code) {
        this.code = code;
        return this;
    }

    @Override
    public @NotNull WarningBuilder status(int status) {
        this.status = status;
        return this;
    }

    @Override
    public @NotNull Warning build() {
        return new PmWarning(code, status, message, null);
    }
}
