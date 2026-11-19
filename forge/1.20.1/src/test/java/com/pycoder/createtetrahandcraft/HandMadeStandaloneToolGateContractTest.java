package com.pycoder.createtetrahandcraft;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HandMadeStandaloneToolGateContractTest {
    @Test
    void registersHighPriorityGateForItemBlockAndEntityUseWithoutChangingRecipes() throws IOException {
        String entry = Files.readString(Path.of("src/main/java/com/pycoder/createtetrahandcraft/CreateTetraHandcraftMod.java"));
        String gate = Files.readString(Path.of("src/main/java/com/pycoder/createtetrahandcraft/HandMadeStandaloneToolGate.java"));

        assertTrue(entry.contains("new HandMadeStandaloneToolGate()"));
        assertTrue(gate.contains("EventPriority.HIGHEST"));
        assertTrue(gate.contains("PlayerInteractEvent.RightClickItem"));
        assertTrue(gate.contains("PlayerInteractEvent.RightClickBlock"));
        assertTrue(gate.contains("PlayerInteractEvent.LeftClickBlock"));
        assertTrue(gate.contains("AttackEntityEvent"));
        assertTrue(gate.contains("PlayerEvent.BreakSpeed"),
                "the legacy hand saw must not retain its standalone mining-speed behavior");
        assertTrue(gate.contains("BlockEvent.BreakEvent"),
                "the legacy tool must not retain block-breaking behavior outside crafting");
        assertTrue(gate.contains("event.setNewSpeed(0.0f)"));
        assertTrue(gate.contains("HandMadeStandaloneToolPolicy.isDisabledStandaloneTool"));
        assertTrue(gate.contains("setCanceled(true)"));
    }
}
