package com.pycoder.createtetrahandcraft.effect;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SweatWetnessHandlerContractTest {
    @Test
    void wetnessHandlerRunsServerSideAndRemovesBothRegisteredEffects() throws Exception {
        Path handlerPath = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/SweatWetnessHandler.java");
        assertTrue(Files.exists(handlerPath), "the server-side wetness cleanup handler must exist");
        String handler = Files.readString(handlerPath, StandardCharsets.UTF_8);
        assertTrue(handler.contains("priority = EventPriority.HIGHEST"));
        assertTrue(handler.contains("TickEvent.Phase.END"));
        assertTrue(handler.contains("event.player instanceof ServerPlayer"));
        assertTrue(handler.contains("StirringStaffHeatBuffController.clearEffectsIfWet(player)"));

        String controller = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/StirringStaffHeatBuffController.java"),
                StandardCharsets.UTF_8);
        assertTrue(controller.contains("public static boolean clearEffectsIfWet(ServerPlayer player)"));
        assertTrue(controller.contains("player.removeEffect(ModEffects.SWEAT.get())"));
        assertTrue(controller.contains("player.removeEffect(ModEffects.STICKY.get())"));

        String effects = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/ModEffects.java"), StandardCharsets.UTF_8);
        assertTrue(effects.contains("RegistryObject<MobEffect> SWEAT"));
        assertTrue(effects.contains("RegistryObject<MobEffect> STICKY"));

        String sticky = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/effect/StickyEffect.java"),
                StandardCharsets.UTF_8);
        assertTrue(sticky.contains("Attributes.MOVEMENT_SPEED"));
        assertTrue(sticky.contains("Attributes.ATTACK_SPEED"));
        assertTrue(sticky.contains("-0.25D"));

        String zh = Files.readString(Path.of(
                "src/main/resources/assets/create_tetra_handcraft/lang/zh_cn.json"), StandardCharsets.UTF_8);
        String en = Files.readString(Path.of(
                "src/main/resources/assets/create_tetra_handcraft/lang/en_us.json"), StandardCharsets.UTF_8);
        assertTrue(zh.contains("effect.create_tetra_handcraft.sweat"));
        assertTrue(zh.contains("effect.create_tetra_handcraft.sticky"));
        assertTrue(en.contains("effect.create_tetra_handcraft.sweat"));
        assertTrue(en.contains("effect.create_tetra_handcraft.sticky"));

        String entryPoint = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/CreateTetraHandcraftMod.java"),
                StandardCharsets.UTF_8);
        assertTrue(entryPoint.contains("new SweatWetnessHandler()"));
    }
}
