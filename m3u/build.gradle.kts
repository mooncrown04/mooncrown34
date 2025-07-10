plugins {
    id("com.android.library")
    kotlin("android")
    id("cloudstream") // CloudStream eklentisi burada
}

cloudstream {
    language.set("tr") // Eklenti dili (Türkçe)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
