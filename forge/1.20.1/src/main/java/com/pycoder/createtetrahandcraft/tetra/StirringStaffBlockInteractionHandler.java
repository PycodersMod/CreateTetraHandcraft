package com.pycoder.createtetrahandcraft.tetra;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Mirrors Hand Made's Basin interaction gate for the Tetra S04 specialization. */
public final class StirringStaffBlockInteractionHandler {
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getEntity().getItemInHand(event.getHand());
        if (TetraSpecializationResolver.resolveUniqueUseMode(stack)
                .filter(TetraUseModePolicy.STIRRING_STAFF::equals)
                .isEmpty()) {
            return;
        }

        if (event.getLevel().getBlockEntity(event.getPos()) instanceof BasinBlockEntity basin
                && !basin.isEmpty()) {
            event.setUseBlock(Event.Result.DENY);
        }
    }
}
