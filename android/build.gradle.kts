plugins {
    alias(libs.plugins.android.application)
}

dependencies {
    implementation(project(":core"))
    implementation(libs.appcompat)
    implementation(libs.legacy.support.v4)
}

android {
    namespace = project.group.toString()
    compileSdk = 36

    defaultConfig {
        minSdk = 29
        targetSdk = 36
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            res.srcDirs("../res")
            assets.srcDirs("../assets")
            java.srcDirs(
                "src/main/java"
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
