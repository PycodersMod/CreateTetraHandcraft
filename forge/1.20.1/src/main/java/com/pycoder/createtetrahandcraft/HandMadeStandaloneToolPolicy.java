package com.pycoder.createtetrahandcraft;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Set;

/** Identifies the eight original Create: Hand Made tools, without matching Tetra tool stacks. */
public final class HandMadeStandaloneToolPolicy {
    private static final Set<ResourceLocation> DISABLED_STANDALONE_TOOLS = Set.of(
            id("press_hammer"),
            id("mortar"),
            id("crusher_mortar"),
            id("pointer"),
            id("stirring_staff"),
            id("hand_saw"),
            id("bellows"),
            id("infusion_gun"));

    private HandMadeStandaloneToolPolicy() {
    }

    public static boolean isDisabledStandaloneTool(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && isDisabledStandaloneTool(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static boolean isDisabledStandaloneTool(ResourceLocation itemId) {
        return itemId != null && DISABLED_STANDALONE_TOOLS.contains(itemId);
    }

    private static ResourceLocation id(String path) {
        return Objects.requireNonNull(ResourceLocation.tryParse("create_hand_made:" + path));
    }
}
