plugins {
    id("org.jetbrains.kotlin.android")
    id("com.android.library")
    id("cloudstream")
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Cloudstream veya diğer eklenti bağımlılıkları buraya eklenebilir
}
