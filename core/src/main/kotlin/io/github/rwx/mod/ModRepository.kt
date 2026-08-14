package io.github.rwx.mod

import com.corrodinggames.rts.gameFramework.GameEngine
import com.corrodinggames.rts.gameFramework.mod.ModManager
import io.github.rwx.PlatformStorage
import io.github.rwx.PreferenceStorage
import io.github.rwx.mod.asset.JvmModAssetKeyStore
import io.github.rwx.ui.model.ModEntry
import java.io.File
import java.util.*

data class ModImportResult(
    val imported: Boolean,
    val message: String,
)

class ModRepository(
    private val storage: PlatformStorage,
    private val preferenceStorage: PreferenceStorage,
) {

    fun listMods(): List<ModEntry> {
        val manager = liveModManager() ?: return emptyList()
        if (manager.mods.isEmpty()) {
            this.runCatching {
                manager.loadAndApply()
            }
        }
        val jvmModManifests = manager.jvmMods.mapNotNull {
            it.manifest
        }
        val legacyModEntries = manager.mods.mapNotNull { modInfo ->
            ModEntry(
                (jvmModManifests.firstOrNull { it.id == modInfo.id }
                    ?: modInfo).apply {
                    if (this is JvmModManifest)
                        this.description = modInfo.fullDescription
                },
                !modInfo.disabled,
                modInfo.errorsAndWarnings,
                modInfo.path
            )
        }
        return legacyModEntries
    }

    fun importMod(path: String): ModImportResult {
        val source = File(path.trim().trim('"', '\'')).absoluteFile
        if (!source.exists()) {
            return ModImportResult(false, "Import failed: file does not exist")
        }
        if (source.isFile && source.extension.lowercase(Locale.ROOT) in ASSET_CREDENTIAL_EXTENSIONS) {
            return runCatching {
                val imported = JvmModAssetKeyStore(storage).importFile(source)
                ModImportResult(
                    true,
                    "Imported JVM mod asset ${imported.kind.name.lowercase().replace('_', ' ')} " +
                            "${imported.id.take(12)}...",
                )
            }.getOrElse { error ->
                ModImportResult(false, "Credential import failed: ${error.message ?: error.javaClass.simpleName}")
            }
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

    fun toggleEnabled(modId: String) {
        val manager = liveModManager() ?: return
        val mod = manager.mods.firstOrNull { it.uuid == modId } ?: return
        val enabling = mod.disabled
        mod.disabled = !mod.disabled
        if (!enabling) {
            manager.disposeJvmMod(modId)
        }
    }

    fun disableAll() {
        liveModManager()?.disableAllMods()
    }

    fun applyChanges() {
        val manager = liveModManager() ?: return
        manager.saveModSelection()
        GameEngine.getInstance()?.settingsEngine?.save()
        preferenceStorage.flush()
    }

    fun reloadAvailableMods() {
        liveModManager()?.loadAndApply()
    }

    fun reloadAppliedMods() {
        val manager = liveModManager() ?: return
        manager.saveModSelection()
        manager.applyAndSaveMods()
    }

    fun delete(modId: String): Boolean {
        val manager = liveModManager() ?: return false
        val mod = manager.mods.firstOrNull { it.uuid == modId } ?: return false
        val deleted = mod.delete()
        if (deleted) {
            manager.removeMod(mod)
            manager.saveModSelection()
            GameEngine.getInstance()?.settingsEngine?.save()
        }
        return deleted
    }

    private fun liveModManager(): ModManager? =
        GameEngine.getInstance()?.modManager

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

    companion object {
        private const val MOD_INFO_FILE = "mod-info.txt"
        private const val MAX_MOD_INFO_DEPTH = 4
        private val ASSET_CREDENTIAL_EXTENSIONS = setOf("rwxkey", "rwxpub", "rwxlicense")
        private val SUPPORTED_MOD_FILE_EXTENSIONS = setOf("rwmod", "zip", "jar", "ini")
    }
}

