package io.github.rwx.ui.model

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.GameSaver
import com.corrodinggames.rts.gameFramework.file.FileHelper
import io.github.rwx.LegacyAssetBridge
import io.github.rwx.PlatformStorage
import io.github.rwx.i18n.I18n
import io.github.rwx.i18n.I18nText
import io.github.rwx.p2p.FeatureIds
import io.github.rwx.p2p.MapFeatureDetector
import io.github.rwx.ui.AppScreen
import java.io.File


enum class LevelSelectMode(
    private val text: I18nText,
    val assetSubdir: String?,
) {
    Campaign(I18n.singleplayer.campaign, "normal"),
    Skirmish(I18n.singleplayer.skirmish, "skirmish"),
    Challenge(I18n.singleplayer.challenge, "challenge"),
    Survival(I18n.singleplayer.survival, "survival"),
    CustomMaps(I18n.singleplayer.customMaps, null),
    SavedGames(I18n.singleplayer.loadSave, null),
    ;

    val label: String
        get() = text()

    override fun toString(): String = label
}

data class MapEntry(
    val mapAssetPath: String,
    val playerCount: Int? = null,
    val requiredRwxFeatures: List<String> = emptyList(),
    val type: LevelSelectMode = LevelSelectMode.Skirmish,
) {
    val previewAssetPath: String? =
        if (type == LevelSelectMode.SavedGames) {
            null
        } else {
            MAP_PREVIEW_SUFFIX.map {
                mapAssetPath.removeSuffix(".tmx") + "_map" + it
            }.firstOrNull { LegacyAssetBridge.assetExists(it) }
        }
    val fileName: String
        get() = mapAssetPath.trimEnd('/', '\\').substringAfterLast('/')
    val displayName: String
        get() = displayName(fileName)
    val isSavedGame: Boolean get() = type == LevelSelectMode.SavedGames

    companion object {
        private fun generateCaseCombinations(strings: Collection<String>): Set<String> = strings.flatMap { str ->
            val chars = str.toList()
            val n = chars.size
            (0 until (1 shl n)).map { mask ->
                chars.mapIndexed { index, ch ->
                    if (ch.isLetter()) {
                        if (mask shr index and 1 == 1) ch.uppercaseChar()
                        else
                            ch.lowercaseChar()
                    } else ch
                }.joinToString("")

            }
        }.toSet()

        private val MAP_PREVIEW_SUFFIX = generateCaseCombinations(
            listOf(".png", ".jpg", ".jpeg")
        )

        private val leadingPlayerTagRegex = Regex("""^\[(?:z;)?[po]\d+]\s*""", RegexOption.IGNORE_CASE)
        private val sortPrefixRegex = Regex("""^[a-z]\d+;""", RegexOption.IGNORE_CASE)
        private val whitespaceRegex = Regex("""\s+""")

        /** Strip the sort prefix (e.g. "l030;") and replace underscores with spaces. */
        fun displayName(fileName: String): String {
            val withoutExt = fileName.removeSuffix(".tmx")
            val afterPrefix = withoutExt.replace(sortPrefixRegex, "")
            return afterPrefix
                .replace(leadingPlayerTagRegex, "")
                .replace('_', ' ')
                .replace(whitespaceRegex, " ")
                .trim()
        }
    }
}

data class LevelSelectFilterOption(
    val label: String,
    val playerCount: Int? = null,
    val rwxOnly: Boolean = false,
) {
    override fun toString(): String = label

    companion object {
        val All: LevelSelectFilterOption = LevelSelectFilterOption("All maps")
    }
}

enum class LevelSelectSortOption(private val label: String) {
    Default("Default"),
    Name("Name"),
    Players("Players"),
    ;

    override fun toString(): String = label
}

object LevelSelectMapBrowser {
    fun filterOptions(maps: List<MapEntry>): List<LevelSelectFilterOption> {
        val playerCounts = maps.mapNotNull { it.playerCount }.distinct().sorted()
        return listOf(LevelSelectFilterOption.All) +
                playerCounts.map { count -> LevelSelectFilterOption("${count}p maps", count) } +
                listOf(LevelSelectFilterOption("RWX modes", rwxOnly = true))
    }

    fun visibleMaps(
        maps: List<MapEntry>,
        query: String,
        filter: LevelSelectFilterOption,
        sort: LevelSelectSortOption,
    ): List<MapEntry> {
        val trimmedQuery = query.trim()
        val textFiltered = if (trimmedQuery.isBlank()) {
            maps
        } else {
            maps.filter {
                it.displayName.contains(trimmedQuery, ignoreCase = true) ||
                        it.fileName.contains(trimmedQuery, ignoreCase = true)
            }
        }
        val filtered = when {
            filter.rwxOnly -> textFiltered.filter { it.requiredRwxFeatures.isNotEmpty() }
            filter.playerCount != null -> textFiltered.filter { it.playerCount == filter.playerCount }
            else -> textFiltered
        }

        return when (sort) {
            LevelSelectSortOption.Default -> filtered
            LevelSelectSortOption.Name -> filtered.sortedBy { it.displayName.lowercase() }
            LevelSelectSortOption.Players -> filtered.sortedWith(
                compareBy<MapEntry> { it.playerCount ?: Int.MAX_VALUE }
                    .thenBy { it.displayName.lowercase() }
            )
        }
    }
}

/**
 * Scans the bundled map assets for the given [mode] and returns entries sorted by file name.
 * Platform-specific asset enumeration and Kool loader paths stay behind [PlatformStorage].
 */
class LevelSelectViewModel(
    val mode: LevelSelectMode,
    private val storage: PlatformStorage,
    private val savedGamesDirectory: () -> File = {
        GameSaver.getSaveFile("", "saves/", false)
    },
    private val customMapPathsProvider: (() -> List<String>)? = null,

    ) {
    @Volatile
    private var builtInItems: List<MapEntry>? = null

    fun items(): List<MapEntry> {
        if (mode == LevelSelectMode.SavedGames) {
            return savedGameItems()
        }
        if (mode == LevelSelectMode.CustomMaps) {
            return customMapItems()
        }
        builtInItems?.let { return it }
        return synchronized(this) {
            builtInItems ?: builtInMapItems().also { builtInItems = it }
        }
    }

    /**
     * Drops the cached built-in map list so the next [items] call re-scans the assets. Called after a
     * mod reload so newly enabled mods that add built-in maps become visible (RWPP forces this via
     * `getAllMaps(true)`). Custom maps are never cached, so they need no invalidation.
     */
    fun invalidateCache() {
        synchronized(this) {
            builtInItems = null
        }
    }

    private fun builtInMapItems(): List<MapEntry> {
        val prefix = "maps/${requireNotNull(mode.assetSubdir)}"
        return storage.listAssets(prefix)
            .asSequence()
            .map { it.replace('\\', '/').trim('/') }
            .filter { it.endsWith(".tmx") }
            .sortedBy { it.substringAfterLast('/') }
            .map { mapEntry(it) }
            .toList()
    }

    private fun customMapItems(): List<MapEntry> {
        val fileMaps = (customMapPathsProvider?.invoke()
            ?: engineCustomMapPaths(ENGINE_CUSTOM_MAP_ROOT, depth = 0))
            .distinct()
            .sortedBy { it.lowercase() }
            .map(::engineCustomMapEntry)
        return fileMaps + modMapItems()
    }

    private fun engineCustomMapPaths(folder: String, depth: Int): List<String> {
        if (depth > MAX_CUSTOM_MAP_SCAN_DEPTH) return emptyList()
        val entries = runCatching { FileHelper.listFiles(folder) }.getOrNull() ?: return emptyList()
        val result = mutableListOf<String>()
        for (entry in entries) {
            if (entry.startsWith('.')) continue
            val childPath = folder.trimEnd('/') + "/" + entry
            if (entry.endsWith(".tmx", ignoreCase = true)) {
                result += childPath
            } else if (runCatching { FileHelper.isDirectory(childPath) }.getOrDefault(false)) {
                result += engineCustomMapPaths(childPath, depth + 1)
            }
        }
        return result
    }

    private fun engineCustomMapEntry(mapPath: String): MapEntry {
        val fileName = mapPath.substringAfterLast('/')
        return MapEntry(
            mapAssetPath = mapPath,
            playerCount = playerCount(fileName),
            requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapPath),
        )
    }

    private fun savedGameItems(): List<MapEntry> {
        val saveRoot = savedGamesDirectory()
        val rootPath = saveRoot.absolutePath
        return FileHelper.listFiles(rootPath)
            ?.asSequence()
            ?.filter { it.endsWith(".rwsave", ignoreCase = true) }
            ?.map { name ->
                val path = rootPath.trimEnd('/', '\\') + "/" + name
                MapEntry(
                    mapAssetPath = path,
                    playerCount = null,
                    type = LevelSelectMode.SavedGames
                ) to FileHelper.getFileSize(path)
            }
            ?.sortedByDescending { (_, modifiedAt) -> modifiedAt }
            ?.map { (entry, _) -> entry }
            ?.toList()
            ?: emptyList()
    }

    private fun modMapItems(): List<MapEntry> {
        val manager = GameEngine.getInstance()?.modManager ?: return emptyList()
        return manager.mods
            .asSequence()
            .filter { it.isEnabled && !it.disabled }
            .mapNotNull { mod -> mod.getSourceFolder()?.takeIf { it.isNotBlank() } }
            .distinct()
            .flatMap { sourceFolder -> modMapTmxPaths(sourceFolder, depth = 0).asSequence() }
            .distinct()
            .sortedBy { it.substringAfterLast('/').lowercase() }
            .map { modMapEntry(it) }
            .toList()
    }

    /** Recursively lists `.tmx` paths under a mod source folder (bounded depth, matches ModInfo scan). */
    private fun modMapTmxPaths(folder: String, depth: Int): List<String> {
        if (depth > MAX_MOD_MAP_SCAN_DEPTH) return emptyList()
        val entries = runCatching { FileHelper.listFiles(folder) }.getOrNull() ?: return emptyList()
        val result = mutableListOf<String>()
        for (entry in entries) {
            if (entry.startsWith(".")) continue
            val childPath = "$folder/$entry"
            if (entry.endsWith(".tmx", ignoreCase = true)) {
                result += childPath
            } else if (runCatching { FileHelper.isDirectory(childPath) }.getOrDefault(false)) {
                result += modMapTmxPaths(childPath, depth + 1)
            }
        }
        return result
    }

    private fun modMapEntry(mapPath: String): MapEntry {
        val fileName = mapPath.substringAfterLast('/')
        return MapEntry(
            mapAssetPath = mapPath,
            playerCount = playerCount(fileName),
            requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapPath),
        )
    }

    fun mapEntry(mapAssetPath: String): MapEntry {
        val fileName = mapAssetPath.substringAfterLast('/')
        return MapEntry(
            mapAssetPath = mapAssetPath,
            playerCount = playerCount(fileName),
            requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapAssetPath),
        )
    }

    companion object {
        private const val MAX_MOD_MAP_SCAN_DEPTH: Int = 4
        private const val MAX_CUSTOM_MAP_SCAN_DEPTH: Int = 8
        private const val ENGINE_CUSTOM_MAP_ROOT: String = "/SD/rustedWarfare/maps"
        private val playerTagRegex = Regex("""\[(?:[^]]*;)?[po](\d+)]""", RegexOption.IGNORE_CASE)
        private val playerSuffixRegex = Regex("""\((\d+)p\)""", RegexOption.IGNORE_CASE)


        fun playerCount(fileName: String): Int? {
            playerTagRegex.find(fileName)?.groupValues?.getOrNull(1)?.toIntOrNull()?.let { return it }
            return playerSuffixRegex.find(fileName)?.groupValues?.getOrNull(1)?.toIntOrNull()
        }
    }
}

fun MapEntry.ModeLabel(): String? = requiredRwxFeatures.ModeLabel()

fun MapEntry.CompatibilityLabel(): String? =
    ModeLabel()?.let { "Single-player / RWX P2P" }

fun List<String>.ModeLabel(): String? =
    when {
        contains(FeatureIds.AREA_CONTROL) -> "Area Control"
        contains(FeatureIds.MAP_LINKS) -> "Map Links"
        else -> firstOrNull { it.startsWith("mode:") }
            ?.substringAfter("mode:")
            ?.takeIf { it.isNotBlank() }
            ?.toDisplayModeLabel()
    }

private fun String.toDisplayModeLabel(): String =
    trim()
        .replace('-', ' ')
        .replace('_', ' ')
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.titlecase() } }

interface LevelSelectViewModelFactory {
    fun create(mode: LevelSelectMode): LevelSelectViewModel

    /** Drops any cached built-in map lists so the next [create] re-scans (e.g. after a mod reload). */
    fun invalidateCaches() {}
}

fun interface LevelSelectActionHandler {
    fun onAction(action: LevelSelectAction)
}

sealed interface LevelSelectAction {
    data class SelectMap(val map: MapEntry) : LevelSelectAction
    data class SelectMode(val mode: LevelSelectMode) : LevelSelectAction
    data object Back : LevelSelectAction
}

sealed interface LevelSelectOutcome {
    data class Navigate(val screen: AppScreen) : LevelSelectOutcome
    data class StartGame(val map: MapEntry) : LevelSelectOutcome
}

object LevelSelectNavigation {
    fun outcomeFor(action: LevelSelectAction): LevelSelectOutcome = when (action) {
        is LevelSelectAction.SelectMap -> LevelSelectOutcome.StartGame(action.map)
        is LevelSelectAction.SelectMode -> LevelSelectOutcome.Navigate(AppScreen.LevelSelect)
        LevelSelectAction.Back -> LevelSelectOutcome.Navigate(AppScreen.MainMenu)
    }
}
