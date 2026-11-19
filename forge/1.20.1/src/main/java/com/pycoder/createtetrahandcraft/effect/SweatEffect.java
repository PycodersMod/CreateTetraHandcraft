package com.pycoder.createtetrahandcraft.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Status-effect type for the Sweat tiers. */
public final class SweatEffect extends MobEffect {
    public SweatEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xD77A32);
        addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "77c1124d-572d-49b3-8e4a-92a66f1ae301", 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED,
                "77c1124d-572d-49b3-8e4a-92a66f1ae302", 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
