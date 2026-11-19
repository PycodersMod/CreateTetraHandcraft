package com.pycoder.createtetrahandcraft.tetra;

import java.util.Map;
import java.util.Objects;

/** Calculates configured Hand Made ability work time without changing native Tetra attributes. */
public final class TetraWorkTimeCalculator {
    public static final int DEFAULT_ADJUSTMENT_TICKS = 0;
    public static final double DEFAULT_MATERIAL_MULTIPLIER = 1.0d;

    private static final Map<String, Integer> DEFAULT_BASE_TICKS = Map.of(
            "press_hammer", 740,
            "mortar", 910,
            "crusher_mortar", 280,
            "stirring_staff", 1350,
            "pointer", 230,
            "hand_saw", 320,
            "bellows", 520,
            "infusion_gun", 100);

    private TetraWorkTimeCalculator() {
    }

    public static int defaultBaseTicks(String abilityId) {
        Objects.requireNonNull(abilityId, "abilityId");
        Integer value = DEFAULT_BASE_TICKS.get(abilityId);
        if (value == null) {
            throw new IllegalArgumentException("Unknown CreateTetraHandcraft ability: " + abilityId);
        }
        return value;
    }

    /** Computes one specialized head using the confirmed default B value for its ability. */
    public static int calculateForAbility(String abilityId, int adjustmentTicks,
                                          double wholeToolSpeed, double behaviorEfficiency,
                                          double materialMultiplier, Integer forcedTicks) {
        double effectiveEfficiency = "pointer".equals(abilityId) ? 1.0d : behaviorEfficiency;
        return calculate(defaultBaseTicks(abilityId), adjustmentTicks, wholeToolSpeed,
                effectiveEfficiency, materialMultiplier, forcedTicks);
    }

    /** Computes T=max(1,a+round(B/(V*E*k))); forced F overrides the ordinary inputs. */
    public static int calculate(int baseTicks, int adjustmentTicks, double wholeToolSpeed,
                                double behaviorEfficiency, double materialMultiplier,
                                Integer forcedTicks) {
        if (forcedTicks != null) {
            return Math.max(1, forcedTicks);
        }
        if (baseTicks <= 0) {
            throw new IllegalArgumentException("Base work time B must be positive");
        }
        requireFinitePositive("wholeToolSpeed", wholeToolSpeed);
        requireFinitePositive("behaviorEfficiency", behaviorEfficiency);
        requireFinitePositive("materialMultiplier", materialMultiplier);

        double denominator = wholeToolSpeed * behaviorEfficiency * materialMultiplier;
        requireFinitePositive("V*E*k", denominator);
        double quotient = baseTicks / denominator;
        if (!Double.isFinite(quotient) || quotient >= Long.MAX_VALUE) {
            throw new IllegalArgumentException("Calculated work time exceeds the supported range");
        }

        long adjusted;
        try {
            adjusted = Math.addExact(Math.round(quotient), (long) adjustmentTicks);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Calculated work time exceeds the supported range", exception);
        }
        if (adjusted > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Calculated work time exceeds the supported range");
        }
        return (int) Math.max(1L, adjusted);
    }

    private static void requireFinitePositive(String name, double value) {
        if (!Double.isFinite(value) || value <= 0.0d) {
            throw new IllegalArgumentException(name + " must be finite and positive");
        }
    }
}
