package com.pycoder.createtetrahandcraft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModIdentityTest {
    @Test
    void provisionalModIdIsForgeCompatible() {
        assertTrue(CreateTetraHandcraftMod.MOD_ID.matches("[a-z][a-z0-9_]{1,63}"));
        assertEquals("create_tetra_handcraft", CreateTetraHandcraftMod.MOD_ID);
    }
}
