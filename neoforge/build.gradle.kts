plugins {
    alias(libs.plugins.cobblenav.convention.common)
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

configurations {
    getByName("developmentNeoForge").extendsFrom(common.get())
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

dependencies {
    neoForge(libs.neoforge)

    shadowBundle(projects.common) {
        targetConfiguration = "transformProductionNeoForge"
    }

    modImplementation(libs.cobblemon.neoforge)
    modCompileOnly(libs.cobblemon.mal.neoforge)
    modCompileOnly(libs.cobblemon.counter.neoforge)

    implementation(libs.kotlinforforge.neoforge) {
        exclude(group = "net.neoforged.fancymodloader", module = "loader")
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }
}

