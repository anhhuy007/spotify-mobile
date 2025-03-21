plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.spotifyclone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.spotifyclone"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.appcompat.v161)
    implementation(libs.material)
    implementation(libs.material.v1100)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)
    implementation(libs.converter.gson)
    implementation(libs.converter.gson.v210)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.logging.interceptor.v500alpha2)
    implementation(libs.adapter.rxjava2)
    implementation(libs.rxjava)
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.v132)
    implementation(libs.core.ktx)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.picasso)
    implementation(libs.circleimageview)

    // Firebase & authentication
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.google.firebase.auth)
    implementation(libs.play.services.auth)

    // Facebook SDK
    implementation(libs.facebook.android.sdk)
    implementation(libs.facebook.android.sdk.v1300)

    // UI & utilities
    implementation(libs.palette.ktx)
    implementation(libs.preference)
    implementation(libs.androidx.material)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // coordinatorlayoyt
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    // navigation fragment
    implementation ("androidx.navigation:navigation-fragment:2.7.0")
    implementation ("androidx.navigation:navigation-ui:2.7.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation ("com.google.android.material:material:1.10.0")

    // push notification FCM
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.core)
}