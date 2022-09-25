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

    default @NotNull None next(@NotNull Supplier<? extends AnyItem> action) {
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

    default @NotNull None next(@NotNull Runnable action) {
        action.run();
        return None.of(this);
    }

}
