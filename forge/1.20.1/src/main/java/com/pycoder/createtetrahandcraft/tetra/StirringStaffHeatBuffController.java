package com.pycoder.createtetrahandcraft.tetra;

import com.alben.createhandmade.item.StirringStaffItem;
import com.pycoder.createtetrahandcraft.ModEffects;
import com.pycoder.createtetrahandcraft.effect.SweatWetnessPolicy;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.ForgeMod;

/** Adapts a successful Hand Made stirring-staff use to the S04 Sweat III reward. */
public final class StirringStaffHeatBuffController {
    private static final int SWEAT_TIER_THREE_TICKS = 10 * 20;

    private StirringStaffHeatBuffController() {}

    /**
     * Starts or refreshes Sweat III only while the native stirring use is active over a
     * Hand Made/Create Basin recipe that is accepted by Hand Made and declares heat.
     */
    public static boolean tryStartOrRefresh(
            Level level, LivingEntity entity, ItemStack stack, boolean nativeUseStarted) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        boolean specializedStirringStaff = TetraSpecializationResolver.resolveUniqueUseMode(stack)
                .filter(TetraUseModePolicy.STIRRING_STAFF::equals)
                .isPresent();
        boolean wetOrRaining = clearEffectsIfWet(player);
        boolean matchingRecipeRequiresHeat = findHeatingBasinRecipe(level, player);
        boolean eligible = StirringStaffHeatBuffPolicy.shouldGrantTierThree(
                !level.isClientSide,
                nativeUseStarted,
                specializedStirringStaff,
                matchingRecipeRequiresHeat,
                wetOrRaining);
        if (!eligible) {
            return false;
        }

        player.addEffect(new MobEffectInstance(
                ModEffects.SWEAT.get(), SWEAT_TIER_THREE_TICKS, 2, false, true, true));
        return true;
    }

    private static boolean findHeatingBasinRecipe(Level level, Player player) {
        BlockHitResult hit = getNativeUseTarget(level, player);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        BlockPos basinPos = hit.getBlockPos();
        if (!(level.getBlockEntity(basinPos) instanceof BasinBlockEntity basin) || basin.isEmpty()) {
            return false;
        }

        Recipe<?> recipe = StirringStaffItem.findMatchingRecipe(level, basin);
        return recipe instanceof ProcessingRecipe<?> processingRecipe
                && processingRecipe.getRequiredHeat() != HeatCondition.NONE;
    }

    private static BlockHitResult getNativeUseTarget(Level level, Player player) {
        Vec3 eye = player.getEyePosition();
        double reach = player.getAttributeValue(ForgeMod.BLOCK_REACH.get()) + 0.5D;
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(reach));
        return level.clip(new ClipContext(
                eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    public static boolean isWetOrRaining(ServerPlayer player) {
        Level level = player.level();
        BlockPos feet = player.blockPosition();
        return SweatWetnessPolicy.shouldClear(
                !level.isClientSide,
                player.isInWater(),
                player.isUnderWater(),
                level.isRainingAt(feet),
                level.isRainingAt(feet.above()));
    }

    public static boolean clearEffectsIfWet(ServerPlayer player) {
        if (!isWetOrRaining(player)) {
            return false;
        }
        player.removeEffect(ModEffects.SWEAT.get());
        player.removeEffect(ModEffects.STICKY.get());
        return true;
    }
}
