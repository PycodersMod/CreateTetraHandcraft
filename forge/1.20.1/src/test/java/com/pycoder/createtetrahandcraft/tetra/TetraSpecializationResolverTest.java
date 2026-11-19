package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TetraSpecializationResolverTest {
    @Test
    void productionAdapterUsesTetraNativeSlotAndImprovementApis() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/TetraSpecializationResolver.java"),
                StandardCharsets.UTF_8);

        assertTrue(source.contains("getModuleFromSlot(toolStack, requestedSlot)"));
        assertTrue(source.contains("instanceof ItemModuleMajor majorModule"));
        assertTrue(source.contains("majorModule.getImprovementLevel(toolStack, improvementKey)"));
        assertTrue(source.contains("majorModule.getKey()"));
        assertFalse(source.contains("getOrCreateTag"), "specialization lookup must not parse private NBT");
    }

    @Test
    void productionAdapterMapsS04StirringStaffImprovementToNativeUseMode() throws Exception {
        String source = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/TetraSpecializationResolver.java"),
                StandardCharsets.UTF_8);

        assertTrue(source.contains("STIRRING_STAFF_IMPROVEMENT"));
        assertTrue(source.contains("TetraUseModePolicy.STIRRING_STAFF"));
    }

    @Test
    void resolvesImprovementFromTheRequestedNativeTetraSlot() {
        String[] requestedSlot = new String[1];

        Optional<TetraSpecializationResolver.Specialization> result = TetraSpecializationResolver.resolve(
                "double/head_right", "create_tetra_handcraft:press_hammer", slot -> {
                    requestedSlot[0] = slot;
                    return new TetraSpecializationResolver.ModuleRead("double/basic_hammer_right", 2);
                });

        assertTrue(result.isPresent());
        assertEquals("double/head_right", requestedSlot[0]);
        assertEquals("double/head_right", result.orElseThrow().slot());
        assertEquals("double/basic_hammer_right", result.orElseThrow().moduleKey());
        assertEquals("create_tetra_handcraft:press_hammer", result.orElseThrow().improvementKey());
        assertEquals(2, result.orElseThrow().level());
    }

    @Test
    void returnsEmptyWhenTheRequestedSlotHasNoModule() {
        assertTrue(TetraSpecializationResolver.resolve("double/head_left",
                "create_tetra_handcraft:press_hammer", slot -> null).isEmpty());
    }

    @Test
    void returnsEmptyWhenTheImprovementIsNotInstalled() {
        Optional<TetraSpecializationResolver.Specialization> result = TetraSpecializationResolver.resolve(
                "double/head_left", "create_tetra_handcraft:press_hammer",
                slot -> new TetraSpecializationResolver.ModuleRead("double/basic_hammer_left", 0));

        assertFalse(result.isPresent());
    }

    @Test
    void returnsBothSpecializedHeadsWithoutChoosingAnInteractionWinner() {
        List<TetraSpecializationResolver.Specialization> result = TetraSpecializationResolver.resolveSlots(
                List.of("double/head_left", "double/head_right"), "create_tetra_handcraft:press_hammer",
                slot -> new TetraSpecializationResolver.ModuleRead(
                        slot.endsWith("left") ? "double/basic_hammer_left" : "double/basic_hammer_right", 1));

        assertEquals(List.of("double/head_left", "double/head_right"),
                result.stream().map(TetraSpecializationResolver.Specialization::slot).toList());
        assertEquals(List.of("double/basic_hammer_left", "double/basic_hammer_right"),
                result.stream().map(TetraSpecializationResolver.Specialization::moduleKey).toList());
    }

    @Test
    void resolvesEachSlotAtMostOnce() {
        List<String> queriedSlots = new java.util.ArrayList<>();
        TetraSpecializationResolver.resolveSlots(
                List.of("double/head_left", "double/head_left"), "create_tetra_handcraft:press_hammer", slot -> {
                    queriedSlots.add(slot);
                    return new TetraSpecializationResolver.ModuleRead("double/basic_hammer_left", 1);
                });

        assertEquals(List.of("double/head_left"), queriedSlots);
    }
}
