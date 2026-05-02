package com.corrodinggames.rts.java;

import android.content.Context;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.Sound;
import com.corrodinggames.rts.gameFramework.audio.SoundFactory;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.ObjectPool;
import com.corrodinggames.rts.java.audio.lwjgl.OpenALAudio;
import com.corrodinggames.rts.java.audio.util.AudioException;
import com.corrodinggames.rts.java.audio.util.AudioFile;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/o.class */
public class OpenALSoundFactory extends SoundFactory {
    final int a = 15;
    LinkedBlockingQueue b = new LinkedBlockingQueue();
    ObjectPool c = new ObjectPool(15);
    SoundPlayThread d;
    Context e;
    public OpenALAudio f;

    public Object b() {
        return this.f;
    }

    public OpenALSoundFactory(OpenALAudio openALAudio) {
        for (int i = 0; i < 15; i++) {
            this.c.release(new SoundPlayRequest());
        }
        this.f = openALAudio;
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public void a(Context context) {
        if (this.e != null) {
            GameEngine.log("SlickSoundFactory:setContext context already set");
        } else {
            this.e = context;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public Sound a(int i) {
        OpenALSound openALSound = new OpenALSound(this, Utility.formatNumber(i), this);
        String number = Utility.formatNumber(i);
        if (number == null) {
            throw new RuntimeException("Failed to find sound for res id:" + i);
        }
        synchronized (b()) {
            openALSound.a = this.f.newSound(new AudioFile(number));
        }
        return openALSound;
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public Sound a(String str, AssetInputStream assetInputStream, boolean z) {
        OpenALSoundFactory openALSoundFactory = this;
        if (!z) {
            openALSoundFactory = null;
        }
        OpenALSound openALSound = new OpenALSound(this, str, openALSoundFactory);
        try {
            synchronized (b()) {
                openALSound.a = this.f.newSound(new AudioFile(assetInputStream, assetInputStream.getPath()));
            }
            return openALSound;
        } catch (AudioException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public void a() {
        if (this.d != null) {
            throw new RuntimeException("startThreads: soundThread!=null");
        }
        this.d = new SoundPlayThread(this);
        this.d.start();
    }
}
