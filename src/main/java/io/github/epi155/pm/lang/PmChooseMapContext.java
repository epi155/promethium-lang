package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChooseMapContext<T, R> implements ChooseMapContext<T, R> {
    private final @NotNull AnyValue<T> parent;
    private boolean branchExecuted = false;
    private @Nullable AnyValue<R> result;

    PmChooseMapContext(@NotNull AnyValue<T> anyValue) {
        this.parent = anyValue;
    }

    @Override
    public @NotNull ChooseMapWhenContext<T, R> when(@NotNull Predicate<T> predicate) {
        return new ChooseMapWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(parent.value());
            }
        };
    }

    @Override
    public @NotNull ChooseMapWhenContext<T, R> when(boolean test) {
        return new ChooseMapWhenContextBase() {
            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull ChooseMapWhenContext<T, R> whenEquals(@NotNull T t) {
        return new ChooseMapWhenContextBase() {
            @Override
            protected boolean test() {
                return parent.value().equals(t);
            }
        };
    }

    @Override
    public @NotNull <U> ChooseMapWhenAsContext<U, T, R> whenInstanceOf(Class<U> cls) {
        //noinspection Convert2Diamond
        return new ChooseMapWhenAsContext<U, T, R>() {
            @Override
            public @NotNull ChooseMapContext<T, R> map(@NotNull Function<? super U, ? extends AnyValue<R>> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChooseMapContext.this;
            }

            @Override
            public @NotNull ChooseMapContext<T, R> mapOf(@NotNull Function<? super U, ? extends R> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    R value = fcn.apply(cls.cast(parent.value()));
                    result = Hope.of(value);
                    branchExecuted = true;
                }
                return PmChooseMapContext.this;
            }

            @Override
            public @NotNull ChooseMapContext<T, R> fault(@NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = Hope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChooseMapContext.this;
            }

            @Override
            public @NotNull ChooseMapContext<T, R> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = Hope.fault(properties, ce, argv);
                    branchExecuted = true;
                }
                return PmChooseMapContext.this;
            }
        };
    }

    @Override
    public @NotNull ChooseMapElseContext<T, R> otherwise() {
        //noinspection Convert2Diamond
        return new ChooseMapElseContext<T, R>() {
            @Override
            public @NotNull ChooseMapExitContext<R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                }
                return new ChooseMapExitContextBase();
            }

            @Override
            public @NotNull ChooseMapExitContext<R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Hope.of(fcn.apply(parent.value()));
                }
                return new ChooseMapExitContextBase();
            }

            @Override
            public @NotNull ChooseMapExitContext<R> fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Hope.fault(ce, argv);
                }
                return new ChooseMapExitContextBase();
            }

            @Override
            public @NotNull ChooseMapExitContext<R> fault(@NotNull Map<String, Object> properties, CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Hope.fault(properties, ce, argv);
                }
                return new ChooseMapExitContextBase();
            }
        };
    }

    private abstract class ChooseMapWhenContextBase implements ChooseMapWhenContext<T, R> {
        protected abstract boolean test();

        @Override
        public @NotNull ChooseMapContext<T, R> map(@NotNull Function<? super T, ? extends AnyValue<R>> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return PmChooseMapContext.this;
        }

        @Override
        public @NotNull ChooseMapContext<T, R> mapOf(@NotNull Function<? super T, ? extends R> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                R value = fcn.apply(parent.value());
                result = Hope.of(value);
                branchExecuted = true;
            }
            return PmChooseMapContext.this;
        }

        @Override
        public @NotNull ChooseMapContext<T, R> fault(@NotNull CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Hope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChooseMapContext.this;
        }

        @Override
        public @NotNull ChooseMapContext<T, R> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Hope.fault(properties, ce, argv);
                branchExecuted = true;
            }
            return PmChooseMapContext.this;
        }

    }

    private class ChooseMapExitContextBase implements ChooseMapExitContext<R> {

        @Override
        @NoBuiltInCapture
        public @NotNull Some<R> end() {
            if (parent.completeSuccess()) {
                //noinspection DataFlowIssue
                if (result.completeSuccess()) {
                    return new PmSome<>(result.value());
                } else if (result.completeWithErrors()) {
                    return new PmSome<>(result.signals());
                } else /*result.completeWithWarnings()*/ {
                    return new PmSome<>(result.value(), result.signals());
                }
            } else if (parent.completeWithErrors()) {
                return new PmSome<>(parent.signals());  // parent errors (warnings)
            } else /*parent.completeWithWarnings()*/ {
                //noinspection DataFlowIssue
                if (result.completeSuccess()) {
                    return new PmSome<>(result.value(), parent.signals());
                } else if (result.completeWithErrors()) {
                    @NotNull SomeBuilder<R> bld = Some.builder();
                    bld.add(parent.signals());  // parent warnings
                    bld.add(result.signals()); // result errors (warnings)
                    return bld.build();
                } else /*result.completeWithWarnings()*/ {
                    @NotNull SomeBuilder<R> bld = Some.builder();
                    bld.add(parent.signals());  // parent warnings
                    bld.add(result.signals());  // result warnings
                    return bld.buildWithValue(result.value());
                }
            }
        }
    }
}
