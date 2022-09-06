package io.github.epi155.test;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static io.github.epi155.pm.smart.NullTrap.trap;

class TestNullTrap {
    @Test
    void testCompose2() {
        String name = null;
        String init = null;
        String cogn = "hello";
        @Nullable String a = trap(name)
            .join(n ->
                trap(init)
                    .join(i ->
                        trap(cogn)
                            .apply(c -> n.trim() + " " + i.trim() + " " + c.trim())
                    )
            )
            .value();
    }
}
