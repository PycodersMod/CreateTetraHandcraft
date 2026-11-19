package com.pycoder.createtetrahandcraft;

import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Disables direct use of Hand Made's standalone tools while leaving Tetra modular stacks alone. */
public final class HandMadeStandaloneToolGate {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        cancelStandaloneToolUse(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        cancelStandaloneToolUse(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        cancelStandaloneToolUse(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(AttackEntityEvent event) {
        if (HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(event.getEntity().getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(event.getEntity().getMainHandItem())) {
            event.setNewSpeed(0.0f);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(event.getPlayer().getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    private static void cancelStandaloneToolUse(PlayerInteractEvent event) {
        if (!HandMadeStandaloneToolPolicy.isDisabledStandaloneTool(event.getItemStack())) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
    }
}
