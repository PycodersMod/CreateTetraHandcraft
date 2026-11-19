package com.pycoder.createtetrahandcraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchematicScrollRecipeContractTest {
    private static final Map<String, ExpectedRecipe> RECIPES = Map.of(
            "s01_press_hammer", new ExpectedRecipe(new String[] {"bcb", "Pab", "PaP"}, Map.of(
                    "P", "item:minecraft:paper", "a", "item:minecraft:stick",
                    "b", "item:minecraft:iron_ingot", "c", "item:create:andesite_alloy")),
            "s02_mortar", new ExpectedRecipe(new String[] {"PPP", "bab", "PbP"}, Map.of(
                    "P", "item:minecraft:paper", "a", "item:create:andesite_alloy", "b", "tag:forge:stone")),
            "s03_crusher_mortar", new ExpectedRecipe(new String[] {"PDP", "PMP", "PPP"}, Map.of(
                    "P", "item:minecraft:paper", "D", "item:minecraft:diamond", "M", "item:create_hand_made:mortar")),
            "s04_stirring_staff", new ExpectedRecipe(new String[] {"PPW", "PSP", "SPP"}, Map.of(
                    "P", "item:minecraft:paper", "S", "item:minecraft:stick", "W", "item:create:whisk")),
            "s05_pointer", new ExpectedRecipe(new String[] {"PPA", "PSP", "SPP"}, Map.of(
                    "P", "item:minecraft:paper", "S", "item:minecraft:stick", "A", "item:create:andesite_alloy")),
            "s06_hand_saw", new ExpectedRecipe(new String[] {"PPI", "PIP", "BPP"}, Map.of(
                    "P", "item:minecraft:paper", "I", "tag:forge:plates/iron", "B", "tag:minecraft:planks")),
            "s07_bellows", new ExpectedRecipe(new String[] {"PSP", "NLL", "PSP"}, Map.of(
                    "P", "item:minecraft:paper", "S", "tag:minecraft:wooden_slabs",
                    "N", "tag:forge:nuggets/iron", "L", "item:minecraft:leather")),
            "s08_infusion_gun", new ExpectedRecipe(new String[] {"PCP", "NBN", "PCP"}, Map.of(
                    "P", "item:minecraft:paper", "C", "tag:forge:plates/copper",
                    "N", "tag:forge:nuggets/copper", "B", "item:minecraft:bucket"))
    );

    @Test
    void eightApprovedRecipesProduceReusableIntricateTetraScrolls() throws IOException {
        for (var entry : RECIPES.entrySet()) {
            String recipeId = entry.getKey();
            Path path = resourcePath("recipes/create_tetra_handcraft/" + recipeId + ".json");
            assertTrue(Files.exists(path), "missing approved schematic recipe " + path);
            try (InputStream stream = Files.newInputStream(path)) {
                JsonObject recipe = JsonParser.parseReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
                ExpectedRecipe expected = entry.getValue();

                assertEquals("minecraft:crafting_shaped", recipe.get("type").getAsString(), recipeId);
                assertEquals(List.of(expected.pattern()), recipe.getAsJsonArray("pattern").asList().stream()
                        .map(value -> value.getAsString()).toList(), recipeId + " pattern");
                JsonObject actualKey = recipe.getAsJsonObject("key");
                assertEquals(expected.ingredients().keySet(), actualKey.keySet(), recipeId + " ingredient symbols");
                for (var ingredient : expected.ingredients().entrySet()) {
                    JsonObject actual = actualKey.getAsJsonObject(ingredient.getKey());
                    String actualValue = actual.has("item")
                            ? "item:" + actual.get("item").getAsString()
                            : "tag:" + actual.get("tag").getAsString();
                    assertEquals(ingredient.getValue(), actualValue,
                            recipeId + " ingredient " + ingredient.getKey());
                }

                JsonObject result = recipe.getAsJsonObject("result");
                assertEquals("tetra:scroll_rolled", result.get("item").getAsString(), recipeId + " item");
                assertEquals(1, result.get("count").getAsInt(), recipeId + " count");
                JsonObject scroll = result.getAsJsonObject("nbt")
                        .getAsJsonObject("BlockEntityTag").getAsJsonArray("data")
                        .get(0).getAsJsonObject();
                String key = "create_tetra_handcraft/" + recipeId;
                assertEquals(key, scroll.get("key").getAsString(), recipeId + " scroll key");
                assertTrue(scroll.get("intricate").getAsBoolean(), recipeId + " must require the workbench-above placement");
                assertTrue(scroll.getAsJsonArray("schematics").asList().stream()
                        .anyMatch(value -> value.getAsString().equals("tetra:" + key)), recipeId + " schematic id");
            }
        }
    }

    private static Path resourcePath(String relativePath) {
        return Path.of("src", "main", "resources", "data", "tetra", relativePath);
    }

    private record ExpectedRecipe(String[] pattern, Map<String, String> ingredients) { }
}
