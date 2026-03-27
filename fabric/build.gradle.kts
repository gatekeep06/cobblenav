plugins {
    alias(libs.plugins.cobblenav.convention.common)
}

architectury {
    platformSetupLoomIde()
    fabric()
}

configurations {
    getByName("developmentFabric").extendsFrom(common.get())
}

dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.fabric.api)

    modImplementation(libs.cobblemon.fabric)

    modCompileOnly(libs.cobblemon.mal.fabric)
    modCompileOnly(libs.cobblemon.counter.fabric)

    shadowBundle(projects.common) {
        targetConfiguration = "transformProductionFabric"
        isTransitive = false
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}

