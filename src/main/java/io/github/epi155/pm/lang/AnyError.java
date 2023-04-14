package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Generic interface for classes with error data
 */
public interface AnyError extends ItemStatus {

    /**
     * summary of any errors
     *
     * @return summary of errors (if any)
     */
    @NotNull Optional<String> summary();

//    /**
//     * performs a fallible action and adds any errors to any previous ones,
//     * the action is performed regardless of the error status.
//     *
//     * @param action fallible action to be performed
//     * @return instance of {@link None} collecting the errors
//     */
//    default @NotNull None anyway(@NotNull Supplier<? extends ItemStatus> action) {
//        val status = action.get();
//        if (status.completeSuccess()) {
//            return None.of(this);
//        } else {
//            val bld = None.builder();
//            bld.add(this);
//            bld.add(status);
//            return bld.build();
//        }
//    }
//
//    /**
//     * performs an action keeping any previous errors,
//     * the action is performed regardless of the error status.
//     *
//     * @param action action to be performed
//     * @return instance of {@link None} with any previous errors
//     */
//    default @NotNull None anyway(@NotNull Runnable action) {
//        action.run();
//        return None.of(this);
//    }
}
