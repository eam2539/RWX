plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core depends only on pure JVM/Kotlin APIs - no Android or desktop-specific libs
    implementation(libs.httpclient)
    implementation(libs.jackson.databind)
    api(libs.jvm.libp2p)
    api(libs.tomlkt)
}