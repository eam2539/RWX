import org.gradle.jvm.toolchain.JavaLanguageVersion.of

plugins {
    id("java")
    id("application")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(project(":core"))
    compileOnly(files("../libs/android.jar"))
    runtimeOnly(files("../libs/android.jar"))
    implementation(files("../libs/slick.jar"))
    implementation(files("../libs/jogg-0.0.7.jar"))
    implementation(files("../libs/jorbis-0.0.15.jar"))
    implementation(files("../libs/android-platform-lib.jar"))
    implementation(libs.httpclient)

    implementation(libs.jackson.databind)
    implementation(libs.lwjgl)
    implementation(libs.lwjgl.util)
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${libs.versions.lwjglVersion}:natives-linux")
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${libs.versions.lwjglVersion}:natives-windows")
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${libs.versions.lwjglVersion}:natives-osx")
}

val appName = "RWX"
val appVersion: String = project.version.toString()
val mainClassName = "com.corrodinggames.rts.java.Main"

application {
    mainClass = mainClassName
}

val os: String = System.getProperty("os.name").lowercase()
val arch: String = System.getProperty("os.arch").lowercase()
val platformId = when {
    os.contains("win") -> "windows-x64"
    os.contains("mac") && (arch.contains("aarch") || arch.contains("arm")) -> "macos-arm64"
    os.contains("mac") -> "macos-x64"
    os.contains("linux") && (arch.contains("aarch") || arch.contains("arm")) -> "linux-arm64"
    else -> "linux-x64"
}

// ======================== rocketConnector Native Build ========================

val rocketConnectorNativeDir = layout.buildDirectory.dir("native/rocketConnector")
val rocketConnectorSourceDir = layout.projectDirectory.dir("../native/rocketConnector")

val configureRocketConnectorNative by tasks.registering(Exec::class) {
    inputs.dir(rocketConnectorSourceDir)
    outputs.dir(rocketConnectorNativeDir)
    doFirst {
        val librocketRoot = System.getenv("LIBROCKET_ROOT")
            ?: throw GradleException("LIBROCKET_ROOT environment variable is not set")
        environment("LIBROCKET_ROOT", librocketRoot)
    }
    val javaHome = System.getProperty("java.home")
    var cmakeArgs = mutableListOf(
        "cmake",
        "-S", rocketConnectorSourceDir.asFile.absolutePath,
        "-B", rocketConnectorNativeDir.get().asFile.absolutePath,
        "-DJAVA_HOME=$javaHome"
    )
    val toolchainFile = System.getenv("CMAKE_TOOLCHAIN_FILE")
    if (toolchainFile != null) {
        cmakeArgs += "-DCMAKE_TOOLCHAIN_FILE=$toolchainFile"
    }
    val cmakeGenerator = System.getenv("CMAKE_GENERATOR")
    if (cmakeGenerator != null) {
        cmakeArgs += listOf("-G", cmakeGenerator)
    }
    commandLine(cmakeArgs)
}

val buildRocketConnectorNative by tasks.registering(Exec::class) {
    dependsOn(configureRocketConnectorNative)
    inputs.dir(rocketConnectorSourceDir)
    outputs.dir(rocketConnectorNativeDir)
    onlyIf {
        val buildDir = rocketConnectorNativeDir.get().asFile
        val soFile = buildDir.resolve("librocketConnector.so")
        val dllFile = buildDir.resolve("rocketConnector.dll")
        val dylibFile = buildDir.resolve("librocketConnector.dylib")
        val releaseDll = buildDir.resolve("Release/rocketConnector.dll")
        !soFile.exists() && !dllFile.exists() && !dylibFile.exists() && !releaseDll.exists()
    }
    doFirst {
        val librocketRoot = System.getenv("LIBROCKET_ROOT")
            ?: throw GradleException("LIBROCKET_ROOT environment variable is not set")
        environment("LIBROCKET_ROOT", librocketRoot)
    }
    commandLine("cmake", "--build", rocketConnectorNativeDir.get().asFile.absolutePath, "--config", "Release")
}

// ======================== Extract LWJGL Natives ========================

val extractNatives by tasks.registering(Sync::class) {
    into(layout.buildDirectory.dir("libs/natives"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.contains("natives-") }
            .map { zipTree(it) }
    })
}

// ======================== Jar ========================

tasks.named<Jar>("jar") {
    dependsOn(extractNatives, buildRocketConnectorNative, "compileJava", "compileKotlin")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(tasks.named<JavaCompile>("compileJava").map { it.outputs.files })
    from(tasks.named("compileKotlin").map { it.outputs.files })

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    manifest {
        attributes["Main-Class"] = mainClassName
        val classPath = configurations.runtimeClasspath.get()
            .filter { it.extension == "jar" && !it.name.contains("natives-") }
            .map { "lib/${it.name}" }
            .joinToString(" ")
        attributes["Class-Path"] = classPath
    }
}

// ======================== Run ========================

tasks.named<JavaExec>("run") {
    dependsOn(extractNatives, buildRocketConnectorNative)
    workingDir = project.file("..")
    val nativePaths = listOf(
        layout.buildDirectory.dir("libs/natives").get().asFile.absolutePath,
        rocketConnectorNativeDir.get().asFile.absolutePath,
        rocketConnectorNativeDir.get().asFile.resolve("Release").absolutePath
    ).joinToString(File.pathSeparator)
    jvmArgs("-Djava.library.path=$nativePaths")
}

// ======================== Zip Distribution ========================

val distributionLibsDir = layout.buildDirectory.dir("distribution-libs")

val prepareDistributionLibs by tasks.registering {
    dependsOn("extractNatives", buildRocketConnectorNative)
    val libsTarget = distributionLibsDir.get().asFile
    libsTarget.mkdirs()

    configurations.runtimeClasspath.get()
        .filter { it.extension == "jar" && !it.name.contains("natives-") }
        .forEach { file ->
            project.copy {
                from(file)
                into(libsTarget)
            }
        }
}

tasks.register<Zip>("distribution") {
    dependsOn(prepareDistributionLibs, "jar")
    archiveFileName.set("$appName-$appVersion.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))

    from(distributionLibsDir) { into("lib") }
    from(layout.buildDirectory.dir("libs/natives")) { into("lib/natives") }
    from(rocketConnectorNativeDir) {
        include("*.dll", "*.dylib", "*.so")
        into("lib/natives")
    }
    from(rocketConnectorNativeDir.map { it.dir("Release") }) {
        include("*.dll", "*.dylib", "*.so")
        into("lib/natives")
    }
    from(tasks.named<Jar>("jar").map { it.archiveFile }) { into("lib") }
    from(project.file("../res")) { into("res") }
    from(project.file("../assets")) { into("assets") }
    from(project.file("../font")) { into("font") }

    val cpJars = (listOf("lib/desktop-$appVersion.jar") +
            configurations.runtimeClasspath.get()
                .filter { it.extension == "jar" && !it.name.contains("natives-") }
                .map { "lib/${it.name}" })
        .joinToString(":")
    val winCpJars = cpJars.replace(":", ";")

    val scriptsDir = layout.buildDirectory.dir("scripts").get().asFile
    scriptsDir.mkdirs()

    val unixScriptFile = scriptsDir.resolve(appName)
    unixScriptFile.writeText(buildString {
        appendLine("#!/bin/bash")
        appendLine("SCRIPT_DIR=\"\$(cd \"\$(dirname \"\$0\")\" && pwd)\"")
        appendLine("cd \"\$SCRIPT_DIR\"")
        appendLine("java -Djava.library.path=lib/natives -cp \"$cpJars\" $mainClassName \"\$@\"")
    })
    unixScriptFile.setExecutable(true)

    val windowsScriptFile = scriptsDir.resolve("$appName.bat")
    windowsScriptFile.writeText(buildString {
        appendLine("@echo off")
        appendLine("set SCRIPT_DIR=%~dp0")
        appendLine("cd /d %SCRIPT_DIR%")
        appendLine("java -Djava.library.path=lib\\natives -cp \"$winCpJars\" $mainClassName %*")
    })

    from(unixScriptFile)
    from(windowsScriptFile)
}

// ======================== Jpackage Distribution ========================

val jpackageInputDir = layout.buildDirectory.dir("jpackage/input")
val jpackageImageDir = layout.buildDirectory.dir("jpackage/image")
val stagedDesktopDistDir = layout.buildDirectory.dir("jpackage/distributions/$platformId")

val stageJpackageInput by tasks.registering(Sync::class) {
    group = "distribution"
    description = "Stage application jars and natives for jpackage."
    dependsOn("jar", extractNatives, buildRocketConnectorNative)

    from(tasks.named<Jar>("jar"))
    from({
        configurations.runtimeClasspath.get()
            .filter { it.extension == "jar" && !it.name.contains("natives-") }
    })
    into(jpackageInputDir)

    doLast {
        val nativesTarget = jpackageInputDir.get().asFile.resolve("natives")
        nativesTarget.mkdirs()
        project.copy {
            from(layout.buildDirectory.dir("libs/natives"))
            into(nativesTarget)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        project.copy {
            from(rocketConnectorNativeDir) { include("*.dll", "*.dylib", "*.so") }
            from(rocketConnectorNativeDir.map { it.dir("Release") }) { include("*.dll", "*.dylib", "*.so") }
            into(nativesTarget)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}

val createJpackageImage by tasks.registering(Exec::class) {
    group = "distribution"
    description = "Create a jpackage app image for the current platform."
    dependsOn(stageJpackageInput)
    inputs.dir(jpackageInputDir)
    outputs.dir(jpackageImageDir)

    doFirst {
        delete(jpackageImageDir.get().asFile)

        val mainJarName = tasks.named<Jar>("jar").get().archiveFileName.get()
        val jpackageBinary = if (os.contains("win")) "jpackage.exe" else "jpackage"
        val jpackageExecutable = javaToolchains.launcherFor {
            this.languageVersion.set(of(21))
        }.get().metadata.installationPath.file("bin/$jpackageBinary").asFile

        val args = mutableListOf(
            "--type", "app-image",
            "--name", appName,
            "--app-version", appVersion,
            "--dest", jpackageImageDir.get().asFile.absolutePath,
            "--input", jpackageInputDir.get().asFile.absolutePath,
            "--main-jar", mainJarName,
            "--main-class", mainClassName,
            "--java-options", "-Dfile.encoding=UTF-8",
            "--java-options", "-Djava.library.path=\$APPDIR/natives"
        )

        val iconDir = file("src/main/resources/icons")
        when {
            os.contains("win") -> {
                val ico = iconDir.resolve("logo.ico")
                if (ico.exists()) args += listOf("--icon", ico.absolutePath)
            }

            os.contains("mac") -> {
                val icns = iconDir.resolve("logo.icns")
                if (icns.exists()) args += listOf("--icon", icns.absolutePath)
            }

            os.contains("linux") -> {
                val png = iconDir.resolve("logo.png")
                if (png.exists()) args += listOf("--icon", png.absolutePath)
            }
        }

        commandLine(jpackageExecutable.absolutePath, *args.toTypedArray())
    }
}

tasks.register<Sync>("stageDesktopDistribution") {
    group = "distribution"
    description = "Stage the jpackage app image and metadata for packaging."
    dependsOn(createJpackageImage)
    from(jpackageImageDir.map { it.dir(if (os.contains("mac")) "$appName.app" else appName) })
    from(rootProject.file("LICENSE"))
    into(stagedDesktopDistDir)
}

tasks.register<Zip>("packageDesktopDistribution") {
    group = "distribution"
    description = "Create a zip archive for the current platform jpackage app image."
    dependsOn("stageDesktopDistribution")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    archiveFileName.set("$appName-$appVersion-$platformId-desktop.zip")
    from(stagedDesktopDistDir)
}
