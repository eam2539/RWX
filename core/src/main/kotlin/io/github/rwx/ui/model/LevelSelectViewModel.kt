package io.github.rwx.ui.model

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.GameSaver
import com.corrodinggames.rts.gameFramework.file.FileHelper
import com.corrodinggames.rts.gameFramework.mod.ModManagerAccess
import io.github.rwx.PlatformStorage
import io.github.rwx.i18n.I18n
import io.github.rwx.i18n.I18nText
import io.github.rwx.p2p.FeatureIds
import io.github.rwx.p2p.MapFeatureDetector
import io.github.rwx.ui.AppScreen
import java.io.File

/**
 * The game mode whose maps the level-select screen shows. Built-in modes map to asset
 * subdirectories under assets/maps/; custom maps enumerate the platform maps directory. Display
 * labels use the same single-player translations as the main menu.
 */
enum class LevelSelectMode(
    private val text: I18nText,
    val assetSubdir: String?,
    val custom: Boolean = false,
    val savedGames: Boolean = false,
) {
    Campaign(I18n.singleplayer.campaign, "normal"),
    Skirmish(I18n.singleplayer.skirmish, "skirmish"),
    Challenge(I18n.singleplayer.challenge, "challenge"),
    Survival(I18n.singleplayer.survival, "survival"),
    CustomMaps(I18n.singleplayer.customMaps, null, custom = true),
    SavedGames(I18n.singleplayer.loadSave, null, savedGames = true),
    ;

    val label: String
        get() = text()

    override fun toString(): String = label
}

enum class LevelEntryType {
    Map,
    SavedGame,
}

/** A single map entry shown in the level-select list. */
data class MapEntry(
    val fileName: String,
    val displayName: String,
    val mapAssetPath: String,
    val previewAssetPath: String?,
    val playerCount: Int?,
    val requiredRwxFeatures: List<String> = emptyList(),
    val type: LevelEntryType = LevelEntryType.Map,
    val saveName: String? = null,
)

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
    private val customMapFileExists: (String) -> Boolean = { path ->
        runCatching { FileHelper.fileExists(path) }.getOrDefault(false)
    },
) {
    @Volatile
    private var builtInItems: List<MapEntry>? = null

    fun items(): List<MapEntry> {
        if (mode.savedGames) {
            return savedGameItems()
        }
        if (mode.custom) {
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
        val previewPath = mapPath.removeSuffix(".tmx") + "_map.png"
        return MapEntry(
            fileName = fileName,
            displayName = displayName(fileName),
            mapAssetPath = mapPath,
            previewAssetPath = previewPath.takeIf {
                !FileHelper.isManagedPath(previewPath) &&
                        customMapFileExists(previewPath)
            },
            playerCount = playerCount(fileName),
            requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapPath),
        )
    }

    private fun savedGameItems(): List<MapEntry> {
        val saveRoot = savedGamesDirectory()
        val localItems = saveRoot
            .takeIf(File::isDirectory)
            ?.walkTopDown()
            ?.filter { it.isFile && it.extension.equals("rwsave", ignoreCase = true) }
            ?.sortedByDescending(File::lastModified)
            ?.map { file ->
                val relativePath = file.relativeTo(saveRoot).invariantSeparatorsPath
                MapEntry(
                    fileName = file.name,
                    displayName = file.nameWithoutExtension.ifBlank { file.name },
                    mapAssetPath = file.absolutePath,
                    previewAssetPath = null,
                    playerCount = null,
                    type = LevelEntryType.SavedGame,
                    saveName = relativePath,
                )
            }
            ?.toList()
        if (localItems != null) return localItems

        val rootPath = saveRoot.absolutePath
        return FileHelper.listFiles(rootPath)
            ?.asSequence()
            ?.filter { it.endsWith(".rwsave", ignoreCase = true) }
            ?.map { name ->
                val path = rootPath.trimEnd('/', '\\') + "/" + name
                MapEntry(
                    fileName = name,
                    displayName = name.substringBeforeLast('.').ifBlank { name },
                    mapAssetPath = path,
                    previewAssetPath = null,
                    playerCount = null,
                    type = LevelEntryType.SavedGame,
                    saveName = name,
                ) to FileHelper.getFileSize(path)
            }
            ?.sortedByDescending { (_, modifiedAt) -> modifiedAt }
            ?.map { (entry, _) -> entry }
            ?.toList()
            ?: emptyList()
    }

    /**
     * Enumerates `.tmx` maps bundled inside enabled mods (including `.rwmod` archives), mirroring the
     * original engine's mod-map merge. `FileHelper.listFiles` delegates `.rwmod/...` paths to the zip
     * loader, and the resulting inner paths are engine-loadable via the same delegation, so a selected
     * mod map loads through the normal `currentMapPath` → `loadGame` path.
     */
    private fun modMapItems(): List<MapEntry> {
        val manager = GameEngine.getInstance()?.modManager ?: return emptyList()
        return ModManagerAccess.allMods(manager)
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
        val previewPath = mapPath.removeSuffix(".tmx") + "_map.png"
        return MapEntry(
            fileName = fileName,
            displayName = displayName(fileName),
            mapAssetPath = mapPath,
            previewAssetPath = previewPath.takeIf {
                runCatching { FileHelper.fileExists(it) }.getOrDefault(false)
            },
            playerCount = playerCount(fileName),
            requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapPath),
        )
    }

    private fun customMapEntry(mapFile: File): MapEntry {
        val relativePath = mapFile.relativeTo(storage.mapsDir.file).invariantSeparatorsPath
        val mapVirtualPath = storage.mapsDir.virtualPath.trimEnd('/') + "/" + relativePath
        val previewFile = File(mapFile.parentFile, mapFile.nameWithoutExtension + "_map.png")
        return MapEntry(
            fileName = mapFile.name,
            displayName = displayName(mapFile.name),
            mapAssetPath = mapVirtualPath,
            previewAssetPath = previewFile.takeIf(File::isFile)?.absolutePath,
            playerCount = playerCount(mapFile.name),
            requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapFile.absolutePath),
        )
    }

    /**
     * Resolves a [MapEntry] (including its `_map.png` preview, if bundled) for a built-in map asset
     * path. Used to recover the preview for a joined multiplayer room where no local [MapEntry] was
     * selected — the room snapshot only carries the map path.
     */
    fun resolveMapEntry(mapAssetPath: String): MapEntry = mapEntry(mapAssetPath)

    private fun mapEntry(mapAssetPath: String): MapEntry {
        val fileName = mapAssetPath.substringAfterLast('/')
        val previewAssetPath = mapAssetPath.removeSuffix(".tmx") + "_map.png"
        return MapEntry(
            fileName = fileName,
            displayName = displayName(fileName),
            mapAssetPath = mapAssetPath,
            previewAssetPath = if (storage.assetExists(previewAssetPath)) {
                storage.assetLoadPath(previewAssetPath)
            } else {
                null
            },
            playerCount = playerCount(fileName),
            requiredRwxFeatures = MapFeatureDetector.requiredFeaturesForMap(mapAssetPath),
        )
    }

    companion object {
        private const val MAX_MOD_MAP_SCAN_DEPTH: Int = 4
        private const val MAX_CUSTOM_MAP_SCAN_DEPTH: Int = 8
        private const val ENGINE_CUSTOM_MAP_ROOT: String = "/SD/rustedWarfare/maps"
        private val leadingPlayerTagRegex = Regex("""^\[(?:z;)?[po]\d+]\s*""", RegexOption.IGNORE_CASE)
        private val playerTagRegex = Regex("""\[(?:[^]]*;)?[po](\d+)]""", RegexOption.IGNORE_CASE)
        private val playerSuffixRegex = Regex("""\((\d+)p\)""", RegexOption.IGNORE_CASE)
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

        fun previewFileName(fileName: String): String = fileName.removeSuffix(".tmx") + "_map.png"

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
