package com.pycoder.createtetrahandcraft.gametest;

import com.pycoder.createtetrahandcraft.CreateTetraHandcraftMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import com.pycoder.createtetrahandcraft.ModEffects;

import java.util.List;

/** Headless checks against the final Forge RecipeManager after all mod data packs load. */
@GameTestHolder(CreateTetraHandcraftMod.MOD_ID)
public final class CreateRecipeGameTests {
    private CreateRecipeGameTests() { }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void approvedMachineRecipesUseBrassCasing(GameTestHelper helper) {
        var recipes = helper.getLevel().getRecipeManager();
        List<MachineRecipe> targets = List.of(
                new MachineRecipe("mechanical_press", 1, 3, "mechanical_press", "andesite_casing"),
                new MachineRecipe("millstone", 1, 3, "millstone", "andesite_casing"),
                new MachineRecipe("mechanical_mixer", 1, 3, "mechanical_mixer", "andesite_casing"),
                new MachineRecipe("deployer", 1, 3, "deployer", "andesite_casing"),
                new MachineRecipe("mechanical_saw", 3, 3, "mechanical_saw", "andesite_casing"),
                new MachineRecipe("encased_fan", 1, 3, "encased_fan", "andesite_casing"),
                new MachineRecipe("spout", 1, 2, "spout", "copper_casing")
        );

        for (MachineRecipe target : targets) {
            ResourceLocation recipeId = new ResourceLocation("create", "crafting/kinetics/" + target.recipePath());
            Recipe<?> recipe = recipes.byKey(recipeId).orElse(null);
            helper.assertTrue(recipe instanceof ShapedRecipe,
                    "expected shaped recipe to load for " + recipeId + ", got " + recipe);
            if (!(recipe instanceof ShapedRecipe shaped)) return;

            helper.assertTrue(shaped.getWidth() == target.width() && shaped.getHeight() == target.height(),
                    "recipe dimensions changed for " + recipeId);
            ItemStack brassCasing = new ItemStack(item("create:brass_casing"));
            ItemStack oldCasing = new ItemStack(item("create:" + target.oldCasing()));
            int brassMatches = 0;
            int oldMatches = 0;
            for (Ingredient ingredient : shaped.getIngredients()) {
                if (ingredient.test(brassCasing)) brassMatches++;
                if (ingredient.test(oldCasing)) oldMatches++;
            }
            helper.assertTrue(brassMatches == 1,
                    "expected exactly one brass casing in " + recipeId + ", got " + brassMatches);
            helper.assertTrue(oldMatches == 0,
                    "old casing still appears in " + recipeId + ": " + target.oldCasing());

            var result = shaped.getResultItem(helper.getLevel().registryAccess());
            ResourceLocation expectedResult = new ResourceLocation("create", target.resultItem());
            helper.assertTrue(BuiltInRegistries.ITEM.getKey(result.getItem()).equals(expectedResult),
                    "recipe output changed for " + recipeId + ": " + result);
            helper.assertTrue(result.getCount() == 1, "recipe output count changed for " + recipeId);
        }

        Recipe<?> wheel = recipes.byKey(new ResourceLocation("create", "mechanical_crafting/crushing_wheel"))
                .orElse(null);
        helper.assertTrue(wheel != null, "original crushing wheel mechanical recipe must remain registered");
        if (wheel == null) return;
        helper.assertTrue(!(wheel instanceof ShapedRecipe), "crushing wheel must not become a crafting-table recipe");
        var wheelResult = wheel.getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(wheelResult.getCount() == 2, "crushing wheel output count must remain two");

        Recipe<?> crafter = recipes.byKey(new ResourceLocation("create", "crafting/kinetics/mechanical_crafter"))
                .orElse(null);
        helper.assertTrue(crafter instanceof ShapedRecipe, "original mechanical crafter recipe must remain shaped");
        if (!(crafter instanceof ShapedRecipe shapedCrafter)) return;
        helper.assertTrue(shapedCrafter.getResultItem(helper.getLevel().registryAccess()).getCount() == 3,
                "mechanical crafter output count must remain three");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void stickyEffectRejectsBedSleepEvent(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.addEffect(new MobEffectInstance(ModEffects.STICKY.get(), 200));
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player,
                java.util.Optional.of(helper.absolutePos(new net.minecraft.core.BlockPos(1, 1, 1))));

        MinecraftForge.EVENT_BUS.post(event);

        helper.assertTrue(event.getResultStatus() == Player.BedSleepingProblem.OTHER_PROBLEM,
                "a player with Sticky must not be allowed to start sleeping");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void noStickyEffectDoesNotChangeBedSleepResult(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player,
                java.util.Optional.of(helper.absolutePos(new net.minecraft.core.BlockPos(1, 1, 1))));

        MinecraftForge.EVENT_BUS.post(event);

        helper.assertTrue(event.getResultStatus() == null,
                "players without Sticky must retain the vanilla sleep-event result");
        helper.succeed();
    }

    private static net.minecraft.world.item.Item item(String id) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(id));
    }

    private record MachineRecipe(String recipePath, int width, int height, String resultItem, String oldCasing) { }
}
