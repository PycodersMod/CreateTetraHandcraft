package com.pycoder.createtetrahandcraft.tetra;

import java.util.Objects;
import java.util.Optional;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.item.ItemStack;
import se.mickelus.tetra.items.modular.IModularItem;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemModuleMajor;

/** Reads an improvement from one exact native Tetra module slot. */
public final class TetraSpecializationResolver {
    public static final String LEFT_HEAD_SLOT = "double/head_left";
    public static final String RIGHT_HEAD_SLOT = "double/head_right";
    public static final String PRESS_HAMMER_IMPROVEMENT = "create_tetra_handcraft:press_hammer";
    public static final String MORTAR_IMPROVEMENT = "create_tetra_handcraft:mortar";
    public static final String CRUSHER_MORTAR_IMPROVEMENT = "create_tetra_handcraft:crusher_mortar";
    public static final String STIRRING_STAFF_IMPROVEMENT = "create_tetra_handcraft:stirring_staff";

    private TetraSpecializationResolver() {}

    /**
     * Resolves an improvement from a real modular Tetra item stack. No slot preference is applied;
     * callers that care about both heads must query each slot explicitly.
     */
    public static Optional<Specialization> resolve(ItemStack toolStack, String slot, String improvementKey) {
        if (toolStack == null || toolStack.isEmpty() || !(toolStack.getItem() instanceof IModularItem modularItem)) {
            return Optional.empty();
        }
        return resolve(modularItem, toolStack, slot, improvementKey);
    }

    /** Returns every press-hammer head installed on a double Tetra tool, without selecting a winner. */
    public static List<Specialization> resolvePressHammerHeads(ItemStack toolStack) {
        return resolveSlots(List.of(LEFT_HEAD_SLOT, RIGHT_HEAD_SLOT), PRESS_HAMMER_IMPROVEMENT, slot ->
                resolve(toolStack, slot, PRESS_HAMMER_IMPROVEMENT)
                        .map(value -> new ModuleRead(value.moduleKey(), value.level()))
                        .orElse(null));
    }

    /**
     * Resolves one unambiguous native-use behavior across both Tetra heads. Crusher mortar replaces
     * its mortar prerequisite on the same head; conflicting behaviors on opposite heads remain
     * unresolved until an explicit per-head input rule is implemented.
     */
    public static Optional<String> resolveUniqueUseMode(ItemStack toolStack) {
        List<String> leftModes = resolveUseModesForSlot(toolStack, LEFT_HEAD_SLOT);
        leftModes.addAll(resolveUseModesForSlot(toolStack, "single/head"));
        return TetraUseModePolicy.resolveUniqueMode(
                leftModes,
                resolveUseModesForSlot(toolStack, RIGHT_HEAD_SLOT));
    }

    private static List<String> resolveUseModesForSlot(ItemStack toolStack, String slot) {
        List<String> modes = new ArrayList<>();
        if (resolve(toolStack, slot, PRESS_HAMMER_IMPROVEMENT).isPresent()) {
            modes.add(TetraUseModePolicy.PRESS_HAMMER);
        }
        if (resolve(toolStack, slot, MORTAR_IMPROVEMENT).isPresent()) {
            modes.add(TetraUseModePolicy.MORTAR);
        }
        if (resolve(toolStack, slot, CRUSHER_MORTAR_IMPROVEMENT).isPresent()) {
            modes.add(TetraUseModePolicy.CRUSHER_MORTAR);
        }
        if (resolve(toolStack, slot, STIRRING_STAFF_IMPROVEMENT).isPresent()) {
            modes.add(TetraUseModePolicy.STIRRING_STAFF);
        }
        return modes;
    }

    static Optional<Specialization> resolve(
            IModularItem modularItem, ItemStack toolStack, String slot, String improvementKey) {
        Objects.requireNonNull(modularItem, "modularItem");
        if (toolStack == null || slot == null || slot.isBlank()
                || improvementKey == null || improvementKey.isBlank()) {
            return Optional.empty();
        }

        return resolve(slot, improvementKey, requestedSlot -> {
            ItemModule module = modularItem.getModuleFromSlot(toolStack, requestedSlot);
            if (!(module instanceof ItemModuleMajor majorModule)) {
                return null;
            }
            return new ModuleRead(majorModule.getKey(), majorModule.getImprovementLevel(toolStack, improvementKey));
        });
    }

    static Optional<Specialization> resolve(
            String slot, String improvementKey, Function<String, ModuleRead> moduleAtSlot) {
        if (slot == null || slot.isBlank() || improvementKey == null || improvementKey.isBlank()) {
            return Optional.empty();
        }
        ModuleRead module = Objects.requireNonNull(moduleAtSlot, "moduleAtSlot").apply(slot);
        if (module == null) {
            return Optional.empty();
        }
        int level = module.level();
        if (level <= 0) {
            return Optional.empty();
        }
        return Optional.of(new Specialization(slot, module.moduleKey(), improvementKey, level));
    }

    static List<Specialization> resolveSlots(
            List<String> slots, String improvementKey, Function<String, ModuleRead> moduleAtSlot) {
        if (slots == null || improvementKey == null || improvementKey.isBlank()) {
            return List.of();
        }
        Objects.requireNonNull(moduleAtSlot, "moduleAtSlot");
        List<Specialization> matches = new ArrayList<>();
        for (String slot : new LinkedHashSet<>(slots)) {
            resolve(slot, improvementKey, moduleAtSlot).ifPresent(matches::add);
        }
        return List.copyOf(matches);
    }

    record ModuleRead(String moduleKey, int level) {}

    public record Specialization(String slot, String moduleKey, String improvementKey, int level) {
        public Specialization {
            Objects.requireNonNull(slot, "slot");
            Objects.requireNonNull(moduleKey, "moduleKey");
            Objects.requireNonNull(improvementKey, "improvementKey");
            if (level <= 0) {
                throw new IllegalArgumentException("level must be positive");
            }
        }
    }
}
