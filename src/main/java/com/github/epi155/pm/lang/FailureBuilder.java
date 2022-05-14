package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface FailureBuilder {
    @NotNull FailureBuilder message(@NotNull String message);

    @NotNull FailureBuilder code(@NotNull String code);

    @NotNull FailureBuilder status(int status);

    @NotNull Failure build();
}
