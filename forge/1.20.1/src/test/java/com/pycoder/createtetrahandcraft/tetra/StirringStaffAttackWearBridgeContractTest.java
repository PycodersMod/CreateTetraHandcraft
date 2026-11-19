package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class StirringStaffAttackWearBridgeContractTest {
    @Test
    void tetraStirringStaffMirrorsNativeHandMadeAttackDurability() throws Exception {
        Path path = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/StirringStaffAttackWearHandler.java");
        assertTrue(Files.exists(path), "S04 must adapt Hand Made's native attack wear for Tetra stacks");
        String source = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(source.contains("@SubscribeEvent"));
        assertTrue(source.contains("AttackEntityEvent"));
        assertTrue(source.contains("TetraSpecializationResolver.resolveUniqueUseMode(stack)"));
        assertTrue(source.contains("TetraUseModePolicy.STIRRING_STAFF::equals"));
        assertTrue(source.contains("hurtAndBreak(1"), "native StirringStaffItem consumes one durability per hit");
        assertTrue(source.contains("EquipmentSlot.MAINHAND"));
        assertTrue(source.contains("EquipmentSlot.OFFHAND"));
        assertTrue(source.contains("broadcastBreakEvent"));

        String entryPoint = Files.readString(Path.of(
                        "src/main/java/com/pycoder/createtetrahandcraft/CreateTetraHandcraftMod.java"),
                StandardCharsets.UTF_8);
        assertTrue(entryPoint.contains("new StirringStaffAttackWearHandler()"));
    }
}
