package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CrusherMortarLifecycleBridgeContractTest {
    @Test
    void crusherMortarLifecycleDelegatesTetraUseToHandMadeNativeImplementation() throws Exception {
        Path path = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/mixin/CrusherMortarLifecycleMixin.java");
        assertTrue(Files.exists(path), "the S03 Tetra lifecycle bridge must exist");
        String source = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(source.contains("TetraSpecializationResolver.resolveUniqueUseMode(stack)"));
        assertTrue(source.contains("TetraUseModePolicy.CRUSHER_MORTAR::equals"));
        assertTrue(source.contains("remap = false"));
        for (String target : new String[] {"use", "m_7203_", "getUseAnimation", "m_6164_",
                "getUseDuration", "m_8105_", "finishUsingItem", "m_5922_", "onUseTick", "m_5929_",
                "releaseUsing", "m_5551_"}) {
            assertTrue(source.contains("\"" + target + "\""), "missing target method " + target);
        }
        for (String method : new String[] {"use(level, player, hand)", "getUseAnimation(stack)",
                "getUseDuration(stack)", "finishUsingItem(stack, level, entity)",
                "onUseTick(level, entity, stack, ticksRemaining)", "releaseUsing(stack, level, entity, timeLeft)"}) {
            assertTrue(source.contains("CRUSHER_MORTAR.get()." + method),
                    "missing native CrusherMortarItem method " + method);
        }
    }

    @Test
    void forgeMixinConfigRegistersCrusherMortarLifecycleBridge() throws Exception {
        String json = Files.readString(Path.of("src/main/resources/create_tetra_handcraft.mixins.json"),
                StandardCharsets.UTF_8);
        assertTrue(json.contains("CrusherMortarLifecycleMixin"));
    }
}
