package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Queue;

class PmNone extends PmAny implements None {

    protected PmNone() {
        super();
    }

    protected PmNone(Queue<Failure> errors) {
        super(errors);
    }

    protected PmNone(Collection<Failure> errors) {
        super(errors);
    }

    public @NotNull Glitches onSuccess(Runnable successAction) {
        if (isSuccess())
            successAction.run();
        return this;
    }


}
