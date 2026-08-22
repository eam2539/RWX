package com.corrodinggames.rts.gameFramework;

import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.gameFramework.network.MasterServerClient;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/i.class */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    /* JADX INFO: renamed from: a */
    private Thread.UncaughtExceptionHandler defaultHandler;

    CustomExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.defaultHandler = uncaughtExceptionHandler;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public synchronized void uncaughtException(Thread thread, Throwable th) {
        GameEngine gameEngine = null;
        try {
            try {
                GameEngine.outOfMemoryReserveBuffer = null;
                GameEngine.secondaryOutOfMemoryReserveBuffer = null;
                GameEngine.exceptionHandlerMemoryReserve = null;
                System.gc();
                try {
                    GameEngine.log("uncaughtException start");
                    gameEngine = GameEngine.getInstance();
                    if (gameEngine != null && (th instanceof OutOfMemoryError)) {
                        GameEngine.log("Freeing memory");
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
                            GameEngine.log("uncaughtException: Memory freed");
                        } catch (Throwable th2) {
                            GameEngine.log("exception freeing memory");
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
                            GameEngine.log("CustomExceptionHandler: no settings");
                        }
                    } else {
                        GameEngine.log("CustomExceptionHandler: no game");
                    }
                    if (GameEngine.hasHandledCrash) {
                        GameEngine.log("CustomExceptionHandler: a crash was already sent");
                        z = false;
                        z2 = true;
                    }
                    GameEngine.hasHandledCrash = true;
                    if (z) {
                        try {
                            GameEngine.log("Starting errorReport");
                            MasterServerClient.sendErrorReportAsync("uncaughtException", stackTrace);
                            GameEngine.log("waiting");
                            Thread.sleep(800L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!z2 && gameEngine != null && gameEngine.platformCallbacks != null) {
                        gameEngine.platformCallbacks.a(th);
                    }
                    GameEngine.writeCrashToFile("fatal", stackTrace);
                } catch (Exception e2) {
                    GameEngine.log("exception sending crash");
                    e2.printStackTrace();
                }
                if (gameEngine != null) {
                    if (gameEngine.platformCallbacks != null && gameEngine.platformCallbacks.a()) {
                        GameEngine.log("gameCrashesDontExit=true");
                        if (1 == 0) {
                            GameEngine.log("Crash was not handled, exiting");
                            Runtime.getRuntime().halt(1);
                            return;
                        }
                        return;
                    }
                    if (gameEngine.networkEngine != null && gameEngine.networkEngine.networkGameActive) {
                        GameEngine.log("Sending disconnect");
                        gameEngine.networkEngine.c("Game crash");
                    }
                }
                if (!GameEngine.isDesktopVersion) {
                    if (this.defaultHandler != null) {
                        GameEngine.log("CustomExceptionHandler: sending to: defaultUEH.uncaughtException");
                        this.defaultHandler.uncaughtException(thread, th);
                        GameEngine.log("CustomExceptionHandler: back from: defaultUEH.uncaughtException");
                    } else {
                        GameEngine.log("CustomExceptionHandler: defaultUEH==null");
                        System.exit(2);
                    }
                }
                GameEngine.lastThrowable = th;
                if (1 == 0) {
                    GameEngine.log("Crash was not handled, exiting");
                    Runtime.getRuntime().halt(1);
                }
            } catch (Throwable th3) {
                GameEngine.log("Exception in uncaughtException");
                th3.printStackTrace();
                if (0 == 0) {
                    GameEngine.log("Crash was not handled, exiting");
                    Runtime.getRuntime().halt(1);
                }
            }
        } catch (Throwable th4) {
            if (0 == 0) {
                GameEngine.log("Crash was not handled, exiting");
                Runtime.getRuntime().halt(1);
            }
            throw th4;
        }
    }
}
