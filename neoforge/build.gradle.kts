import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("idea")
    id("maven-publish")
    id("net.neoforged.gradle.userdev") version "7.0.41"
    id("java-library")
    kotlin("jvm") version "1.9.22"
}

jarJar.enable()

base {
    archivesName = "${project.property("mod_name")}-neoforge-${project.property("minecraft_version")}"
}

if (file("src/main/resources/META-INF/accesstransformer.cfg").exists()) {
    minecraft.accessTransformers.file(file("src/main/resources/META-INF/accesstransformer.cfg"))
}

repositories {
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

runs {
    configureEach {
        modSource(project.sourceSets.main.get())
    }

    create("client") {
        systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id") as String)
    }

    create("server") {
        systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id") as String)
        programArgument("--nogui")
    }

    create("gameTestServer") {
        systemProperty("neoforge.enabledGameTestNamespaces", project.property("mod_id") as String)
    }

    create("data") {
        programArguments.addAll(listOf(
            "--mod", project.property("mod_id") as String,
            "--all",
            "--output", file("src/generated/resources").absolutePath,
            "--existing", file("src/main/resources/").absolutePath
        ))
    }
}

sourceSets.main.configure {
    resources { srcDir("src/generated/resources") }
}

dependencies {
    implementation("net.neoforged:neoforge:${project.property("neoforge_version")}")
    implementation("thedarkcolour:kotlinforforge-neoforge:4.10.0")
    compileOnly(project(":common"))
    jarJar(project(":common"))
}

val notNeoGradleTask = Spec<Task> { !it.name.startsWith("neo") }

tasks {
    withType<KotlinCompile>().matching(notNeoGradleTask).configureEach {
        source(project(":common").sourceSets.main.get().allSource)
    }
    withType<JavaCompile>().matching(notNeoGradleTask).configureEach {
        source(project(":common").sourceSets.main.get().allSource)
    }

    withType<Javadoc>().matching(notNeoGradleTask).configureEach {
        source(project(":common").sourceSets.main.get().allJava)
    }

    sourcesJar {
        from(project(":common").sourceSets.main.get().allSource)
    }

    withType<ProcessResources>().matching(notNeoGradleTask).configureEach {
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