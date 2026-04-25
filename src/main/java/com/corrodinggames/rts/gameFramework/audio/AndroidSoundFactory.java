package com.corrodinggames.rts.gameFramework.audio;

import android.content.Context;
import android.media.SoundPool;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.sound.AndroidSound;
import com.corrodinggames.rts.gameFramework.sound.SoundRequest;
import com.corrodinggames.rts.gameFramework.sound.SoundThread;
import com.corrodinggames.rts.gameFramework.utility.AssetInputStream;
import com.corrodinggames.rts.gameFramework.utility.ObjectPool;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/a/a.class */
public class AndroidSoundFactory extends SoundFactory {

    /* JADX INFO: renamed from: d */
    SoundThread soundThread;

    /* JADX INFO: renamed from: f */
    Context context;

    /* JADX INFO: renamed from: g */
    public SoundPool soundPool;

    /* JADX INFO: renamed from: a */
    public LinkedBlockingQueue soundRequestQueue = new LinkedBlockingQueue();

    /* JADX INFO: renamed from: b */
    final int MAX_STREAMS = 15;

    /* JADX INFO: renamed from: c */
    public ObjectPool soundPlaybackList = new ObjectPool(15);

    /* JADX INFO: renamed from: e */
    public int SOUND_THREAD_SLEEP_MS = 1000;

    public AndroidSoundFactory() {
        for (int i = 0; i < 15; i++) {
            this.soundPlaybackList.release(new SoundRequest());
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public void a(Context context) {
        if (this.context != null) {
            GameEngine.isInSpace("AndroidSoundFactory:setContext context already set");
        } else {
            this.context = context;
            this.soundPool = new SoundPool(16, 3, 0);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public void a() {
        if (this.soundThread != null) {
            throw new RuntimeException("soundThread!=null");
        }
        this.soundThread = new SoundThread(this);
        this.soundThread.start();
    }

    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public Sound a(int i) {
        AndroidSound androidSound = new AndroidSound(this, Utility.countChars(R.raw.class, i), this);
        androidSound.soundFactory = this;
        androidSound.soundId = this.soundPool.load(this.context, i, 1);
        return androidSound;
    }

    /* JADX WARN: Finally extract failed */
    @Override // com.corrodinggames.rts.gameFramework.audio.SoundFactory
    public Sound a(String str, AssetInputStream assetInputStream, boolean z) {
        int iLoad;
        AndroidSoundFactory androidSoundFactory = this;
        if (!z) {
            androidSoundFactory = null;
        }
        if (!assetInputStream.isDirect()) {
            try {
                File fileCreateTempFileInContext = FileHelper.createTempFileInContext(this.context, "audio", "ogg");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFileInContext);
                    Utility.copyStream(assetInputStream, fileOutputStream);
                    fileOutputStream.close();
                    try {
                        assetInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FileInputStream fileInputStream = new FileInputStream(fileCreateTempFileInContext);
                    try {
                        iLoad = this.soundPool.load(fileInputStream.getFD(), 0L, fileInputStream.available(), 1);
                        fileInputStream.close();
                        fileCreateTempFileInContext.delete();
                    } catch (Throwable th) {
                        fileInputStream.close();
                        throw th;
                    }
                } catch (Throwable th2) {
                    fileCreateTempFileInContext.delete();
                    throw th2;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        } else {
            try {
                iLoad = this.soundPool.load(assetInputStream.getFileDescriptor(), 0L, assetInputStream.available(), 1);
                try {
                    assetInputStream.close();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            } catch (IOException e4) {
                e4.printStackTrace();
                return null;
            }
        }
        AndroidSound androidSound = new AndroidSound(this, str, androidSoundFactory);
        androidSound.soundFactory = this;
        androidSound.soundId = iLoad;
        return androidSound;
    }
}
