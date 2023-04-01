package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class PmSearchResult<T> implements SearchResult<T>{
    private final T value;
    private final Failure fault;

    PmSearchResult(T value, Failure fault) {
        this.value = value;
        this.fault = fault;
    }

    @Override
    public @NotNull SearchResult2 onFound(@NotNull Consumer<? super T> action) {
        if (value != null)
            action.accept(value);
        return this;
    }

    @Override
    public @NotNull <R> SearchValueBuilder.Found<T, R> valueBuilder() {
        return this.new PmPartialValue<>() ;
    }

    @Override
    public @NotNull <R> SearchResultBuilder.Found<T, R> resultBuilder() {
        return this.new PmPartialResult<>();
    }

    @Override
    public boolean isFailure() {
        return fault!=null;
    }

    @Override
    public boolean isFound() {
        return value!=null;
    }

    @Override
    public boolean isNotFound() {
        return value==null && fault==null;
    }

    @Override
    public @NotNull T value() {
        if (fault==null) {
            if (value==null) {
                throw new NoSuchElementException("Attempt to get value when nothing was found");
            } else {
                return value;
            }
        } else {
            throw new NoSuchElementException("Attempt to get value when there is the error: "+fault.message());
        }
    }

    @Override
    public @NotNull Failure fault() {
        if (fault != null) {
            return fault;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public @NotNull SearchResult1 onNotFound(@NotNull Runnable action) {
        if (value == null && fault == null)
            action.run();
        return this;
    }

    @Override
    public void onFailure(Consumer<Failure> action) {
        if (fault != null)
            action.accept(fault);
    }

    private class PmPartialValue<R> implements SearchValueBuilder.Found<T, R>, SearchValueBuilder.NotFound<R>, SearchValueBuilder<R> {
        private AnyValue<R> outcome;
        private R raw;

        @Override
        public @NotNull SearchValueBuilder.NotFound<R> onFound(@NotNull Function<? super T, ? extends AnyValue<R>> func) {
            if (value != null)
                this.outcome = func.apply(value);
            return this;
        }

        @Override
        public @NotNull SearchValueBuilder.NotFound<R> onFoundOf(@NotNull Function<? super T, ? extends R> func) {
            if (value != null)
                this.raw = func.apply(value);
            return this;
        }

        @Override
        public @NotNull SearchValueBuilder.NotFound<R> onFoundSetError(@NotNull Nuntium ce, Object...argv) {
            if (value != null)
                this.outcome = Hope.failure(ce, argv);
            return this;
        }

        @Override
        public @NotNull SearchValueBuilder<R> onNotFound(@NotNull Supplier<AnyValue<R>> ctor) {
            if (value == null && fault == null)
                this.outcome = ctor.get();
            return this;
        }

        @Override
        public @NotNull SearchValueBuilder<R> onNotFoundOf(@NotNull Supplier<R> ctor) {
            if (value == null && fault == null)
                this.raw = ctor.get();
            return this;
        }

        @Override
        public @NotNull SearchValueBuilder<R> onNotFoundSetError(@NotNull Nuntium ce, Object... argv) {
            if (value == null && PmSearchResult.this.fault == null)
                this.outcome = Hope.failure(ce, argv);
            return this;
        }

        @Override
        public @NotNull Some<R> build() {
            if (fault != null)
                return new PmSome<>(Collections.singletonList(fault));
            if (raw != null)
                return Some.of(raw);
            if (outcome.completeSuccess())
                return Some.of(outcome.value());
            else if (outcome.completeWithErrors())
                return new PmSome<>(outcome.signals());
            else /*outcome.completeWithWarnings()*/
                return new PmSome<>(outcome.value(), outcome.signals());
        }
    }

    private class PmPartialResult<R> implements SearchResultBuilder.Found<T, R>, SearchResultBuilder.NotFound<R>, SearchResultBuilder<R> {
        private SearchResult<R> outcome;

        @Override
        public @NotNull SearchResultBuilder.NotFound<R> onFound(@NotNull Function<? super T, SearchResult<R>> func) {
            if (value != null)
                this.outcome = func.apply(value);
            return this;
        }

        @Override
        public @NotNull SearchResultBuilder.NotFound<R> onFoundSetError(@NotNull Nuntium ce, Object...argv) {
            if (value != null)
                this.outcome = SearchResult.failure(ce, argv);
            return this;
        }

        @Override
        public @NotNull SearchResultBuilder<R> onNotFound(@NotNull Supplier<SearchResult<R>> ctor) {
            if (value == null && fault == null)
                this.outcome = ctor.get();
            return this;
        }

        @Override
        public @NotNull SearchResultBuilder<R> onNotFoundSetError(@NotNull Nuntium ce, Object... argv) {
            if (value == null && PmSearchResult.this.fault == null)
                this.outcome = SearchResult.failure(ce, argv);
            return this;
        }

        @Override
        public @NotNull SearchResult<R> build() {
            if (fault != null)
                return new PmSearchResult<>(null, fault);
            return outcome;
        }
    }
}
