plugins {
    id("com.android.library")
    kotlin("jvm") version "1.9.0"
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
}
