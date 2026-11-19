package com.pycoder.createtetrahandcraft.tetra;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TetraWorkTimeCalculatorTest {
    @Test
    void exposesTheEightConfirmedBaseTimes() {
        assertEquals(740, TetraWorkTimeCalculator.defaultBaseTicks("press_hammer"));
        assertEquals(910, TetraWorkTimeCalculator.defaultBaseTicks("mortar"));
        assertEquals(280, TetraWorkTimeCalculator.defaultBaseTicks("crusher_mortar"));
        assertEquals(1350, TetraWorkTimeCalculator.defaultBaseTicks("stirring_staff"));
        assertEquals(230, TetraWorkTimeCalculator.defaultBaseTicks("pointer"));
        assertEquals(320, TetraWorkTimeCalculator.defaultBaseTicks("hand_saw"));
        assertEquals(520, TetraWorkTimeCalculator.defaultBaseTicks("bellows"));
        assertEquals(100, TetraWorkTimeCalculator.defaultBaseTicks("infusion_gun"));
    }

    @Test
    void computesUsingWholeToolSpeedAndBehaviorEfficiency() {
        assertEquals(195, TetraWorkTimeCalculator.calculateForAbility(
                "press_hammer", 10, 2.0d, 2.0d, 1.0d, null));
    }

    @Test
    void usesJavaMathRoundSemanticsAndClampsTheMinimumToOneTick() {
        assertEquals(6, TetraWorkTimeCalculator.calculate(11, 0, 2.0d, 1.0d, 1.0d, null));
        assertEquals(1, TetraWorkTimeCalculator.calculate(10, -20, 1.0d, 1.0d, 1.0d, null));
    }

    @Test
    void forcedDurationOverridesOrdinaryInputsAndClampsToOne() {
        assertEquals(17, TetraWorkTimeCalculator.calculate(0, 0, 0.0d, Double.NaN, 0.0d, 17));
        assertEquals(1, TetraWorkTimeCalculator.calculate(0, 0, 0.0d, Double.NaN, 0.0d, -3));
    }

    @Test
    void pointerUsesItsApprovedUnitEfficiencyConvention() {
        assertEquals(115, TetraWorkTimeCalculator.calculateForAbility(
                "pointer", 0, 2.0d, 99.0d, 1.0d, null));
    }

    @Test
    void rejectsInvalidInputsAndIntegerOverflow() {
        assertThrows(IllegalArgumentException.class,
                () -> TetraWorkTimeCalculator.calculate(10, 0, 0.0d, 1.0d, 1.0d, null));
        assertThrows(IllegalArgumentException.class,
                () -> TetraWorkTimeCalculator.calculate(10, 0, 1.0d, Double.POSITIVE_INFINITY, 1.0d, null));
        assertThrows(IllegalArgumentException.class,
                () -> TetraWorkTimeCalculator.calculate(10, 0, 1.0d, 1.0d, Double.NaN, null));
        assertThrows(IllegalArgumentException.class,
                () -> TetraWorkTimeCalculator.calculate(Integer.MAX_VALUE, 0, 1.0e-12d, 1.0d, 1.0d, null));
    }
}
