package com.pycoder.createtetrahandcraft.tetra;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;

/** Resolves two-head Hand Made behavior without silently preferring either Tetra head. */
public final class TetraUseModePolicy {
    public static final String PRESS_HAMMER = "press_hammer";
    public static final String MORTAR = "mortar";
    public static final String CRUSHER_MORTAR = "crusher_mortar";
    public static final String STIRRING_STAFF = "stirring_staff";

    private TetraUseModePolicy() {}

    public static Optional<String> resolveUniqueMode(List<String> leftHeadModes, List<String> rightHeadModes) {
        Set<String> left = resolveHeadModes(leftHeadModes);
        Set<String> right = resolveHeadModes(rightHeadModes);
        if (left.size() > 1 || right.size() > 1) {
            return Optional.empty();
        }
        left.addAll(right);
        return left.size() == 1 ? Optional.of(left.iterator().next()) : Optional.empty();
    }

    private static Set<String> resolveHeadModes(List<String> modes) {
        Set<String> effective = new LinkedHashSet<>(modes);
        if (effective.contains(CRUSHER_MORTAR)) {
            effective.remove(MORTAR);
        }
        return effective;
    }
}
