package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Error builder extension for {@link None}
 */
public interface NoneBuilder extends ErrorBuilder {
    /**
     * Final builder
     *
     * @return {@link None} instance
     */
    @NotNull None build();

    /**
     * Add error(s)
     *
     * @param any object with error(s) payload
     * @return {@link NoneBuilder} instance
     */
    default @NotNull NoneBuilder withStatus(@NotNull ItemStatus any) {
        add(any);
        return this;
    }

    /**
     * Add a warning message in builder style
     *
     * @param ce   warning message
     * @param argv message arguments
     * @return builder itself
     */
    default @NotNull NoneBuilder withAlert(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Warning warn = PmWarning.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(warn);
        return this;
    }

    /**
     * Add a warning message with properties in builder style
     *
     * @param properties warning properties
     * @param ce         warning message
     * @param argv       message arguments
     * @return builder itself
     */
    default @NotNull NoneBuilder withAlert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Warning warn = PmWarning.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(warn);
        return this;
    }

    /**
     * Add an error message in builder style
     *
     * @param ce   error message
     * @param argv message arguments
     * @return builder itself
     */
    default @NotNull NoneBuilder withFault(@NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Failure fail = PmFailure.of(stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(fail);
        return this;
    }

    /**
     * Add an error message with properties in builder style
     *
     * @param properties error properties
     * @param ce         error message
     * @param argv       message arguments
     * @return builder itself
     */
    default @NotNull NoneBuilder withFault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
        StackTraceElement[] stPtr = Thread.currentThread().getStackTrace();
        @NotNull Failure fail = PmFailure.of(properties, stPtr[PmAnyBuilder.J_LOCATE], ce, argv);
        add(fail);
        return this;
    }

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param iterable fallible values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> iterable(@NotNull Iterable<? extends AnyValue<U>> iterable);

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param iterable values to loop on
     * @param <U>      type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> iterableOf(@NotNull Iterable<? extends U> iterable);

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param stream fallible values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> stream(@NotNull Stream<? extends AnyValue<U>> stream);

    /**
     * It generates a {@link LoopBuilderConsumer} instance to loop on
     *
     * @param stream values to loop on
     * @param <U>    type of value generated
     * @return {@link LoopBuilderConsumer} instance
     */
    @NotNull <U> LoopBuilderConsumer<U> streamOf(@NotNull Stream<? extends U> stream);
}
