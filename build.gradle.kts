buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("com.google.gms:google-services:4.3.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven{setUrl("https://jitpack.io")}
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}