package io.github.rwx.mod

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.Utility
import com.corrodinggames.rts.gameFramework.mod.ModInfo
import com.corrodinggames.rts.gameFramework.mod.ModManager
import com.corrodinggames.rts.gameFramework.mod.ModManagerAccess
import io.github.rwx.PREFERENCE_NAME
import io.github.rwx.PlatformStorage
import io.github.rwx.PreferenceStorage
import io.github.rwx.ui.ModEntry
import net.peanuuutz.tomlkt.Toml
import net.peanuuutz.tomlkt.asTomlTable
import net.peanuuutz.tomlkt.getString
import java.io.File
import java.util.*
import java.util.zip.ZipFile

interface ModRepository {
    fun listMods(): List<ModEntry>
    fun importMod(path: String): ModImportResult
    fun toggleEnabled(modId: String)
    fun disableAll()
    fun applyChanges()
    fun reloadAvailableMods()
    fun reloadAppliedMods()
    fun discardPendingChanges()
    fun delete(modId: String): Boolean
}

data class ModImportResult(
    val imported: Boolean,
    val message: String,
)

class FileSystemModRepository(
    private val storage: PlatformStorage,
    preferenceStorage: PreferenceStorage,
) : ModRepository {
    private val preferences = preferenceStorage.preference(PREFERENCE_NAME)
    private val pendingEnabled = LinkedHashMap<String, Boolean>()
    private val preferenceStorage = preferenceStorage

    override fun listMods(): List<ModEntry> {
        liveModManager()?.let { manager ->
            return listLiveMods(manager)
        }
        return scanModSources().map { source ->
            val scan = scanSource(source)
            val enabled = pendingEnabled[scan.id] ?: loadSelection()[scan.id] ?: DEFAULT_ENABLED
            ModEntry(
                id = scan.id,
                name = scan.title,
                isEnabled = enabled,
                description = scan.description.orEmpty(),
                errorMessage = scan.errorMessage,
            )
        }
    }

    override fun importMod(path: String): ModImportResult {
        val source = File(path.trim().trim('"', '\'')).absoluteFile
        if (!source.exists()) {
            return ModImportResult(false, "Import failed: file does not exist")
        }
        if (!source.isModSourceCandidate()) {
            return ModImportResult(false, "Import failed: unsupported mod source")
        }

        val targetRoot = storage.unitsDir.file.also { it.mkdirs() }
        val target = uniqueDestination(targetRoot, source.name)
        return runCatching {
            if (source.isDirectory) {
                source.copyRecursively(target, overwrite = false)
            } else {
                source.copyTo(target, overwrite = false)
            }
            liveModManager()?.let { manager ->
                manager.loadAllMods()
                manager.loadModSelection()
            }
            ModImportResult(true, "Imported ${source.name}")
        }.getOrElse { error ->
            ModImportResult(false, "Import failed: ${error.message ?: error.javaClass.simpleName}")
        }
    }

    override fun toggleEnabled(modId: String) {
        liveModManager()?.let { manager ->
            ModManagerAccess.findById(manager, modId)?.let { mod ->
                mod.disabled = !mod.disabled
                return
            }
        }
        val current = listMods().firstOrNull { it.id == modId }?.isEnabled
            ?: pendingEnabled[modId]
            ?: loadSelection()[modId]
            ?: DEFAULT_ENABLED
        pendingEnabled[modId] = !current
    }

    override fun disableAll() {
        liveModManager()?.let { manager ->
            manager.disableAllMods()
            return
        }
        listMods().forEach { mod ->
            pendingEnabled[mod.id] = false
        }
    }

    override fun applyChanges() {
        liveModManager()?.let { manager ->
            manager.saveModSelection()
            GameEngine.getInstance()?.settingsEngine?.save()
            preferenceStorage.flush()
            return
        }
        val sources = scanModSources()
        val current = loadSelection()
        val serialized = sources.joinToString(separator = ",") { source ->
            val id = scanSource(source).id
            val enabled = pendingEnabled[id] ?: current[id] ?: DEFAULT_ENABLED
            "${selectionLabelFor(source.name)}|$id|${if (enabled) "enabled" else "disabled"}"
        }
        preferences.putString(ORIGINAL_MOD_SETTINGS_KEY, serialized)
        preferences.putInt(ORIGINAL_MOD_SETTINGS_VERSION_KEY, ORIGINAL_MOD_SETTINGS_VERSION)
        preferences.putInt(ORIGINAL_LAST_MOD_COUNT_KEY, sources.size)
        pendingEnabled.clear()
        preferenceStorage.flush()
        updateLiveSettings(serialized, sources.size)
    }

    override fun reloadAvailableMods() {
        liveModManager()?.let { manager ->
            manager.loadAllMods()
            manager.loadModSelection()
        }
    }

    override fun reloadAppliedMods() {
        liveModManager()?.let { manager ->
            manager.saveModSelection()
            manager.applyAndSaveMods()
            return
        }
        applyChanges()
    }

    override fun discardPendingChanges() {
        pendingEnabled.clear()
    }

    override fun delete(modId: String): Boolean {
        liveModManager()?.let { manager ->
            val mod = ModManagerAccess.findById(manager, modId) ?: return false
            val deleted = mod.delete()
            if (deleted) {
                manager.removeMod(mod)
                manager.saveModSelection()
                GameEngine.getInstance()?.settingsEngine?.save()
            }
            return deleted
        }
        val source = scanModSources().firstOrNull { stableIdFor(it.name) == modId } ?: return false
        val deleted = if (source.isDirectory) source.deleteRecursively() else source.delete()
        if (deleted) {
            pendingEnabled.remove(modId)
            applyChanges()
        }
        return deleted
    }

    private fun liveModManager(): ModManager? =
        GameEngine.getInstance()?.modManager

    private fun listLiveMods(manager: ModManager): List<ModEntry> {
        if (ModManagerAccess.allMods(manager).isEmpty()) {
            runCatching {
                manager.loadAllMods()
                manager.loadModSelection()
            }
        }
        return ModManagerAccess.allMods(manager).map { mod ->
            mod.toModEntryModel()
        }
    }

    private fun scanModSources(): List<File> =
        modScanRoots()
            .flatMap { root -> root.listFiles()?.asList().orEmpty() }
            .filter { it.isModSourceCandidate() }
            .distinctBy { it.absoluteFile.normalize().path }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name })

    private fun scanSource(source: File): ScannedMod {
        val fallbackTitle = source.nameWithoutExtension.ifBlank { source.name }
        val info = when {
            source.isDirectory -> readDirectoryModInfo(source)
            source.isFile && source.extension.lowercase(Locale.ROOT) == "rwmod" -> readArchiveModInfo(source)
            source.isFile && source.extension.lowercase(Locale.ROOT) == "zip" -> readArchiveModInfo(source)
            source.isFile && source.extension.lowercase(Locale.ROOT) == "jar" -> readArchiveModInfo(source)
            source.isFile && source.extension.lowercase(Locale.ROOT) == "ini" -> readTextModInfo(source)
            else -> ModInfoReadResult(null, null)
        }
        return ScannedMod(
            id = info.info?.id?.takeIf { it.isNotBlank() } ?: stableIdFor(source.name),
            title = info.info?.title?.takeIf { it.isNotBlank() } ?: fallbackTitle,
            description = info.info?.description,
            errorMessage = info.errorMessage,
        )
    }

    private fun readDirectoryModInfo(source: File): ModInfoReadResult {
        val modInfo = source.walkTopDown()
            .onEnter { dir -> source.toPath().relativize(dir.toPath()).nameCount <= MAX_MOD_INFO_DEPTH }
            .firstOrNull { file -> file.isFile && file.name.equals(MOD_INFO_FILE, ignoreCase = true) }
            ?: return ModInfoReadResult(null, null)
        return runCatching {
            ModInfoReadResult(parseModInfo(modInfo.readText()), null)
        }.getOrElse { error ->
            ModInfoReadResult(null, "Failed to read $MOD_INFO_FILE: ${error.message ?: error.javaClass.simpleName}")
        }
    }

    private fun readTextModInfo(source: File): ModInfoReadResult =
        runCatching {
            ModInfoReadResult(parseModInfo(source.readText()), null)
        }.getOrElse { error ->
            ModInfoReadResult(null, "Failed to read ${source.name}: ${error.message ?: error.javaClass.simpleName}")
        }

    private fun readArchiveModInfo(source: File): ModInfoReadResult =
        runCatching {
            ZipFile(source).use { zip ->
                val entries = zip.entries().asSequence()
                    .filterNot { it.isDirectory }
                    .map { it to it.name.replace('\\', '/').trim('/') }
                    .toList()
                val modInfoEntry = entries.asSequence()
                    .filter { (_, name) ->
                        name.endsWith("/$MOD_INFO_FILE", ignoreCase = true) ||
                                name.equals(MOD_INFO_FILE, ignoreCase = true)
                    }
                    .filter { (_, name) -> name.count { it == '/' } <= MAX_MOD_INFO_DEPTH }
                    .sortedBy { (_, name) -> name.count { it == '/' } }
                    .firstOrNull()
                if (modInfoEntry != null) {
                    val text = zip.getInputStream(modInfoEntry.first).bufferedReader().use { it.readText() }
                    return ModInfoReadResult(parseModInfo(text), null)
                }
                val jvmManifest = entries.firstOrNull { (_, name) -> name == JVM_MOD_MANIFEST }
                    ?: return ModInfoReadResult(null, null)
                val text = zip.getInputStream(jvmManifest.first).bufferedReader().use { it.readText() }
                ModInfoReadResult(parseJvmModManifest(text), null)
            }
        }.getOrElse { error ->
            ModInfoReadResult(null, "Failed to read archive: ${error.message ?: error.javaClass.simpleName}")
        }

    private fun parseModInfo(text: String): ParsedModInfo {
        var section = ""
        var title: String? = null
        var description: String? = null
        text.lineSequence().forEach { rawLine ->
            val line = rawLine.trim()
            if (line.isBlank() || line.startsWith("#") || line.startsWith(";")) return@forEach
            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length - 1).trim().lowercase(Locale.ROOT)
                return@forEach
            }
            if (section != "mod") return@forEach

            val separator = listOf(line.indexOf(':'), line.indexOf('='))
                .filter { it >= 0 }
                .minOrNull()
                ?: return@forEach
            val key = line.substring(0, separator).trim().lowercase(Locale.ROOT)
            val value = line.substring(separator + 1).trim().replace("\\n", "\n")
            when (key) {
                "title" -> title = value
                "description" -> description = value
            }
        }
        return ParsedModInfo(id = null, title = title, description = description)
    }

    private fun parseJvmModManifest(text: String): ParsedModInfo {
        val table = Toml { ignoreUnknownKeys = true }.parseToTomlTable(text).asTomlTable()
        return ParsedModInfo(
            id = runCatching { table.getString("id") }.getOrNull(),
            title = runCatching { table.getString("name") }.getOrNull(),
            description = runCatching { table.getString("description") }.getOrNull(),
        )
    }

    private fun File.isModSourceCandidate(): Boolean {
        if (isDirectory) return containsModInfo()
        if (!isFile) return false
        return extension.lowercase(Locale.ROOT) in SUPPORTED_MOD_FILE_EXTENSIONS
    }

    private fun File.containsModInfo(): Boolean =
        walkTopDown()
            .onEnter { dir -> toPath().relativize(dir.toPath()).nameCount <= MAX_MOD_INFO_DEPTH }
            .any { file -> file.isFile && file.name.equals(MOD_INFO_FILE, ignoreCase = true) }

    private fun uniqueDestination(root: File, sourceName: String): File {
        val first = File(root, sourceName)
        if (!first.exists()) {
            return first
        }
        val base = first.nameWithoutExtension.ifBlank { first.name }
        val extension = first.extension.takeIf { it.isNotBlank() }?.let { ".$it" }.orEmpty()
        for (index in 1..999) {
            val candidate = File(root, "$base-$index$extension")
            if (!candidate.exists()) {
                return candidate
            }
        }
        return File(root, "$base-${System.currentTimeMillis()}$extension")
    }

    private fun modScanRoots(): List<File> = buildList {
        add(File(storage.modsDir.file, "units"))
        add(storage.unitsDir.file)
    }.distinctBy { it.absoluteFile.normalize().path }
        .filter { it.isDirectory }

    private fun loadSelection(): Map<String, Boolean> =
        preferences.getString(ORIGINAL_MOD_SETTINGS_KEY, "").orEmpty()
            .split(",")
            .mapNotNull { rawEntry ->
                val parts = rawEntry.split("|")
                if (parts.size != 3) return@mapNotNull null
                val enabled = when (parts[2]) {
                    "enabled" -> true
                    "disabled" -> false
                    else -> return@mapNotNull null
                }
                parts[1] to enabled
            }
            .toMap()

    private fun updateLiveSettings(serialized: String, modCount: Int) {
        val settings = GameEngine.getInstance()?.settingsEngine ?: return
        settings.modSettings = serialized
        settings.modSettingsVersion = ORIGINAL_MOD_SETTINGS_VERSION
        settings.lastModCount = modCount
    }

    private fun stableIdFor(sourceName: String): String {
        return Utility.truncate(sourceName)
    }

    private fun selectionLabelFor(sourceName: String): String {
        val safe = sourceName.replace(",", " ").replace("|", " ")
        return if (safe.length > MAX_ORIGINAL_SELECTION_LABEL_LENGTH) {
            safe.substring(12) + "..."
        } else {
            safe
        }
    }

    private data class ScannedMod(
        val id: String,
        val title: String,
        val description: String?,
        val errorMessage: String?,
    )

    private data class ModInfoReadResult(
        val info: ParsedModInfo?,
        val errorMessage: String?,
    )

    private data class ParsedModInfo(
        val id: String?,
        val title: String?,
        val description: String?,
    )

    companion object {
        private const val MOD_INFO_FILE = "mod-info.txt"
        private const val JVM_MOD_MANIFEST = "mod.toml"
        private const val MAX_MOD_INFO_DEPTH = 4
        private const val MAX_ORIGINAL_SELECTION_LABEL_LENGTH = 15
        private const val ORIGINAL_MOD_SETTINGS_KEY = "modSettings"
        private const val ORIGINAL_MOD_SETTINGS_VERSION_KEY = "modSettingsVersion"
        private const val ORIGINAL_MOD_SETTINGS_VERSION = 1
        private const val ORIGINAL_LAST_MOD_COUNT_KEY = "lastModCount"
        private const val DEFAULT_ENABLED = false
        private val SUPPORTED_MOD_FILE_EXTENSIONS = setOf("rwmod", "zip", "jar", "ini")
    }
}

internal fun ModInfo.toModEntryModel(): ModEntry =
    ModEntry(
        id = uuid,
        name = displayTitle(),
        isEnabled = isSelectedInPreferences(),
        description = description.orEmpty(),
        ramLabel = getMemoryUsageString(),
        errorMessage = statusErrorMessage(),
    )

private fun ModInfo.displayTitle(): String =
    title ?: name ?: dirName ?: uuid

private fun ModInfo.isSelectedInPreferences(): Boolean =
    !disabled

private fun ModInfo.statusErrorMessage(): String? =
    firstError?.takeIf { it.isNotBlank() }
        ?: getErrorsAndWarnings()?.takeIf { it.isNotBlank() }
