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

/** 将 Tetra 碾钵特化的生命周期委托给手搓学原生碾钵。 */
@Mixin(Item.class)
abstract class CrusherMortarLifecycleMixin {
    @Inject(method = {"use", "m_7203_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$crusherMortarUse(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        ItemStack stack = player.getItemInHand(hand);
        if (isSpecializedCrusherMortar(stack)) {
            callback.setReturnValue(ModItems.CRUSHER_MORTAR.get().use(level, player, hand));
        }
    }

    @Inject(method = {"getUseAnimation", "m_6164_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$crusherMortarAnimation(
            ItemStack stack, CallbackInfoReturnable<UseAnim> callback) {
        if (isSpecializedCrusherMortar(stack)) {
            callback.setReturnValue(ModItems.CRUSHER_MORTAR.get().getUseAnimation(stack));
        }
    }

    @Inject(method = {"getUseDuration", "m_8105_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$crusherMortarDuration(
            ItemStack stack, CallbackInfoReturnable<Integer> callback) {
        if (isSpecializedCrusherMortar(stack)) {
            callback.setReturnValue(ModItems.CRUSHER_MORTAR.get().getUseDuration(stack));
        }
    }

    @Inject(method = {"finishUsingItem", "m_5922_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$crusherMortarFinish(
            ItemStack stack, Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> callback) {
        if (isSpecializedCrusherMortar(stack)) {
            callback.setReturnValue(ModItems.CRUSHER_MORTAR.get().finishUsingItem(stack, level, entity));
        }
    }

    @Inject(method = {"onUseTick", "m_5929_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$crusherMortarUseTick(
            Level level,
            LivingEntity entity,
            ItemStack stack,
            int ticksRemaining,
            CallbackInfo callback) {
        if (isSpecializedCrusherMortar(stack)) {
            ModItems.CRUSHER_MORTAR.get().onUseTick(level, entity, stack, ticksRemaining);
            callback.cancel();
        }
    }

    @Inject(method = {"releaseUsing", "m_5551_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$crusherMortarRelease(
            ItemStack stack,
            Level level,
            LivingEntity entity,
            int timeLeft,
            CallbackInfo callback) {
        if (isSpecializedCrusherMortar(stack)) {
            ModItems.CRUSHER_MORTAR.get().releaseUsing(stack, level, entity, timeLeft);
            callback.cancel();
        }
    }

    private static boolean isSpecializedCrusherMortar(ItemStack stack) {
        return TetraSpecializationResolver.resolveUniqueUseMode(stack)
                .filter(TetraUseModePolicy.CRUSHER_MORTAR::equals)
                .isPresent();
    }
}
