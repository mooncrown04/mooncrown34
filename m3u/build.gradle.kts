plugins {
    id("com.android.library")
    id("kotlin-android")
}

apply(plugin = "com.lagradost.cloudstream3.gradle")

cloudstream {
    language = "kotlin"
    mainClass = "com.mooncrown04.M3UStreamProvider"
    status = 1 // 0 = Broken, 1 = Working, 2 = Beta
    description = "Türkiye için M3U canlı yayın sağlayıcısı"
    authors = listOf("mooncrown04")
}

dependencies {
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
