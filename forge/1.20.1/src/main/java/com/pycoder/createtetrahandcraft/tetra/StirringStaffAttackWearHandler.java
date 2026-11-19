package com.pycoder.createtetrahandcraft.tetra;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Applies Hand Made's native one-durability attack cost to a Tetra S04 tool stack. */
public final class StirringStaffAttackWearHandler {
    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        damageIfSpecialized(player.getMainHandItem(), player, EquipmentSlot.MAINHAND);
        damageIfSpecialized(player.getOffhandItem(), player, EquipmentSlot.OFFHAND);
    }

    private static void damageIfSpecialized(ItemStack stack, Player player, EquipmentSlot slot) {
        if (TetraSpecializationResolver.resolveUniqueUseMode(stack)
                .filter(TetraUseModePolicy.STIRRING_STAFF::equals)
                .isEmpty()) {
            return;
        }

        stack.hurtAndBreak(1, player, brokenSlot -> player.broadcastBreakEvent(slot));
    }
}
