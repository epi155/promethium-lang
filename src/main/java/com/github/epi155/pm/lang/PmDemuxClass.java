package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

class PmDemuxClass implements DemuxClass {
    private final Object o;
    private boolean notConsumed = true;

    protected PmDemuxClass(Object o) {
        this.o = o;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <W> DemuxClass when(@NotNull Class<W> cls, @NotNull Consumer<W> action) {
        if (cls.isAssignableFrom(o.getClass()) && notConsumed) {
            notConsumed = false;
            action.accept((W) o);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <W> void whenOther(@NotNull Consumer<W> action) {
        if (notConsumed) {
            action.accept((W) o);
        }
    }
}
