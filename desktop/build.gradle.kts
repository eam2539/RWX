import java.util.Locale.getDefault

plugins {
    id("java")
    id("application")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

enum class PlatformType {
    WINDOWS_X64,
    WINDOWS_ARM64,
    MACOS_X64,
    MACOS_ARM64,
    LINUX_X64,
    LINUX_ARM64;
}

enum class OSType {
    WINDOWS,
    MACOS,
    LINUX
}

val os: String = System.getProperty("os.name").lowercase()
val arch: String = System.getProperty("os.arch").lowercase()
val platformType = when {
    os.contains("win") && (arch.contains("aarch") || arch.contains("arm")) -> PlatformType.WINDOWS_ARM64
    os.contains("win") -> PlatformType.WINDOWS_X64
    os.contains("mac") && (arch.contains("aarch") || arch.contains("arm")) -> PlatformType.MACOS_ARM64
    os.contains("mac") -> PlatformType.MACOS_X64
    os.contains("linux") && (arch.contains("aarch") || arch.contains("arm")) -> PlatformType.LINUX_ARM64
    else -> PlatformType.LINUX_X64
}
val osType = when (platformType) {
    PlatformType.WINDOWS_X64, PlatformType.WINDOWS_ARM64 -> OSType.WINDOWS
    PlatformType.MACOS_X64, PlatformType.MACOS_ARM64 -> OSType.MACOS
    PlatformType.LINUX_X64, PlatformType.LINUX_ARM64 -> OSType.LINUX
}
dependencies {
    implementation(project(":mod-api"))
    implementation(project(":core"))
    compileOnly(files("../libs/android.jar"))
    runtimeOnly(files("../libs/android.jar"))
    implementation(files("../libs/slick.jar"))
    implementation(files("../libs/jogg-0.0.7.jar"))
    implementation(files("../libs/jorbis-0.0.15.jar"))
    implementation(files("../libs/android-platform-lib.jar"))
    implementation(libs.httpclient)
    implementation(libs.jackson.databind)
    implementation(libs.webrtc.java)
    when (platformType) {
        PlatformType.WINDOWS_X64 -> runtimeOnly("dev.onvoid.webrtc:webrtc-java:${libs.versions.webrtcJavaVersion}:windows-x86_64")
        PlatformType.MACOS_X64 -> runtimeOnly("dev.onvoid.webrtc:webrtc-java:${libs.versions.webrtcJavaVersion}:macos-x86_64")
        PlatformType.MACOS_ARM64 -> runtimeOnly("dev.onvoid.webrtc:webrtc-java:${libs.versions.webrtcJavaVersion}:macos-aarch64")
        PlatformType.LINUX_X64 -> runtimeOnly("dev.onvoid.webrtc:webrtc-java:${libs.versions.webrtcJavaVersion}:linux-x86_64")
        PlatformType.WINDOWS_ARM64, PlatformType.LINUX_ARM64 -> {}
    }
    implementation(libs.lwjgl)
    implementation(libs.lwjgl.util)
    when (osType) {
        OSType.WINDOWS -> runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${libs.versions.lwjglVersion}:natives-windows")
        OSType.LINUX -> runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${libs.versions.lwjglVersion}:natives-linux")
        OSType.MACOS -> runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${libs.versions.lwjglVersion}:natives-osx")
    }
}

val appName: String by project

val appVersion: String = project.version.toString()
val mainClassName = "${project.group}.java.Main"

application {
    applicationName = appName
    mainClass = mainClassName
    applicationDefaultJvmArgs = listOf(
        "-Djava.library.path=lib/natives"
    )
}


val platformId = platformType.name.lowercase(getDefault()).replace("_", "-")

// ======================== rocketConnector Native Build ========================

val rocketConnectorNativeDir = layout.buildDirectory.dir("native/rocketConnector")
val rocketConnectorSourceDir = layout.projectDirectory.dir("../native/rocketConnector")


fun isRocketConnectorBuilt(buildDir: File): Boolean {
    return when (osType) {
        OSType.WINDOWS -> buildDir.resolve("rocketConnector.dll").exists()
        OSType.LINUX -> buildDir.resolve("librocketConnector.so").exists()
        OSType.MACOS -> buildDir.resolve("librocketConnector.dylib").exists()
    }
}

fun getLibrocketRoot(): String? = System.getenv("LIBROCKET_ROOT")

fun libRocketRuntimePatterns(): List<String> = when (osType) {
    OSType.WINDOWS -> listOf("Rocket*.dll", "libRocket*.dll")
    OSType.LINUX -> listOf("libRocket*.so", "libRocket*.so.*")
    OSType.MACOS -> listOf("libRocket*.dylib", "libRocket*.dylib.*")
}


val configureRocketConnectorNative by tasks.registering(Exec::class) {
    inputs.dir(rocketConnectorSourceDir)
    outputs.dir(rocketConnectorNativeDir)
    onlyIf {
        !isRocketConnectorBuilt(rocketConnectorNativeDir.get().asFile)
    }
    doFirst {
        val root = getLibrocketRoot() ?: throw GradleException("LIBROCKET_ROOT environment variable is not set")
        environment("LIBROCKET_ROOT", root)
    }
    val javaHome = System.getProperty("java.home").replace("\\", "/")
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
        !isRocketConnectorBuilt(rocketConnectorNativeDir.get().asFile)
    }
    doFirst {
        val root = getLibrocketRoot() ?: throw GradleException("LIBROCKET_ROOT environment variable is not set")
        environment("LIBROCKET_ROOT", root)
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
    }
}

// ======================== Run ========================

tasks.named<JavaExec>("run") {
    dependsOn(extractNatives, buildRocketConnectorNative)
    workingDir = project.file("..")
    val nativePaths = listOf(
        layout.buildDirectory.dir("libs/natives").get().asFile.absolutePath,
        rocketConnectorNativeDir.get().asFile.absolutePath
    ).joinToString(File.pathSeparator)
    jvmArgs("-Djava.library.path=$nativePaths")
}

// ======================== Tar/Zip Distribution ========================

tasks.named("distTar").configure { dependsOn(extractNatives, buildRocketConnectorNative) }
tasks.named("distZip").configure { dependsOn(extractNatives, buildRocketConnectorNative) }

distributions {
    main {
        contents {
            from(layout.buildDirectory.dir("libs/natives")) {
                when (osType) {
                    OSType.WINDOWS -> include("*.dll")
                    OSType.LINUX -> include("*.so")
                    OSType.MACOS -> include("*.dylib")
                }
                into("lib/natives")
            }
            from(rocketConnectorNativeDir) {
                when (osType) {
                    OSType.WINDOWS -> include("*.dll")
                    OSType.LINUX -> include("*.so")
                    OSType.MACOS -> include("*.dylib")
                }
                into("lib/natives")
            }
            getLibrocketRoot()?.let { root ->
                listOf(File(root).resolve("Build/build"), File(root).resolve("Build/build/Release"))
                    .filter { it.exists() }
                    .forEach { libDir ->
                        from(libDir) {
                            include(libRocketRuntimePatterns())
                            into("lib/natives")
                        }
                    }
            }

            from(project.file("../res")) { into("res") }
            from(project.file("../assets")) { into("assets") }
            from(project.file("../font")) { into("font") }
        }
    }
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
            from(layout.buildDirectory.dir("libs/natives")) {
                when (osType) {
                    OSType.WINDOWS -> include("*.dll")
                    OSType.LINUX -> include("*.so")
                    OSType.MACOS -> include("*.dylib")
                }
            }
            from(rocketConnectorNativeDir) {
                when (osType) {
                    OSType.WINDOWS -> include("*.dll")
                    OSType.LINUX -> include("*.so")
                    OSType.MACOS -> include("*.dylib")
                }
            }
            getLibrocketRoot()?.let { root ->
                listOf(File(root).resolve("Build/build"), File(root).resolve("Build/build/Release"))
                    .filter { it.exists() }
                    .forEach { libDir ->
                        from(libDir) {
                            include(libRocketRuntimePatterns())
                        }
                    }
            }

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
        val currentJavaHome = File(System.getProperty("java.home"))
        val currentJpackage = currentJavaHome.resolve("bin/$jpackageBinary")
        val jpackageExecutable = if (currentJpackage.exists()) {
            currentJpackage
        } else
            throw GradleException(
                "jpackage not found. Install a JDK that includes jpackage (17+)"
            )

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
        when (osType) {
            OSType.WINDOWS -> {
                val ico = iconDir.resolve("logo.ico")
                if (ico.exists()) args += listOf("--icon", ico.absolutePath)
            }

            OSType.MACOS -> {
                val icns = iconDir.resolve("logo.icns")
                if (icns.exists()) args += listOf("--icon", icns.absolutePath)
            }

            OSType.LINUX -> {
                val png = iconDir.resolve("logo.png")
                if (png.exists()) args += listOf("--icon", png.absolutePath)
            }
        }

        commandLine(jpackageExecutable.absolutePath, *args.toTypedArray())
    }

    doLast {
        val imageAppDir = jpackageImageDir.get().asFile.resolve(if (os.contains("mac")) "$appName.app" else appName)
        listOf("res", "assets", "font").forEach { resDir ->
            val srcDir = rootProject.file(resDir)
            if (srcDir.exists() && srcDir.isDirectory) {
                project.copy {
                    from(srcDir)
                    into(imageAppDir.resolve(resDir))
                }
            }
        }


    }
}

tasks.register<Sync>("stageDesktopDistribution") {
    group = "distribution"
    description = "Stage the jpackage app image and metadata for packaging."
    dependsOn(createJpackageImage)
    doFirst {
        val existing = stagedDesktopDistDir.get().asFile
        if (existing.exists()) {
            existing.deleteRecursively()
        }
    }
    from(jpackageImageDir.map { it.dir(if (os.contains("mac")) "$appName.app" else appName) })
    from(rootProject.file("LICENSE"))
    into(stagedDesktopDistDir.map { it.dir(appName) })


}

if (osType == OSType.WINDOWS) {
    tasks.register<Zip>("packageDesktopDistribution") {
        group = "distribution"
        description = "Create a zip archive for the current platform jpackage app image."
        dependsOn("stageDesktopDistribution")
        destinationDirectory.set(layout.buildDirectory.dir("distributions"))
        archiveFileName.set("$appName-$appVersion-$platformId-desktop.zip")

        from(stagedDesktopDistDir.map { it.dir(appName) }) {
            into(appName)
        }
    }
} else {
    tasks.register<Exec>("packageDesktopDistribution") {
        group = "distribution"
        description =
            "Create a zip archive for the current platform jpackage app image using system zip to preserve Unix permissions."
        dependsOn("stageDesktopDistribution")

        val stagedAppDir = stagedDesktopDistDir.get().asFile.resolve(appName)
        val outputZip = layout.buildDirectory.dir("distributions")
            .get().asFile.resolve("$appName-$appVersion-$platformId-desktop.zip")

        workingDir(stagedAppDir.parentFile)
        outputs.file(outputZip)

        doFirst {
            val outputDir = layout.buildDirectory.dir("distributions").get().asFile
            outputDir.mkdirs()
            if (outputZip.exists()) {
                outputZip.delete()
            }
        }

        commandLine(
            "zip", "-qr",
            outputZip.absolutePath,
            appName
        )
    }
}

// ================== P2P Config========================
tasks.register<JavaExec>("p2pDiagnostics") {
    group = "verification"
    description = "Runs headless P2P discovery/join diagnostics."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "com.corrodinggames.rts.gameFramework.p2p.P2PDiagnosticsMain"
    workingDir = rootProject.projectDir
    args(project.findProperty("p2pDiagArgs")?.toString()?.split(" ")?.filter { it.isNotBlank() } ?: listOf(
        "discover",
        "60000"
    ))
}

tasks.register<JavaExec>("p2pRendezvous") {
    group = "verification"
    description = "Runs a public RWX P2P rendezvous/relay node."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "com.corrodinggames.rts.gameFramework.p2p.P2PRendezvousMain"
    workingDir = rootProject.projectDir
    args(project.findProperty("p2pRendezvousArgs")?.toString()?.split(" ")?.filter { it.isNotBlank() }
        ?: listOf("4001"))
}

tasks.register("writeP2PRendezvousArgfile") {
    group = "verification"
    description = "Writes a Java argfile for running the RWX P2P rendezvous node."
    val outputFile = rootProject.layout.buildDirectory.file("p2p/rendezvous.args")
    outputs.file(outputFile)
    doLast {
        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        val classpath = sourceSets.main.get().runtimeClasspath.asPath
        file.writeText(
            listOf(
                "-Dfile.encoding=UTF-8",
                "-cp",
                classpath,
                "com.corrodinggames.rts.gameFramework.p2p.P2PRendezvousMain"
            ).joinToString(System.lineSeparator()) + System.lineSeparator(),
            Charsets.UTF_8
        )
        println(file.absolutePath)
    }
}
