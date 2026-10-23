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
}

dependencies {
    minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    implementation(fg.deobf("curse.maven:tetra-289712:8570756"))
    implementation(fg.deobf("curse.maven:mutil-351914:7772906"))
    implementation(fg.deobf("curse.maven:create-328085:7178761"))
    implementation(fg.deobf("curse.maven:create-hand-made-1712724:9239982"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

minecraft {
    mappings("official", minecraftVersion)
    runs {
        create("client") {
            workingDirectory(file("run"))
            property("forge.logging.console.level", "info")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods { create(modId) { source(sourceSets.main.get()) } }
        }
        create("server") {
            workingDirectory(file("run"))
            arg("nogui")
            property("forge.logging.console.level", "info")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods { create(modId) { source(sourceSets.main.get()) } }
        }
    }
}

tasks.test { useJUnitPlatform() }

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
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
