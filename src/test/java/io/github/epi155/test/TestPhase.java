package io.github.epi155.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Phaser;

@Slf4j
public class TestPhase {
    @Test
    void testPhaser() {
        Phaser phaser = new Phaser();
        phaser.register();
        log.info("Started");
        phaser.arriveAndDeregister();
        log.info("Finished");
        phaser.awaitAdvance(0);
        log.info("Next");
    }
}
