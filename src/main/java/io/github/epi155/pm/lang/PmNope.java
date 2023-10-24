package io.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

class PmNope extends PmSingleError implements Nope {
    private PmNope() {
        super(null);
    }

    @NoBuiltInCapture
    protected static Nope nope() {
        return NopeHelper.NOPE_INSTANCE;
    }

    protected PmNope(@NotNull Failure fault) {
        super(fault);
    }

    @Override
    @NoBuiltInCapture
    public @NotNull None ergo(@NotNull Supplier<? extends ItemStatus> fcn) {
        if (completeSuccess()) {
            ItemStatus one = fcn.get();
            if (one.completeSuccess()) {
                return None.none();
            } else {
                return new PmNone(one.signals());
            }
        } else {
            return new PmNone(this.signals());
        }
    }

    @Override
    public @NotNull Glitch onSuccess(Runnable action) {
        if (completeSuccess()) {
            action.run();
        }
        return this;
    }

    @Override
    @NoBuiltInCapture
    public @NotNull Nope ergoes(@NotNull Supplier<? extends SingleError> fcn) {
        if (completeSuccess()) {
            SingleError one = fcn.get();
            if (one.completeSuccess()) {
                return Nope.nope();
            } else {
                return new PmNope(one.failure());
            }
        } else {
            return this;
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Hope<R> maps(@NotNull Supplier<? extends Hope<R>> fcn) {
        if (completeSuccess()) {
            return fcn.get();
        } else {
            return new PmHope<>(null, failure());
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Hope<R> mapsOf(@NotNull Supplier<? extends R> fcn) {
        return completeSuccess() ? Hope.of(fcn.get()) : new PmHope<>(null, failure());
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> map(@NotNull Supplier<? extends AnyValue<R>> fcn) {
        if (completeSuccess()) {
            return PmSome.of(fcn.get());
        } else {
            return new PmSome<>(Collections.singletonList(failure()));
        }
    }

    @Override
    @NoBuiltInCapture
    public @NotNull <R> Some<R> mapOf(@NotNull Supplier<? extends R> fcn) {
        if (completeSuccess()) {
            return new PmSome<>(fcn.get());
        } else {
            return new PmSome<>(Collections.singletonList(failure()));
        }
    }

    @Override
    public <R> R mapTo(Supplier<R> onSuccess, Function<Failure, R> onFailure) {
        return completeSuccess() ? onSuccess.get() : onFailure.apply(failure());
    }

    @NoBuiltInCapture
    public @NotNull Nope implies(@NotNull Runnable action) {
        if (completeSuccess()) {
            action.run();
            return Nope.nope();
        } else {
            return this;
        }
    }

    private static class NopeHelper {
        private static final PmNope NOPE_INSTANCE = new PmNope();
    }

}
