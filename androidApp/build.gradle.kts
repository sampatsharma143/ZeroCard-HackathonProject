plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
    id("com.google.gms.google-services")

}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.shunyank.zerocard.android"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("com.airbnb.android:lottie:5.2.0")
    implementation("io.appwrite:sdk-for-android:0.6.1")
    implementation("com.google.firebase:firebase-auth:19.2.0")
    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation("com.github.romellfudi:FudiNFC:1.2.0")
//    implementation("com.github.kenglxn.QRGen:android:2.6.0")
    implementation("com.google.zxing:core:3.4.0")

    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")
    implementation("androidx.browser:browser:1.4.0")
    // Also declare the dependency for the Google Play services library and specify its version
}