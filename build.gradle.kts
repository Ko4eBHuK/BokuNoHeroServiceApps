// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("com.android.library") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
}

buildscript {
    extra.apply {
        set("compose_version", "1.5.1")
        set("nav_version", "2.7.7")
        set("room_version", "2.6.1")
        set("api_url", "\"http://localhost:8080/bokunohero-0.0.1-SNAPSHOT/\"")
        set("date_common_format", "\"dd-MM-yyyy HH:mm:ss\"")

        // Android dependencies
        set("androidCoreKtx", "androidx.core:core-ktx:1.12.0")

        // DI
        set("hiltDependency", "com.google.dagger:hilt-android:2.48")
        set("hiltCompiler", "com.google.dagger:hilt-compiler:2.48")

        // Map
        set("playServicesLocation", "com.google.android.gms:play-services-location:21.1.0")
        set("maplibre", "org.maplibre.gl:android-sdk:10.2.0")

        // Network dependencies
        set("gson", "com.squareup.retrofit2:converter-gson:2.5.0")

        // Development dependencies
        set("sshTool", "org.netbeans.external:com-jcraft-jsch:RELEASE190")
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}
