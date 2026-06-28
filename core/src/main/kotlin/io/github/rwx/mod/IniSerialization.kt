package io.github.rwx.mod

import io.github.rwx.logger
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

object IniSerialization {
    inline fun <reified T : Any> fromMap(data: Map<String, String>?): T = fromMap(T::class, data)

    fun <T : Any> fromMap(clazz: KClass<T>, data: Map<String, String>?): T {
        val map = data ?: emptyMap()
        val ctor = clazz.primaryConstructor
            ?: throw IllegalArgumentException("Class ${clazz.simpleName} has no primary constructor")
        val args = HashMap<KParameter, Any?>()
        for (param in ctor.parameters) {
            val name = param.name ?: continue
            if (map.containsKey(name)) {
                args[param] = parseValue(clazz, map[name], param.type)
                continue
            }

            if (param.type.jvmErasure == Map::class) {
                val collected = mutableMapOf<String, String>()
                for ((key, value) in map) {
                    if (key.startsWith(name + "_")) collected[key.substringAfter(name + "_")] = value
                }
                if (collected.isNotEmpty()) args[param] = collected
            }

            val alt = camelToSnakeCase(name)
            if (map.containsKey(alt)) args[param] = parseValue(clazz, map[alt], param.type)
        }
        return ctor.callBy(args)
    }

    fun camelToSnakeCase(input: String): String {
        if (input.isEmpty()) return input
        val result = StringBuilder()
        for (i in input.indices) {
            val current = input[i]
            val previousUpper = i > 0 && input[i - 1].isUpperCase()
            val nextLower = i < input.length - 1 && input[i + 1].isLowerCase()
            if (i > 0 && current.isUpperCase() && (!previousUpper || nextLower)) result.append('_')
            result.append(current.lowercaseChar())
        }
        return result.toString()
    }

    private fun parseValue(clazz: KClass<*>, raw: String?, type: KType): Any? {
        if (raw == null) return null
        val isNullable = type.isMarkedNullable
        return when (val classifier = type.jvmErasure) {
            String::class -> raw
            Int::class -> raw.toIntOrNull() ?: if (isNullable) null else 0.also {
                logger.warn { "Failed to parse Int '$raw' for non-nullable parameter in ${clazz.simpleName}, using 0" }
            }

            Float::class -> parseFloatWithUnit(raw) ?: if (isNullable) null else 0f.also {
                logger.warn { "Failed to parse Float '$raw' for non-nullable parameter in ${clazz.simpleName}, using 0f" }
            }

            Double::class -> raw.toDoubleOrNull() ?: if (isNullable) null else 0.0.also {
                logger.warn { "Failed to parse Double '$raw' for non-nullable parameter in ${clazz.simpleName}, using 0.0" }
            }

            Boolean::class -> parseBoolean(raw)
            List::class -> parseList(raw, type, clazz)
            Map::class -> parseMap(raw, type, clazz)
            else -> {
                if (classifier.java.isEnum) {
                    val normalized = raw.trim()
                    classifier.java.enumConstants.firstOrNull { constant ->
                        (constant as Enum<*>).name.equals(normalized, ignoreCase = true)
                    } ?: throw IllegalArgumentException("No enum constant ${classifier.qualifiedName} for value '$raw'")
                } else {
                    raw
                }
            }
        }
    }

    private fun parseList(raw: String, type: KType, clazz: KClass<*>): List<*> {
        val elements = raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        val typeArg = type.arguments.firstOrNull()?.type
        if (typeArg == null) {
            logger.warn { "List type has no generic argument in ${clazz.simpleName}, returning List<String>" }
            return elements
        }
        val elementClass = typeArg.jvmErasure
        return elements.mapNotNull { element ->
            runCatching {
                when (elementClass) {
                    String::class -> element
                    Int::class -> element.toIntOrNull()
                    Long::class -> element.toLongOrNull()
                    Float::class -> parseFloatWithUnit(element)
                    Double::class -> element.toDoubleOrNull()
                    Boolean::class -> parseBoolean(element)
                    else -> parseValue(clazz, element, typeArg)
                }
            }.onFailure {
                logger.warn(it) { "Failed to parse list element '$element' as ${elementClass.simpleName} in ${clazz.simpleName}" }
            }.getOrNull()
        }
    }

    private fun parseMap(raw: String, type: KType, clazz: KClass<*>): Map<*, *> {
        val entries = raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        val keyTypeArg = type.arguments.getOrNull(0)?.type
        val valueTypeArg = type.arguments.getOrNull(1)?.type
        if (keyTypeArg == null || valueTypeArg == null) {
            logger.warn { "Map type missing generic arguments in ${clazz.simpleName}, returning Map<String, String>" }
            return entries.associate { entry ->
                val parts = entry.split('=', limit = 2)
                parts[0].trim() to parts.getOrElse(1) { "" }.trim()
            }
        }
        val keyClass = keyTypeArg.jvmErasure
        val valueClass = valueTypeArg.jvmErasure
        return entries.mapNotNull { entry ->
            runCatching {
                val parts = entry.split('=', limit = 2)
                if (parts.size != 2) return@mapNotNull null
                val keyStr = parts[0].trim()
                val valueStr = parts[1].trim()
                val key = when (keyClass) {
                    String::class -> keyStr
                    Int::class -> keyStr.toIntOrNull()
                    Long::class -> keyStr.toLongOrNull()
                    else -> keyStr
                } ?: return@mapNotNull null
                val value = when (valueClass) {
                    String::class -> valueStr
                    Int::class -> valueStr.toIntOrNull()
                    Long::class -> valueStr.toLongOrNull()
                    Float::class -> parseFloatWithUnit(valueStr)
                    Double::class -> valueStr.toDoubleOrNull()
                    Boolean::class -> parseBoolean(valueStr)
                    else -> valueStr
                } ?: return@mapNotNull null
                key to value
            }.onFailure {
                logger.warn(it) { "Failed to parse map entry '$entry' in ${clazz.simpleName}" }
            }.getOrNull()
        }.toMap()
    }

    private fun parseBoolean(raw: String): Boolean {
        val value = raw.trim().lowercase(Locale.ROOT)
        return value == "true" || value == "1"
    }

    private fun parseFloatWithUnit(raw: String): Float? {
        val value = raw.trim()
        return if (value.endsWith("s", ignoreCase = true)) value.dropLast(1).toFloatOrNull() else value.toFloatOrNull()
    }

    fun toMap(obj: Any): Map<String, String> {
        val result = linkedMapOf<String, String>()
        for (property in obj::class.memberProperties) {
            val value = runCatching { property.call(obj) }.getOrNull() ?: continue
            result[property.name] = when (value) {
                is String -> value
                is Number -> value.toString()
                is Boolean -> value.toString()
                is Enum<*> -> value.name
                is List<*> -> value.joinToString(",") { it.toString() }
                is Map<*, *> -> value.entries.joinToString(",") { "${it.key}=${it.value}" }
                else -> value.toString()
            }
        }
        return result
    }
}
