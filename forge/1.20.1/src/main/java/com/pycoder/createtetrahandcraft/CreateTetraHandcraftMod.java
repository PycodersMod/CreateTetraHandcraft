package com.pycoder.createtetrahandcraft;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.MinecraftForge;
import com.pycoder.createtetrahandcraft.tetra.PressHammerBlockInteractionHandler;
import com.pycoder.createtetrahandcraft.tetra.StirringStaffAttackWearHandler;
import com.pycoder.createtetrahandcraft.tetra.StirringStaffBlockInteractionHandler;
import com.pycoder.createtetrahandcraft.tetra.SweatWetnessHandler;
import com.pycoder.createtetrahandcraft.tetra.StickySleepHandler;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Minimal Forge entry point. Mod ID and Java package remain provisional pending U21. */
@Mod(CreateTetraHandcraftMod.MOD_ID)
public final class CreateTetraHandcraftMod {
    public static final String MOD_ID = "create_tetra_handcraft";

    public CreateTetraHandcraftMod() {
        ModEffects.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(new PressHammerBlockInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new StirringStaffAttackWearHandler());
        MinecraftForge.EVENT_BUS.register(new StirringStaffBlockInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new SweatWetnessHandler());
        MinecraftForge.EVENT_BUS.register(new StickySleepHandler());
        MinecraftForge.EVENT_BUS.register(new HandMadeStandaloneToolGate());
    }
}
