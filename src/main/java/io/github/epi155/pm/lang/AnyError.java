package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Generic interface for classes with error data
 */
public interface AnyError extends AnyItem {

    /**
     * amount of errors
     *
     * @return errors counted
     */
    int count();

    /**
     * summary of any errors
     *
     * @return summary of errors (if any)
     */
    @NotNull Optional<String> summary();

    /**
     * performs a fallible action and adds any errors to any previous ones
     *
     * @param action fallible action to be performed
     * @return instance of {@link None} collecting the errors
     */
    default @NotNull None then(@NotNull Supplier<? extends AnyItem> action) {
        val status = action.get();
        if (status.isSuccess()) {
            return None.of(this);
        } else {
            val bld = None.builder();
            bld.add(this);
            bld.add(status);
            return bld.build();
        }
    }

    /**
     * performs an action keeping any previous errors
     *
     * @param action action to be performed
     * @return instance of {@link None} with any previous errors
     */
    default @NotNull None then(@NotNull Runnable action) {
        action.run();
        return None.of(this);
    }
}
