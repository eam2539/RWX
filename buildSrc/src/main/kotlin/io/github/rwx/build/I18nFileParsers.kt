package io.github.rwx.build

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.serializer
import net.peanuuutz.tomlkt.Toml
import java.io.File
import java.util.*

object I18nFileParsers {
    private val toml = Toml {}

    fun parseToml(file: File): LinkedHashMap<String, String> = try {
        val stringSerializer = serializer<String>()
        val mapSerializer = MapSerializer(stringSerializer, stringSerializer)
        linkedMapOf<String, String>().apply {
            putAll(toml.decodeFromString(mapSerializer, file.readText(Charsets.UTF_8)))
        }
    } catch (exception: Exception) {
        throw IllegalArgumentException("Failed to parse TOML i18n file ${file.name}: ${exception.message}", exception)
    }

    fun parseProperties(file: File): LinkedHashMap<String, String> {
        val properties = Properties()
        file.reader(Charsets.UTF_8).use(properties::load)
        return properties.stringPropertyNames()
            .sorted().associateWithTo(linkedMapOf()) { key -> properties.getProperty(key) }
    }
}
