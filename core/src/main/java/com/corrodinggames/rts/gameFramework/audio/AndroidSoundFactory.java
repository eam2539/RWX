package com.corrodinggames.rts.gameFramework.audio;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.sound.AndroidSound;
import com.corrodinggames.rts.gameFramework.sound.SoundRequest;
import com.corrodinggames.rts.gameFramework.sound.SoundThread;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.ObjectPool;
import io.github.rwx.PlatformAudio;
import io.github.rwx.PlatformBridge;
import org.koin.java.KoinJavaComponent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Legacy sound factory backed by the active platform audio implementation.
 *
 * The game engine still owns sound prioritization, spatial attenuation, and settings volume
 * calculation. This adapter only loads the legacy resources and forwards the final channel
 * volumes to desktop OpenAL or Android SoundPool.
 */
public class AndroidSoundFactory extends SoundFactory {
    SoundThread soundThread;
    public LinkedBlockingQueue soundRequestQueue = new LinkedBlockingQueue();
    final int MAX_STREAMS = 15;
    public ObjectPool soundPlaybackList = new ObjectPool(15);
    public int SOUND_THREAD_SLEEP_MS = 1000;

    private static final AtomicInteger NEXT_CUSTOM_SOUND_ID = new AtomicInteger();

    private PlatformAudio platformAudio;

    public AndroidSoundFactory() {
        this(null);
    }

    AndroidSoundFactory(PlatformAudio platformAudio) {
        this.platformAudio = platformAudio;
        for (int i = 0; i < 15; i++) {
            this.soundPlaybackList.release(new SoundRequest());
        }
    }

    @Override
    public void initialize() {
        if (this.platformAudio != null) {
            return;
        }
        try {
            PlatformBridge bridge = KoinJavaComponent.get(PlatformBridge.class);
            this.platformAudio = bridge.getAudio();
        } catch (RuntimeException e) {
            GameEngine.log("Platform audio is not available; legacy sounds will be disabled", e);
            this.platformAudio = null;
        }
    }

    @Override
    public void a() {
        if (this.soundThread != null) {
            throw new RuntimeException("soundThread!=null");
        }
        this.soundThread = new SoundThread(this);
        this.soundThread.start();
    }

    @Override
    public Sound a(int resourceId) {
        String name = Utility.countChars(R.raw.class, resourceId);
        AndroidSound sound = new AndroidSound(this, name, this);
        sound.soundFactory = this;
        sound.backendId = registerBuiltInSound(name);
        sound.soundId = sound.backendId == null ? -1 : 1;
        return sound;
    }

    @Override
    public Sound a(String name, AssetInputStream assetInputStream, boolean registerByName) {
        AndroidSoundFactory registrationFactory = registerByName ? this : null;
        AndroidSound sound = new AndroidSound(this, name, registrationFactory);
        sound.soundFactory = this;
        sound.backendId = registerStreamSound(name, assetInputStream);
        sound.soundId = sound.backendId == null ? -1 : 1;
        return sound;
    }

    public void play(AndroidSound sound, float leftVolume, float rightVolume, float rate) {
        if (this.platformAudio == null || sound.backendId == null || !SoundEngine.areGameSoundsEnabled()) {
            return;
        }
        float left = Math.max(0.0f, leftVolume);
        float right = Math.max(0.0f, rightVolume);
        float volume = Math.max(left, right);
        if (volume <= 0.0f) {
            return;
        }
        float pan;
        if (right > left) {
            pan = 1.0f - (left / right);
        } else if (left > right) {
            pan = (right / left) - 1.0f;
        } else {
            pan = 0.0f;
        }
        try {
            this.platformAudio.playSound(sound.backendId, Math.min(volume, 1.0f), pan, rate);
        } catch (RuntimeException e) {
            GameEngine.log("Failed to play legacy sound: " + sound.e, e);
        }
    }

    private String registerBuiltInSound(String name) {
        if (this.platformAudio == null || name == null) {
            return null;
        }
        String backendId = "legacy:built-in:" + name;
        try {
            if (this.platformAudio.registerBuiltInSound(backendId, name)) {
                return backendId;
            }
            GameEngine.log("Legacy sound resource was not found: " + name);
        } catch (RuntimeException e) {
            GameEngine.log("Failed to load legacy sound: " + name, e);
        }
        return null;
    }

    private String registerStreamSound(String name, AssetInputStream stream) {
        if (this.platformAudio == null || stream == null) {
            closeQuietly(stream);
            return null;
        }
        String sourcePath = stream.getPath() != null ? stream.getPath() : name;
        String fileName = sourcePath == null ? name : new File(sourcePath).getName();
        String backendId = "legacy:custom:" + NEXT_CUSTOM_SOUND_ID.incrementAndGet();
        try {
            byte[] data = readBytes(stream);
            this.platformAudio.registerSound(backendId, data, fileName);
            return backendId;
        } catch (IOException | RuntimeException e) {
            GameEngine.log("Failed to load custom sound: " + name, e);
            return null;
        } finally {
            closeQuietly(stream);
        }
    }

    private static byte[] readBytes(AssetInputStream stream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int count;
        while ((count = stream.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }
        return output.toByteArray();
    }

    private static void closeQuietly(AssetInputStream stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            GameEngine.log("Failed to close sound stream", e);
        }
    }
}
