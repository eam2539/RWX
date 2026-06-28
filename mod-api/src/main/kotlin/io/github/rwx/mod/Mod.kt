package io.github.rwx.mod

interface Mod {
    val metadata: ModMetadata
    val type: ModType

    fun init()

    fun dispose() {}
}

enum class ModType {
    JVM,
    INI
}

open class ModMetadata {
    open var path: String = ""
    open var id: String = ""
    open var name: String = ""
    var author: String = ""
    var version: String = ""
    open var minGameVersionName: String = "1.0.0"
    open var description: String = ""
    var dependencies: List<String> = emptyList()
    var priority: Int = 0
}
