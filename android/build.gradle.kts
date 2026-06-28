import io.github.rwx.build.AssetListGenerationSupport

plugins {
    alias(libs.plugins.android.application)
}

val assetListGeneration = AssetListGenerationSupport.register(project)
val legacyAudioResourcesRoot = layout.buildDirectory.dir("generated/legacy-audio-res")
val syncLegacyAudioResources = tasks.register<Sync>("syncLegacyAudioResources") {
    val sourceDirectory = rootProject.layout.projectDirectory.dir("res/raw")
    from(sourceDirectory)
    into(legacyAudioResourcesRoot.map { directory -> directory.dir("raw") })
    doLast {
        val resourceNames = sourceDirectory.asFile.listFiles()
            .orEmpty()
            .filter(File::isFile)
            .map { file -> file.nameWithoutExtension }
            .distinct()
            .sorted()
        val keepFile = legacyAudioResourcesRoot.get().file("raw/keep.xml").asFile
        keepFile.writeText(
            """<resources xmlns:tools="http://schemas.android.com/tools" tools:keep="${resourceNames.joinToString(",") { "@raw/$it" }}" />
""",
        )
    }
}

val releaseVersionName = providers.gradleProperty("rwxVersionName")
    .orElse(providers.environmentVariable("RWX_VERSION_NAME"))
    .getOrElse(project.version.toString())
val releaseVersionCode = providers.gradleProperty("rwxVersionCode")
    .orElse(providers.environmentVariable("RWX_VERSION_CODE"))
    .orNull
    ?.toIntOrNull()
    ?: releaseVersionName.split('.', '-', '+')
        .take(3)
        .map { it.toIntOrNull() ?: 0 }
        .let { parts ->
            (parts.getOrElse(0) { 1 } * 10_000) +
                    (parts.getOrElse(1) { 0 } * 100) +
                    parts.getOrElse(2) { 0 }
        }

fun signingValue(environmentName: String, propertyName: String) =
    providers.gradleProperty(propertyName).orElse(providers.environmentVariable(environmentName))

val releaseKeystoreFile = signingValue("KEYSTORE_FILE", "rwxAndroidKeystoreFile")
val releaseKeystorePassword = signingValue("KEYSTORE_PASSWORD", "rwxAndroidKeystorePassword")
val releaseKeyAlias = signingValue("KEY_ALIAS", "rwxAndroidKeyAlias")
val releaseKeyPassword = signingValue("KEY_PASSWORD", "rwxAndroidKeyPassword")
val signingValues = listOf(
    releaseKeystoreFile,
    releaseKeystorePassword,
    releaseKeyAlias,
    releaseKeyPassword,
)
val releaseSigningConfigured = signingValues.all { it.isPresent }
val androidAbiSplitsEnabled = providers.gradleProperty("rwxAndroidAbiSplits")
    .map(String::toBoolean)
    .getOrElse(false)

if (signingValues.any { it.isPresent } && !releaseSigningConfigured) {
    throw GradleException(
        "Android release signing is only partially configured. Set KEYSTORE_FILE, " +
                "KEYSTORE_PASSWORD, KEY_ALIAS, and KEY_PASSWORD together.",
    )
}

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
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly(libs.junit.platform.launcher)
}

android {
    namespace = project.group.toString()
    compileSdk = 36

    defaultConfig {
        applicationId = project.group.toString()
        minSdk = 29
        targetSdk = 36
        versionCode = releaseVersionCode
        versionName = releaseVersionName
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
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

    splits {
        abi {
            isEnable = androidAbiSplitsEnabled
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64")
            isUniversalApk = true
        }
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            assets.srcDirs("../assets")
            res.directories.add(legacyAudioResourcesRoot.get().asFile.absolutePath)
        }
    }

    androidResources {
        // Android uses the committed MSDF atlases. Source fonts are only needed by the desktop atlas baker.
        ignoreAssetsPattern = "*.otf:*.ttf"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
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

tasks.named("preBuild") {
    dependsOn(syncLegacyAudioResources)
}

tasks.matching { task -> task.name.startsWith("merge") && task.name.endsWith("Assets") }
    .configureEach {
        dependsOn(assetListGeneration.task)
    }

tasks.matching { task -> task.name.contains("lintVital", ignoreCase = true) }
    .configureEach {
        dependsOn(assetListGeneration.task)
    }
