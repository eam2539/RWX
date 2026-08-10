package com.corrodinggames.rts.java.audio.lwjgl;

import com.corrodinggames.rts.java.audio.AudioDevice;
import com.corrodinggames.rts.java.audio.util.AudioException;
import com.corrodinggames.rts.java.audio.util.MathUtils;
import org.lwjgl.BufferUtils;
import org.lwjglx.openal.AL10;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/OpenALAudioDevice.class */
public class OpenALAudioDevice implements AudioDevice {
    private static final int bytesPerSample = 2;
    private final OpenALAudio audio;
    private final int channels;
    private IntBuffer buffers;
    private int format;
    private int sampleRate;
    private boolean isPlaying;
    private float renderedSeconds;
    private float secondsPerBuffer;
    private byte[] bytes;
    private final int bufferSize;
    private final int bufferCount;
    private final ByteBuffer tempBuffer;
    private int sourceID = -1;
    private float volume = 1.0f;

    public OpenALAudioDevice(OpenALAudio openALAudio, int i, boolean z, int i2, int i3) {
        this.audio = openALAudio;
        this.channels = z ? 1 : 2;
        this.bufferSize = i2;
        this.bufferCount = i3;
        this.format = this.channels > 1 ? 4355 : 4353;
        this.sampleRate = i;
        this.secondsPerBuffer = ((i2 / 2.0f) / this.channels) / i;
        this.tempBuffer = BufferUtils.createByteBuffer(i2);
    }

    @Override // com.corrodinggames.rts.java.audio.AudioDevice
    public void writeSamples(short[] sArr, int i, int i2) {
        if (this.bytes == null || this.bytes.length < i2 * 2) {
            this.bytes = new byte[i2 * 2];
        }
        int iMin = Math.min(i + i2, sArr.length);
        int i3 = 0;
        for (int i4 = i; i4 < iMin; i4++) {
            short s = sArr[i4];
            int i5 = i3;
            int i6 = i3 + 1;
            this.bytes[i5] = (byte) (s & 255);
            i3 = i6 + 1;
            this.bytes[i6] = (byte) ((s >> 8) & 255);
        }
        writeSamples(this.bytes, 0, i2 * 2);
    }

    @Override // com.corrodinggames.rts.java.audio.AudioDevice
    public void writeSamples(float[] fArr, int i, int i2) {
        if (this.bytes == null || this.bytes.length < i2 * 2) {
            this.bytes = new byte[i2 * 2];
        }
        int iMin = Math.min(i + i2, fArr.length);
        int i3 = 0;
        for (int i4 = i; i4 < iMin; i4++) {
            int iA = (int) (MathUtils.a(fArr[i4], -1.0f, 1.0f) * 32767.0f);
            int i5 = i3;
            int i6 = i3 + 1;
            this.bytes[i5] = (byte) (iA & 255);
            i3 = i6 + 1;
            this.bytes[i6] = (byte) ((iA >> 8) & 255);
        }
        writeSamples(this.bytes, 0, i2 * 2);
    }

    public void writeSamples(byte[] bArr, int i, int i2) {
        if (i2 < 0) {
            throw new IllegalArgumentException("length cannot be < 0.");
        }
        if (this.sourceID == -1) {
            this.sourceID = this.audio.obtainSource(true);
            if (this.sourceID == -1) {
                return;
            }
            if (this.buffers == null) {
                this.buffers = BufferUtils.createIntBuffer(this.bufferCount);
                AL10.alGenBuffers(this.buffers);
                if (AL10.alGetError() != 0) {
                    throw new AudioException("Unabe to allocate audio buffers.");
                }
            }
            AL10.alSourcei(this.sourceID, 4103, 0);
            AL10.alSourcef(this.sourceID, 4106, this.volume);
            int i3 = 0;
            for (int i4 = 0; i4 < this.bufferCount; i4++) {
                int i5 = this.buffers.get(i4);
                int iMin = Math.min(this.bufferSize, i2);
                this.tempBuffer.clear();
                this.tempBuffer.put(bArr, i, iMin).flip();
                AL10.alBufferData(i5, this.format, this.tempBuffer, this.sampleRate);
                AL10.alSourceQueueBuffers(this.sourceID, i5);
                i2 -= iMin;
                i += iMin;
                i3++;
            }
            this.tempBuffer.clear().flip();
            for (int i6 = i3; i6 < this.bufferCount; i6++) {
                int i7 = this.buffers.get(i6);
                AL10.alBufferData(i7, this.format, this.tempBuffer, this.sampleRate);
                AL10.alSourceQueueBuffers(this.sourceID, i7);
            }
            AL10.alSourcePlay(this.sourceID);
            this.isPlaying = true;
        }
        while (i2 > 0) {
            int iFillBuffer = fillBuffer(bArr, i, i2);
            i2 -= iFillBuffer;
            i += iFillBuffer;
        }
    }

    private int fillBuffer(byte[] bArr, int i, int i2) {
        int iAlSourceUnqueueBuffers;
        int iMin = Math.min(this.bufferSize, i2);
        while (true) {
            int iAlGetSourcei = AL10.alGetSourcei(this.sourceID, 4118);
            int i3 = iAlGetSourcei - 1;
            if (iAlGetSourcei > 0 && (iAlSourceUnqueueBuffers = AL10.alSourceUnqueueBuffers(this.sourceID)) != 40963) {
                break;
            }
            try {
                Thread.sleep((long) (1000.0f * this.secondsPerBuffer));
            } catch (InterruptedException e) {
            }
        }
        this.renderedSeconds += this.secondsPerBuffer;
        this.tempBuffer.clear();
        this.tempBuffer.put(bArr, i, iMin).flip();
        AL10.alBufferData(iAlSourceUnqueueBuffers, this.format, this.tempBuffer, this.sampleRate);
        AL10.alSourceQueueBuffers(this.sourceID, iAlSourceUnqueueBuffers);
        if (!this.isPlaying || AL10.alGetSourcei(this.sourceID, 4112) != 4114) {
            AL10.alSourcePlay(this.sourceID);
            this.isPlaying = true;
        }
        return iMin;
    }

    public void stop() {
        if (this.sourceID == -1) {
            return;
        }
        this.audio.freeSource(this.sourceID);
        this.sourceID = -1;
        this.renderedSeconds = 0.0f;
        this.isPlaying = false;
    }

    public boolean isPlaying() {
        if (this.sourceID == -1) {
            return false;
        }
        return this.isPlaying;
    }

    @Override // com.corrodinggames.rts.java.audio.AudioDevice
    public void setVolume(float f) {
        this.volume = f;
        if (this.sourceID != -1) {
            AL10.alSourcef(this.sourceID, 4106, f);
        }
    }

    public float getPosition() {
        if (this.sourceID == -1) {
            return 0.0f;
        }
        return this.renderedSeconds + AL10.alGetSourcef(this.sourceID, 4132);
    }

    public void setPosition(float f) {
        this.renderedSeconds = f;
    }

    public int getChannels() {
        return this.format == 4355 ? 2 : 1;
    }

    public int getRate() {
        return this.sampleRate;
    }

    @Override // com.corrodinggames.rts.java.audio.AudioDevice
    public void dispose() {
        if (this.buffers == null) {
            return;
        }
        if (this.sourceID != -1) {
            this.audio.freeSource(this.sourceID);
            this.sourceID = -1;
        }
        AL10.alDeleteBuffers(this.buffers);
        this.buffers = null;
    }

    @Override // com.corrodinggames.rts.java.audio.AudioDevice
    public boolean isMono() {
        return this.channels == 1;
    }

    @Override // com.corrodinggames.rts.java.audio.AudioDevice
    public int getLatency() {
        return (int) (this.secondsPerBuffer * this.bufferCount * 1000.0f);
    }
}
