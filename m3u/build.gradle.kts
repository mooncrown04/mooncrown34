plugins {
    kotlin("android")
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
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-Xno-call-assertions",
            "-Xno-param-assertions",
            "-Xno-receiver-assertions"
        )
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jsoup:jsoup:1.18.3")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.github.recloudstream.cloudstream:cloudstream3:3.3.1")
}
