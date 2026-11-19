package com.pycoder.createtetrahandcraft.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Partial Sticky status effect; mining and bed restrictions are tracked separately. */
public final class StickyEffect extends MobEffect {
    public StickyEffect() {
        super(MobEffectCategory.HARMFUL, 0x80604A);
        addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "18a5f4d7-2a41-4d08-a40e-c16a7001a101", -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED,
                "18a5f4d7-2a41-4d08-a40e-c16a7001a102", -0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
