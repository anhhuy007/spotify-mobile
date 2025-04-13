// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

}
buildscript {
    repositories {
        google()
        mavenCentral()



    }
    dependencies {
        val nav_version = "2.7.5" // hoặc phiên bản mới nhất
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}



