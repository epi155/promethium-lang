package io.github.epi155.pm.lang;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

class PmLoopFactory {
    private PmLoopFactory() {}
    /**
     * Creates a {@link LoopConsumer} from a {@link LoopBuilderConsumer}
     *
     * @param loop {@link LoopBuilderConsumer} instance
     * @param <U>  loop data type
     * @return {@link LoopConsumer} instance
     */
    @SuppressWarnings("Convert2Diamond")
    @Contract(value = "_ -> new", pure = true)
    static <U> @NotNull LoopConsumer<U> of(@NotNull LoopBuilderConsumer<? extends U> loop) {
        return new LoopConsumer<U>() {

            @Override
            public @NotNull None forEach(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                return loop.forEach(fcn).build();
            }

            @Override
            public @NotNull None forEachParallel(int maxThread, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                return loop.forEachParallel(maxThread, fcn).build();
            }

            @Override
            public @NotNull None forEachParallel(@NotNull ExecutorService executor, @NotNull Function<? super U, ? extends ItemStatus> fcn) {
                return loop.forEachParallel(executor, fcn).build();
            }
        };
    }
}
