package com.pycoder.createtetrahandcraft.tetra;

/** Pure gate for the S04 heat-process Sweat III trigger. */
final class StirringStaffHeatBuffPolicy {
    private StirringStaffHeatBuffPolicy() {}

    static boolean shouldGrantTierThree(
            boolean serverSide,
            boolean nativeUseStarted,
            boolean specializedStirringStaff,
            boolean matchingRecipeRequiresHeat,
            boolean wetOrRaining) {
        return serverSide && nativeUseStarted && specializedStirringStaff
                && matchingRecipeRequiresHeat && !wetOrRaining;
    }
}
