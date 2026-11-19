package com.pycoder.createtetrahandcraft.effect;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SweatWetnessPolicyTest {
    @Test
    void anyWaterOrRainStateClearsEffectsOnServer() {
        assertTrue(SweatWetnessPolicy.shouldClear(true, true, false, false, false));
        assertTrue(SweatWetnessPolicy.shouldClear(true, false, true, false, false));
        assertTrue(SweatWetnessPolicy.shouldClear(true, false, false, true, false));
        assertTrue(SweatWetnessPolicy.shouldClear(true, false, false, false, true));
    }

    @Test
    void dryServerAndAllClientStatesKeepTheirEffects() {
        assertFalse(SweatWetnessPolicy.shouldClear(true, false, false, false, false));
        assertFalse(SweatWetnessPolicy.shouldClear(false, true, true, true, true));
    }
}
