package com.corrodinggames.rts.kool

import com.corrodinggames.rts.gameFramework.*
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10
import org.lwjgl.openal.ALC10
import org.lwjgl.openal.ALC11
import org.lwjgl.openal.ALCCapabilities
import org.lwjgl.stb.STBVorbis
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class KoolAudioLWJGL : KoolAudio {

    private var device: Long = MemoryUtil.NULL
    private var context: Long = MemoryUtil.NULL
    private var _hasDevice = false

    private var nextSoundId = 1
    private val sounds = mutableMapOf<Int, SoundData>()
    private var nextMusicId = 1
    private val musicTracks = mutableMapOf<Int, MusicData>()
    private val sourcePool = mutableListOf<Int>()
    private val activeSources = mutableListOf<SourceInfo>()

    // Music state
    private var currentMusicId: Int? = null
    private var musicVolume = 1f
    private var musicLooping = true
    private var musicPlaying = false

    private val allSources = mutableListOf<Int>()
    private var nextSourceIndex = 0

    override val hasDevice: Boolean get() = _hasDevice

    init {
        try {
            val defaultDeviceName = ALC10.alcGetString(MemoryUtil.NULL, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER)
            device = ALC10.alcOpenDevice(defaultDeviceName)
            if (device != MemoryUtil.NULL) {
                val attr = IntBuffer.allocate(0)
                context = ALC10.alcCreateContext(device, attr)
                ALC10.alcMakeContextCurrent(context)
                val alcCaps = AL.createCapabilities(ALCCapabilities(device).apply {
                    // ALC capabilities loaded
                })
                _hasDevice = AL10.alGetError() == AL10.AL_NO_ERROR

                // Create source pool
                for (i in 0 until 32) {
                    val src = AL10.alGenSources()
                    if (AL10.alGetError() == AL10.AL_NO_ERROR) {
                        allSources.add(src)
                        sourcePool.add(src)
                    }
                }

                // Set listener
                AL10.alListener3f(AL10.AL_POSITION, 0f, 0f, 0f)
                AL10.alListener3f(AL10.AL_VELOCITY, 0f, 0f, 0f)
                AL10.alListener3f(AL10.AL_ORIENTATION, 0f, 0f, -1f, 0f, 1f, 0f)
            }
        } catch (e: Exception) {
            _hasDevice = false
            System.err.println("OpenAL init failed: ${e.message}")
        }
    }

    override fun loadSound(filePath: String): KoolSoundHandle {
        val (buffer, format, sampleRate) = loadAudioFile(filePath)
        val alBuffer = AL10.alGenBuffers()
        AL10.alBufferData(alBuffer, format, buffer, sampleRate)
        val id = nextSoundId++
        sounds[id] = SoundData(alBuffer)
        return KoolSoundHandle(id)
    }

    override fun loadSound(pcmData: ByteArray, sampleRate: Int, numChannels: Int): KoolSoundHandle {
        val format = if (numChannels == 1) AL10.AL_FORMAT_MONO16 else AL10.AL_FORMAT_STEREO16
        val buf = BufferUtils.createByteBuffer(pcmData.size)
        buf.put(pcmData).flip()
        val alBuffer = AL10.alGenBuffers()
        AL10.alBufferData(alBuffer, format, buf, sampleRate)
        val id = nextSoundId++
        sounds[id] = SoundData(alBuffer)
        return KoolSoundHandle(id)
    }

    override fun playSound(sound: KoolSoundHandle, volume: Float, pitch: Float, pan: Float): Int {
        val data = sounds[sound.id] ?: return -1
        val source = allocateSource() ?: return -1

        AL10.alSourcei(source, AL10.AL_BUFFER, data.alBuffer)
        AL10.alSourcef(source, AL10.AL_GAIN, volume.coerceIn(0f, 1f))
        AL10.alSourcef(source, AL10.AL_PITCH, pitch.coerceIn(0.5f, 2f))
        AL10.alSource3f(source, AL10.AL_POSITION, pan, 0f, -1f)
        AL10.alSourcePlay(source)

        activeSources.add(SourceInfo(source, sound.id))
        return source
    }

    override fun stopSound(sourceId: Int) {
        AL10.alSourceStop(sourceId)
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, 0)
        releaseSource(sourceId)
    }

    override fun setSoundVolume(sourceId: Int, volume: Float) {
        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume.coerceIn(0f, 1f))
    }

    override fun loadMusic(filePath: String): KoolMusicHandle {
        val (buffer, format, sampleRate) = loadAudioFile(filePath)
        val alBuffer = AL10.alGenBuffers()
        AL10.alBufferData(alBuffer, format, buffer, sampleRate)
        val id = nextMusicId++
        musicTracks[id] = MusicData(alBuffer)
        return KoolMusicHandle(id)
    }

    override fun playMusic(music: KoolMusicHandle, looping: Boolean, volume: Float) {
        stopMusic()
        val data = musicTracks[music.id] ?: return
        currentMusicId = music.id
        musicLooping = looping
        musicVolume = volume

        val source = allocateSource() ?: return
        AL10.alSourcei(source, AL10.AL_BUFFER, data.alBuffer)
        AL10.alSourcef(source, AL10.AL_GAIN, volume.coerceIn(0f, 1f))
        AL10.alSourcei(source, AL10.AL_LOOPING, if (looping) AL10.AL_TRUE else AL10.AL_FALSE)
        AL10.alSourcePlay(source)
        musicPlaying = true
    }

    override fun pauseMusic() {
        if (musicPlaying) {
            // Find the music source and pause it
            activeSources.forEach { AL10.alSourcePause(it.source) }
            musicPlaying = false
        }
    }

    override fun resumeMusic() {
        if (!musicPlaying) {
            activeSources.forEach { AL10.alSourcePlay(it.source) }
            musicPlaying = true
        }
    }

    override fun stopMusic() {
        stopSourcesForMusic()
        currentMusicId = null
        musicPlaying = false
    }

    override fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        // Update any active music sources
        activeSources.forEach { AL10.alSourcef(it.source, AL10.AL_GAIN, musicVolume) }
    }

    override fun update() {
        // Clean up finished sources
        val finished = mutableListOf<SourceInfo>()
        activeSources.forEach { info ->
            val state = AL10.alGetSourcei(info.source, AL10.AL_SOURCE_STATE)
            if (state == AL10.AL_STOPPED) {
                finished.add(info)
            }
        }
        finished.forEach {
            AL10.alSourcei(it.source, AL10.AL_BUFFER, 0)
            sourcePool.add(it.source)
            activeSources.remove(it)
        }
    }

    override fun deleteSound(sound: KoolSoundHandle) {
        val data = sounds.remove(sound.id) ?: return
        AL10.alDeleteBuffers(data.alBuffer)
    }

    override fun deleteMusic(music: KoolMusicHandle) {
        val data = musicTracks.remove(music.id) ?: return
        if (currentMusicId == music.id) stopMusic()
        AL10.alDeleteBuffers(data.alBuffer)
    }

    fun dispose() {
        stopMusic()
        activeSources.forEach {
            AL10.alSourceStop(it.source)
            AL10.alSourcei(it.source, AL10.AL_BUFFER, 0)
        }
        activeSources.clear()
        allSources.forEach { AL10.alDeleteSources(it) }
        sourcePool.clear()
        sounds.values.forEach { AL10.alDeleteBuffers(it.alBuffer) }
        sounds.clear()
        musicTracks.values.forEach { AL10.alDeleteBuffers(it.alBuffer) }
        musicTracks.clear()

        if (context != MemoryUtil.NULL) {
            ALC10.alcDestroyContext(context)
        }
        if (device != MemoryUtil.NULL) {
            ALC10.alcCloseDevice(device)
        }
    }

    // ======================== Private ========================

    private fun allocateSource(): Int? {
        // Recycle finished sources
        update()

        if (sourcePool.isNotEmpty()) {
            return sourcePool.removeAt(sourcePool.size - 1)
        }
        return null
    }

    private fun releaseSource(source: Int) {
        activeSources.removeAll { it.source == source }
        if (source !in sourcePool) {
            sourcePool.add(source)
        }
    }

    private fun stopSourcesForMusic() {
        val toStop = activeSources.filter { it.musicId == currentMusicId }
        toStop.forEach {
            AL10.alSourceStop(it.source)
            AL10.alSourcei(it.source, AL10.AL_BUFFER, 0)
            sourcePool.add(it.source)
            activeSources.remove(it)
        }
    }

    private data class SoundData(val alBuffer: Int)
    private data class MusicData(val alBuffer: Int)
    private data class SourceInfo(val source: Int, val musicId: Int?)

    companion object {
        fun loadAudioFile(path: String): Triple<ByteBuffer, Int, Int> {
            val file = File(path)
            if (!file.exists()) throw RuntimeException("Audio file not found: $path")

            return when {
                path.lowercase().endsWith(".ogg") -> loadOgg(file)
                path.lowercase().endsWith(".wav") -> loadWav(file)
                else -> throw RuntimeException("Unsupported audio format: $path")
            }
        }

        private fun loadOgg(file: File): Triple<ByteBuffer, Int, Int> {
            MemoryStack.stackPush().use { stack ->
                val channels = stack.mallocInt(1)
                val sampleRate = stack.mallocInt(1)
                val decoded: ShortBuffer = STBVorbis.stb_vorbis_decode_filename(file.absolutePath, channels, sampleRate)
                    ?: throw RuntimeException("Failed to decode OGG: ${file.name}")
                val format = if (channels[0] == 1) AL10.AL_FORMAT_MONO16 else AL10.AL_FORMAT_STEREO16
                val buf = BufferUtils.createByteBuffer(decoded.remaining() * 2)
                buf.asShortBuffer().put(decoded).flip()
                return Triple(buf, format, sampleRate[0])
            }
        }

        private fun loadWav(file: File): Triple<ByteBuffer, Int, Int> {
            val ais = AudioSystem.getAudioInputStream(file)
            val format = ais.format
            val data = readAllBytes(ais)

            val openALFormat = when {
                format.channels == 1 && format.sampleSizeInBits == 8 -> AL10.AL_FORMAT_MONO8
                format.channels == 1 && format.sampleSizeInBits == 16 -> AL10.AL_FORMAT_MONO16
                format.channels == 2 && format.sampleSizeInBits == 8 -> AL10.AL_FORMAT_STEREO8
                format.channels == 2 && format.sampleSizeInBits == 16 -> AL10.AL_FORMAT_STEREO16
                else -> throw RuntimeException("Unsupported WAV format")
            }

            val buf = BufferUtils.createByteBuffer(data.size)
            buf.put(data).flip()
            return Triple(buf, openALFormat, format.sampleRate.toInt())
        }

        private fun readAllBytes(ais: AudioInputStream): ByteArray {
            val baos = java.io.ByteArrayOutputStream()
            val buffer = ByteArray(4096)
            var read: Int
            while (ais.read(buffer).also { read = it } != -1) {
                baos.write(buffer, 0, read)
            }
            return baos.toByteArray()
        }
    }
}