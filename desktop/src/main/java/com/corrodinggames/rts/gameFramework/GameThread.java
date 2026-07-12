package com.corrodinggames.rts.gameFramework;

import android.os.Process;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.z */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/z.class */
public class GameThread extends Thread {
    static int a = 0;
    public boolean b;
    long c;

    public synchronized void a(boolean z) {
        this.b = z;
    }

    public GameThread() {
        super("GameThread" + a);
        this.b = true;
        a++;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        long j;
        GameEngine.setupUncaughtExceptionHandler();
        if (!GameEngine.isNonAndroidVersion) {
            Process.setThreadPriority(-4);
        }
        a();
        long j2 = this.c;
        GameEngine gameEngine = GameEngine.getInstance();
        while (this.b) {
            long jNanoTime = System.nanoTime();
            long j3 = this.c;
            a();
            try {
                gameEngine.gameLoop((this.c - j3) * 0.060000002f, (int) (this.c - j3));
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
        this.c = System.currentTimeMillis();
    }
}
