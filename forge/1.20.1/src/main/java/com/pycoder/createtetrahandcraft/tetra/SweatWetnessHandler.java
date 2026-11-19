package com.pycoder.createtetrahandcraft.tetra;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Gives rain and water precedence over both S04 status effects on the server. */
public final class SweatWetnessHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        StirringStaffHeatBuffController.clearEffectsIfWet(player);
    }
}
