package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
@Deprecated
public interface ChoiceEmptyContext extends ChoiceExitContext {
    @Deprecated
    @NotNull WhenEmptyContext when(boolean test);

    @Deprecated
    @NotNull ElseEmptyContext otherwise();
}
