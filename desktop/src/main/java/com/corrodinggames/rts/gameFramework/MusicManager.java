package com.corrodinggames.rts.gameFramework;

import android.content.Context;
import android.util.Log;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.mod.ModInfo;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.ui.GameUI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.am */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/am.class */
public class MusicManager {

    /* JADX INFO: renamed from: e */
    MusicUpdateThread musicUpdateThread;

    /* JADX INFO: renamed from: k */
    MusicTrack currentTrack;

    /* JADX INFO: renamed from: l */
    boolean isPlaying;

    /* JADX INFO: renamed from: m */
    String currentTrackPath;

    /* JADX INFO: renamed from: n */
    boolean noLoop;

    /* JADX INFO: renamed from: o */
    boolean volumeChanged;

    /* JADX INFO: renamed from: p */
    float currentVolume;

    /* JADX INFO: renamed from: q */
    float trackTimeout;

    /* JADX INFO: renamed from: r */
    float trackTimeoutRetry;

    /* JADX INFO: renamed from: s */
    public boolean nextTrackRequested;

    /* JADX INFO: renamed from: t */
    public String nextTrackPath;

    /* JADX INFO: renamed from: u */
    public boolean disabled;

    /* JADX INFO: renamed from: v */
    String lastMessage;

    /* JADX INFO: renamed from: w */
    Context context;

    /* JADX INFO: renamed from: x */
    boolean firstPlay;

    /* JADX INFO: renamed from: y */
    boolean checkNextTrack;

    /* JADX INFO: renamed from: z */
    int errorCount;

    /* JADX INFO: renamed from: A */
    MusicTrack nextTrack;

    /* JADX INFO: renamed from: B */
    boolean fadingIn;

    /* JADX INFO: renamed from: C */
    boolean fadingOut;

    /* JADX INFO: renamed from: D */
    float fadeLevel;

    /* JADX INFO: renamed from: H */
    float fadeTimer;

    /* JADX INFO: renamed from: L */
    boolean crashed;

    /* JADX INFO: renamed from: M */
    boolean crashedMessageShown;

    /* JADX INFO: renamed from: a */
    public static MusicFactory musicFactory = new AndroidMusicFactory();

    /* JADX INFO: renamed from: J */
    static HashMap musicCache = new HashMap();

    /* JADX INFO: renamed from: K */
    static int loadErrorCount = 0;

    /* JADX INFO: renamed from: b */
    Object pauseLock = new Object();

    /* JADX INFO: renamed from: c */
    Object updateLock = new Object();

    /* JADX INFO: renamed from: d */
    volatile float delta = 1.0f;

    /* JADX INFO: renamed from: f */
    volatile boolean updateRunning = false;

    /* JADX INFO: renamed from: g */
    volatile boolean updateCoreRunning = true;

    /* JADX INFO: renamed from: h */
    float updateCoreTime = 0.0f;

    /* JADX INFO: renamed from: i */
    int updateCoreCount = 0;

    /* JADX INFO: renamed from: j */
    boolean lockupDetected = false;

    /* JADX INFO: renamed from: E */
    boolean crossfade = false;

    /* JADX INFO: renamed from: F */
    public boolean quickFade = false;

    /* JADX INFO: renamed from: G */
    boolean fadedOut = false;

    /* JADX INFO: renamed from: I */
    ArrayList recentTracks = new ArrayList();

    /* JADX INFO: renamed from: N */
    long lastUpdate = -1;

    /* JADX INFO: renamed from: a */
    public float getMusicVolume() {
        GameEngine gameEngine = GameEngine.getInstance();
        return gameEngine.settingsEngine.musicVolume * gameEngine.settingsEngine.masterVolume;
    }

    /* JADX INFO: renamed from: b */
    public boolean isMusicEnabled() {
        return (GameEngine.isDedicatedServer() || this.disabled || getMusicVolume() <= 0.01f) ? false : true;
    }

    /* JADX INFO: renamed from: a */
    public void init(Context context) {
        this.context = context;
        if (GameEngine.isDedicatedServer()) {
            return;
        }
        musicFactory.a(this);
        this.currentTrack = musicFactory.a();
        this.nextTrack = musicFactory.a();
        MusicCategory.loadAll();
        if (musicFactory.isThreaded()) {
            this.musicUpdateThread = new MusicUpdateThread(this);
            this.musicUpdateThread.start();
        }
    }

    /* JADX INFO: renamed from: c */
    public void onNewGame() {
        if (!GameEngine.isPC()) {
            this.isPlaying = false;
            this.currentTrackPath = null;
            this.firstPlay = true;
            this.fadingIn = false;
        }
        this.checkNextTrack = true;
        this.disabled = false;
    }

    /* JADX INFO: renamed from: a */
    static Music loadMusic(String str, boolean z) {
        Music music = (Music) musicCache.get(str);
        if (music != null) {
            return music;
        }
        try {
            Music musicA = musicFactory.a(str);
            if (z) {
                musicCache.put(str, musicA);
            }
            return musicA;
        } catch (ArithmeticException e) {
            loadErrorCount++;
            GameEngine.log("Error loading:" + str, (Throwable) e);
            if (loadErrorCount > 2 && loadErrorCount <= 4) {
                GameEngine.getInstance().alert("Failed to load music track:" + str + ". Music track skipped.");
            }
            if (!z) {
                throw new RuntimeException(e);
            }
            return null;
        } catch (Exception e2) {
            loadErrorCount++;
            GameEngine.log("Exception loading:" + str, (Throwable) e2);
            if (loadErrorCount > 2 && loadErrorCount <= 4) {
                GameEngine.getInstance().alert("Unknown error loading music track:" + str + ". Music track skipped.");
            }
            if (!z) {
                throw new RuntimeException(e2);
            }
            return null;
        } catch (OutOfMemoryError e3) {
            loadErrorCount++;
            GameEngine.log("OutOfMemoryError loading:" + str, e3);
            GameEngine.printMemoryInfo();
            System.gc();
            GameEngine.printMemoryInfo();
            if (loadErrorCount < 3) {
                GameEngine.getInstance().alert("Ran out of memory loading music track:" + str + ". Music track skipped.");
            }
            if (!z) {
                throw new RuntimeException(e3);
            }
            return null;
        }
    }

    /* JADX INFO: renamed from: d */
    public ArrayList getAllMusicPaths() {
        ArrayList arrayList = new ArrayList();
        for (String str : MusicCategory.starting.getTrackPaths()) {
            arrayList.add(str);
        }
        for (String str2 : MusicCategory.buildup.getTrackPaths()) {
            arrayList.add(str2);
        }
        for (String str3 : MusicCategory.starting.getTrackPaths()) {
            arrayList.add(str3);
        }
        return arrayList;
    }

    /* JADX INFO: renamed from: a */
    public String getRandomMusicPathFromCategories(MusicCategory musicCategory) {
        return getRandomMusicPath(musicCategory, musicCategory);
    }

    /* JADX INFO: renamed from: a */
    public String getRandomMusicPath(MusicCategory musicCategory, MusicCategory musicCategory2) {
        MusicCategory musicCategory3;
        GameEngine.getInstance();
        if (Utility.getRandomInt(musicCategory.getTrackPaths().length + musicCategory2.getTrackPaths().length) < musicCategory.getTrackPaths().length) {
            musicCategory3 = musicCategory;
        } else {
            musicCategory3 = musicCategory2;
        }
        String[] trackPaths = musicCategory3.getTrackPaths();
        return musicCategory3.getFullPath(trackPaths[Utility.getRandomInt(trackPaths.length)]);
    }

    /* JADX INFO: renamed from: e */
    public synchronized void skipToNextTrack() {
        this.nextTrackRequested = true;
        this.disabled = false;
        this.nextTrackPath = null;
    }

    /* JADX INFO: renamed from: a */
    public synchronized void playMusic(String str) {
        this.nextTrackRequested = true;
        this.disabled = false;
        this.nextTrackPath = str;
    }

    /* JADX INFO: renamed from: a */
    public synchronized void update(float f) {
        if (GameEngine.isDedicatedServer()) {
            return;
        }
        if (!musicFactory.isThreaded()) {
            if (!this.crashed) {
                updateMusic(f);
            }
            this.updateCoreRunning = true;
        }
        this.lastUpdate = GameEngine.getCurrentTimeMillis();
        if (GameEngine.getInstance().inputController.H.a()) {
            skipToNextTrack();
        }
        if (this.lastMessage != null) {
            NetworkEngine.a((String) null, this.lastMessage);
            this.lastMessage = null;
        }
        if (this.currentVolume != getMusicVolume()) {
            this.currentVolume = getMusicVolume();
            this.volumeChanged = true;
        }
        synchronized (this.updateLock) {
            this.delta = f;
            if (this.crashed) {
                if (!this.crashedMessageShown) {
                    this.crashedMessageShown = true;
                    GameEngine.reportProblem("Music subsystem crashed, music has been disabled to keep your game running. Please send your logs.");
                }
                return;
            }
            if (!this.updateCoreRunning) {
                this.updateCoreTime += f;
                this.updateCoreCount++;
                if (this.updateCoreTime > 320.0f && this.updateCoreCount > 80 && !this.lockupDetected) {
                    this.lockupDetected = true;
                    GameEngine.reportProblem("Lockup detected in music subsystem");
                }
            } else {
                this.updateCoreTime = 0.0f;
                this.updateCoreCount = 0;
            }
            this.updateCoreRunning = false;
            this.updateRunning = true;
            this.updateLock.notifyAll();
        }
    }

    /* JADX INFO: renamed from: b */
    public String getMusicDisplayName(String str) {
        return Utility.getFileNameFromPath(Utility.getFileNameWithoutExtension(str)).replace("[noloop]", VariableScope.nullOrMissingString).replace("_", " ");
    }

    /* JADX INFO: renamed from: b */
    public boolean updateMusic(float f) {
        try {
            updateMusicInner(f);
            return true;
        } catch (Exception e) {
            GameEngine.log("Music system crashed", (Throwable) e);
            this.crashed = true;
            GameEngine.log("Stopping music");
            try {
                pauseMusic();
                return false;
            } catch (Exception e2) {
                GameEngine.log("crash stopping music", (Throwable) e2);
                return false;
            }
        }
    }

    /* JADX INFO: renamed from: c */
    public void updateMusicInner(float f) {
        float musicVolume;
        float musicVolume2;
        if (GameEngine.isDedicatedServer()) {
            return;
        }
        musicFactory.createMusicTrack(f);
        if (!isMusicEnabled()) {
            if (this.isPlaying && this.currentTrack.c()) {
                pauseMusic();
                this.isPlaying = false;
                this.fadingIn = false;
                return;
            }
            return;
        }
        boolean z = false;
        if (!this.isPlaying) {
            z = true;
        }
        if (this.noLoop) {
            if (!this.fadingOut) {
                this.trackTimeout += f;
            }
            if (this.trackTimeout > 600.0f) {
                this.trackTimeoutRetry += f;
                if (this.trackTimeoutRetry > 100.0f) {
                    this.trackTimeoutRetry = 0.0f;
                    if (!this.isPlaying || !this.currentTrack.c()) {
                        z = true;
                        this.trackTimeout = 0.0f;
                    }
                }
            }
        } else {
            this.trackTimeout += f;
            if (this.trackTimeout > 3600.0f) {
                GameEngine.log("Next music track, timer:" + this.trackTimeout);
                z = true;
                this.trackTimeout = 0.0f;
            }
        }
        if (this.checkNextTrack) {
            ModInfo lastSelectedUnitModInfo = GameUI.getLastSelectedUnitModInfo();
            if (lastSelectedUnitModInfo != null && lastSelectedUnitModInfo.playMusicExclusively) {
                z = true;
            }
            this.checkNextTrack = false;
        }
        if (z || this.nextTrackRequested) {
            boolean z2 = this.nextTrackRequested;
            String str = this.nextTrackPath;
            if (this.nextTrackRequested) {
                GameEngine.log("Next music track requested");
                this.nextTrackRequested = false;
                this.trackTimeout = 0.0f;
                this.nextTrackPath = null;
            }
            String randomMusicPath = null;
            boolean z3 = false;
            ModInfo modInfo = null;
            if (str != null) {
                ArrayList allUnitBlueprintsFromEnabledMods = GameEngine.getInstance().modManager.getAllUnitBlueprintsFromEnabledMods();
                allUnitBlueprintsFromEnabledMods.addAll(getAllMusicPaths());
                if (str.endsWith(".ogg") || str.endsWith(".wav")) {
                    getMusicDisplayName(str);
                }
                Iterator it = allUnitBlueprintsFromEnabledMods.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String str2 = (String) it.next();
                    if (getMusicDisplayName(str2).equalsIgnoreCase(str)) {
                        z3 = true;
                        randomMusicPath = str2;
                        break;
                    }
                }
                if (randomMusicPath == null) {
                    GameEngine.log("Failed to find requested music: " + str);
                }
            }
            ModInfo lastSelectedUnitModInfo2 = GameUI.getLastSelectedUnitModInfo();
            if (randomMusicPath == null && lastSelectedUnitModInfo2 != null && lastSelectedUnitModInfo2.hasImages < 10 && lastSelectedUnitModInfo2.playMusicExclusively) {
                ArrayList unitBlueprints = lastSelectedUnitModInfo2.getUnitBlueprints();
                if (unitBlueprints.size() > 0) {
                    z3 = true;
                    modInfo = lastSelectedUnitModInfo2;
                    randomMusicPath = (String) unitBlueprints.get(Utility.getRandomIntInRange(0, unitBlueprints.size() - 1));
                    if (z2 || this.recentTracks.contains(randomMusicPath)) {
                        for (int i = 0; i < 30 && (randomMusicPath.equals(this.currentTrackPath) || this.recentTracks.contains(randomMusicPath)); i++) {
                            randomMusicPath = (String) unitBlueprints.get(Utility.getRandomIntInRange(0, unitBlueprints.size() - 1));
                            if (i > 20) {
                                this.recentTracks.clear();
                            }
                        }
                    }
                    GameEngine.log("Playing music from mod:" + lastSelectedUnitModInfo2.getDisplayTitle() + " - '" + randomMusicPath + "'");
                }
            }
            if (randomMusicPath == null) {
                if (this.firstPlay) {
                    randomMusicPath = getRandomMusicPathFromCategories(MusicCategory.starting);
                } else {
                    randomMusicPath = getRandomMusicPath(MusicCategory.buildup, MusicCategory.starting);
                }
                if (z2 || this.recentTracks.contains(randomMusicPath)) {
                    for (int i2 = 0; i2 < 30 && (randomMusicPath.equals(this.currentTrackPath) || this.recentTracks.contains(randomMusicPath)); i2++) {
                        randomMusicPath = getRandomMusicPath(MusicCategory.buildup, MusicCategory.starting);
                        if (i2 > 20) {
                            this.recentTracks.clear();
                        }
                    }
                }
            }
            if (!randomMusicPath.equals(this.currentTrackPath)) {
                this.currentTrackPath = randomMusicPath;
                this.firstPlay = false;
                this.trackTimeout = 0.0f;
                this.noLoop = z3 || randomMusicPath.contains("[noloop]");
                this.recentTracks.add(randomMusicPath);
                if (this.recentTracks.size() > 4) {
                    this.recentTracks.remove(0);
                }
                if (z2) {
                    this.lastMessage = "Now playing: " + getMusicDisplayName(randomMusicPath);
                }
                MusicTrack musicTrack = this.currentTrack;
                this.currentTrack = this.nextTrack;
                this.nextTrack = musicTrack;
                try {
                    try {
                        this.currentTrack.a(loadMusic(randomMusicPath, false));
                        this.currentTrack.a(!this.noLoop);
                        this.crossfade = false;
                        if (!z2 && this.fadingIn) {
                            this.crossfade = true;
                        }
                        if (this.isPlaying) {
                            this.fadingIn = true;
                        }
                        this.fadingOut = true;
                        this.fadedOut = false;
                        this.fadeLevel = 1.0f;
                        this.isPlaying = true;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        if (this.errorCount < 3) {
                            this.lastMessage = "Failed to play music track: " + randomMusicPath;
                            this.errorCount++;
                        }
                        if (modInfo != null) {
                            modInfo.hasImages++;
                            return;
                        }
                        return;
                    }
                } catch (RuntimeException e2) {
                    e2.printStackTrace();
                    if (this.errorCount < 3) {
                        this.lastMessage = "Failed to open music track: " + randomMusicPath;
                        this.errorCount++;
                    }
                    if (modInfo != null) {
                        modInfo.hasImages++;
                        return;
                    }
                    return;
                }
            } else if (z2) {
                GameEngine.log("Same music found");
            }
        }
        if (this.fadingOut || this.volumeChanged) {
            boolean zIsMusicPlaying = musicFactory.isMusicPlaying();
            if (!zIsMusicPlaying) {
                if (this.quickFade) {
                    this.fadeLevel -= f * 0.1f;
                } else {
                    this.fadeLevel -= f * 0.006f;
                }
            } else if (this.quickFade) {
                this.fadeLevel -= f * 0.1f;
            } else if (this.crossfade) {
                this.fadeLevel -= f * 0.003f;
            } else {
                this.fadeLevel -= f * 0.008f;
            }
            if (!zIsMusicPlaying) {
                musicVolume = this.fadeLevel * getMusicVolume();
                musicVolume2 = (1.0f - this.fadeLevel) * getMusicVolume();
            } else {
                musicVolume = ((this.fadeLevel * 2.0f) - 1.0f) * getMusicVolume();
                musicVolume2 = (1.0f - (this.fadeLevel * 2.0f)) * getMusicVolume();
            }
            float fClampTo255 = Utility.clampTo255(musicVolume, 0.0f, 1.0f);
            float fClampTo2552 = Utility.clampTo255(musicVolume2, 0.0f, 1.0f);
            if (this.fadingOut) {
                if (this.fadeLevel <= 0.0f) {
                    this.fadingOut = false;
                    this.crossfade = false;
                    if (this.fadingIn && !this.fadedOut) {
                        this.fadedOut = true;
                        this.nextTrack.d();
                    }
                    if (this.isPlaying) {
                        this.currentTrack.a(getMusicVolume(), getMusicVolume());
                    }
                } else {
                    this.fadeTimer += f;
                    if (this.fadeTimer > 10.0f) {
                        this.fadeTimer = 0.0f;
                        if (this.fadingIn && !this.fadedOut) {
                            this.nextTrack.a(fClampTo255, fClampTo255);
                            if (fClampTo255 < 0.02f) {
                                this.fadedOut = true;
                                this.nextTrack.d();
                            }
                        }
                        if (this.isPlaying) {
                            this.currentTrack.a(fClampTo2552, fClampTo2552);
                        }
                    }
                }
            } else if (this.isPlaying) {
                this.currentTrack.a(fClampTo2552, fClampTo2552);
            }
        }
        this.volumeChanged = false;
    }

    /* JADX INFO: renamed from: f */
    public void pause() {
        Log.a("RustedWarfare", "Music:pause()");
        new Thread() { // from class: com.corrodinggames.rts.gameFramework.am.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                if (MusicManager.this.lockupDetected) {
                    Log.a("RustedWarfare", "Music:pause() unsynchronized");
                    MusicManager.this.pauseMusic();
                } else {
                    synchronized (MusicManager.this.pauseLock) {
                        Log.a("RustedWarfare", "Music:pause() synchronized");
                        MusicManager.this.pauseMusic();
                    }
                }
            }
        }.start();
    }

    /* JADX INFO: renamed from: g */
    public void pauseMusic() {
        if (this.isPlaying) {
            this.currentTrack.a();
        }
        if (this.fadingIn) {
            this.nextTrack.a();
        }
    }

    /* JADX INFO: renamed from: h */
    public void resume() {
        new Thread() { // from class: com.corrodinggames.rts.gameFramework.am.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                synchronized (MusicManager.this.pauseLock) {
                    if (MusicManager.this.isPlaying) {
                        MusicManager.this.currentTrack.b();
                        if (!MusicManager.this.fadingOut) {
                            MusicManager.this.currentTrack.a(MusicManager.this.getMusicVolume(), MusicManager.this.getMusicVolume());
                        }
                    }
                    if (MusicManager.this.fadingIn) {
                        MusicManager.this.nextTrack.b();
                    }
                }
            }
        }.start();
    }

    /* JADX INFO: renamed from: i */
    public void release() {
        musicFactory.release();
        if (this.fadingIn) {
            this.nextTrack.d();
            this.nextTrack.e();
        }
        if (this.currentTrack != null) {
            this.currentTrack.d();
            this.currentTrack.e();
        }
        this.currentTrack = null;
        this.currentTrackPath = null;
        this.isPlaying = false;
    }

    /* JADX INFO: renamed from: j */
    public boolean isFading() {
        if (this.fadingOut) {
            return true;
        }
        return false;
    }
}
