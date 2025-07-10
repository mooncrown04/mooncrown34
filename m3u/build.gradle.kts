plugins {
    id("com.android.library")
    kotlin("android")
}

buildscript {
    repositories {
        maven("https://repo.recloudstream.com/releases")
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.lagradost:cloudstream:1.0.0")
    }
}

apply(plugin = "cloudstream")

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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Ekstra bağımlılık gerekiyorsa buraya
}
