package com.corrodinggames.rts.java.audio.lwjgl;

import com.corrodinggames.rts.java.audio.Audio;
import com.corrodinggames.rts.java.audio.AudioDevice;
import com.corrodinggames.rts.java.audio.AudioRecorder;
import com.corrodinggames.rts.java.audio.lwjgl.Ogg;
import com.corrodinggames.rts.java.audio.lwjgl.Wav;
import com.corrodinggames.rts.java.audio.util.AudioException;
import com.corrodinggames.rts.java.audio.util.AudioFile;
import com.corrodinggames.rts.java.audio.util.IntArray;
import com.corrodinggames.rts.java.audio.util.IntObjectMap;
import com.corrodinggames.rts.java.audio.util.LongObjectMap;
import com.corrodinggames.rts.java.audio.util.MathUtils;
import com.corrodinggames.rts.java.audio.util.ObjectMap;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Locale;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/OpenALAudio.class */
public class OpenALAudio implements Audio {
    private final int deviceBufferSize;
    private final int deviceBufferCount;
    private IntArray idleSources;
    private IntArray allSources;
    private LongObjectMap soundIdToSource;
    private IntObjectMap sourceToSoundId;
    private long nextSoundId;
    private ObjectMap extensionToSoundClass;
    private ObjectMap extensionToMusicClass;
    private OpenALSound[] recentSounds;
    private int mostRecetSound;
    ArrayList music;
    boolean noDevice;

    public OpenALAudio() {
        this(16, 9, 512);
    }

    public OpenALAudio(int i, int i2, int i3) {
        this.nextSoundId = 0L;
        this.extensionToSoundClass = new ObjectMap();
        this.extensionToMusicClass = new ObjectMap();
        this.mostRecetSound = -1;
        this.music = new ArrayList();
        this.noDevice = false;
        this.deviceBufferSize = i3;
        this.deviceBufferCount = i2;
        registerSound("ogg", Ogg.Sound.class);
        registerMusic("ogg", Ogg.Music.class);
        registerSound("wav", Wav.Sound.class);
        registerMusic("wav", Wav.Music.class);
        try {
            AL.create();
            this.allSources = new IntArray(false, i);
            for (int i4 = 0; i4 < i; i4++) {
                int iAlGenSources = AL10.alGenSources();
                if (AL10.alGetError() != 0) {
                    break;
                }
                this.allSources.a(iAlGenSources);
            }
            this.idleSources = new IntArray(this.allSources);
            this.soundIdToSource = new LongObjectMap();
            this.sourceToSoundId = new IntObjectMap();
            AL10.alListener(4111, (FloatBuffer) BufferUtils.createFloatBuffer(6).put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f}).flip());
            AL10.alListener(4102, (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip());
            AL10.alListener(4100, (FloatBuffer) BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f}).flip());
            this.recentSounds = new OpenALSound[i];
        } catch (LWJGLException e) {
            this.noDevice = true;
            e.printStackTrace();
        } catch (NullPointerException e2) {
            this.noDevice = true;
            e2.printStackTrace();
        } catch (OpenALException e3) {
            this.noDevice = true;
            e3.printStackTrace();
        }
    }

    public void registerSound(String str, Class cls) {
        if (str == null) {
            throw new IllegalArgumentException("extension cannot be null.");
        }
        if (cls == null) {
            throw new IllegalArgumentException("soundClass cannot be null.");
        }
        this.extensionToSoundClass.a(str, cls);
    }

    public void registerMusic(String str, Class cls) {
        if (str == null) {
            throw new IllegalArgumentException("extension cannot be null.");
        }
        if (cls == null) {
            throw new IllegalArgumentException("musicClass cannot be null.");
        }
        this.extensionToMusicClass.a(str, cls);
    }

    @Override // com.corrodinggames.rts.java.audio.Audio
    public OpenALSound newSound(AudioFile audioFile) {
        if (audioFile == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        Class cls = (Class) this.extensionToSoundClass.a(audioFile.b().toLowerCase(Locale.ROOT));
        if (cls == null) {
            throw new AudioException("Unknown file extension for sound: " + audioFile);
        }
        try {
            return (OpenALSound) cls.getConstructor(OpenALAudio.class, AudioFile.class).newInstance(this, audioFile);
        } catch (Exception e) {
            throw new AudioException("Error creating sound " + cls.getName() + " for file: " + audioFile, e);
        }
    }

    @Override // com.corrodinggames.rts.java.audio.Audio
    public OpenALMusic newMusic(AudioFile audioFile) {
        if (audioFile == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        Class cls = (Class) this.extensionToMusicClass.a(audioFile.b().toLowerCase(Locale.ROOT));
        if (cls == null) {
            throw new AudioException("Unknown file extension for music: " + audioFile);
        }
        try {
            return (OpenALMusic) cls.getConstructor(OpenALAudio.class, AudioFile.class).newInstance(this, audioFile);
        } catch (Exception e) {
            throw new AudioException("Error creating music " + cls.getName() + " for file: " + audioFile, e);
        }
    }

    int obtainSource(boolean z) {
        if (this.noDevice) {
            return 0;
        }
        int i = this.idleSources.b;
        for (int i2 = 0; i2 < i; i2++) {
            int iB = this.idleSources.b(i2);
            int iAlGetSourcei = AL10.alGetSourcei(iB, 4112);
            if (iAlGetSourcei != 4114 && iAlGetSourcei != 4115) {
                if (z) {
                    this.idleSources.c(i2);
                } else {
                    if (this.sourceToSoundId.e(iB)) {
                        long jLongValue = ((Long) this.sourceToSoundId.a(iB)).longValue();
                        this.sourceToSoundId.b(iB);
                        this.soundIdToSource.b(jLongValue);
                    }
                    long j = this.nextSoundId;
                    this.nextSoundId = j + 1;
                    this.sourceToSoundId.a(iB, Long.valueOf(j));
                    this.soundIdToSource.a(j, Integer.valueOf(iB));
                }
                AL10.alSourceStop(iB);
                AL10.alSourcei(iB, 4105, 0);
                AL10.alSourcef(iB, 4106, 1.0f);
                AL10.alSourcef(iB, 4099, 1.0f);
                AL10.alSource3f(iB, 4100, 0.0f, 0.0f, 1.0f);
                return iB;
            }
        }
        return -1;
    }

    void freeSource(int i) {
        if (this.noDevice) {
            return;
        }
        AL10.alSourceStop(i);
        AL10.alSourcei(i, 4105, 0);
        if (this.sourceToSoundId.e(i)) {
            this.soundIdToSource.b(((Long) this.sourceToSoundId.b(i)).longValue());
        }
        this.idleSources.a(i);
    }

    void freeBuffer(int i) {
        if (this.noDevice) {
            return;
        }
        int i2 = this.idleSources.b;
        for (int i3 = 0; i3 < i2; i3++) {
            int iB = this.idleSources.b(i3);
            if (AL10.alGetSourcei(iB, 4105) == i) {
                if (this.sourceToSoundId.e(iB)) {
                    this.soundIdToSource.b(((Long) this.sourceToSoundId.b(iB)).longValue());
                }
                AL10.alSourceStop(iB);
                AL10.alSourcei(iB, 4105, 0);
            }
        }
    }

    void stopSourcesWithBuffer(int i) {
        if (this.noDevice) {
            return;
        }
        int i2 = this.idleSources.b;
        for (int i3 = 0; i3 < i2; i3++) {
            int iB = this.idleSources.b(i3);
            if (AL10.alGetSourcei(iB, 4105) == i) {
                if (this.sourceToSoundId.e(iB)) {
                    this.soundIdToSource.b(((Long) this.sourceToSoundId.b(iB)).longValue());
                }
                AL10.alSourceStop(iB);
            }
        }
    }

    void pauseSourcesWithBuffer(int i) {
        if (this.noDevice) {
            return;
        }
        int i2 = this.idleSources.b;
        for (int i3 = 0; i3 < i2; i3++) {
            int iB = this.idleSources.b(i3);
            if (AL10.alGetSourcei(iB, 4105) == i) {
                AL10.alSourcePause(iB);
            }
        }
    }

    void resumeSourcesWithBuffer(int i) {
        if (this.noDevice) {
            return;
        }
        int i2 = this.idleSources.b;
        for (int i3 = 0; i3 < i2; i3++) {
            int iB = this.idleSources.b(i3);
            if (AL10.alGetSourcei(iB, 4105) == i && AL10.alGetSourcei(iB, 4112) == 4115) {
                AL10.alSourcePlay(iB);
            }
        }
    }

    public void backgroundUpdate() {
        if (this.noDevice) {
            return;
        }
        for (int i = 0; i < this.music.size(); i++) {
            ((OpenALMusic) this.music.get(i)).backgroundUpdate();
        }
    }

    public boolean hasDevice() {
        return this.noDevice;
    }

    public void update() {
        if (this.noDevice) {
            return;
        }
        for (int i = 0; i < this.music.size(); i++) {
            ((OpenALMusic) this.music.get(i)).update();
        }
    }

    public long getSoundId(int i) {
        if (this.sourceToSoundId.e(i)) {
            return ((Long) this.sourceToSoundId.a(i)).longValue();
        }
        return -1L;
    }

    public void stopSound(long j) {
        if (this.soundIdToSource.d(j)) {
            AL10.alSourceStop(((Integer) this.soundIdToSource.a(j)).intValue());
        }
    }

    public void pauseSound(long j) {
        if (this.soundIdToSource.d(j)) {
            AL10.alSourcePause(((Integer) this.soundIdToSource.a(j)).intValue());
        }
    }

    public void resumeSound(long j) {
        if (this.soundIdToSource.d(j)) {
            int iIntValue = ((Integer) this.soundIdToSource.a(j)).intValue();
            if (AL10.alGetSourcei(iIntValue, 4112) == 4115) {
                AL10.alSourcePlay(iIntValue);
            }
        }
    }

    public void setSoundGain(long j, float f) {
        if (this.soundIdToSource.d(j)) {
            AL10.alSourcef(((Integer) this.soundIdToSource.a(j)).intValue(), 4106, f);
        }
    }

    public void setSoundLooping(long j, boolean z) {
        if (this.soundIdToSource.d(j)) {
            AL10.alSourcei(((Integer) this.soundIdToSource.a(j)).intValue(), 4103, z ? 1 : 0);
        }
    }

    public void setSoundPitch(long j, float f) {
        if (this.soundIdToSource.d(j)) {
            AL10.alSourcef(((Integer) this.soundIdToSource.a(j)).intValue(), 4099, f);
        }
    }

    public void setSoundPan(long j, float f, float f2) {
        if (this.soundIdToSource.d(j)) {
            int iIntValue = ((Integer) this.soundIdToSource.a(j)).intValue();
            AL10.alSource3f(iIntValue, 4100, MathUtils.b(((f - 1.0f) * 3.1415927f) / 2.0f), 0.0f, MathUtils.a(((f + 1.0f) * 3.1415927f) / 2.0f));
            AL10.alSourcef(iIntValue, 4106, f2);
        }
    }

    public void dispose() {
        if (this.noDevice) {
            return;
        }
        int i = this.allSources.b;
        for (int i2 = 0; i2 < i; i2++) {
            int iB = this.allSources.b(i2);
            if (AL10.alGetSourcei(iB, 4112) != 4116) {
                AL10.alSourceStop(iB);
            }
            AL10.alDeleteSources(iB);
        }
        this.sourceToSoundId.a();
        this.soundIdToSource.a();
        AL.destroy();
        while (AL.isCreated()) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override // com.corrodinggames.rts.java.audio.Audio
    public AudioDevice newAudioDevice(int i, final boolean z) {
        return this.noDevice ? new AudioDevice() { // from class: com.corrodinggames.rts.java.audio.lwjgl.OpenALAudio.1
            @Override // com.corrodinggames.rts.java.audio.AudioDevice
            public void writeSamples(float[] fArr, int i2, int i3) {
            }

            @Override // com.corrodinggames.rts.java.audio.AudioDevice
            public void writeSamples(short[] sArr, int i2, int i3) {
            }

            @Override // com.corrodinggames.rts.java.audio.AudioDevice
            public void setVolume(float f) {
            }

            @Override // com.corrodinggames.rts.java.audio.AudioDevice
            public boolean isMono() {
                return z;
            }

            @Override // com.corrodinggames.rts.java.audio.AudioDevice
            public int getLatency() {
                return 0;
            }

            @Override // com.corrodinggames.rts.java.audio.AudioDevice
            public void dispose() {
            }
        } : new OpenALAudioDevice(this, i, z, this.deviceBufferSize, this.deviceBufferCount);
    }

    @Override // com.corrodinggames.rts.java.audio.Audio
    public AudioRecorder newAudioRecorder(int i, boolean z) {
        return this.noDevice ? new AudioRecorder() { // from class: com.corrodinggames.rts.java.audio.lwjgl.OpenALAudio.2
            @Override // com.corrodinggames.rts.java.audio.AudioRecorder
            public void read(short[] sArr, int i2, int i3) {
            }

            @Override // com.corrodinggames.rts.java.audio.AudioRecorder
            public void dispose() {
            }
        } : new JavaSoundAudioRecorder(i, z);
    }

    protected void retain(OpenALSound openALSound, boolean z) {
        this.mostRecetSound++;
        this.mostRecetSound %= this.recentSounds.length;
        if (z && this.recentSounds[this.mostRecetSound] != null) {
            this.recentSounds[this.mostRecetSound].stop();
        }
        this.recentSounds[this.mostRecetSound] = openALSound;
    }

    public void forget(OpenALSound openALSound) {
        for (int i = 0; i < this.recentSounds.length; i++) {
            if (this.recentSounds[i] == openALSound) {
                this.recentSounds[i] = null;
            }
        }
    }
}
