package com.github.epi155.pm.lang;

import java.util.function.Consumer;

public interface ThenConsume<U> {
    void andThen(Consumer<U> action);
}