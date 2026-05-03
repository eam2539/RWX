import org.gradle.api.tasks.bundling.Zip
import org.gradle.jvm.toolchain.JavaLanguageVersion.of
import java.util.Locale.getDefault

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
fun patchLinuxSharedObjects(rootDir: File) {
    if (osType != OSType.LINUX || !rootDir.exists()) return

    rootDir.walkTopDown()
        .filter { it.isFile && it.extension == "so" }
        .forEach { soFile ->
            runCatching {
                ProcessBuilder(
                    "patchelf",
                    "--clear-symbol-version",
                    "SUNWprivate_1.1",
                    soFile.absolutePath
                )
                    .inheritIO()
                    .start()
                    .waitFor()
            }
        }
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
        val buildDir = rocketConnectorNativeDir.get().asFile
        val soFile = buildDir.resolve("librocketConnector.so")
        val dllFile = buildDir.resolve("rocketConnector.dll")
        val dylibFile = buildDir.resolve("librocketConnector.dylib")
        !soFile.exists() && !dllFile.exists() && !dylibFile.exists()
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

// ======================== Tar/Zip Distribution ========================

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
            from(rocketConnectorNativeDir.map { it.dir("Release") }) {
                when (osType) {
                    OSType.WINDOWS -> include("*.dll")
                    OSType.LINUX -> include("*.so")
                    OSType.MACOS -> include("*.dylib")
                }
                into("lib/natives")
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
            from(layout.buildDirectory.dir("libs/natives/")) {
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
            from(rocketConnectorNativeDir.map { it.dir("Release") }) {
                when (osType) {
                    OSType.WINDOWS -> include("*.dll")
                    OSType.LINUX -> include("*.so")
                    OSType.MACOS -> include("*.dylib")
                }
            }
            into(nativesTarget)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        patchLinuxSharedObjects(nativesTarget)
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

        from(stagedDesktopDistDir) {
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
