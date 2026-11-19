package com.pycoder.createtetrahandcraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateRecipeOverrideContractTest {
    private static final Map<String, ExpectedRecipe> RECIPES = Map.of(
            "mechanical_press", new ExpectedRecipe("S\nC\nI", "create:mechanical_press", Map.of(
                    "C", "item:create:brass_casing", "I", "tag:forge:storage_blocks/iron", "S", "item:create:shaft")),
            "millstone", new ExpectedRecipe("C\nS\nI", "create:millstone", Map.of(
                    "C", "item:create:cogwheel", "I", "tag:forge:stone", "S", "item:create:brass_casing")),
            "mechanical_mixer", new ExpectedRecipe("S\nC\nI", "create:mechanical_mixer", Map.of(
                    "C", "item:create:brass_casing", "I", "item:create:whisk", "S", "item:create:cogwheel")),
            "deployer", new ExpectedRecipe("B\nC\nI", "create:deployer", Map.of(
                    "B", "item:create:electron_tube", "C", "item:create:brass_casing", "I", "item:create:brass_hand")),
            "mechanical_saw", new ExpectedRecipe(" A \nAIA\n C ", "create:mechanical_saw", Map.of(
                    "A", "tag:forge:plates/iron", "C", "item:create:brass_casing", "I", "tag:forge:ingots/iron")),
            "encased_fan", new ExpectedRecipe("S\nA\nP", "create:encased_fan", Map.of(
                    "A", "item:create:brass_casing", "P", "item:create:propeller", "S", "item:create:shaft")),
            "spout", new ExpectedRecipe("T\nP", "create:spout", Map.of(
                    "P", "item:minecraft:dried_kelp", "T", "item:create:brass_casing"))
    );

    @Test
    void leavesCrushingWheelAndMechanicalCrafterRecipesUntouched() {
        assertFalse(Files.exists(resourcePath("mechanical_crafting/crushing_wheel.json")));
        assertFalse(Files.exists(resourcePath("crafting/kinetics/mechanical_crafter.json")));
    }

    @Test
    void pointerRecipeReplacesOnlyBrassHandWithAndesiteAlloy() throws java.io.IOException {
        Path path = Path.of("src", "main", "resources", "data", "create_hand_made", "recipes", "pointer.json");
        assertTrue(Files.exists(path), "missing Hand Made pointer recipe override " + path);
        try (InputStream stream = Files.newInputStream(path)) {
            JsonObject recipe = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            assertEquals("minecraft:crafting_shaped", recipe.get("type").getAsString());
            assertArrayEquals(new String[] {"  h", " s ", "s  "},
                    recipe.getAsJsonArray("pattern").asList().stream().map(element -> element.getAsString()).toArray(String[]::new));
            JsonObject key = recipe.getAsJsonObject("key");
            assertEquals("create:andesite_alloy", key.getAsJsonObject("h").get("item").getAsString());
            assertEquals("minecraft:stick", key.getAsJsonObject("s").get("item").getAsString());
            JsonObject result = recipe.getAsJsonObject("result");
            assertEquals("create_hand_made:pointer", result.get("item").getAsString());
            assertEquals(1, result.get("count").getAsInt());
        }
    }

    @Test
    void overridesOnlyTheApprovedMachineCasingAndPreservesRecipeStructure() {
        for (var entry : RECIPES.entrySet()) {
            Path path = resourcePath("crafting/kinetics/" + entry.getKey() + ".json");
            try (InputStream stream = Files.exists(path) ? Files.newInputStream(path) : null) {
                assertNotNull(stream, "missing Create recipe override " + path);
                JsonObject recipe = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
                ExpectedRecipe expected = entry.getValue();

                assertEquals("minecraft:crafting_shaped", recipe.get("type").getAsString(), entry.getKey());
                assertEquals("misc", recipe.get("category").getAsString(), entry.getKey());
                assertArrayEquals(expected.pattern().split("\\n", -1),
                        recipe.getAsJsonArray("pattern").asList().stream().map(element -> element.getAsString()).toArray(String[]::new),
                        entry.getKey() + " pattern");
                assertEquals(expected.result(), recipe.getAsJsonObject("result").get("item").getAsString(), entry.getKey() + " result");
                assertEquals(1, recipe.getAsJsonObject("result").has("count")
                        ? recipe.getAsJsonObject("result").get("count").getAsInt() : 1, entry.getKey() + " output count");

                JsonObject actualKey = recipe.getAsJsonObject("key");
                assertEquals(expected.key().keySet(), actualKey.keySet(), entry.getKey() + " ingredient symbols");
                for (var ingredient : expected.key().entrySet()) {
                    JsonObject actualIngredient = actualKey.getAsJsonObject(ingredient.getKey());
                    String actual = actualIngredient.has("item")
                            ? "item:" + actualIngredient.get("item").getAsString()
                            : "tag:" + actualIngredient.get("tag").getAsString();
                    assertEquals(ingredient.getValue(), actual, entry.getKey() + " ingredient " + ingredient.getKey());
                }
                assertEquals(true, recipe.get("show_notification").getAsBoolean(), entry.getKey() + " notification");
            } catch (java.io.IOException exception) {
                throw new AssertionError("could not read recipe override " + path, exception);
            }
        }
    }

    private static Path resourcePath(String recipePath) {
        return Path.of("src", "main", "resources", "data", "create", "recipes", recipePath);
    }

    private record ExpectedRecipe(String pattern, String result, Map<String, String> key) { }
}
