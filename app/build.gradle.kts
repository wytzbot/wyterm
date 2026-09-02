plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.wyte.wyterm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wyte.wyterm"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
    }
}

dependencies {
    implementation("com.android.billingclient:billing-ktx:8.0.0")
}
