package com.corrodinggames.rts.gameFramework.mod

import com.corrodinggames.rts.gameFramework.GameEngine
import net.peanuuutz.tomlkt.Toml
import java.io.Closeable
import java.io.File


abstract class JvmMod : Mod {
    override lateinit var metadata: ModMetadata
    lateinit var classLoader: ClassLoader
    override val type = ModType.JVM
}

class JvmModLoader(
    private val platformClassLoader: ClassLoader = JvmModLoader::class.java.classLoader,
    private val createClassLoader: ((File, ClassLoader) -> ClassLoader)? = null,
) : AutoCloseable {
    val errors: MutableList<Throwable> = mutableListOf()

    //   private val platformBridge: PlatformBridge by inject()
    private val toml = Toml { ignoreUnknownKeys = true }
    val loadedMods = mutableListOf<Mod>()
    /*
         fun loadMods(handle: FileHandle?): List<Mod> {
            val currentBatch = mutableListOf<JvmMod>()
            listCandidateModFiles(handle).forEach { file ->
                runCatching {
                    loadModFile(file.file())?.let { mod ->
                        currentBatch += mod
                    }
                }.onFailure { e ->
                    GameEngine.log( "Failed to load JVM mod from ${file.path()}",e )
                }
            }
            return resolveDependencies(currentBatch)
        }

        fun listCandidateModFiles(handle: FileHandle? = null): List<FileHandle> {
            val directory = handle ?: when (Gdx.app.type) {
                Application.ApplicationType.Android -> StorageManager.rwExternalModsForAndroid()
                else -> StorageManager.mods()
            }

            if (!directory.exists()) return emptyList()

            return directory.list()
                .filter { !it.isDirectory && it.name().endsWith(".jar") }
                .sortedBy { it.path().lowercase() }
        }

        fun loadModFile(jarFile: File): JvmMod? {
            val result = peekModToml(jarFile) ?: run {
                GameEngine.log("mod.toml missing or invalid in ${jarFile.name}")
                return null
            }
            val metadata = result.metadata
            val entryClassName = result.entryClassName

            val classLoader = createClassLoader?.invoke(jarFile, platformClassLoader)
                ?: platformBridge.createModClassLoader(jarFile, platformClassLoader)

            val mod = (instantiateMod(entryClassName, classLoader, jarFile.name)
                ?: return null)
                .apply {
                    this.classLoader = classLoader
                    this.metadata = metadata
                }

            ModRegistry.register(mod)
            runCatching { mod.initialize() }.onFailure { e ->
                GameEngine.log("initialize() failed for mod ${metadata.id}",e)
                runCatching { mod.dispose() }
                return null
            }
            loadedMods += mod
            return mod
        }



        fun registerAllEventListeners(eventBus: ModEventBus) {
            resolveDependencies(loadedMods).forEach { mod ->
                runCatching { mod.registerEventListeners(eventBus) }.onFailure { e ->
                    GameEngine.log("registerEventListeners() failed for mod ${mod.metadata.id}",e)
                }
            }
        }

        private data class ModTomlResult(val metadata: ModMetadata, val entryClassName: String)


        private fun peekModToml(jarFile: File): ModTomlResult? =
            runCatching {
                JarFile(jarFile).use { jar ->
                    val entry = jar.getJarEntry("mod.toml") ?: return null
                    val table = jar.getInputStream(entry).reader(Charsets.UTF_8).use { reader ->
                        toml.parseToTomlTable(reader).asTomlTable()
                    }
                    ModTomlResult(
                        entryClassName =  runCatching { table.getString("entrypoint") }.getOrElse {
                            return null
                        }.takeIf { it.isNotBlank() }?:run {
                            return null
                        },
                        metadata = ModMetadata().apply {
                            path=jarFile.path
                            id = runCatching { table.getString("id") }.getOrElse {
                                return null
                            }.takeIf { it.isNotBlank() }?:run {
                                return null
                            }
                            name = runCatching { table.getString("name") }.getOrElse {
                                return null
                            }.takeIf { it.isNotBlank() }?:run {
                                return null
                            }
                            author = runCatching { table.getString("author") }.getOrDefault(author)
                            version = runCatching { table.getString("version") }.getOrDefault(version)
                            minGameVersionName = runCatching { table.getString("minGameVersion") }.getOrDefault(minGameVersionName)
                            description = runCatching { table.getString("description") }.getOrDefault(description)
                            dependencies =  table.getArrayOrNull("dependencies")?.let { dependencyArray ->
                                dependencyArray.mapIndexedNotNull { index , _ ->
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
                GameEngine.log("Error reading mod.toml from ${jarFile.name}", e)
            }.getOrNull()
    */

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
                    GameEngine.log("Entrypoint class '$className' not found in $jarName", e)


                is NoSuchMethodException ->
                    GameEngine.log("Entrypoint '$className' in $jarName must have a public no-arg constructor", e)

                is ClassCastException ->
                    GameEngine.log("Entrypoint '$className' in $jarName does not implement JvmModEntryPoint", e)

                else ->
                    GameEngine.log("Failed to instantiate entrypoint '$className' in $jarName", e)
            }
        }.getOrNull()
    }


    /*
        private fun resolveDependencies(mods: List<Mod>): List<Mod> {
            val modMap = mods.associateBy { it.metadata.id }
            val adjacency = mutableMapOf<String, MutableSet<String>>()
            val inDegree = mutableMapOf<String, Int>().withDefault { 0 }

            for (mod in mods) {
                for (depId in mod.metadata.dependencies) {
                    if (depId !in modMap) {
                        GameEngine.log("Mod ${mod.metadata.id} depends on missing mod $depId; skipping this dependency")
                        continue
                    }
                    adjacency.getOrPut(depId) { mutableSetOf() }.add(mod.metadata.id)
                    inDegree[mod.metadata.id] = inDegree.getValue(mod.metadata.id) + 1
                }
            }

            val queue = PriorityQueue<Mod>(
                compareByDescending { it.metadata.priority }
            )
            for (mod in mods) {
                if (inDegree.getValue(mod.metadata.id) == 0) queue.add(mod)
            }

            val sorted = mutableListOf<Mod>()
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                sorted.add(current)
                adjacency[current.metadata.id]?.forEach { dependentId ->
                    val newDegree = inDegree.getValue(dependentId) - 1
                    inDegree[dependentId] = newDegree
                    if (newDegree == 0) modMap[dependentId]?.let { queue.add(it) }
                }
            }

            if (sorted.size != mods.size) {
                val remaining = mods.filter { it.metadata.id !in sorted.map { m -> m.metadata.id } }
                GameEngine.log("Cyclic or missing dependencies detected; skipping mods: ${remaining.joinToString { it.metadata.id }}")
            }

            return sorted
        }
    */

    override fun close() {
        loadedMods.forEach { mod ->
            runCatching { mod.dispose() }.onFailure { e ->
                GameEngine.log("Error disposing mod ${mod.metadata.id}", e)
            }
            runCatching {
                val cl = (mod as? JvmMod)?.classLoader ?: return@runCatching
                if (cl is Closeable) cl.close()
            }.onFailure { e ->
                GameEngine.log("Error closing classloader for mod ${mod.metadata.id}", e)
            }
        }
        loadedMods.clear()
    }
}
