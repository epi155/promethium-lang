package io.github.epi155.test;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static io.github.epi155.pm.smart.Voidable.voidable;

@Slf4j
class TestVoidable {
    @Test
    void testCompose1() {
        String name = "Neil";
        String init = null;
        String cogn = "Armstrong";
        @Nullable String a = voidable(name)
            .join(n ->
                voidable(init)
                    .join(i ->
                        voidable(cogn)
                            .apply(c -> n.trim() + " " + i.trim() + " " + c.trim())
                    )
            )
            .otherwise("Houston, we've had a problem ");
        log.info(a);
    }

    @Test
    void testCompose2() {
        String name = "Neil";
        String init = "J.";
        String cogn = "Armstrong";
        @Nullable String a = voidable(name)
            .join(n ->
                voidable(init)
                    .join(i ->
                        voidable(cogn)
                            .apply(c -> n.trim() + " " + i.trim() + " " + c.trim())
                    )
            )
            .otherwise("Houston, we've had a problem ");
        log.info(a);
    }
}
