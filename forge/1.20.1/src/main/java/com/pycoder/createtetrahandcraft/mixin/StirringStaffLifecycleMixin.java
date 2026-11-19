package com.pycoder.createtetrahandcraft.mixin;

import com.alben.createhandmade.registry.ModItems;
import com.pycoder.createtetrahandcraft.tetra.StirringStaffHeatBuffController;
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

/** 将 S04 Tetra 搅拌杖生命周期委托给 Hand Made 原生实现。 */
@Mixin(Item.class)
abstract class StirringStaffLifecycleMixin {
    @Inject(method = {"use", "m_7203_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$stirringStaffUse(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        ItemStack stack = player.getItemInHand(hand);
        if (isSpecializedStirringStaff(stack)) {
            InteractionResultHolder<ItemStack> result = ModItems.STIRRING_STAFF.get().use(level, player, hand);
            if (result.getResult().consumesAction()) {
                StirringStaffHeatBuffController.tryStartOrRefresh(level, player, stack, true);
            }
            callback.setReturnValue(result);
        }
    }

    @Inject(method = {"getUseAnimation", "m_6164_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$stirringStaffAnimation(
            ItemStack stack, CallbackInfoReturnable<UseAnim> callback) {
        if (isSpecializedStirringStaff(stack)) {
            callback.setReturnValue(ModItems.STIRRING_STAFF.get().getUseAnimation(stack));
        }
    }

    @Inject(method = {"getUseDuration", "m_8105_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$stirringStaffDuration(
            ItemStack stack, CallbackInfoReturnable<Integer> callback) {
        if (isSpecializedStirringStaff(stack)) {
            callback.setReturnValue(ModItems.STIRRING_STAFF.get().getUseDuration(stack));
        }
    }

    @Inject(method = {"finishUsingItem", "m_5922_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$stirringStaffFinish(
            ItemStack stack, Level level, LivingEntity entity,
            CallbackInfoReturnable<ItemStack> callback) {
        if (isSpecializedStirringStaff(stack)) {
            callback.setReturnValue(ModItems.STIRRING_STAFF.get().finishUsingItem(stack, level, entity));
        }
    }

    @Inject(method = {"onUseTick", "m_5929_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void createTetraHandcraft$stirringStaffUseTick(
            Level level,
            LivingEntity entity,
            ItemStack stack,
            int ticksRemaining,
            CallbackInfo callback) {
        if (isSpecializedStirringStaff(stack)) {
            ModItems.STIRRING_STAFF.get().onUseTick(level, entity, stack, ticksRemaining);
            if (entity instanceof Player player && player.isUsingItem() && player.getUseItem() == stack) {
                StirringStaffHeatBuffController.tryStartOrRefresh(level, player, stack, true);
            }
            callback.cancel();
        }
    }

    private static boolean isSpecializedStirringStaff(ItemStack stack) {
        return TetraSpecializationResolver.resolveUniqueUseMode(stack)
                .filter(TetraUseModePolicy.STIRRING_STAFF::equals)
                .isPresent();
    }
}
