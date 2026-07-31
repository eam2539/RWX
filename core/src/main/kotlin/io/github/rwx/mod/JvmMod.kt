package io.github.rwx.mod

import io.github.rwx.PlatformBridge
import io.github.rwx.logger
import io.github.rwx.mod.assets.AssetCredentialResolver
import net.peanuuutz.tomlkt.*
import org.koin.mp.KoinPlatform.getKoin
import java.io.Closeable
import java.io.File
import java.util.zip.ZipFile

class JvmModLoader @JvmOverloads constructor(
    private val platformClassLoader: ClassLoader = JvmModLoader::class.java.classLoader,
    private val createClassLoader: ((File, ClassLoader) -> ClassLoader)? = null,
    private val platformBridge: PlatformBridge? = null,
    private val assetCredentialResolver: AssetCredentialResolver? = null,
) : AutoCloseable {
    val errors: MutableList<Throwable> = mutableListOf()

    private val toml = Toml { ignoreUnknownKeys = true }
    private val discoveredMods = mutableListOf<JvmMod>()
    val loadedMods = mutableListOf<Mod>()

    fun loadMods(handle: File): List<Mod> {
        return discoverMods(handle).mapNotNull { mod ->
            runCatching {
                initializeMod(mod)
                mod
            }.onFailure { e ->
                errors += e
                logger.error(e) { "init() failed for mod ${mod.metadata.id}" }
            }.getOrNull()
        }
    }

    fun discoverMods(handle: File): List<JvmMod> {
        val currentBatch = mutableListOf<JvmMod>()
        listCandidateModFiles(handle).forEach { file ->
            runCatching {
                discoverModFile(file)?.let { mod ->
                    currentBatch += mod
                }
            }.onFailure { e ->
                errors += e
                logger.error(e) { "Failed to load JVM mod from ${file.path}" }
            }
        }
        return resolveDependencies(currentBatch).filterIsInstance<JvmMod>()
    }

    fun listCandidateModFiles(handle: File): List<File> {

        if (!handle.exists()) return emptyList()

        return handle
            .listFiles { !it.isDirectory && it.name.endsWith(".jar") }
            ?.sortedBy { it.path.lowercase() } ?: emptyList()
    }

    fun loadModFile(jarFile: File): JvmMod? {
        val mod = discoverModFile(jarFile) ?: return null
        return runCatching {
            initializeMod(mod)
            mod
        }.onFailure { e ->
            errors += e
            logger.error(e) { "init() failed for mod ${mod.metadata.id}" }
        }.getOrNull()
    }

    private fun discoverModFile(jarFile: File): JvmMod? {
        val result = peekModToml(jarFile) ?: run {
            logger.warn { "Failed to load JVM mod from ${jarFile.path}" }
            return null
        }
        val metadata = result.metadata
        val entryClassName = result.entryClassName

        val resolvedPlatformBridge = platformBridge
            ?: runCatching { getKoin().get<PlatformBridge>() }.getOrNull()
        val classLoader = createClassLoader?.invoke(jarFile, platformClassLoader)
            ?: requireNotNull(resolvedPlatformBridge) { "A PlatformBridge or classloader factory is required" }
                .createModClassLoader(jarFile, platformClassLoader)

        val mod = instantiateMod(entryClassName, classLoader, jarFile.name) ?: return null
        try {
            mod.apply {
                this.classLoader = classLoader
                this.metadata = metadata
                this.api = ApiImpl.create(metadata, jarFile, resolvedPlatformBridge, assetCredentialResolver)
            }
        } catch (error: Throwable) {
            if (classLoader is Closeable) runCatching(classLoader::close)
            throw error
        }

        discoveredMods += mod
        return mod
    }

    fun initializeMod(mod: JvmMod) {
        if (mod in loadedMods) return
        ModRegistry.register(mod)
        try {
            mod.init()
            loadedMods += mod
        } catch (error: Throwable) {
            ModRegistry.unregister(mod)
            runCatching { mod.dispose() }
            throw error
        }
    }


    private data class ModTomlResult(val metadata: ModMetadata, val entryClassName: String)


    private fun peekModToml(jarFile: File): ModTomlResult? =
        runCatching {
            val zip = ZipFile(jarFile)
            zip.use { zip ->
                val entry = zip.getEntry("mod.toml") ?: return null
                val reader = zip.getInputStream(entry).reader(Charsets.UTF_8)
                val table = reader.use { reader ->
                    toml.parseToTomlTable(reader).asTomlTable()
                }
                ModTomlResult(
                    entryClassName = runCatching { table.getString("entrypoint") }.getOrElse {
                        return null
                    }.takeIf { it.isNotBlank() } ?: run {
                        return null
                    },
                    metadata = ModMetadata().apply {
                        path = jarFile.path
                        id = runCatching { table.getString("id") }.getOrElse {
                            return null
                        }.takeIf { it.isNotBlank() } ?: run {
                            return null
                        }
                        name = runCatching { table.getString("name") }.getOrElse {
                            return null
                        }.takeIf { it.isNotBlank() } ?: run {
                            return null
                        }
                        author = runCatching { table.getString("author") }.getOrDefault(author)
                        version = runCatching { table.getString("version") }.getOrDefault(version)
                        minGameVersionName =
                            runCatching { table.getString("minGameVersion") }.getOrDefault(minGameVersionName)
                        description = runCatching { table.getString("description") }.getOrDefault(description)
                        dependencies = table.getArrayOrNull("dependencies")?.let { dependencyArray ->
                            dependencyArray.mapIndexedNotNull { index, _ ->
                                runCatching { dependencyArray.getString(index) }
                                    .getOrNull()
                                    ?.takeIf { it.isNotBlank() }
                            }
                        } ?: dependencies
                        priority = runCatching { table.getString("priority").toInt() }.getOrDefault(priority)
                    }

                )
            }
        }.onFailure { e ->
            logger.error(e) { "Error reading mod.toml from ${jarFile.name}" }
        }.getOrNull()

    private fun instantiateMod(
        className: String,
        classLoader: ClassLoader,
        jarName: String
    ): JvmMod? {
        return runCatching {
            val clazz = classLoader.loadClass(className)
            val ctor = clazz.getDeclaredConstructor()
            ctor.newInstance() as JvmMod
        }.onFailure { e ->
            when (e) {
                is ClassNotFoundException ->
                    logger.error(e) { "Entrypoint class '$className' not found in $jarName" }

                is NoSuchMethodException ->
                    logger.error(e) { "Entrypoint '$className' in $jarName must have a public no-arg constructor" }

                is ClassCastException ->
                    logger.error(e) { "Entrypoint '$className' in $jarName does not implement JvmModEntryPoint" }

                else ->
                    logger.error(e) { "Failed to instantiate entrypoint '$className' in $jarName" }
            }
        }.getOrNull()
    }


    private fun resolveDependencies(mods: List<Mod>): List<Mod> {
        val modMap = mods.associateBy { it.metadata.id }
        val adjacency = mutableMapOf<String, MutableSet<String>>()
        val inDegree = mutableMapOf<String, Int>().withDefault { 0 }

        for (mod in mods) {
            for (depId in mod.metadata.dependencies) {
                if (depId !in modMap) {
                    logger.warn { "Mod ${mod.metadata.id} depends on missing mod $depId; skipping this dependency" }
                    continue
                }
                adjacency.getOrPut(depId) { mutableSetOf() }.add(mod.metadata.id)
                inDegree[mod.metadata.id] = inDegree.getValue(mod.metadata.id) + 1
            }
        }

        val queue = mutableListOf<Mod>()
        for (mod in mods) {
            if (inDegree.getValue(mod.metadata.id) == 0) queue.add(mod)
        }

        val sorted = mutableListOf<Mod>()
        while (queue.isNotEmpty()) {
            queue.sortByDescending { it.metadata.priority }
            val current = queue.removeAt(0)
            sorted.add(current)
            adjacency[current.metadata.id]?.forEach { dependentId ->
                val newDegree = inDegree.getValue(dependentId) - 1
                inDegree[dependentId] = newDegree
                if (newDegree == 0) modMap[dependentId]?.let { queue.add(it) }
            }
        }

        if (sorted.size != mods.size) {
            val remaining = mods.filter { it.metadata.id !in sorted.map { m -> m.metadata.id } }
            logger.warn { "Cyclic or missing dependencies detected; skipping mods: ${remaining.joinToString { it.metadata.id }}" }
        }

        return sorted
    }

    override fun close() {
        loadedMods.forEach { mod ->
            runCatching { mod.dispose() }.onFailure { e ->
                logger.error(e) { "Failed to dispose mod ${mod.metadata.id}" }
            }
            ModRegistry.unregister(mod)
        }
        discoveredMods.distinct().forEach { mod ->
            runCatching {
                (runCatching { mod.api }.getOrNull() as? ApiImpl)?.close()
            }.onFailure { e ->
                logger.error(e) { "Error closing assets for mod ${mod.metadata.id}" }
            }
            runCatching {
                val cl = mod.classLoader
                if (cl is Closeable) cl.close()
            }.onFailure { e ->
                logger.error(e) { "Error closing classloader for mod ${mod.metadata.id}" }
            }
        }
        loadedMods.clear()
        discoveredMods.clear()
    }
}
