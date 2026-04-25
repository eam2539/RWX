package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.gameFramework.network.MasterServerClient;
import java.lang.Thread;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/i.class */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler a;

    CustomExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.a = uncaughtExceptionHandler;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public synchronized void uncaughtException(Thread thread, Throwable th) {
        GameEngine gameEngine = null;
        try {
            try {
                GameEngine.oomCheckBuffer = null;
                GameEngine.oomCheckBuffer2 = null;
                GameEngine.tempBuffer = null;
                System.gc();
                try {
                    GameEngine.isInSpace("uncaughtException start");
                    gameEngine = GameEngine.getInstance();
                    if (gameEngine != null && (th instanceof OutOfMemoryError)) {
                        GameEngine.isInSpace("Freeing memory");
                        try {
                            TileMap.layerBufferManager = null;
                            if (gameEngine.tileMap != null) {
                                gameEngine.tileMap = null;
                            }
                            if (gameEngine.musicManager != null) {
                                gameEngine.musicManager.release();
                                gameEngine.musicManager = null;
                            }
                            System.gc();
                            GameEngine.isInSpace("uncaughtException: Memory freed");
                        } catch (Throwable th2) {
                            GameEngine.isInSpace("exception freeing memory");
                            th2.printStackTrace();
                        }
                    }
                    GameEngine.log("gameEngine:uncaughtExceptionHandler", th);
                    String stackTrace = GameEngine.getStackTrace(th);
                    boolean z = false;
                    boolean z2 = false;
                    if (gameEngine != null) {
                        SettingsEngine settingsEngine = gameEngine.settingsEngine;
                        if (settingsEngine != null) {
                            z = settingsEngine.sendReports;
                        } else {
                            GameEngine.isInSpace("CustomExceptionHandler: no settings");
                        }
                    } else {
                        GameEngine.isInSpace("CustomExceptionHandler: no game");
                    }
                    if (GameEngine.isGameModeSandbox) {
                        GameEngine.isInSpace("CustomExceptionHandler: a crash was already sent");
                        z = false;
                        z2 = true;
                    }
                    GameEngine.isGameModeSandbox = true;
                    if (z) {
                        try {
                            GameEngine.isInSpace("Starting errorReport");
                            MasterServerClient.sendErrorReportAsync("uncaughtException", stackTrace);
                            GameEngine.isInSpace("waiting");
                            Thread.sleep(800L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!z2 && gameEngine != null && gameEngine.missionEngine2 != null) {
                        gameEngine.missionEngine2.a(th);
                    }
                    GameEngine.writeCrashToFile("fatal", stackTrace);
                } catch (Exception e2) {
                    GameEngine.isInSpace("exception sending crash");
                    e2.printStackTrace();
                }
                if (gameEngine != null) {
                    if (gameEngine.missionEngine2 != null && gameEngine.missionEngine2.a()) {
                        GameEngine.isInSpace("gameCrashesDontExit=true");
                        if (1 == 0) {
                            GameEngine.isInSpace("Crash was not handled, exiting");
                            Runtime.getRuntime().halt(1);
                            return;
                        }
                        return;
                    }
                    if (gameEngine.networkEngine != null && gameEngine.networkEngine.B) {
                        GameEngine.isInSpace("Sending disconnect");
                        gameEngine.networkEngine.c("Game crash");
                    }
                }
                if (!GameEngine.isDesktopVersionStatic) {
                    if (this.a != null) {
                        GameEngine.isInSpace("CustomExceptionHandler: sending to: defaultUEH.uncaughtException");
                        this.a.uncaughtException(thread, th);
                        GameEngine.isInSpace("CustomExceptionHandler: back from: defaultUEH.uncaughtException");
                    } else {
                        GameEngine.isInSpace("CustomExceptionHandler: defaultUEH==null");
                        System.exit(2);
                    }
                }
                GameEngine.lastThrowable = th;
                if (1 == 0) {
                    GameEngine.isInSpace("Crash was not handled, exiting");
                    Runtime.getRuntime().halt(1);
                }
            } catch (Throwable th3) {
                GameEngine.isInSpace("Exception in uncaughtException");
                th3.printStackTrace();
                if (0 == 0) {
                    GameEngine.isInSpace("Crash was not handled, exiting");
                    Runtime.getRuntime().halt(1);
                }
            }
        } catch (Throwable th4) {
            if (0 == 0) {
                GameEngine.isInSpace("Crash was not handled, exiting");
                Runtime.getRuntime().halt(1);
            }
            throw th4;
        }
    }
}
