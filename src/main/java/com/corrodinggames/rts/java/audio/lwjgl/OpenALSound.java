package com.corrodinggames.rts.java.audio.lwjgl;

import com.corrodinggames.rts.java.audio.Sound;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.openal.AL10;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/OpenALSound.class */
public class OpenALSound implements Sound {
    private int bufferID = -1;
    private final OpenALAudio audio;
    private float duration;
    private int bytesUsed;

    public OpenALSound(OpenALAudio openALAudio) {
        this.audio = openALAudio;
    }

    void setup(byte[] bArr, int i, int i2) {
        int length = bArr.length - (bArr.length % (i > 1 ? 4 : 2));
        this.duration = (length / (2 * i)) / i2;
        ByteBuffer byteBufferAllocateDirect = ByteBuffer.allocateDirect(length);
        byteBufferAllocateDirect.order(ByteOrder.nativeOrder());
        byteBufferAllocateDirect.put(bArr, 0, length);
        byteBufferAllocateDirect.flip();
        this.bytesUsed = length;
        if (this.bufferID == -1) {
            this.bufferID = AL10.alGenBuffers();
            AL10.alBufferData(this.bufferID, i > 1 ? 4355 : 4353, byteBufferAllocateDirect.asShortBuffer(), i2);
        }
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public int getBytesUsed() {
        return this.bytesUsed;
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public long play() {
        return play(1.0f);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public long play(float f) {
        if (this.audio.noDevice) {
            return 0L;
        }
        int iObtainSource = this.audio.obtainSource(false);
        if (iObtainSource == -1) {
            return -1L;
        }
        this.audio.retain(this, false);
        if (iObtainSource == -1) {
            return -1L;
        }
        long soundId = this.audio.getSoundId(iObtainSource);
        AL10.alSourcei(iObtainSource, 4105, this.bufferID);
        AL10.alSourcei(iObtainSource, 4103, 0);
        AL10.alSourcef(iObtainSource, 4106, f);
        AL10.alSourcePlay(iObtainSource);
        return soundId;
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public long loop() {
        return loop(1.0f);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public long loop(float f) {
        if (this.audio.noDevice) {
            return 0L;
        }
        int iObtainSource = this.audio.obtainSource(false);
        if (iObtainSource == -1) {
            return -1L;
        }
        long soundId = this.audio.getSoundId(iObtainSource);
        AL10.alSourcei(iObtainSource, 4105, this.bufferID);
        AL10.alSourcei(iObtainSource, 4103, 1);
        AL10.alSourcef(iObtainSource, 4106, f);
        AL10.alSourcePlay(iObtainSource);
        return soundId;
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void stop() {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.stopSourcesWithBuffer(this.bufferID);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void dispose() {
        if (this.audio.noDevice || this.bufferID == -1) {
            return;
        }
        this.audio.freeBuffer(this.bufferID);
        AL10.alDeleteBuffers(this.bufferID);
        this.bufferID = -1;
        this.audio.forget(this);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void stop(long j) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.stopSound(j);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void pause() {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.pauseSourcesWithBuffer(this.bufferID);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void pause(long j) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.pauseSound(j);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void resume() {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.resumeSourcesWithBuffer(this.bufferID);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void resume(long j) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.resumeSound(j);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void setPitch(long j, float f) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundPitch(j, f);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void setVolume(long j, float f) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundGain(j, f);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void setLooping(long j, boolean z) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundLooping(j, z);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public void setPan(long j, float f, float f2) {
        if (this.audio.noDevice) {
            return;
        }
        this.audio.setSoundPan(j, f, f2);
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public long play(float f, float f2, float f3) {
        long jPlay = play();
        setPitch(jPlay, f2);
        setPan(jPlay, f3, f);
        return jPlay;
    }

    @Override // com.corrodinggames.rts.java.audio.Sound
    public long loop(float f, float f2, float f3) {
        long jLoop = loop();
        setPitch(jLoop, f2);
        setPan(jLoop, f3, f);
        return jLoop;
    }

    public float duration() {
        return this.duration;
    }
}
