import internal.libs

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm")
}
val archives_name: String by project

base {
    // Set up a suffixed format for the mod jar names, e.g. `example-fabric`.
    archivesName.set("${archives_name}-${project.name}")
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenCentral()
    maven {
        name = "Cobblemon"
        url = uri("https://maven.impactdev.net/repository/development/")
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}