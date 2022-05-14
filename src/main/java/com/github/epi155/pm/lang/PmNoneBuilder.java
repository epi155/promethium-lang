package com.github.epi155.pm.lang;

import org.jetbrains.annotations.NotNull;

class PmNoneBuilder extends PmAnyBuilder implements NoneBuilder {
    @Override
    public @NotNull None build() {
        return new PmNone(errors());
    }

}
