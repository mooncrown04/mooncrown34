pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.recloudstream.com/releases") }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "cloudstream") {
                useModule("com.lagradost:cloudstream:1.0.0")
            }
        }
    }
}

rootProject.name = "mooncrown34"
include(":m3u")
