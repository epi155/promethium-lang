package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

public interface ChoiceEmptyContext extends ChoiceExitContext {
    @NotNull WhenEmptyContext when(boolean test);

    @NotNull ElseEmptyContext otherwise();
}
