plugins {
    alias(libs.plugins.android.application)
}

dependencies {
    implementation(project(":core"))
}

android {
    namespace = "com.corrodinggames.rts.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        targetSdk = 36
    }

    sourceSets {
        named("main") {
            resources.srcDirs("../assets", "../res", "../font")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

