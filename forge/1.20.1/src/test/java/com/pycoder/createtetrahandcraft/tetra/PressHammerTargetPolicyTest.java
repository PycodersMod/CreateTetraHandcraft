package com.pycoder.createtetrahandcraft.tetra;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PressHammerTargetPolicyTest {
    @Test
    void capturesOnlyAnOccupiedHandMadeProcessingTargetWhenSpecializedHeadIsPresent() {
        assertTrue(PressHammerTargetPolicy.shouldCaptureBlockUse(true, true, false, false));
        assertTrue(PressHammerTargetPolicy.shouldCaptureBlockUse(true, false, true, false));
        assertTrue(PressHammerTargetPolicy.shouldCaptureBlockUse(true, false, false, true));
        assertFalse(PressHammerTargetPolicy.shouldCaptureBlockUse(false, true, true, true));
        assertFalse(PressHammerTargetPolicy.shouldCaptureBlockUse(true, false, false, false));
    }
}
