package com.pycoder.createtetrahandcraft.effect;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StickySleepHandlerContractTest {
    @Test
    void stickySleepListenerRejectsSleepingAndShowsLocalizedReason() throws Exception {
        Path handlerPath = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/StickySleepHandler.java");
        assertTrue(Files.exists(handlerPath), "the Sticky sleep event handler must exist");
        String source = Files.readString(handlerPath, StandardCharsets.UTF_8);

        assertTrue(source.contains("PlayerSleepInBedEvent"));
        assertTrue(source.contains("ModEffects.STICKY"));
        assertTrue(source.contains("Player.BedSleepingProblem.OTHER_PROBLEM"));
        assertTrue(source.contains("message.create_tetra_handcraft.sticky_sleep_blocked"));
    }

    @Test
    void modRegistersStickySleepListenerOnForgeEventBus() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/CreateTetraHandcraftMod.java"),
                StandardCharsets.UTF_8);
        assertTrue(source.contains("new StickySleepHandler()"));
        assertTrue(source.contains("MinecraftForge.EVENT_BUS.register"));
    }
}
