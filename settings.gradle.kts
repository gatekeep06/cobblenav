enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
        gradlePluginPortal()
    }

    includeBuild("gradle/build-logic")
}

rootProject.name = "cobblenav"

include("common")
include("fabric")
include("neoforge")
