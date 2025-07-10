// root build.gradle.kts
plugins {
    kotlin("android") version "1.9.10" apply false
    id("com.android.library") apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.recloudstream.com/releases")
    }
}
