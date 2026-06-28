package com.corrodinggames.rts.java;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.MusicTrack;
import com.corrodinggames.rts.java.audio.Music;

/* JADX INFO: renamed from: com.corrodinggames.rts.java.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/n.class */
public class OpenALMusicTrack extends MusicTrack {
    OpenALMusic a;
    OpenALMusicFactory b;
    Music c;
    boolean d = false;
    boolean e = false;
    boolean f = false;

    public OpenALMusicTrack(OpenALMusicFactory openALMusicFactory) {
        this.b = openALMusicFactory;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(com.corrodinggames.rts.gameFramework.Music music) {
        this.a = (OpenALMusic) music;
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(boolean z) {
        synchronized (this.b.f()) {
            this.d = true;
            this.e = z;
            this.f = false;
            GameEngine.log("Queued:" + this.a.path);
            if (this.c != null) {
                GameEngine.log("startPlaying: Stopping old music");
                this.c.stop();
            }
            this.c = this.a.c;
        }
    }

    public void f() {
        if (this.f) {
            return;
        }
        synchronized (this.b.f()) {
            if (this.c != null) {
                GameEngine.log("Now playing:" + this.a.path);
                if (this.e) {
                    this.c.setVolume(this.c.getVolume());
                    this.c.setLooping(true);
                    this.c.play();
                } else {
                    this.c.setVolume(this.c.getVolume());
                    this.c.play();
                }
                this.f = true;
            } else {
                GameEngine.log("realPlay: playingMusic==null");
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a() {
        synchronized (this.b.f()) {
            if (this.c != null) {
                this.c.pause();
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void b() {
        synchronized (this.b.f()) {
            if (this.c != null && !this.c.isPlaying()) {
                this.c.play();
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void d() {
        synchronized (this.b.f()) {
            if (this.c != null) {
                this.c.stop();
                this.f = false;
                this.d = false;
                this.c = null;
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void e() {
        synchronized (this.b.f()) {
            if (this.c != null) {
                this.c.stop();
            }
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public boolean c() {
        synchronized (this.b.f()) {
            if (this.f && this.c != null) {
                return this.c.isPlaying();
            }
            return false;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.MusicTrack
    public void a(float f) {
        synchronized (this.b.f()) {
            if (this.c != null) {
                if (f > 0.05f && !this.f && this.d) {
                    f();
                }
                this.c.setVolume(f);
            } else {
                GameEngine.log("setVolume: playingMusic==null");
            }
        }
    }
}
