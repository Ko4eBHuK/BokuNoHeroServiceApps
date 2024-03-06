plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(rootProject.properties["gson"] as String)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
}
