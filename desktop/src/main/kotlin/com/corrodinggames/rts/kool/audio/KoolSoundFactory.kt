package com.corrodinggames.rts.kool.audio

import com.corrodinggames.rts.gameFramework.KoolEngine
import com.corrodinggames.rts.gameFramework.KoolSoundHandle
import com.corrodinggames.rts.gameFramework.audio.Sound
import com.corrodinggames.rts.gameFramework.audio.SoundFactory
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream
import android.content.Context
import com.corrodinggames.rts.kool.KoolBridge
import java.io.File

/**
 * SoundFactory implementation that uses kool-engine's KoolAudio.
 */
class KoolSoundFactory : SoundFactory() {

    override fun a(id: Int): Sound {
        return KoolSoundImpl(id.toString())
    }

    override fun a(name: String, assetInputStream: AssetInputStream, managed: Boolean): Sound {
        // Write the stream to a temp file so kool-audio can load it
        val tempFile = File.createTempFile("kool_sound_", ".ogg")
        tempFile.deleteOnExit()
        assetInputStream.copyTo(tempFile.outputStream())
        return KoolSoundImpl(name).apply {
            handle = tempFile.absolutePath.let { path ->
                try {
                    KoolBridge.audio.loadSound(path)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override fun a(context: Context) {
        // Not needed on desktop
    }
}

class KoolSoundImpl(
    val name: String,
) : Sound {
    var handle: KoolSoundHandle? = null

    override fun play(volume: Float, pitch: Float, pan: Float) {
        val h = handle ?: return
        KoolBridge.audio.playSound(h, volume, pitch, pan)
    }

    override fun stop() {
        // Stopping all instances of this sound would need tracking
    }

    override fun dispose() {
        handle?.let { KoolBridge.audio.deleteSound(it) }
        handle = null
    }

    override fun getId(): String = name
}