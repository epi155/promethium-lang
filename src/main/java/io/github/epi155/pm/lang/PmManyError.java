package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Stream;

abstract class PmManyError implements ManyErrors, Glitches {
    private final Collection<Failure> errors;

    protected PmManyError() {
        this.errors = Collections.emptyList();
    }

    protected PmManyError(Queue<Failure> errors) {
        this.errors = Collections.unmodifiableCollection(errors);
    }

    protected PmManyError(Collection<Failure> errors) {
        this.errors = Collections.unmodifiableCollection(errors);
    }

    @Override
    public boolean isSuccess() {
        return errors.isEmpty();
    }

    @Override
    public int count() {
        return errors.size();
    }

    @Override
    public @NotNull Collection<Failure> errors() {
        return errors;
    }

    @Override
    public void onFailure(@NotNull Consumer<Stream<Failure>> errorAction) {
        if (!isSuccess())
            errorAction.accept(errors().stream());
    }

    @Override
    public @NotNull Optional<String> summary() {
        if (errors.isEmpty()) return Optional.empty();
        int size = errors.size();
        if (size > 1) return Optional.of(String.format("%d errors found", size));
        Failure error = errors.iterator().next();
        return Optional.of(error.message());
    }

}
