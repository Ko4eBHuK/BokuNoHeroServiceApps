plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.preachooda.assets"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "MAPTILER_API_KEY", project.property("MAPTILER_API_KEY") as String)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.properties["compose_version"] as String
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    val composeVersion = rootProject.extra["compose_version"] as String
    val navVersion = rootProject.extra["nav_version"] as String

    implementation(project(":domain"))

    // Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3-android:1.2.0")
    implementation("androidx.compose.material:material")
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // DI
    implementation("com.google.dagger:hilt-android:2.48")

    // Map
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("org.maplibre.gl:android-sdk:10.2.0")
}