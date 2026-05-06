plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    compileOnly(files("../libs/android.jar"))
    implementation(files("../libs/android-platform-lib.jar"))
    implementation(libs.httpclient)
    implementation(libs.jackson.databind)
    api(libs.jvm.libp2p)
    api(libs.tomlkt)
}

sourceSets {
    named("main") {
        resources.srcDirs("../assets", "../res", "../font")
    }
}
