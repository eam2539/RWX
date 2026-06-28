plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    api(project(":mod-api"))
    compileOnly(libs.httpclient)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    api(libs.kool.core)
    api(libs.tomlkt)
    api(libs.koin.core)
    api(libs.jvm.libp2p)
    api(libs.kotlinx.serialization.json)
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(libs.httpclient)
}
