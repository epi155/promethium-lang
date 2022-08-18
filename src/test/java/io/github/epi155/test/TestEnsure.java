package io.github.epi155.test;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static io.github.epi155.pm.smart.Ensure.ensure;

public class TestEnsure {
    @Test
    void testCompose2() {
        String name = null;
        String init = null;
        String cogn = "hello";
        @Nullable String a = ensure(name)
            .join(n ->
                ensure(init)
                    .join(i ->
                        ensure(cogn)
                            .apply(c -> n.trim() + " " + i.trim() + " " + c.trim())
                    )
            )
            .value();
    }
}
