plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

dependencies {
    api(libs.kool.core)
}

val iniSpecSources = fileTree("src/main/kotlin/io/github/rwx/mod/api/specs") {
    include("*Spec.kt")
}
val generatedIniSpecDir = layout.buildDirectory.dir("generated/sources/iniSpecMetadata/main/kotlin")
val generateIniSpecMetadata by tasks.registering {
    inputs.files(iniSpecSources)
    outputs.dir(generatedIniSpecDir)

    doLast {
        val output = generatedIniSpecDir.get().file(
            "io/github/rwx/mod/api/GeneratedIniSpecKeys.kt",
        ).asFile
        output.parentFile.mkdirs()
        val classes = linkedMapOf<String, LinkedHashMap<String, String>>()
        iniSpecSources.files.sortedBy { it.name }.forEach { source ->
            var className: String? = null
            var pendingKey: String? = null
            var pendingKeyIsTemplate = false
            source.readLines().forEach { line ->
                Regex("data class ([A-Za-z0-9_]+)").find(line)?.let { match ->
                    className = match.groupValues[1]
                    classes.getOrPut(className) { linkedMapOf() }
                }
                val trimmed = line.trim()
                val marker = when {
                    trimmed.startsWith("// Template:") -> "// Template:"
                    trimmed.startsWith("// Directive:") -> "// Directive:"
                    trimmed.startsWith("// Example:") -> "// Example:"
                    else -> null
                }
                if (marker != null) {
                    var example = trimmed.substringAfter(marker).trim()
                    if (example.startsWith("eg:")) example = example.removePrefix("eg:").trim()
                    pendingKey = example.substringBefore(':').substringBefore(" - ").trim()
                    pendingKeyIsTemplate = marker != "// Example:"
                }
                Regex("var `?([A-Za-z0-9_]+)`?\\s*:").find(trimmed)?.let { match ->
                    val owner = checkNotNull(className) { "Missing data class for ${source.name}" }
                    val property = match.groupValues[1]
                    val candidate = pendingKey?.takeIf { it.isNotBlank() }
                    val normalizedProperty = property.filter(Char::isLetterOrDigit).lowercase()
                    val normalizedCandidate = candidate.orEmpty().filter(Char::isLetterOrDigit).lowercase()
                    classes.getValue(owner)[property] = if (
                        candidate != null && (pendingKeyIsTemplate || normalizedCandidate == normalizedProperty)
                    ) candidate else property
                    pendingKey = null
                    pendingKeyIsTemplate = false
                }
            }
        }
        output.writeText(buildString {
            appendLine("package io.github.rwx.mod.api")
            appendLine()
            appendLine("internal object GeneratedIniSpecKeys {")
            appendLine(
                "    val byClass: kotlin.collections.Map<String, " +
                        "kotlin.collections.Map<String, String>> = mapOf("
            )
            classes.forEach { (className, properties) ->
                appendLine("        \"$className\" to mapOf(")
                properties.forEach { (property, key) ->
                    val escaped = key.replace("\\", "\\\\").replace("\"", "\\\"")
                    appendLine("            \"$property\" to \"$escaped\",")
                }
                appendLine("        ),")
            }
            appendLine("    )")
            appendLine("}")
        })
    }
}

kotlin {
    jvmToolchain(25)
    sourceSets.named("main") {
        kotlin.srcDir(generatedIniSpecDir)
    }
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

tasks.named("compileKotlin") {
    dependsOn(generateIniSpecMetadata)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(generateIniSpecMetadata)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
