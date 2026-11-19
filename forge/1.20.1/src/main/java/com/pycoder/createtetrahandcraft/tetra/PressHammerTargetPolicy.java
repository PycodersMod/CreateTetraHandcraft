package com.pycoder.createtetrahandcraft.tetra;

/** Pure interaction gate matching Hand Made's occupied Depot/Basin/belt targets. */
final class PressHammerTargetPolicy {
    private PressHammerTargetPolicy() {}

    static boolean shouldCaptureBlockUse(
            boolean hasPressHammerHead,
            boolean occupiedDepot,
            boolean hasTransportedItemHandler,
            boolean occupiedBasin) {
        return hasPressHammerHead && (occupiedDepot || hasTransportedItemHandler || occupiedBasin);
    }
}
