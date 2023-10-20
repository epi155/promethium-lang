package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * context to perform an action on a condition
 *
 * @param <T> value type of the chosen value
 */
public interface ChooseNixWhenContext<T> extends ChooseNixWhenStdContext<T> {
    /**
     * performs an action on the value
     *
     * @param action action on the value
     * @return instance of {@link ChooseNixContext}
     */
    @NotNull ChooseNixContext<T> thenAccept(@NotNull Consumer<? super T> action);

    /**
     * performs a fallible function on the value
     *
     * @param fcn fallible function on the value
     * @return instance of {@link ChooseNixContext}
     */
    @NotNull ChooseNixContext<T> thenApply(@NotNull Function<? super T, ? extends ItemStatus> fcn);

}
