package io.github.epi155.pm.lang;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChoiceValueContext<T> implements ChoiceValueContext<T> {
    private final AnyValue<T> parent;
    private boolean branchExecuted = false;
    private ItemStatus result;

    PmChoiceValueContext(AnyValue<T> anyValue) {
        this.parent = anyValue;
        this.result = anyValue;
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new ChoiceValueWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(parent.value());
            }
        };
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(boolean test) {
        return new ChoiceValueWhenContextBase() {
            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull ChoiceValueElseContext<T> otherwise() {
        return this.new ChoiceValueElseContextBase() {

            @Override
            public @NotNull ChoiceValueExitContext peek(@NotNull Consumer<? super T> action) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    action.accept(parent.value());
                    branchExecuted = true;
                }
                return this;
            }

            @Override
            public @NotNull ChoiceValueExitContext ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                    branchExecuted = true;
                }
                return this;
            }

            @Override
            public @NotNull ChoiceValueExitContext fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return this;
            }

            @Override
            public @NotNull ChoiceValueExitContext nop() {
                return this;
            }
        };
    }

    private abstract class ChoiceValueElseContextBase implements ChoiceValueElseContext<T>, ChoiceValueExitContext {
        @Override
        public @NotNull None end() {
            if (parent.completeSuccess()) {
                return result.completeSuccess() ? PmNone.none() : new PmNone(result.signals());
            } else if (parent.completeWithErrors()) {
                return new PmNone(parent.signals());    // parent errors (warnings)
            } else if (result.completeSuccess()) {
                return new PmNone(parent.signals());    // parent warnings
            } else {
                val bld = None.builder();
                bld.add(parent.signals()); // parent warnings
                bld.add(result.signals()); // result errors/warnings
                return bld.build();
            }
        }
    }


    @Override
    public @NotNull <U> ChoiceValueWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        //noinspection Convert2Diamond
        return new ChoiceValueWhenAsContext<U, T>() {
            @Override
            public @NotNull ChoiceValueContext<T> peek(@NotNull Consumer<? super U> action) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    action.accept(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> ergo(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }

            @Override
            public @NotNull ChoiceValueContext<T> fault(CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && parent.value().getClass().isAssignableFrom(cls)) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChoiceValueContext.this;
            }
        };
    }

    @Override
    public @NotNull ChoiceValueWhenContext<T> when(@NotNull T t) {
        return new ChoiceValueWhenContextBase() {
            @Override
            protected boolean test() {
                return parent.value().equals(t);
            }
        };
    }

    abstract class ChoiceValueWhenContextBase implements ChoiceValueWhenContext<T> {
        protected abstract boolean test();

        @Override
        public @NotNull ChoiceValueContext<T> peek(@NotNull Consumer<? super T> action) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                action.accept(parent.value());
                branchExecuted = true;
            }
            return PmChoiceValueContext.this;
        }

        @Override
        public @NotNull ChoiceValueContext<T> ergo(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return PmChoiceValueContext.this;
        }

        @Override
        public @NotNull ChoiceValueContext<T> fault(CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Nope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChoiceValueContext.this;
        }
    }
}
