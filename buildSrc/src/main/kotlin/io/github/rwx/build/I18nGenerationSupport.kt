package io.github.rwx.build

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import java.io.File

enum class I18nInputFormat(
    val id: String,
    val extension: String,
) {
    TOML("toml", "toml"),
    PROPERTIES("properties", "properties");

    companion object {
        fun parse(rawValue: String): I18nInputFormat {
            val normalized = rawValue.trim().lowercase()
            return entries.firstOrNull { it.id == normalized || it.extension == normalized }
                ?: error("Unsupported i18n input format: $rawValue. Supported values: toml, properties")
        }
    }
}

open class I18nGenerationExtension {
    var baseName: String = "ui"
    var inputFormat: String = I18nInputFormat.TOML.id
    var inputDir: String = "assets/i18n"
    var outputFile: String = "core/src/main/kotlin/io/github/rwx/i18n/I18n.kt"
}

data class LocaleBundle(
    val localeTag: String,
    val entries: LinkedHashMap<String, String>,
)

private data class I18nInputConfig(
    val baseName: String,
    val inputFormat: I18nInputFormat,
    val inputDir: File,
    val outputFile: File,
) {
    val filePattern: Regex = Regex("^${Regex.escape(baseName)}\\.(.+)\\.${Regex.escape(inputFormat.extension)}$")
    val fileGlob: String = "$baseName.*.${inputFormat.extension}"
}

private class I18nTreeNode(
    val segment: String,
) {
    val children = linkedMapOf<String, I18nTreeNode>()
    var keyPath: String? = null
}

object I18nGenerationSupport {
    private val kotlinKeywords = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface",
        "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias",
        "typeof", "val", "var", "when", "while", "by", "catch", "constructor", "delegate", "dynamic", "field",
        "file", "finally", "get", "import", "init", "param", "property", "receiver", "set", "setparam", "where",
        "actual", "abstract", "annotation", "companion", "const", "crossinline", "data", "enum", "expect",
        "external", "final", "infix", "inline", "inner", "internal", "lateinit", "noinline", "open", "operator",
        "out", "override", "private", "protected", "public", "reified", "sealed", "suspend", "tailrec", "value",
        "vararg",
    )

    fun register(project: Project): TaskProvider<Task> {
        val extension = project.extensions.create("i18nGeneration", I18nGenerationExtension::class.java)
        return project.tasks.register("generateI18nSources") {
            description = "Generate the hierarchical Kotlin I18n object from locale TOML or properties files."
            inputs.files(project.providers.provider {
                val config = resolveConfig(project, extension)
                project.fileTree(config.inputDir) {
                    include(config.fileGlob)
                }
            })
            outputs.file(project.providers.provider { resolveConfig(project, extension).outputFile })
            doLast {
                generate(project, extension)
            }
        }
    }

    private fun generate(project: Project, extension: I18nGenerationExtension) {
        val config = resolveConfig(project, extension)

        require(config.inputDir.exists() && config.inputDir.isDirectory) {
            "I18n input directory not found: ${config.inputDir.absolutePath}"
        }

        val localeFiles = config.inputDir.listFiles { file -> file.isFile && config.filePattern.matches(file.name) }
            ?.sortedBy { it.name }
            .orEmpty()

        require(localeFiles.isNotEmpty()) {
            "No ${config.inputFormat.id} i18n files found in ${config.inputDir.absolutePath}. Expected files like ${config.baseName}.en.${config.inputFormat.extension}"
        }

        val bundles = localeFiles.map { file -> parseLocaleBundle(file, config) }
        validateLocaleBundles(bundles)

        config.outputFile.parentFile.mkdirs()
        config.outputFile.writeText(generateI18nSourceContent(bundles))
        project.logger.lifecycle("Generated I18n.kt from ${bundles.size} ${config.inputFormat.id} locale files.")
    }

    private fun resolveConfig(project: Project, extension: I18nGenerationExtension): I18nInputConfig {
        val baseName = project.findProperty("i18n.baseName")
            ?.toString()
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: extension.baseName.trim().takeIf { it.isNotEmpty() }
            ?: "ui"
        val inputFormat = project.findProperty("i18n.inputFormat")
            ?.toString()
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: extension.inputFormat.trim().takeIf { it.isNotEmpty() }
            ?: I18nInputFormat.TOML.id
        val inputDir = project.findProperty("i18n.inputDir")
            ?.toString()
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: extension.inputDir.trim().takeIf { it.isNotEmpty() }
            ?: "assets/i18n"
        val outputFile = project.findProperty("i18n.outputFile")
            ?.toString()
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: extension.outputFile.trim().takeIf { it.isNotEmpty() }
            ?: "core/src/main/kotlin/io/github/rwx/i18n/I18n.kt"
        return I18nInputConfig(
            baseName = baseName,
            inputFormat = I18nInputFormat.parse(inputFormat),
            inputDir = project.rootProject.file(inputDir),
            outputFile = project.rootProject.file(outputFile),
        )
    }

    private fun parseLocaleBundle(file: File, config: I18nInputConfig): LocaleBundle {
        val match = config.filePattern.matchEntire(file.name)
            ?: error("Unsupported i18n filename for format ${config.inputFormat.id}: ${file.name}")
        val entries = when (config.inputFormat) {
            I18nInputFormat.TOML -> I18nFileParsers.parseToml(file)
            I18nInputFormat.PROPERTIES -> I18nFileParsers.parseProperties(file)
        }
        return LocaleBundle(
            localeTag = match.groupValues[1],
            entries = entries,
        )
    }

    private fun validateLocaleBundles(bundles: List<LocaleBundle>) {
        val baseline = bundles.first()
        bundles.drop(1).forEach { bundle ->
            val missingKeys = baseline.entries.keys - bundle.entries.keys
            val extraKeys = bundle.entries.keys - baseline.entries.keys
            require(missingKeys.isEmpty() && extraKeys.isEmpty()) {
                buildString {
                    append("Locale file ${bundle.localeTag} is out of sync with ${baseline.localeTag}.")
                    if (missingKeys.isNotEmpty()) {
                        append(" Missing keys: ${missingKeys.sorted().joinToString()}")
                    }
                    if (extraKeys.isNotEmpty()) {
                        append(" Extra keys: ${extraKeys.sorted().joinToString()}")
                    }
                }
            }
        }
    }

    private fun sanitizeIdentifier(segment: String): String {
        val cleaned = buildString {
            segment.forEach { char ->
                append(if (char.isLetterOrDigit() || char == '_') char else '_')
            }
        }.ifBlank { "_" }
        val normalized = if (cleaned.first().isDigit()) "_$cleaned" else cleaned
        return if (normalized in kotlinKeywords) "`$normalized`" else normalized
    }

    private fun escapeKotlinString(value: String): String = buildString {
        value.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '$' -> append("\\$")
                else -> append(char)
            }
        }
    }

    private fun buildI18nTree(keys: Collection<String>): I18nTreeNode {
        val root = I18nTreeNode("<root>")
        keys.sorted().forEach { key ->
            var current = root
            key.split('.')
                .filter { it.isNotBlank() }
                .forEach { segment ->
                    current = current.children.getOrPut(segment) { I18nTreeNode(segment) }
                }
            require(current.keyPath == null) { "Duplicate i18n key detected: $key" }
            current.keyPath = key
        }
        return root
    }

    private fun appendEntryInitializer(
        builder: StringBuilder,
        indent: String,
        keyPath: String,
        bundles: List<LocaleBundle>,
        firstLineIndent: String = indent,
    ) {
        builder.append(firstLineIndent).append("entry(\n")
        builder.append(indent).append("    key = \"").append(escapeKotlinString(keyPath)).append("\",\n")
        builder.append(indent).append("    translations = linkedMapOf(\n")
        bundles.forEach { bundle ->
            val value = bundle.entries.getValue(keyPath)
            builder.append(indent)
                .append("        \"")
                .append(escapeKotlinString(bundle.localeTag))
                .append("\" to \"")
                .append(escapeKotlinString(value))
                .append("\",\n")
        }
        builder.append(indent).append("    ),\n")
        builder.append(indent).append(")")
    }

    private fun appendTreeNode(
        builder: StringBuilder,
        node: I18nTreeNode,
        indent: String,
        bundles: List<LocaleBundle>,
    ) {
        node.children.toSortedMap().values.forEach { child ->
            val identifier = sanitizeIdentifier(child.segment)
            val hasValue = child.keyPath != null
            val hasChildren = child.children.isNotEmpty()
            when {
                hasValue && !hasChildren -> {
                    builder.append(indent).append("val ").append(identifier).append(" = ")
                    appendEntryInitializer(builder, indent, child.keyPath!!, bundles, firstLineIndent = "")
                    builder.append("\n\n")
                }

                hasValue && hasChildren -> {
                    builder.append(indent).append("object ").append(identifier).append(" : I18nNode(\n")
                    appendEntryInitializer(builder, indent + "    ", child.keyPath!!, bundles)
                    builder.append("\n")
                    builder.append(indent).append(") {\n")
                    appendTreeNode(builder, child, indent + "    ", bundles)
                    builder.append(indent).append("}\n\n")
                }

                else -> {
                    builder.append(indent).append("object ").append(identifier).append(" {\n")
                    appendTreeNode(builder, child, indent + "    ", bundles)
                    builder.append(indent).append("}\n\n")
                }
            }
        }
    }

    private fun generateI18nSourceContent(bundles: List<LocaleBundle>): String {
        val keys = bundles.first().entries.keys
        val tree = buildI18nTree(keys)
        val defaultLocaleTag = bundles.firstOrNull { it.localeTag.equals("en", ignoreCase = true) }?.localeTag
            ?: bundles.first().localeTag

        return buildString {
            appendLine("package io.github.rwx.i18n")
            appendLine()
            appendLine("import java.util.Locale")
            appendLine()
            appendLine("// Auto-generated file - Do not modify")
            appendLine("object I18n {")
            appendLine("    const val defaultLocaleTag: String = \"${escapeKotlinString(defaultLocaleTag)}\"")
            appendLine()
            appendLine("    @Volatile")
            appendLine("    var currentLocale: Locale = Locale.getDefault()")
            appendLine("        private set")
            appendLine()
            appendLine("    val supportedLocaleTags: Set<String> = linkedSetOf(")
            bundles.forEach { bundle ->
                appendLine("        \"${escapeKotlinString(bundle.localeTag)}\",")
            }
            appendLine("    )")
            appendLine()
            appendLine("    val supportedLocales: List<Locale> = supportedLocaleTags.map(Locale::forLanguageTag)")
            appendLine()
            appendLine("    fun setLocale(locale: Locale) {")
            appendLine("        currentLocale = locale")
            appendLine("    }")
            appendLine()
            appendLine("    private fun entry(")
            appendLine("        key: String,")
            appendLine("        translations: Map<String, String>,")
            appendLine("    ): I18nText = I18nText(")
            appendLine("        key = key,")
            appendLine("        translations = translations,")
            appendLine("        localeProvider = { currentLocale },")
            appendLine("        fallbackLocaleTag = defaultLocaleTag,")
            appendLine("    )")
            appendLine()
            appendTreeNode(this, tree, "    ", bundles)
            appendLine("}")
        }
    }
}
