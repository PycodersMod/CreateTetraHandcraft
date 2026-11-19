package com.pycoder.createtetrahandcraft.tetra;

import com.pycoder.createtetrahandcraft.ModEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** Prevents sleeping while the confirmed Sticky status effect is active. */
public final class StickySleepHandler {
    private static final String BLOCKED_MESSAGE =
            "message.create_tetra_handcraft.sticky_sleep_blocked";

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !player.hasEffect(ModEffects.STICKY.get())) {
            return;
        }

        event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
        player.displayClientMessage(Component.translatable(BLOCKED_MESSAGE), true);
    }
}
