package io.github.rwx.p2p

import com.corrodinggames.rts.game.map.TileMap
import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.mission.AreaControlMode
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.ByteArrayInputStream
import java.util.concurrent.ConcurrentHashMap
import javax.xml.parsers.DocumentBuilderFactory

object MapFeatureDetector {
    private val requiredFeaturesByPath = ConcurrentHashMap<String, List<String>>()

    fun requiredFeaturesForMap(mapPath: String?): List<String> {
        val path = mapPath?.trim()?.takeIf { it.isNotBlank() } ?: return emptyList()
        return requiredFeaturesByPath.computeIfAbsent(cacheKey(path)) {
            detectRequiredFeatures(path)
        }
    }

    internal fun clearCacheForTests() {
        requiredFeaturesByPath.clear()
    }

    private fun cacheKey(path: String): String =
        path.replace('\\', '/')

    private fun detectRequiredFeatures(path: String): List<String> {
        return runCatching {
            TileMap.openMapInputStreamWithMovedFallback(path)?.use { input ->
                val builderFactory = DocumentBuilderFactory.newInstance()
                builderFactory.isValidating = false
                val builder = builderFactory.newDocumentBuilder()
                builder.setEntityResolver { _, _ -> InputSource(ByteArrayInputStream(ByteArray(0))) }
                val document = builder.parse(input)
                val mapInfo = findMapInfo(document.documentElement) ?: return@use emptyList()
                val mode = readProperty(mapInfo, AreaControlMode.MAP_INFO_PROPERTY)
                    ?: readProperty(mapInfo, AreaControlMode.MAP_INFO_PROPERTY_ALIAS)
                val features = mutableListOf<String>()
                when {
                    AreaControlMode.isAreaControlMode(mode) -> features += FeatureIds.AREA_CONTROL
                    !mode.isNullOrBlank() -> features += "mode:${mode.trim()}"
                }
                if (hasMapLinks(mapInfo, document.documentElement)) {
                    features += FeatureIds.MAP_LINKS
                }
                features.distinct()
            } ?: emptyList()
        }.getOrElse { error ->
            GameEngine.log("RWX map feature detection failed for $path: ${error.message}")
            emptyList()
        }
    }

    private fun findMapInfo(root: Element): Element? {
        val objects = root.getElementsByTagName("object")
        for (index in 0 until objects.length) {
            val element = objects.item(index) as? Element ?: continue
            if (element.getAttribute("name").equals("map_info", ignoreCase = true)) {
                return element
            }
        }
        return null
    }

    private fun readProperty(objectElement: Element, name: String): String? {
        val properties = objectElement.getElementsByTagName("property")
        for (index in 0 until properties.length) {
            val property = properties.item(index) as? Element ?: continue
            if (!property.getAttribute("name").equals(name, ignoreCase = true)) {
                continue
            }
            val value = if (property.hasAttribute("value")) {
                property.getAttribute("value")
            } else {
                property.textContent
            }
            return value?.trim()?.takeIf { it.isNotEmpty() }
        }
        return null
    }

    private fun hasMapLinks(mapInfo: Element, root: Element): Boolean {
        if (readProperty(mapInfo, "rwxMapLinks") != null || readProperty(mapInfo, "rwx_map_links") != null) {
            return true
        }
        val objects = root.getElementsByTagName("object")
        for (index in 0 until objects.length) {
            val element = objects.item(index) as? Element ?: continue
            if (element.getAttribute("type").equals("rwx_map_portal", ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}
