plugins {
    alias(libs.plugins.android.application)
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

    // For recycler view
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("com.squareup.retrofit2:retrofit:2.1.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.1.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

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

    // For extracting color
    implementation("androidx.palette:palette-ktx:1.0.0")

    //shaping image
    implementation("androidx.compose.material:material:1.7.5")

    //bottom sheet
    implementation ("com.google.android.material:material:1.11.0")


}