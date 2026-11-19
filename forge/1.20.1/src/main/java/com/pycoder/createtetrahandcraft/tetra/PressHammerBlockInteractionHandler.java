package com.pycoder.createtetrahandcraft.tetra;

import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Lets the original Hand Made press-hammer lifecycle receive occupied Create machine targets. */
public final class PressHammerBlockInteractionHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        var player = event.getEntity();
        if (TetraSpecializationResolver.resolveUniqueUseMode(player.getMainHandItem())
                .filter(TetraUseModePolicy.PRESS_HAMMER::equals)
                .isEmpty()) {
            return;
        }

        var level = event.getLevel();
        var pos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean occupiedDepot = blockEntity instanceof DepotBlockEntity depot && !depot.getHeldItem().isEmpty();
        boolean hasTransportedItemHandler = BlockEntityBehaviour.get(
                level, pos, TransportedItemStackHandlerBehaviour.TYPE) != null;
        boolean occupiedBasin = level.getBlockState(pos).getBlock() instanceof BasinBlock
                && blockEntity instanceof BasinBlockEntity basin && !basin.isEmpty();

        if (PressHammerTargetPolicy.shouldCaptureBlockUse(
                true, occupiedDepot, hasTransportedItemHandler, occupiedBasin)) {
            // Hand Made's original onRightClickBlock applies the same gate to its own item.
            // The vanilla block use is denied so the delegated Item.use/releaseUsing lifecycle runs.
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
