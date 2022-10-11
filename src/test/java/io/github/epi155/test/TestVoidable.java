package io.github.epi155.test;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
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

    @Test
    void testApply1() {
        String a = null;
        @Nullable String x = voidable(a).apply(s -> s.replace('a', 'e')).value();
        Assertions.assertNull(x);
    }

    @Test
    void testApply2() {
        String a = "large";
        @Nullable String x = voidable(a).apply(s -> s.replace('a', 'e')).value();
        Assertions.assertNotNull(x);
    }

    @Test
    void testAccept1() {
        String a = null;
        voidable(a).accept(s -> log.info("Result id {}", s)).otherwise(() -> log.info("oops. NULL result"));
    }

    @Test
    void testAccept2() {
        String a = "Hello";
        voidable(a).accept(s -> log.info("Result id {}", s)).otherwise(() -> log.info("oops. NULL result"));
    }

    @Test
    void testFilter1() {
        String a = null;
        @Nullable String x = voidable(a).filter(it -> it.contains("e")).value();
        Assertions.assertNull(x);
    }

    @Test
    void testFilter2() {
        String a = "Hello";
        @Nullable String x = voidable(a).filter(it -> it.contains("e")).value();
        Assertions.assertNotNull(x);
    }

    @Test
    void testFilter3() {
        String a = "Hola";
        @Nullable String x = voidable(a).filter(it -> it.contains("e")).value();
        Assertions.assertNull(x);
    }
}
