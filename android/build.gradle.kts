plugins {
    alias(libs.plugins.android.application)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.appcompat)
    implementation(libs.legacy.support.v4)
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
            java.srcDirs(
                "src/main/java",
                "../core/src/main/java"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/**"
            )
        }
    }
}

