plugins {
    id("com.android.library") apply false
    kotlin("jvm") version "X.Y.Z"
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
