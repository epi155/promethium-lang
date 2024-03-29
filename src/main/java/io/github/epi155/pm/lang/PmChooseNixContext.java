package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class PmChooseNixContext<T> implements ChooseNixContext<T> {
    private final AnyValue<T> parent;
    private boolean branchExecuted = false;
    private @Nullable ItemStatus result;

    PmChooseNixContext(AnyValue<T> anyValue) {
        this.parent = anyValue;
    }

    @Override
    public @NotNull ChooseNixWhenContext<T> when(@NotNull Predicate<T> predicate) {
        return new ChooseNixWhenContextBase() {
            @Override
            protected boolean test() {
                return predicate.test(parent.value());
            }
        };
    }

    @Override
    public @NotNull ChooseNixWhenContext<T> when(boolean test) {
        return new ChooseNixWhenContextBase() {
            @Override
            protected boolean test() {
                return test;
            }
        };
    }

    @Override
    public @NotNull ChooseNixWhenContext<T> whenEquals(@NotNull T t) {
        return new ChooseNixWhenContextBase() {
            @Override
            protected boolean test() {
                return parent.value().equals(t);
            }
        };
    }

    @Override
    public @NotNull <U> ChooseNixWhenAsContext<U, T> whenInstanceOf(@NotNull Class<U> cls) {
        //noinspection Convert2Diamond
        return new ChooseNixWhenAsContext<U, T>() {
            @Override
            public @NotNull ChooseNixContext<T> thenAccept(@NotNull Consumer<? super U> action) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    action.accept(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChooseNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> thenApply(@NotNull Function<? super U, ? extends ItemStatus> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = fcn.apply(cls.cast(parent.value()));
                    branchExecuted = true;
                }
                return PmChooseNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> fault(@NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = Nope.fault(ce, argv);
                    branchExecuted = true;
                }
                return PmChooseNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = Nope.fault(properties, ce, argv);
                    branchExecuted = true;
                }
                return PmChooseNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> alert(@NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = None.alert(ce, argv);
                    branchExecuted = true;
                }
                return PmChooseNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> alert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    result = None.alert(properties, ce, argv);
                    branchExecuted = true;
                }
                return PmChooseNixContext.this;
            }

            @Override
            public @NotNull ChooseNixContext<T> nop() {
                if (parent.completeWithoutErrors() && !branchExecuted && cls.isInstance(parent.value())) {
                    branchExecuted = true;
                }
                return PmChooseNixContext.this;
            }
        };
    }

    @Override
    public @NotNull ChooseNixElseContext<T> otherwise() {
        //noinspection Convert2Diamond
        return new ChooseNixElseContext<T>() {
            @Override
            public @NotNull ChooseNixExitContext thenAccept(@NotNull Consumer<? super T> action) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    action.accept(parent.value());
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext thenApply(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = fcn.apply(parent.value());
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext fault(@NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Nope.fault(ce, argv);
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = Nope.fault(properties, ce, argv);
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext alert(@NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = None.alert(ce, argv);
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext alert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
                if (parent.completeWithoutErrors() && !branchExecuted) {
                    result = None.alert(properties, ce, argv);
                }
                return new ChooseNixElseContextBase();
            }

            @Override
            public @NotNull ChooseNixExitContext nop() {
                return new ChooseNixElseContextBase();
            }
        };
    }

    private abstract class ChooseNixWhenContextBase implements ChooseNixWhenContext<T> {
        protected abstract boolean test();

        @Override
        public @NotNull ChooseNixContext<T> thenAccept(@NotNull Consumer<? super T> action) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                action.accept(parent.value());
                branchExecuted = true;
            }
            return PmChooseNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> thenApply(@NotNull Function<? super T, ? extends ItemStatus> fcn) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = fcn.apply(parent.value());
                branchExecuted = true;
            }
            return PmChooseNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> fault(@NotNull CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Nope.fault(ce, argv);
                branchExecuted = true;
            }
            return PmChooseNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> fault(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = Nope.fault(properties, ce, argv);
                branchExecuted = true;
            }
            return PmChooseNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> alert(@NotNull CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = None.alert(ce, argv);
                branchExecuted = true;
            }
            return PmChooseNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> alert(@NotNull Map<String, Object> properties, @NotNull CustMsg ce, Object... argv) {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                result = None.alert(properties, ce, argv);
                branchExecuted = true;
            }
            return PmChooseNixContext.this;
        }

        @Override
        public @NotNull ChooseNixContext<T> nop() {
            if (parent.completeWithoutErrors() && !branchExecuted && test()) {
                branchExecuted = true;
            }
            return PmChooseNixContext.this;
        }
    }

    private class ChooseNixElseContextBase implements ChooseNixExitContext {
        @Override
        @NoBuiltInCapture
        public @NotNull None end() {
            if (parent.completeSuccess()) {
                return result == null || result.completeSuccess() ? PmNone.none() : new PmNone(result.signals());
            } else if (parent.completeWithErrors()) {
                return new PmNone(parent.signals());    // parent errors (warnings)
            } else if (result == null || result.completeSuccess()) {
                return new PmNone(parent.signals());    // parent warnings
            } else {
                @NotNull NoneBuilder bld = None.builder();
                bld.add(parent.signals()); // parent warnings
                bld.add(result.signals()); // result errors/warnings
                return bld.build();
            }
        }
    }


}
