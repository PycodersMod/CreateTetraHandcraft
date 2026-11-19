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

/** Delegates a Tetra mortar specialization to Create: Hand Made's existing mortar lifecycle. */
@Mixin(Item.class)
abstract class MortarLifecycleMixin {
    @Inject(method = {"use", "m_7203_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$mortarUse(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        ItemStack stack = player.getItemInHand(hand);
        if (isSpecializedMortar(stack)) {
            callback.setReturnValue(ModItems.MORTAR.get().use(level, player, hand));
        }
    }

    @Inject(method = {"getUseAnimation", "m_6164_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$mortarAnimation(
            ItemStack stack, CallbackInfoReturnable<UseAnim> callback) {
        if (isSpecializedMortar(stack)) {
            callback.setReturnValue(ModItems.MORTAR.get().getUseAnimation(stack));
        }
    }

    @Inject(method = {"getUseDuration", "m_8105_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$mortarDuration(
            ItemStack stack, CallbackInfoReturnable<Integer> callback) {
        if (isSpecializedMortar(stack)) {
            callback.setReturnValue(ModItems.MORTAR.get().getUseDuration(stack));
        }
    }

    @Inject(method = {"finishUsingItem", "m_5922_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$mortarFinish(
            ItemStack stack, Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> callback) {
        if (isSpecializedMortar(stack)) {
            callback.setReturnValue(ModItems.MORTAR.get().finishUsingItem(stack, level, entity));
        }
    }

    @Inject(method = {"onUseTick", "m_5929_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$mortarUseTick(
            Level level,
            LivingEntity entity,
            ItemStack stack,
            int ticksRemaining,
            CallbackInfo callback) {
        if (isSpecializedMortar(stack)) {
            ModItems.MORTAR.get().onUseTick(level, entity, stack, ticksRemaining);
            callback.cancel();
        }
    }

    @Inject(method = {"releaseUsing", "m_5551_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$mortarRelease(
            ItemStack stack,
            Level level,
            LivingEntity entity,
            int timeLeft,
            CallbackInfo callback) {
        if (isSpecializedMortar(stack)) {
            ModItems.MORTAR.get().releaseUsing(stack, level, entity, timeLeft);
            callback.cancel();
        }
    }

    private static boolean isSpecializedMortar(ItemStack stack) {
        return TetraSpecializationResolver.resolveUniqueUseMode(stack)
                .filter(TetraUseModePolicy.MORTAR::equals)
                .isPresent();
    }
}
