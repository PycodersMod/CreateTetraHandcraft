package com.pycoder.createtetrahandcraft;

import net.minecraft.resources.ResourceLocation;
import java.util.Objects;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandMadeStandaloneToolPolicyTest {
    @Test
    void disablesOnlyTheEightOriginalHandMadeToolItems() {
        for (String path : new String[]{
                "press_hammer", "mortar", "crusher_mortar", "pointer",
                "stirring_staff", "hand_saw", "bellows", "infusion_gun"}) {
            assertTrue(HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(
                    id("create_hand_made:" + path)), path);
        }
    }

    @Test
    void leavesTetraToolsAndUnrelatedItemsOutsideTheStandaloneGate() {
        assertFalse(HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(
                id("tetra:modular_double")));
        assertFalse(HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(
                id("create:andesite_alloy")));
        assertFalse(HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(
                id("create_hand_made:unrelated_item")));
    }

    private static ResourceLocation id(String value) {
        return Objects.requireNonNull(ResourceLocation.tryParse(value));
    }
}
