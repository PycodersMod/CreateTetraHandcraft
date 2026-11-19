package com.pycoder.createtetrahandcraft.mixin;

import com.alben.createhandmade.registry.ModItems;
import com.pycoder.createtetrahandcraft.tetra.TetraSpecializationResolver;
import com.pycoder.createtetrahandcraft.tetra.TetraUseModePolicy;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Delegates the use/charge/release contract for an S01-specialized Tetra double tool to Hand Made's
 * original PressHammerItem implementation. The actual ItemStack remains the Tetra tool.
 */
@Mixin(Item.class)
abstract class PressHammerLifecycleMixin {
    @Inject(method = {"use", "m_7203_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$pressHammerUse(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        ItemStack stack = player.getItemInHand(hand);
        if (isSpecializedPressHammer(stack)) {
            callback.setReturnValue(ModItems.PRESS_HAMMER.get().use(level, player, hand));
        }
    }

    @Inject(method = {"getUseAnimation", "m_6164_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$pressHammerAnimation(
            ItemStack stack, CallbackInfoReturnable<UseAnim> callback) {
        if (isSpecializedPressHammer(stack)) {
            callback.setReturnValue(ModItems.PRESS_HAMMER.get().getUseAnimation(stack));
        }
    }

    @Inject(method = {"getUseDuration", "m_8105_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$pressHammerDuration(
            ItemStack stack, CallbackInfoReturnable<Integer> callback) {
        if (isSpecializedPressHammer(stack)) {
            callback.setReturnValue(ModItems.PRESS_HAMMER.get().getUseDuration(stack));
        }
    }

    @Inject(method = {"onUseTick", "m_5929_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$pressHammerChargeTick(
            Level level,
            LivingEntity entity,
            ItemStack stack,
            int ticksRemaining,
            CallbackInfo callback) {
        if (isSpecializedPressHammer(stack)) {
            ModItems.PRESS_HAMMER.get().onUseTick(level, entity, stack, ticksRemaining);
            callback.cancel();
        }
    }

    @Inject(method = {"releaseUsing", "m_5551_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$pressHammerRelease(
            ItemStack stack,
            Level level,
            LivingEntity entity,
            int timeLeft,
            CallbackInfo callback) {
        if (isSpecializedPressHammer(stack)) {
            ModItems.PRESS_HAMMER.get().releaseUsing(stack, level, entity, timeLeft);
            callback.cancel();
        }
    }

    private static boolean isSpecializedPressHammer(ItemStack stack) {
        return TetraSpecializationResolver.resolveUniqueUseMode(stack)
                .filter(TetraUseModePolicy.PRESS_HAMMER::equals)
                .isPresent();
    }
}
