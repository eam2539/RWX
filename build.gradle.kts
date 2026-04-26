plugins {
    id("java")
    id("application")
    id("org.jetbrains.kotlin.jvm") version "2.0.21"
}

version = "1.0.0"
group = "com.corrodinggames.rts"

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/libp2p/jvm-libp2p/maven/")
    maven("https://artifacts.consensys.net/public/maven/maven/")
    maven("https://jitpack.io")
}



dependencies {
    compileOnly(files("libs/android.jar"))
    runtimeOnly(files("libs/android.jar"))
    implementation(files("libs/slick.jar"))
    implementation(files("libs/jogg-0.0.7.jar"))
    implementation(files("libs/jorbis-0.0.15.jar"))
    implementation(files("libs/android-platform-lib.jar"))
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("io.libp2p:jvm-libp2p:1.2.0-RELEASE")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("org.lwjgl.lwjgl:lwjgl:2.9.3")
    implementation("org.lwjgl.lwjgl:lwjgl_util:2.9.3")
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-linux")
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-windows")
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:2.9.3:natives-osx")
}

application {
    mainClass = "com.corrodinggames.rts.java.Main"
}

val rocketConnectorNativeDir = layout.buildDirectory.dir("native/rocketConnector")
val rocketConnectorSourceDir = layout.projectDirectory.dir("native/rocketConnector")

val configureRocketConnectorNative by tasks.registering(Exec::class) {
    inputs.dir(rocketConnectorSourceDir)
    outputs.dir(rocketConnectorNativeDir)
    doFirst {
        val librocketRoot = System.getenv("LIBROCKET_ROOT")
            ?: throw GradleException("LIBROCKET_ROOT environment variable is not set")
        environment("LIBROCKET_ROOT", librocketRoot)
    }
    commandLine("cmake", "-S", rocketConnectorSourceDir.asFile.absolutePath, "-B", rocketConnectorNativeDir.get().asFile.absolutePath)
}

val buildRocketConnectorNative by tasks.registering(Exec::class) {
    dependsOn(configureRocketConnectorNative)
    inputs.dir(rocketConnectorSourceDir)
    outputs.dir(rocketConnectorNativeDir)
    onlyIf {
        val soFile = rocketConnectorNativeDir.get().asFile.resolve("librocketConnector.so")
        val dllFile = rocketConnectorNativeDir.get().asFile.resolve("rocketConnector.dll")
        val dylibFile = rocketConnectorNativeDir.get().asFile.resolve("librocketConnector.dylib")
        !soFile.exists() && !dllFile.exists() && !dylibFile.exists()
    }
    doFirst {
        val librocketRoot = System.getenv("LIBROCKET_ROOT")
            ?: throw GradleException("LIBROCKET_ROOT environment variable is not set")
        environment("LIBROCKET_ROOT", librocketRoot)
    }
    commandLine("cmake", "--build", rocketConnectorNativeDir.get().asFile.absolutePath, "--config", "Release")
}

val extractNatives by tasks.registering(Sync::class) {
    into(layout.buildDirectory.dir("libs/natives"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.contains("natives-") }
            .map { zipTree(it) }
    })
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
}

tasks.named<Jar>("jar") {
    dependsOn(extractNatives, buildRocketConnectorNative, "compileJava", "compileKotlin")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(tasks.named<JavaCompile>("compileJava").map { it.outputs.files })
    from(tasks.named("compileKotlin").map { it.outputs.files })
    from(layout.buildDirectory.dir("libs/natives")) {
        into("natives")
    }
    from(rocketConnectorNativeDir) {
        include("*.dll", "*.dylib", "*.so")
        into("natives")
    }
    from(rocketConnectorNativeDir.map { it.dir("Release") }) {
        include("*.dll", "*.dylib", "*.so")
        into("natives")
    }
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
        val classPath = configurations.runtimeClasspath.get()
            .filter { it.extension == "jar" && !it.name.contains("natives-") }
            .map { "lib/${it.name}" }
            .joinToString(" ")
        attributes["Class-Path"] = classPath
    }
}

tasks.named<JavaExec>("run") {
    dependsOn(extractNatives, buildRocketConnectorNative)
    val nativePaths = listOf(
        layout.buildDirectory.dir("libs/natives").get().asFile.absolutePath,
        rocketConnectorNativeDir.get().asFile.absolutePath,
        rocketConnectorNativeDir.get().asFile.resolve("Release").absolutePath
    ).joinToString(File.pathSeparator)
    jvmArgs("-Djava.library.path=$nativePaths")
}

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
    archiveFileName.set("RWX-${version}.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))

    from(distributionLibsDir) {
        into("lib")
    }
    from(layout.buildDirectory.dir("libs/natives")) {
        into("lib/natives")
    }
    from(rocketConnectorNativeDir.map { it.dir("Release") }) {
        include("*.dll", "*.dylib", "*.so")
        into("lib/natives")
    }
    from(tasks.named<Jar>("jar").map { it.archiveFile }) {
        into("lib")
    }
    from(project.file("res")) {
        into("res")
    }
    from(project.file("assets")) {
        into("assets")
    }
    from(project.file("font")){
        into("font")
    }

    // Generate launch scripts
    val cpJars = (listOf("lib/RWX-${version}.jar") +
        configurations.runtimeClasspath.get()
            .filter { it.extension == "jar" && !it.name.contains("natives-") }
            .map { "lib/${it.name}" })
        .joinToString(":")

    val winCpJars = cpJars.replace(":", ";")

    val unixScript = buildString {
        appendLine("#!/bin/bash")
        appendLine("# RWX Launcher")
        appendLine("SCRIPT_DIR=\"\$(cd \"\$(dirname \"\$0\")\" && pwd)\"")
        appendLine("cd \"\$SCRIPT_DIR\"")
        appendLine("java -Djava.library.path=lib/natives -cp \"$cpJars\" com.corrodinggames.rts.java.Main \"\$@\"")
    }
    val windowsScript = buildString {
        appendLine("@echo off")
        appendLine("REM RWX Launcher")
        appendLine("set SCRIPT_DIR=%~dp0")
        appendLine("cd /d %SCRIPT_DIR%")
        appendLine("java -Djava.library.path=lib\\natives -cp \"$winCpJars\" com.corrodinggames.rts.java.Main %*")
    }

    val scriptsDir = layout.buildDirectory.dir("scripts").get().asFile
    scriptsDir.mkdirs()

    val unixScriptFile = scriptsDir.resolve("RWX")
    unixScriptFile.writeText(unixScript)
    unixScriptFile.setExecutable(true)

    val windowsScriptFile = scriptsDir.resolve("RWX.bat")
    windowsScriptFile.writeText(windowsScript)

    from(unixScriptFile)
    from(windowsScriptFile)
}
