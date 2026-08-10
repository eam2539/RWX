package io.github.rwx.mod

import com.corrodinggames.rts.gameFramework.GameEngine
import io.github.rwx.mod.api.ResourcePath
import kotlin.math.sqrt

object ModAudioRegistry {
    private class RegisteredSound(
        val owner: ApiImpl,
        val backendId: String,
        val path: ResourcePath,
        val minimumVolume: Float,
        var loaded: Boolean = false,
    )

    private val sounds = linkedMapOf<String, RegisteredSound>()

    fun registerSound(owner: ApiImpl, id: String, file: ResourcePath, properties: Map<String, Any?>) {
        require(id.isNotBlank() && id == id.trim()) { "Sound id must be non-blank and trimmed" }
        val key = key(owner, id)
        check(key !in sounds) { "Sound is already registered: $id" }
        val backendId = "${owner.metadata.id}:$id"
        val minimumVolume = (properties["minimumVolume"] as? Number)?.toFloat() ?: 0f
        require(minimumVolume in 0f..1f) { "Sound minimumVolume must be between 0 and 1: $id" }
        sounds[key] = RegisteredSound(owner, backendId, file, minimumVolume)
    }

    fun materialize(owner: ApiImpl) {
        sounds.values.filter { it.owner === owner }.forEach(::materialize)
    }

    fun playSound(owner: ApiImpl, id: String, x: Float?, y: Float?, volume: Float) {
        val sound = sounds[key(owner, id)] ?: error("Sound is not registered: $id")
        materialize(sound)
        val engine = GameEngine.getInstance()
        var resolvedVolume = volume.coerceAtLeast(0f)
        var pan = 0f
        if (engine != null && x != null && y != null) {
            val centerX = engine.viewpointXSnapped + engine.halfVisibleWorldWidth
            val centerY = engine.viewpointYSnapped + engine.halfVisibleWorldHeight
            val dx = x - centerX
            val dy = y - centerY
            val hearingRadius = engine.halfVisibleWorldWidth.coerceAtLeast(1f)
            val distance = sqrt(dx * dx + dy * dy)
            val distanceVolume = (1f - ((distance - hearingRadius) / hearingRadius)).coerceIn(0f, 1f)
            resolvedVolume *= distanceVolume.coerceAtLeast(sound.minimumVolume)
            pan = (dx / hearingRadius).coerceIn(-1f, 1f)
        }
        if (engine?.settingsEngine != null) {
            resolvedVolume *= engine.settingsEngine.masterVolume * engine.settingsEngine.gameVolume
            if (!engine.settingsEngine.enableSounds) resolvedVolume = 0f
        }
        if (resolvedVolume >= 0.01f) {
            owner.platformBridge?.audio?.playSound(sound.backendId, resolvedVolume.coerceAtMost(1f), pan)
        }
    }

    fun unregister(owner: ApiImpl) {
        sounds.entries.removeAll { (_, sound) ->
            if (sound.owner === owner) {
                if (sound.loaded) owner.platformBridge?.audio?.unregisterSound(sound.backendId)
                true
            } else {
                false
            }
        }
    }

    private fun materialize(sound: RegisteredSound) {
        if (sound.loaded) return
        val audio = sound.owner.platformBridge?.audio ?: return
        val data = sound.owner.openPackagedResource(sound.path).use { it.readBytes() }
        audio.registerSound(sound.backendId, data, sound.path.value)
        sound.loaded = true
    }

    private fun key(owner: ApiImpl, id: String): String = "${owner.metadata.id}\u0000$id"
}
