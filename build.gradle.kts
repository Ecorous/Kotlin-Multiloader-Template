import groovy.lang.Closure
import io.github.pacifistmc.forgix.plugin.ForgixMergeExtension.FabricContainer
import io.github.pacifistmc.forgix.plugin.ForgixMergeExtension.ForgeContainer
import java.text.SimpleDateFormat
import java.util.*

plugins {
    // Required for NeoGradle
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
    id("io.github.pacifistmc.forgix") version "1.2.6"
}

forgix {
    group = "com.example.examplemod"
    mergedJarName = "${project.property("mod_name")}-merged-${project.property("minecraft_version")}-${project.version}.jar"
    outputDir = "artifacts"

    forge(closureOf<ForgeContainer> {
        projectName = "neoforge"
        jarLocation = "build/libs/${project.property("mod_name")}-neoforge-${project.property("minecraft_version")}-${project.version}.jar"

        mixin("examplemod.mixins.json")
        mixin("examplemod.neoforge.mixins.json")
    } as Closure<ForgeContainer>) // this is a workaround

    fabric(closureOf<FabricContainer> {
        projectName = "fabric"
        jarLocation = "build/libs/${project.property("mod_name")}-fabric-${project.property("minecraft_version")}-${project.version}.jar"
    } as Closure<FabricContainer>) // this is a workaround


}

subprojects {
    apply(plugin = "java")

    extensions.getByType<JavaPluginExtension>().apply {
        toolchain.languageVersion = JavaLanguageVersion.of(17)
        withSourcesJar()
        withJavadocJar()
    }

    val copyJars by tasks.registering {

        dependsOn("build")
        doLast {
            copy {
                from("build/libs/${project.property("mod_name")}-neoforge-${project.property("minecraft_version")}-${project.version}.jar")
                into(rootProject.file("artifacts"))
            }
            copy {
                from("build/libs/${project.property("mod_name")}-fabric-${project.property("minecraft_version")}-${project.version}.jar")
                into(rootProject.file("artifacts"))
            }
        }
        finalizedBy(":mergeJars")
    }

    tasks {
        "build"(DefaultTask::class) {
            // run the copyJars task after the build task
            finalizedBy(copyJars)
        }
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
