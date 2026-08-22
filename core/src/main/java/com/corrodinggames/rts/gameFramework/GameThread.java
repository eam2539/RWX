package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.units.custom.ConfigParseException;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.z */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/z.class */
public class GameThread extends Thread {

    /* JADX INFO: renamed from: a */
    static int threadCount = 0;

    /* JADX INFO: renamed from: b */
    public boolean running;

    /* JADX INFO: renamed from: c */
    long lastFrameTime;

    public synchronized void a(boolean z) {
        this.running = z;
    }

    public GameThread() {
        super("GameThread" + threadCount);
        this.running = true;
        threadCount++;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        long j;
        GameEngine.setupUncaughtExceptionHandler();
        if (!GameEngine.isNonAndroidVersion) {
        }
        a();
        long j2 = this.lastFrameTime;
        GameEngine gameEngine = GameEngine.getInstance();
        while (this.running) {
            long jNanoTime = System.nanoTime();
            long j3 = this.lastFrameTime;
            a();
            try {
                gameEngine.gameLoop((this.lastFrameTime - j3) * 0.060000002f, (int) (this.lastFrameTime - j3));
            } catch (ConfigParseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!gameEngine.settingsEngine.batterySaving) {
            }
            if (gameEngine.settingsEngine.batterySaving) {
                j = 32258064;
            } else if (gameEngine.settingsEngine.highRefreshRate) {
                j = 3333333;
            } else {
                j = 16393442;
            }
            long jRound = Math.round(j - (System.nanoTime() - jNanoTime));
            if (jRound > 0) {
                long jNanoTime2 = System.nanoTime();
                while (true) {
                    long jNanoTime3 = System.nanoTime() - jNanoTime2;
                    if (jNanoTime3 > jRound || jNanoTime3 < 0) {
                        break;
                    }
                    long j4 = (long) ((jRound - jNanoTime3) / 1000000.0d);
                    if (j4 > 1) {
                        try {
                            Thread.sleep(j4);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    public void a() {
        this.lastFrameTime = System.currentTimeMillis();
    }
}
