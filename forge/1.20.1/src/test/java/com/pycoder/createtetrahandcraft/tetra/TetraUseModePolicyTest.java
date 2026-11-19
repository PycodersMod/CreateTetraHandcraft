package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TetraUseModePolicyTest {
    @Test
    void noSpecializedHeadDoesNotSelectAUseMode() {
        assertTrue(TetraUseModePolicy.resolveUniqueMode(List.of(), List.of()).isEmpty());
    }

    @Test
    void identicalSpecializationOnEitherOrBothHeadsSelectsThatMode() {
        assertEquals("press_hammer", TetraUseModePolicy.resolveUniqueMode(
                List.of("press_hammer"), List.of()).orElseThrow());
        assertEquals("mortar", TetraUseModePolicy.resolveUniqueMode(
                List.of("mortar"), List.of("mortar")).orElseThrow());
    }

    @Test
    void conflictingHeadModesDoNotSilentlyChooseLeftOrRight() {
        assertTrue(TetraUseModePolicy.resolveUniqueMode(
                List.of("press_hammer"), List.of("mortar")).isEmpty());
    }

    @Test
    void crusherMortarUpgradeReplacesItsMortarPrerequisiteOnTheSameHead() {
        assertEquals("crusher_mortar", TetraUseModePolicy.resolveUniqueMode(
                List.of("mortar", "crusher_mortar"), List.of()).orElseThrow());
    }

    @Test
    void differentEffectiveModesAcrossHeadsRemainUnresolved() {
        assertTrue(TetraUseModePolicy.resolveUniqueMode(
                List.of("mortar"), List.of("mortar", "crusher_mortar")).isEmpty());
    }

    @Test
    void inconsistentMultipleModesOnOneHeadFailClosed() {
        assertTrue(TetraUseModePolicy.resolveUniqueMode(
                List.of("press_hammer", "mortar"), List.of()).isEmpty());
    }
}
