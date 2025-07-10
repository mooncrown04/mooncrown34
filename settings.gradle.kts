pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.recloudstream.com/releases")
    }

    plugins {
        id("org.jetbrains.kotlin.android") version "1.9.23"
        id("com.android.library") version "8.2.2"
        id("cloudstream") version "1.0.0" // Cloudstream plugin versiyonunu belirle (gerekirse sabit veya kaldır)
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.recloudstream.com/releases")
    }
}

rootProject.name = "mooncrown34"
include(":m3u")
