plugins {
    id("com.github.johnrengelman.shadow")
    id("cobblenav.base-convention")
}

val common: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

configurations {
    getByName("compileClasspath").extendsFrom(common)
    getByName("runtimeClasspath").extendsFrom(common)
}

dependencies {
    common(project(":common", "namedElements")) {
        isTransitive = false
    }
}

tasks {

    shadowJar {
        configurations = listOf(shadowBundle)
        archiveClassifier = "dev-shadow"
    }

    remapJar {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}

