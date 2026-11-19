package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PressHammerLifecycleBridgeContractTest {
    @Test
    void mixinDelegatesTheTetraStackLifecycleToHandMadeOriginalItem() throws Exception {
        Path sourcePath = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/mixin/PressHammerLifecycleMixin.java");
        assertTrue(Files.exists(sourcePath), "the Tetra-to-Hand Made lifecycle bridge must exist");
        String source = Files.readString(sourcePath, StandardCharsets.UTF_8);

        assertTrue(source.contains("TetraSpecializationResolver.resolveUniqueUseMode(stack)"));
        assertTrue(source.contains("TetraUseModePolicy.PRESS_HAMMER::equals"));
        assertTrue(source.contains("remap = false"), "Forge runtime uses explicit Mojmap/SRG target aliases");
        for (String target : new String[] {"use", "m_7203_", "getUseAnimation", "m_6164_",
                "getUseDuration", "m_8105_", "onUseTick", "m_5929_", "releaseUsing", "m_5551_"}) {
            assertTrue(source.contains("\"" + target + "\""), "missing Mojmap/SRG lifecycle target: " + target);
        }
        for (String method : new String[] {"use(level, player, hand)", "getUseAnimation(stack)",
                "getUseDuration(stack)", "onUseTick(level, entity, stack, ticksRemaining)",
                "releaseUsing(stack, level, entity, timeLeft)"}) {
            assertTrue(source.contains("PRESS_HAMMER.get()." + method), "missing native Hand Made delegation: " + method);
        }
    }

    @Test
    void targetBlockInterceptionMatchesHandMadeMachineCategoriesAndKeepsMainHandOnly() throws Exception {
        Path sourcePath = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/PressHammerBlockInteractionHandler.java");
        assertTrue(Files.exists(sourcePath), "the specialized press hammer interaction handler must exist");
        String source = Files.readString(sourcePath, StandardCharsets.UTF_8);

        assertTrue(source.contains("getHand() != InteractionHand.MAIN_HAND"));
        assertTrue(source.contains("DepotBlockEntity"));
        assertTrue(source.contains("TransportedItemStackHandlerBehaviour.TYPE"));
        assertTrue(source.contains("BasinBlockEntity"));
        assertTrue(source.contains("setUseBlock(Event.Result.DENY)"));
        assertTrue(source.contains("TetraSpecializationResolver.resolveUniqueUseMode"));
    }

    @Test
    void forgeMixinConfigRegistersTheLifecycleBridge() throws Exception {
        Path path = Path.of("src/main/resources/create_tetra_handcraft.mixins.json");
        assertTrue(Files.exists(path), "Forge must load the Item lifecycle mixin config");
        String json = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(json.contains("PressHammerLifecycleMixin"));
    }

    @Test
    void forgeGradleFeedsSrgMappingsAndRefmapOutputToMixinProcessor() throws Exception {
        String buildScript = Files.readString(Path.of("build.gradle.kts"), StandardCharsets.UTF_8);
        assertTrue(buildScript.contains("-AreobfSrgFile="));
        assertTrue(buildScript.contains("build/createSrgToMcp/output.srg"));
        assertTrue(buildScript.contains("-AoutRefMapFile="));
        assertTrue(buildScript.contains("build/resources/main/create_tetra_handcraft.refmap.json"));
    }
}
