package com.corrodinggames.rts.gameFramework.mod

interface Mod {
    val metadata: ModMetadata
    val type: ModType

    fun initialize()

    fun registerEventListeners(eventBus: ModEventBus)

    fun dispose()
}

enum class ModType {
    JVM,
    INI,
    MIXED //JS+INI
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

interface ModEventBus {
//
//    fun post(event: GameEvent)
//
//    fun postImmediate(event: GameEvent)
//
//    fun <T : GameEvent> subscribe(eventClass: Class<T>, listener: EventListener<T>)
//    fun <T : GameEvent> unsubscribe(eventClass: Class<T>, listener: EventListener<T>)
}