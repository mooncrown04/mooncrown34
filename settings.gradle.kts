pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://recloudstream.github.io/maven")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "cloudstream-plugin") {
                useModule("com.github.recloudstream.cloudstream:gradle:master-SNAPSHOT")
            }
        }
    }
}

rootProject.name = "mooncrown35"
include(":m3u")
