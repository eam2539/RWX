import io.github.rwx.build.AssetListGenerationSupport

plugins {
    alias(libs.plugins.android.application)
}

val assetListGeneration = AssetListGenerationSupport.register(project)

val releaseVersionCode =
     project.version.toString().split('.', '-', '+')
        .take(3)
        .map { it.toIntOrNull() ?: 0 }
        .let { parts ->
            (parts[0] * 10_000) +
                    (parts[1] * 100) +
                    parts[2]
        }

fun signingValue(environmentName: String, propertyName: String) =
    providers.gradleProperty(propertyName).orElse(providers.environmentVariable(environmentName))

val releaseKeystoreFile = signingValue("KEYSTORE_FILE", "androidKeystoreFile")
val releaseKeystorePassword = signingValue("KEYSTORE_PASSWORD", "androidKeystorePassword")
val releaseKeyAlias = signingValue("KEY_ALIAS", "androidKeyAlias")
val releaseKeyPassword = signingValue("KEY_PASSWORD", "androidKeyPassword")
val signingValues = listOf(
    releaseKeystoreFile,
    releaseKeystorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
)
val releaseSigningConfigured = signingValues.all { it.isPresent }

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.android.webrtc)
    implementation(libs.koin.android)
    implementation(libs.timber)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly(libs.junit.platform.launcher)
}

android {
    namespace = project.group.toString()
    compileSdk = 36
    useLibrary("org.apache.http.legacy")

    defaultConfig {
        applicationId = project.group.toString()
        minSdk = 28
        targetSdk = 36
        versionCode = releaseVersionCode
        versionName = project.version.toString()
        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = file(releaseKeystoreFile.get())
                storePassword = releaseKeystorePassword.get()
                keyAlias = releaseKeyAlias.get()
                keyPassword = releaseKeyPassword.get()
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (releaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            assets.directories.add("../assets")
        }
    }

    androidResources {
        ignoreAssetsPattern = "*.otf:*.ttf"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
        resources {
            excludes += listOf(
                "META-INF/**"
            )
        }
    }
}

tasks.matching { task -> task.name.startsWith("merge") && task.name.endsWith("Assets") }
    .configureEach {
        dependsOn(assetListGeneration.task)
    }

tasks.matching { task -> task.name.contains("lintVital", ignoreCase = true) }
    .configureEach {
        dependsOn(assetListGeneration.task)
    }
