package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Consumer;

class PmDemuxEnum<E extends Enum<E>, T> implements DemuxEnum<E, T> {
    public final E e;
    public final T value;
    private boolean notConsumed = true;

    protected PmDemuxEnum(E e, T value) {
        this.e = e;
        this.value = value;
    }

    @Override
    public DemuxEnum<E, T> on(@NotNull E e, @NotNull Consumer<T> action) {
        if (this.e == e && notConsumed) {
            notConsumed = false;
            action.accept(value);
        }
        return this;
    }

    @Override
    public DemuxEnum<E, T> on(@NotNull Set<E> e, @NotNull Consumer<T> action) {
        if (e.contains(this.e) && notConsumed) {
            notConsumed = false;
            action.accept(value);
        }
        return this;
    }

    @Override
    public DemuxEnum<E, T> notOn(@NotNull E e, @NotNull Consumer<T> action) {
        if (this.e != e && notConsumed) {
            notConsumed = false;
            action.accept(value);
        }
        return this;
    }

    @Override
    public DemuxEnum<E, T> notOn(@NotNull Set<E> e, @NotNull Consumer<T> action) {
        if (!e.contains(this.e) && notConsumed) {
            notConsumed = false;
            action.accept(value);
        }
        return this;
    }

    @Override
    public void onElse(@NotNull Consumer<T> action) {
        if (notConsumed) {
            action.accept(value);
        }
    }

}
