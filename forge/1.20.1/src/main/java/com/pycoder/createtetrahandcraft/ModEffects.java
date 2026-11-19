package com.pycoder.createtetrahandcraft;

import com.pycoder.createtetrahandcraft.effect.SweatEffect;
import com.pycoder.createtetrahandcraft.effect.StickyEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registered status effects used by the handcraft gameplay rules. */
public final class ModEffects {
    private static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CreateTetraHandcraftMod.MOD_ID);

    public static final RegistryObject<MobEffect> SWEAT = EFFECTS.register(
            "sweat", SweatEffect::new);
    public static final RegistryObject<MobEffect> STICKY = EFFECTS.register(
            "sticky", StickyEffect::new);

    private ModEffects() {}

    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }
}
