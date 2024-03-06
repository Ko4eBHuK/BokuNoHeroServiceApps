plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.preachooda.bokunoheroservice"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.preachooda.bokunoheroservice"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "0.2"

        testInstrumentationRunner = "com.preachooda.bokunoheroservice.composeTestUtils.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "API_URL", rootProject.properties["api_url"] as String)
        buildConfigField("String", "MAPTILER_API_KEY", project.property("MAPTILER_API_KEY") as String)
        buildConfigField("String", "DATE_COMMON_FORMAT", rootProject.properties["date_common_format"] as String)
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    val roomVersion = rootProject.extra["room_version"] as String

    // Domain
    implementation(project(":domain"))
    implementation(project(":assets"))

    // Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material3:material3-android:1.2.0")
    implementation("androidx.compose.material:material")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.hilt:hilt-navigation-fragment:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")

    // DI
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.navigation:navigation-testing:$navVersion")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // DB
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")

    // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.48")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptTest("com.google.dagger:hilt-compiler:2.48")

    // Map
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("org.maplibre.gl:android-sdk:10.2.0")

    // CameraX core library using the camera2 implementation
    val cameraxVersion = "1.4.0-alpha04"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-video:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")

    // dev dependencies
    implementation("org.netbeans.external:com-jcraft-jsch:RELEASE190")
}
