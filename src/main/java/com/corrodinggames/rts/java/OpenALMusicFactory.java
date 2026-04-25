package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Music;
import com.corrodinggames.rts.gameFramework.MusicFactory;
import com.corrodinggames.rts.gameFramework.MusicManager;
import com.corrodinggames.rts.gameFramework.MusicTrack;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.java.audio.lwjgl.OpenALAudio;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.l */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/l.class */
public class OpenALMusicFactory extends MusicFactory {
    volatile boolean a;
    public OpenALAudio b;
    boolean c = false;

    public Object f() {
        return this.b;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: a */
    public void createMusicTrack(float f) {
        synchronized (f()) {
            if (this.a) {
                return;
            }
            long jA = PerformanceProfiler.a();
            this.b.update();
            double dA = PerformanceProfiler.a(jA);
            if (dA > 16.0d) {
                GameEngine.isInSpace("music poll took:" + PerformanceProfiler.a(dA));
            }
            super.createMusicTrack(f);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: a */
    public void createMusic(int i) {
    }

    public OpenALMusicFactory(OpenALAudio openALAudio) {
        this.b = openALAudio;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public Music a(String str) {
        return new OpenALMusic(str, this);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public MusicTrack a() {
        return new OpenALMusicTrack(this);
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    public void a(MusicManager musicManager) {
        this.musicManager = musicManager;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: b */
    public void release() {
        synchronized (f()) {
            this.a = true;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: c */
    public boolean isMusicPlaying() {
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: d */
    public boolean isThreaded() {
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicFactory
    /* JADX INFO: renamed from: e */
    public int isMusicSupported() {
        return 100;
    }
}
