import java.text.SimpleDateFormat
import java.util.*

plugins {
    // Required for NeoGradle
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}

subprojects {
    apply(plugin = "java")

    extensions.getByType<JavaPluginExtension>().apply {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
        withSourcesJar()
        withJavadocJar()
    }

    tasks {
        "jar"(Jar::class) {
            val jar = this

            from(rootProject.file("LICENSE")) {
                rename { "${it}_${project.property("mod_name")}" }
            }

            manifest {
                attributes(
                    mapOf(
                        "Specification-Title" to project.property("mod_name"),
                        "Specification-Vendor" to project.property("mod_author"),
                        "Specification-Version" to jar.archiveVersion,
                        "Implementation-Title" to project.name,
                        "Implementation-Version" to jar.archiveVersion,
                        "Implementation-Vendor" to project.property("mod_author"),
                        "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                        "Timestamp" to System.currentTimeMillis(),
                        "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                        "Built-On-Minecraft" to project.property("minecraft_version")
                    )
                )
            }
        }

        "sourcesJar"(Jar::class) {
            from(rootProject.file("LICENSE")) {
                rename { "${it}_${project.property("mod_name")}" }
            }
        }

        "processResources"(ProcessResources::class) {
            val properties = mapOf(
                "version" to project.version,
                "group" to project.group,
                "minecraft_version" to project.property("minecraft_version"),
                "minecraft_version_range" to project.property("minecraft_version_range"),
                "fabric_version" to project.property("fabric_version"),
                "fabric_loader_version" to project.property("fabric_loader_version"),
                "mod_name" to project.property("mod_name"),
                "mod_author" to project.property("mod_author"),
                "mod_id" to project.property("mod_id"),
                "license" to project.property("license"),
                "description" to project.description,
                "neoforge_version" to project.property("neoforge_version"),
                "neoforge_loader_version_range" to project.property("neoforge_loader_version_range"),
                "credits" to project.property("credits")
            )

            filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "*.mixins.json")) {
                expand(properties)
            }

            inputs.properties(properties)
        }

        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release = 17
        }

        withType<GenerateModuleMetadata>().configureEach {
            enabled = false
        }
    }

    repositories {
        mavenCentral()

        maven {
            name = "Sponge / Mixin"
            url = uri("https://repo.spongepowered.org/repository/maven-public/")
        }
    }
}