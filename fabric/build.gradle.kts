import org.jetbrains.kotlin.gradle.tasks.KotlinCompile



plugins {
    id("java")
    id("idea")
    id("maven-publish")
    id("fabric-loom") version "1.4-SNAPSHOT"
    kotlin("jvm") version "1.9.22"
}

base {
    archivesName = "${project.property("mod_name")}-fabric-${project.property("minecraft_version")}"
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.17+kotlin.1.9.22")

    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.1")
    implementation(project(":common"))
}



loom {
    if (project(":common").file("src/main/resources/${project.property("mod_id")}.accesswidener").exists()) {
        accessWidenerPath.set(project(":common").file("src/main/resources/${project.property("mod_id")}.accesswidener"))
    }

    mixin {
        defaultRefmapName = "${project.property("mod_id")}.refmap.json"
    }

    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }

        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}


tasks {
    withType<KotlinCompile> {
        source(project(":common").sourceSets.main.get().allSource)
    }

    withType<JavaCompile> {
        source(project(":common").sourceSets.main.get().allSource)
    }

    withType<Javadoc> {
        source(project(":common").sourceSets.main.get().allJava)
    }

    sourcesJar {
        from(project(":common").sourceSets.main.get().allSource)
    }

    processResources {
        from(project(":common").sourceSets.main.get().resources)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components.getByName("java"))
        }
    }

    repositories {
        maven {
            url = uri("file://" + System.getenv("local_maven"))
        }
    }
}