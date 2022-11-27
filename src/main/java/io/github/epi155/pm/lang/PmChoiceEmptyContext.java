package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

class PmChoiceEmptyContext implements ChoiceEmptyContext {
    private final AnyError parent;
    private AnyError result;
    private boolean branchExecuted = false;

    public PmChoiceEmptyContext(AnyError anyError) {
        this.parent = anyError;
        this.result = anyError;
    }

    @Override
    public @NotNull WhenEmptyContext when(boolean test) {
        return new WhenEmptyContext() {
            @Override
            public @NotNull ChoiceEmptyContext implies(@NotNull Runnable action) {
                if (parent.isSuccess() && !branchExecuted && test) {
                    action.run();
                    branchExecuted = true;
                }
                return PmChoiceEmptyContext.this;
            }

            @Override
            public @NotNull ChoiceEmptyContext perform(@NotNull Supplier<? extends AnyError> action) {
                if (parent.isSuccess() && !branchExecuted && test) {
                    result = action.get();
                    branchExecuted = true;
                }
                return PmChoiceEmptyContext.this;
            }
        };
    }

    @Override
    public @NotNull ElseEmptyContext otherwise() {
        return new ElseEmptyContext() {
            @Override
            public @NotNull ChoiceExitContext implies(@NotNull Runnable action) {
                if (parent.isSuccess() && !branchExecuted) {
                    action.run();
                    branchExecuted = true;
                }
                return PmChoiceEmptyContext.this;
            }

            @Override
            public @NotNull ChoiceExitContext perform(@NotNull Supplier<? extends AnyError> action) {
                if (parent.isSuccess() && !branchExecuted) {
                    result = action.get();
                    branchExecuted = true;
                }
                return PmChoiceEmptyContext.this;
            }
        };
    }

    @Override
    public @NotNull None end() {
        return result.isSuccess() ? new PmNone() : new PmNone(result.errors());
    }
}
