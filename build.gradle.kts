plugins {
    id("com.android.library") version "8.0.2" apply false
    kotlin("android") version "1.8.20" apply false
    id("cloudstream") apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.recloudstream.com/releases") }
    }
}

