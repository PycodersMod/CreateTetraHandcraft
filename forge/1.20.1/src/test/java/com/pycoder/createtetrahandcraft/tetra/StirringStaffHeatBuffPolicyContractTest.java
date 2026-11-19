package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class StirringStaffHeatBuffPolicyContractTest {
    @Test
    void heatTierThreeRequiresSuccessfulNativeS04StartAndMustRespectWetPrecedence() throws Exception {
        Path policyPath = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/StirringStaffHeatBuffPolicy.java");
        assertTrue(Files.exists(policyPath), "the S04 heat-buff gate must exist");
        String policy = Files.readString(policyPath, StandardCharsets.UTF_8);
        for (String requirement : new String[] {
                "serverSide",
                "nativeUseStarted",
                "specializedStirringStaff",
                "matchingRecipeRequiresHeat",
                "wetOrRaining",
                "return serverSide && nativeUseStarted && specializedStirringStaff",
                "&& matchingRecipeRequiresHeat && !wetOrRaining"
        }) {
            assertTrue(policy.contains(requirement), "S04 heat-buff policy must encode: " + requirement);
        }
    }

    @Test
    void onlyTheCompleteServerSideHeatStartTupleIsEligible() {
        assertTrue(StirringStaffHeatBuffPolicy.shouldGrantTierThree(
                true, true, true, true, false));
        assertFalse(StirringStaffHeatBuffPolicy.shouldGrantTierThree(
                false, true, true, true, false));
        assertFalse(StirringStaffHeatBuffPolicy.shouldGrantTierThree(
                true, false, true, true, false));
        assertFalse(StirringStaffHeatBuffPolicy.shouldGrantTierThree(
                true, true, false, true, false));
        assertFalse(StirringStaffHeatBuffPolicy.shouldGrantTierThree(
                true, true, true, false, false));
        assertFalse(StirringStaffHeatBuffPolicy.shouldGrantTierThree(
                true, true, true, true, true));
    }

    @Test
    void productionControllerUsesHandMadeRecipeAuthorityAndCreateHeatRequirement() throws Exception {
        Path controllerPath = Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/tetra/StirringStaffHeatBuffController.java");
        String controller = Files.readString(controllerPath, StandardCharsets.UTF_8);
        assertTrue(controller.contains("StirringStaffItem.findMatchingRecipe(level, basin)"));
        assertTrue(controller.contains("processingRecipe.getRequiredHeat() != HeatCondition.NONE"));
        assertTrue(controller.contains("player.isInWater()"));
        assertTrue(controller.contains("level.isRainingAt(feet)"));
        assertTrue(controller.contains("level.clip(new ClipContext("));
        assertTrue(controller.contains("ForgeMod.BLOCK_REACH.get()"));
        assertTrue(controller.contains("SWEAT_TIER_THREE_TICKS = 10 * 20"));
        assertTrue(controller.contains("new MobEffectInstance("));
    }

    @Test
    void nativeLifecycleGrantsOnlyAfterSuccessfulStartAndRefreshesDuringActiveUse() throws Exception {
        String mixin = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/mixin/StirringStaffLifecycleMixin.java"),
                StandardCharsets.UTF_8);
        assertTrue(mixin.contains("result.getResult().consumesAction()"));
        assertTrue(mixin.contains("tryStartOrRefresh(level, player, stack, true)"));
        assertTrue(mixin.contains("player.isUsingItem() && player.getUseItem() == stack"));

        String entryPoint = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/CreateTetraHandcraftMod.java"),
                StandardCharsets.UTF_8);
        assertTrue(entryPoint.contains("ModEffects.register("));
    }

    @Test
    void sweatTierScalesNativeMovementAndAttackSpeedModifiersByAmplifier() throws Exception {
        String effect = Files.readString(Path.of(
                "src/main/java/com/pycoder/createtetrahandcraft/effect/SweatEffect.java"),
                StandardCharsets.UTF_8);
        assertTrue(effect.contains("Attributes.MOVEMENT_SPEED"));
        assertTrue(effect.contains("Attributes.ATTACK_SPEED"));
        assertTrue(effect.contains("AttributeModifier.Operation.MULTIPLY_TOTAL"));
        assertTrue(effect.contains("addAttributeModifier("));
        assertTrue(effect.contains("0.10D"));
    }
}
