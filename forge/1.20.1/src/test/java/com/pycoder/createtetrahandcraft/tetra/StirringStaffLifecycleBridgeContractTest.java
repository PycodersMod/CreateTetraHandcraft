package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class StirringStaffLifecycleBridgeContractTest {
    @Test
    void tetraUseLifecycleDelegatesToHandMadeNativeStirringStaff() throws Exception {
        Path path = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/mixin/StirringStaffLifecycleMixin.java");
        assertTrue(Files.exists(path), "the S04 Tetra lifecycle bridge must exist");
        String source = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(source.contains("TetraSpecializationResolver.resolveUniqueUseMode(stack)"));
        assertTrue(source.contains("TetraUseModePolicy.STIRRING_STAFF::equals"));
        for (String target : new String[] {"use", "m_7203_", "getUseAnimation", "m_6164_",
                "getUseDuration", "m_8105_", "finishUsingItem", "m_5922_", "onUseTick", "m_5929_"}) {
            assertTrue(source.contains("\"" + target + "\""), "missing target method " + target);
        }
        for (String method : new String[] {"use(level, player, hand)", "getUseAnimation(stack)",
                "getUseDuration(stack)", "finishUsingItem(stack, level, entity)",
                "onUseTick(level, entity, stack, ticksRemaining)"}) {
            assertTrue(source.contains("STIRRING_STAFF.get()." + method),
                    "missing native StirringStaffItem method " + method);
        }
    }

    @Test
    void nativeBasinInteractionGateIsExtendedForTetraStaff() throws Exception {
        Path path = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/StirringStaffBlockInteractionHandler.java");
        assertTrue(Files.exists(path), "the S04 Tetra Basin event adapter must exist");
        String source = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(source.contains("onRightClickBlock"));
        assertTrue(source.contains("@SubscribeEvent"));
        assertTrue(source.contains("BasinBlockEntity"));
        assertTrue(source.contains("basin.isEmpty()"));
        assertTrue(source.contains("Event.Result.DENY"));
        assertTrue(source.contains("TetraSpecializationResolver.resolveUniqueUseMode(stack)"));
        assertTrue(source.contains("TetraUseModePolicy.STIRRING_STAFF::equals"));

        String entryPoint = Files.readString(Path.of(
                        "src/main/java/com/pycoder/createtetrahandcraft/CreateTetraHandcraftMod.java"),
                StandardCharsets.UTF_8);
        assertTrue(entryPoint.contains("new StirringStaffBlockInteractionHandler()"));
    }
}
