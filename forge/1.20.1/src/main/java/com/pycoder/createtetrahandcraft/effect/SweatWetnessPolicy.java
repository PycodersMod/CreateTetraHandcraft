package com.pycoder.createtetrahandcraft.effect;

/** Shared server-side precedence rule for clearing Sweat and Sticky. */
public final class SweatWetnessPolicy {
    private SweatWetnessPolicy() {}

    public static boolean shouldClear(
            boolean serverSide,
            boolean inWater,
            boolean underwater,
            boolean rainingAtFeet,
            boolean rainingAbove) {
        return serverSide && (inWater || underwater || rainingAtFeet || rainingAbove);
    }
}
