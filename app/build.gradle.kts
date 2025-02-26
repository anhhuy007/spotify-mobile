plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.spotifyclone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.spotifyclone"
        minSdk = 24
        targetSdk = 34
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
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.auth)
    implementation(libs.facebook.android.sdk)
    implementation(libs.google.firebase.auth)
    implementation(libs.facebook.android.sdk.v1300)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation(libs.circleimageview)
    implementation(libs.glide)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
}