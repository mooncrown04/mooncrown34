pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.recloudstream.com/releases") } // <-- Bunu ekle
    }
}

rootProject.name = "mooncrown34"
include(":m3u")
