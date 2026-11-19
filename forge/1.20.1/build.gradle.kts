import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    java
    id("net.minecraftforge.gradle") version "6.0.54"
}

fun prop(name: String): String = providers.gradleProperty(name).get()

val minecraftVersion = prop("minecraft_version")
val forgeVersion = prop("forge_version")
val modId = prop("mod_id")
val modGroupId = prop("mod_group_id")
val modVersion = prop("mod_version")
val gameTestRunDir = file("run/gametest-create-casing")

 group = modGroupId
version = modVersion

base { archivesName.set(modId) }

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net")
    maven("https://cursemaven.com")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://maven.createmod.net")
    maven("https://maven.ithundxr.dev/mirror")
}

dependencies {
    minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    implementation(fg.deobf("curse.maven:tetra-289712:8570756"))
    implementation(fg.deobf("curse.maven:mutil-351914:7772906"))
    implementation(fg.deobf("curse.maven:create-328085:7178761"))
    implementation(fg.deobf("curse.maven:create-hand-made-1712724:9239982"))
    compileOnly(fg.deobf("net.createmod.ponder:Ponder-Forge-1.20.1:1.0.91"))
    compileOnly(fg.deobf("com.tterrag.registrate:Registrate:MC1.20-1.3.3"))
    compileOnly("org.spongepowered:mixin:0.8.5")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

minecraft {
    mappings("official", minecraftVersion)
    runs {
        create("client") {
            workingDirectory(file("run"))
            arg("-mixin.config=create_tetra_handcraft.mixins.json")
            property("forge.logging.console.level", "info")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods { create(modId) { source(sourceSets.main.get()) } }
        }
        create("server") {
            workingDirectory(file("run"))
            arg("nogui")
            arg("-mixin.config=create_tetra_handcraft.mixins.json")
            property("forge.logging.console.level", "info")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods { create(modId) { source(sourceSets.main.get()) } }
        }
        create("gameTestServer") {
            workingDirectory(gameTestRunDir)
            arg("-mixin.config=create_tetra_handcraft.mixins.json")
            property("forge.logging.console.level", "info")
            property("forge.enabledGameTestNamespaces", modId)
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods { create(modId) { source(sourceSets.main.get()) } }
        }
    }
}

val prepareGameTestMods = tasks.register<Copy>("prepareGameTestMods") {
    from(configurations.runtimeClasspath)
    into(gameTestRunDir.resolve("mods"))
    include("*tetra-289712-8570756_mapped_official_1.20.1.jar")
    include("*mutil-351914-7772906_mapped_official_1.20.1.jar")
    include("*create-328085-7178761_mapped_official_1.20.1.jar")
    include("*create-hand-made-1712724-9239982_mapped_official_1.20.1.jar")
    rename { name -> name.replace("_mapped_official_1.20.1", "") }
}

val prepareGameTestStructures = tasks.register<Copy>("prepareGameTestStructures") {
    from(layout.projectDirectory.dir("gameteststructures"))
    into(gameTestRunDir.resolve("gameteststructures"))
}

tasks.configureEach {
    if (name == "runGameTestServer") {
        dependsOn(prepareGameTestMods, prepareGameTestStructures)
    }
}

tasks.test { useJUnitPlatform() }

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
    options.compilerArgs.add("-AreobfSrgFile=${file("build/createSrgToMcp/output.srg").absolutePath}")
    options.compilerArgs.add("-AoutRefMapFile=${file("build/resources/main/create_tetra_handcraft.refmap.json").absolutePath}")
}

val resourceProperties = mapOf(
    "mod_id" to modId,
    "mod_version" to modVersion,
    "mod_name" to prop("mod_name"),
    "mod_authors" to prop("mod_authors"),
    "mod_description" to prop("mod_description"),
    "mod_license" to prop("mod_license"),
    "forge_version_range" to prop("forge_version_range")
)

tasks.named<Copy>("processResources") {
    inputs.properties(resourceProperties)
    filesMatching("META-INF/mods.toml") { expand(resourceProperties) }
}

tasks.jar {
    manifest.attributes["MixinConfigs"] = "create_tetra_handcraft.mixins.json"
}
