plugins {
    id("com.android.library") version "8.2.2"
    kotlin("android") version "1.9.23"
    id("cloudstream") version "1.0.0"
}

cloudstream {
    language.set("tr")
    description.set("M3U Yayınları için canlı yayın sağlayıcısı")
    authors.set(listOf("mooncrown04"))
}

android {
    namespace = "com.mooncrown04"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.recloudstream.com/releases")
}
