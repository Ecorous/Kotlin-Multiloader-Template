plugins {
    id("idea")
    id("java")
    id("maven-publish")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
    kotlin("jvm") version "1.9.22"
}

base {
    archivesName = "${project.property("mod_name")}-common-${project.property("minecraft_version")}"
}

minecraft {
    version(project.property("minecraft_version") as String)

    if (file("src/main/resources/${project.property("mod_id")}.accesswidener").exists()) {
        accessWideners(file("src/main/resources/${project.property("mod_id")}.accesswidener"))
    }
}

dependencies {
    compileOnly(group = "org.spongepowered", name = "mixin", version = "0.8.5")
    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.1")
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