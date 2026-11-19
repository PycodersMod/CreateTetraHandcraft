package com.pycoder.createtetrahandcraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TetraSpecializationDataContractTest {
    private static final String SCHEMATIC_ID = "tetra:create_tetra_handcraft/s01_press_hammer";
    private static final String IMPROVEMENT_KEY = "create_tetra_handcraft:press_hammer";

    @Test
    void s01UsesNativeHammerImprovementAndConsumesTemporaryOakLogPlaceholder() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s01_press_hammer.json");
        Path improvementPath = resource(
                "data/tetra/improvements/double/hammer/create_tetra_handcraft/s01_press_hammer.json");
        assertTrue(Files.exists(schematicPath), "missing S01 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S01 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s01_press_hammer",
                schematic.get("localizationKey").getAsString());
        assertEquals(List.of("double/head_left", "double/head_right"),
                stringList(schematic.getAsJsonArray("slots")));
        assertEquals(List.of("_left", "_right"), stringList(schematic.getAsJsonArray("keySuffixes")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());
        assertEquals("improvement", schematic.get("displayType").getAsString());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        assertEquals("tetra:and", requirement.get("type").getAsString());
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(nestedRequirementTypes.containsAll(List.of("tetra:or", "tetra:module", "tetra:not", "tetra:improvement")));
        assertTrue(requiredModules.containsAll(List.of("double/basic_hammer_left", "double/basic_hammer_right")));
        assertTrue(forbiddenImprovements.containsAll(List.of("double/warforged", IMPROVEMENT_KEY)));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements").get(IMPROVEMENT_KEY).getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals(1, improvement.size());
        assertEquals(IMPROVEMENT_KEY, improvement.get(0).getAsJsonObject().get("key").getAsString());

        JsonObject scrollRecipe = JsonParser.parseString(Files.readString(resource(
                "data/tetra/recipes/create_tetra_handcraft/s01_press_hammer.json"))).getAsJsonObject();
        JsonArray scrollData = scrollRecipe.getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains(SCHEMATIC_ID), "S01 scroll must reference the exact Tetra schematic resource key");
    }

    @Test
    void s02UsesNativeHammerImprovementAndConsumesTemporaryOakLogPlaceholder() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s02_mortar.json");
        Path improvementPath = resource(
                "data/tetra/improvements/double/hammer/create_tetra_handcraft/s02_mortar.json");
        assertTrue(Files.exists(schematicPath), "missing S02 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S02 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s02_mortar", schematic.get("localizationKey").getAsString());
        assertEquals(List.of("double/head_left", "double/head_right"),
                stringList(schematic.getAsJsonArray("slots")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(nestedRequirementTypes.containsAll(List.of("tetra:or", "tetra:module", "tetra:not", "tetra:improvement")));
        assertTrue(requiredModules.containsAll(List.of("double/basic_hammer_left", "double/basic_hammer_right")));
        assertTrue(forbiddenImprovements.containsAll(List.of(
                "double/warforged", "create_tetra_handcraft:press_hammer", "create_tetra_handcraft:mortar")));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements").get("create_tetra_handcraft:mortar").getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals(1, improvement.size());
        assertEquals("create_tetra_handcraft:mortar", improvement.get(0).getAsJsonObject().get("key").getAsString());

        JsonObject scrollRecipe = JsonParser.parseString(Files.readString(resource(
                "data/tetra/recipes/create_tetra_handcraft/s02_mortar.json"))).getAsJsonObject();
        JsonArray scrollData = scrollRecipe.getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains("tetra:create_tetra_handcraft/s02_mortar"));
    }

    @Test
    void s03RequiresMortarSpecializationBeforeCrusherAndUsesTemporaryOakLog() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s03_crusher_mortar.json");
        Path improvementPath = resource(
                "data/tetra/improvements/double/hammer/create_tetra_handcraft/s03_crusher_mortar.json");
        assertTrue(Files.exists(schematicPath), "missing S03 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S03 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s03_crusher_mortar", schematic.get("localizationKey").getAsString());
        assertEquals(List.of("double/head_left", "double/head_right"),
                stringList(schematic.getAsJsonArray("slots")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(nestedRequirementTypes.containsAll(List.of(
                "tetra:and", "tetra:or", "tetra:module", "tetra:improvement", "tetra:not")));
        assertTrue(requiredModules.containsAll(List.of("double/basic_hammer_left", "double/basic_hammer_right")));
        assertTrue(containsImprovementRequirement(requirement, "create_tetra_handcraft:mortar"),
                "S03 must require the existing S02 mortar specialization on the selected head");
        assertTrue(forbiddenImprovements.containsAll(List.of(
                "double/warforged", "create_tetra_handcraft:crusher_mortar")));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements")
                .get("create_tetra_handcraft:crusher_mortar").getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals("create_tetra_handcraft:crusher_mortar",
                improvement.get(0).getAsJsonObject().get("key").getAsString());
        JsonObject scrollRecipe = JsonParser.parseString(Files.readString(resource(
                "data/tetra/recipes/create_tetra_handcraft/s03_crusher_mortar.json"))).getAsJsonObject();
        JsonArray scrollData = scrollRecipe.getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains("tetra:create_tetra_handcraft/s03_crusher_mortar"));
    }

    @Test
    void s04UsesNativeSingleShovelImprovementAndConsumesTemporaryOakLog() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s04_stirring_staff.json");
        Path improvementPath = resource(
                "data/tetra/improvements/single/shovel/create_tetra_handcraft/s04_stirring_staff.json");
        assertTrue(Files.exists(schematicPath), "missing S04 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S04 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s04_stirring_staff", schematic.get("localizationKey").getAsString());
        assertEquals(List.of("single/head"), stringList(schematic.getAsJsonArray("slots")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());
        assertEquals("improvement", schematic.get("displayType").getAsString());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(requiredModules.contains("single/basic_shovel"),
                "S04 must start from the native Tetra basic shovel module");
        assertFalse(requiredModules.contains("single/spearhead"));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements")
                .get("create_tetra_handcraft:stirring_staff").getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals(1, improvement.size());
        assertEquals("create_tetra_handcraft:stirring_staff",
                improvement.get(0).getAsJsonObject().get("key").getAsString());

        JsonArray scrollData = JsonParser.parseString(Files.readString(resource(
                        "data/tetra/recipes/create_tetra_handcraft/s04_stirring_staff.json")))
                .getAsJsonObject().getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains("tetra:create_tetra_handcraft/s04_stirring_staff"));
    }

    @Test
    void s05UsesNativeSingleSpearheadImprovementAndConsumesTemporaryOakLog() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s05_pointer.json");
        Path improvementPath = resource(
                "data/tetra/improvements/single/spearhead/create_tetra_handcraft/s05_pointer.json");
        assertTrue(Files.exists(schematicPath), "missing S05 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S05 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s05_pointer", schematic.get("localizationKey").getAsString());
        assertEquals(List.of("single/head"), stringList(schematic.getAsJsonArray("slots")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());
        assertEquals("improvement", schematic.get("displayType").getAsString());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(requiredModules.contains("single/spearhead"),
                "S05 must start from the native Tetra spearhead module");
        assertFalse(requiredModules.contains("single/basic_shovel"));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements")
                .get("create_tetra_handcraft:pointer").getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals(1, improvement.size());
        assertEquals("create_tetra_handcraft:pointer", improvement.get(0).getAsJsonObject().get("key").getAsString());

        JsonArray scrollData = JsonParser.parseString(Files.readString(resource(
                        "data/tetra/recipes/create_tetra_handcraft/s05_pointer.json")))
                .getAsJsonObject().getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains("tetra:create_tetra_handcraft/s05_pointer"));
    }

    @Test
    void s06UsesNativeDoubleAxeImprovementAndConsumesTemporaryOakLog() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s06_hand_saw.json");
        Path improvementPath = resource(
                "data/tetra/improvements/double/axe/create_tetra_handcraft/s06_hand_saw.json");
        assertTrue(Files.exists(schematicPath), "missing S06 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S06 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s06_hand_saw", schematic.get("localizationKey").getAsString());
        assertEquals(List.of("double/head_left", "double/head_right"),
                stringList(schematic.getAsJsonArray("slots")));
        assertEquals(List.of("_left", "_right"), stringList(schematic.getAsJsonArray("keySuffixes")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());
        assertEquals("improvement", schematic.get("displayType").getAsString());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(requiredModules.containsAll(List.of("double/basic_axe_left", "double/basic_axe_right")),
                "S06 must use either native Tetra basic axe head module");
        assertTrue(forbiddenImprovements.containsAll(List.of(
                "double/warforged", "create_tetra_handcraft:hand_saw")));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements").get("create_tetra_handcraft:hand_saw").getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals(1, improvement.size());
        assertEquals("create_tetra_handcraft:hand_saw", improvement.get(0).getAsJsonObject().get("key").getAsString());

        JsonArray scrollData = JsonParser.parseString(Files.readString(resource(
                        "data/tetra/recipes/create_tetra_handcraft/s06_hand_saw.json")))
                .getAsJsonObject().getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains("tetra:create_tetra_handcraft/s06_hand_saw"));
    }

    @Test
    void s07UsesNativeDoubleSickleImprovementAndConsumesTemporaryOakLog() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s07_bellows.json");
        Path improvementPath = resource(
                "data/tetra/improvements/double/sickle/create_tetra_handcraft/s07_bellows.json");
        assertTrue(Files.exists(schematicPath), "missing S07 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S07 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s07_bellows", schematic.get("localizationKey").getAsString());
        assertEquals(List.of("double/head_left", "double/head_right"),
                stringList(schematic.getAsJsonArray("slots")));
        assertEquals(List.of("_left", "_right"), stringList(schematic.getAsJsonArray("keySuffixes")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());
        assertEquals("improvement", schematic.get("displayType").getAsString());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(requiredModules.containsAll(List.of("double/sickle_left", "double/sickle_right")),
                "S07 must use either native Tetra sickle head module");
        assertTrue(forbiddenImprovements.containsAll(List.of(
                "double/warforged", "create_tetra_handcraft:bellows")));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements").get("create_tetra_handcraft:bellows").getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals(1, improvement.size());
        assertEquals("create_tetra_handcraft:bellows", improvement.get(0).getAsJsonObject().get("key").getAsString());

        JsonArray scrollData = JsonParser.parseString(Files.readString(resource(
                        "data/tetra/recipes/create_tetra_handcraft/s07_bellows.json")))
                .getAsJsonObject().getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains("tetra:create_tetra_handcraft/s07_bellows"));
    }

    @Test
    void s08UsesNativeDoubleHoeImprovementAndConsumesTemporaryOakLog() throws IOException {
        Path schematicPath = resource("data/tetra/schematics/create_tetra_handcraft/s08_infusion_gun.json");
        Path improvementPath = resource(
                "data/tetra/improvements/double/hoe/create_tetra_handcraft/s08_infusion_gun.json");
        assertTrue(Files.exists(schematicPath), "missing S08 native Tetra schematic " + schematicPath);
        assertTrue(Files.exists(improvementPath), "missing S08 native Tetra improvement " + improvementPath);

        JsonObject schematic = JsonParser.parseString(Files.readString(schematicPath)).getAsJsonObject();
        assertEquals("create_tetra_handcraft/s08_infusion_gun", schematic.get("localizationKey").getAsString());
        assertEquals(List.of("double/head_left", "double/head_right"),
                stringList(schematic.getAsJsonArray("slots")));
        assertEquals(List.of("_left", "_right"), stringList(schematic.getAsJsonArray("keySuffixes")));
        assertEquals(1, schematic.get("materialSlotCount").getAsInt());
        assertEquals("improvement", schematic.get("displayType").getAsString());

        JsonObject requirement = schematic.getAsJsonObject("requirement");
        List<String> nestedRequirementTypes = new ArrayList<>();
        List<String> requiredModules = new ArrayList<>();
        List<String> forbiddenImprovements = new ArrayList<>();
        collectRequirements(requirement, nestedRequirementTypes, requiredModules, forbiddenImprovements);
        assertTrue(requiredModules.containsAll(List.of("double/hoe_left", "double/hoe_right")),
                "S08 must use either native Tetra hoe head module");
        assertTrue(forbiddenImprovements.containsAll(List.of(
                "double/warforged", "create_tetra_handcraft:infusion_gun")));

        JsonObject outcome = schematic.getAsJsonArray("outcomes").get(0).getAsJsonObject();
        JsonObject material = outcome.getAsJsonObject("material");
        assertEquals(List.of("minecraft:oak_log"), stringList(material.getAsJsonArray("items")));
        assertEquals(1, material.get("count").getAsInt());
        assertEquals("minecraft:iron", outcome.getAsJsonObject("requiredTools").get("hammer_dig").getAsString());
        assertEquals(0, outcome.getAsJsonObject("improvements").get("create_tetra_handcraft:infusion_gun").getAsInt());

        JsonArray improvement = JsonParser.parseString(Files.readString(improvementPath)).getAsJsonArray();
        assertEquals(1, improvement.size());
        assertEquals("create_tetra_handcraft:infusion_gun",
                improvement.get(0).getAsJsonObject().get("key").getAsString());

        JsonArray scrollData = JsonParser.parseString(Files.readString(resource(
                        "data/tetra/recipes/create_tetra_handcraft/s08_infusion_gun.json")))
                .getAsJsonObject().getAsJsonObject("result").getAsJsonObject("nbt")
                .getAsJsonObject("BlockEntityTag").getAsJsonArray("data");
        assertTrue(stringList(scrollData.get(0).getAsJsonObject().getAsJsonArray("schematics"))
                .contains("tetra:create_tetra_handcraft/s08_infusion_gun"));
    }

    private static void collectRequirements(JsonObject node, List<String> types,
                                            List<String> modules, List<String> forbiddenImprovements) {
        String type = node.get("type").getAsString();
        types.add(type);
        if ("tetra:module".equals(type)) {
            modules.add(node.get("module").getAsString());
        }
        if ("tetra:not".equals(type)) {
            JsonObject child = node.getAsJsonObject("requirement");
            if ("tetra:improvement".equals(child.get("type").getAsString())) {
                forbiddenImprovements.add(child.get("improvement").getAsString());
            }
            collectRequirements(child, types, modules, forbiddenImprovements);
        }
        if (node.has("requirement")) {
            collectRequirements(node.getAsJsonObject("requirement"), types, modules, forbiddenImprovements);
        }
        if (node.has("requirements")) {
            for (var child : node.getAsJsonArray("requirements")) {
                collectRequirements(child.getAsJsonObject(), types, modules, forbiddenImprovements);
            }
        }
    }

    private static boolean containsImprovementRequirement(JsonObject node, String improvement) {
        if ("tetra:improvement".equals(node.get("type").getAsString())
                && improvement.equals(node.get("improvement").getAsString())) {
            return true;
        }
        if (node.has("requirement") && containsImprovementRequirement(node.getAsJsonObject("requirement"), improvement)) {
            return true;
        }
        if (node.has("requirements")) {
            for (var child : node.getAsJsonArray("requirements")) {
                if (containsImprovementRequirement(child.getAsJsonObject(), improvement)) return true;
            }
        }
        return false;
    }

    private static List<String> stringList(JsonArray values) {
        return values.asList().stream().map(value -> value.getAsString()).toList();
    }

    private static Path resource(String relativePath) {
        return Path.of("src", "main", "resources", relativePath);
    }
}
