@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        // gradlePluginPortal is last for faster builds
        // see: https://developer.android.com/studio/build/optimize-your-build#gradle_plugin_portal
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven(url = "https://central.sonatype.com/repository/maven-snapshots/")
    }
}

rootProject.name = "SmileID"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include("sample")
rootProject.children.forEach { it.buildFileName = "${it.name}.gradle.kts" }
