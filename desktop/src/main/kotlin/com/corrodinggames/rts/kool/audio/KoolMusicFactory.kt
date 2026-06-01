package com.corrodinggames.rts.kool.audio

import com.corrodinggames.rts.gameFramework.Music
import com.corrodinggames.rts.gameFramework.MusicFactory
import com.corrodinggames.rts.gameFramework.MusicManager
import com.corrodinggames.rts.gameFramework.MusicTrack
import com.corrodinggames.rts.gameFramework.KoolMusicHandle
import com.corrodinggames.rts.kool.KoolBridge

/**
 * MusicFactory implementation that uses kool-engine's KoolAudio.
 */
class KoolMusicFactory : MusicFactory() {

    private var activeMusic: KoolMusicHandle? = null

    override fun a(path: String): Music {
        return KoolMusicImpl(path, this)
    }

    override fun a(): MusicTrack {
        return KoolMusicTrack(this)
    }

    override fun a(musicManager: MusicManager) {
        this.musicManager = musicManager
    }

    override fun release() {
        stopAll()
    }

    override fun isMusicPlaying(): Boolean {
        return activeMusic != null
    }

    override fun isThreaded(): Boolean {
        return false
    }

    override fun isMusicSupported(): Int {
        return if (KoolBridge.audio.hasDevice) 1 else 0
    }

    fun playTrack(path: String, looping: Boolean, volume: Float) {
        stopAll()
        try {
            val music = KoolBridge.audio.loadMusic(path)
            activeMusic = music
            KoolBridge.audio.playMusic(music, looping, volume)
        } catch (e: Exception) {
            System.err.println("Failed to play music: ${e.message}")
        }
    }

    fun stopAll() {
        activeMusic?.let {
            KoolBridge.audio.stopMusic()
            KoolBridge.audio.deleteMusic(it)
        }
        activeMusic = null
    }
}

class KoolMusicImpl(
    private val path: String,
    private val factory: KoolMusicFactory,
) : Music {

    override fun play(loop: Boolean, volume: Float) {
        factory.playTrack(path, loop, volume)
    }

    override fun stop() {
        factory.stopAll()
    }

    override fun dispose() {
        factory.stopAll()
    }
}

class KoolMusicTrack(
    private val factory: KoolMusicFactory,
) : MusicTrack {

    override fun play(song: String, looping: Boolean, volume: Float) {
        factory.playTrack(song, looping, volume)
    }

    override fun stop() {
        factory.stopAll()
    }

    override fun dispose() {
        factory.stopAll()
    }

    override fun isPlaying(): Boolean = factory.isMusicPlaying()
}