plugins {
    kotlin("android")
    id("com.android.library")
}

apply(plugin = "com.lagradost.cloudstream3.gradle")

cloudstream {
    language = "kotlin"
    main = "com.mooncrown04.M3UStreamProvider"
    status = 1  // 0 = Broken, 1 = Working, 2 = Beta
    description = "Türkiye için M3U canlı yayın sağlayıcısı"
    authors = listOf("mooncrown04")
}

android {
    namespace = "com.mooncrown04"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35
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

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation(kotlin("stdlib"))
}
