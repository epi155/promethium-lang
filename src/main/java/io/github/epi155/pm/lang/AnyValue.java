package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Extension for class with payload
 *
 * @param <T> payload type
 */
public interface AnyValue<T> extends AnyItem {
    /**
     * Returns the value
     *
     * @return value
     */
    @NotNull T value();

    /**
     * Transforms one value into many values
     *
     * @param fcn fallible function returning iterable values
     * @param <U> iterable values type
     * @return iterable values
     */
    default <U> @NotNull LoopValue<U> timesOf(@NotNull Function<? super T, Iterable<U>> fcn) {
        if (isSuccess()) {
            val list = fcn.apply(value());
            return fcn1 -> {
                val bld = None.builder();
                list.forEach(u -> bld.add(fcn1.apply(u)));
                return bld.build();
            };
        } else {
            return fcn2 -> None.of(AnyValue.this);
        }
    }

    /**
     * Transforms one value into many values
     *
     * @param fcn fallible function returning iterable fallible values
     * @param <U> iterable values type
     * @return iterable values
     */
    default <U> @NotNull LoopValue<U> times(@NotNull Function<? super T, Iterable<? extends AnyValue<U>>> fcn) {
        if (isSuccess()) {
            val list = fcn.apply(value());
            return fcn1 -> {
                val bld = None.builder();
                list.forEach(u -> {
                    if (u.isSuccess()) {
                        bld.add(fcn1.apply(u.value()));
                    } else {
                        bld.add(u.errors());
                    }
                });
                return bld.build();
            };
        } else {
            return fcn2 -> None.of(AnyValue.this);
        }
    }

}
