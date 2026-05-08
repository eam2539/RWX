package com.corrodinggames.rts.java;

import android.content.ServerContext;
import android.graphics.Point;
import android.os.Looper;
import com.corrodinggames.librocket.GameMainManager;
import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.appFramework.MultiplayerBattleroomActivity;
import com.corrodinggames.rts.debug.DebugSocketServer;
import com.corrodinggames.rts.game.GameLogic;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.*;
import com.corrodinggames.rts.gameFramework.audio.NullSound;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.SoftwareGraphicsInterface;
import com.corrodinggames.rts.gameFramework.network.MasterServerClient;
import com.corrodinggames.rts.gameFramework.network.NetworkCallbacks;
import com.corrodinggames.rts.gameFramework.network.NetworkConnection;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.network.PasswordHandler;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import com.corrodinggames.rts.gameFramework.utility.BufferedReaderImpl;
import com.corrodinggames.rts.gameFramework.utility.RunnableQueue;
import com.corrodinggames.rts.java.audio.lwjgl.OpenALAudio;
import com.corrodinggames.rts.java.gui.CommonGuiEngine;
import com.corrodinggames.rts.java.slick.SlickLibRocketManager;
import com.corrodinggames.rts.java.steam.JavaSteamEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Input;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.opengl.renderer.VBORenderer;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/Main.class */
public class Main extends NetworkCallbacks {
    public static boolean a = false;
    public static boolean b = true;
    public static String c = "Rusted Warfare";
    public HeadlessGameView d;
    public NetworkEngine h;
    CommonGuiEngine i;
    public SlickGameHandler j;
    SlickGameContainer k;
    String[] l;
    static Main m;
    int n;
    SlickLibRocketManager p;
    Thread r;
    public boolean u;
    public int v;
    public String e = "#28";
    RunnableQueue f = new RunnableQueue();
    boolean g = true;
    long o = System.nanoTime();
    MissionEngine q = new JavaMissionEngine(this);
    boolean s = true;
    Object t = new Object();

    public static void main(String[] strArr) {
        m = new Main();
        GlobalLogger.storage = new DesktopPlatformBridge().getStorage();
        m.a(strArr);
        new Runnable() { // from class: com.corrodinggames.rts.java.Main.1
            @Override // java.lang.Runnable
            public void run() {
                Main.m.f();
            }
        };
    }

    public static void a(String str) {
        GameEngine.log(str);
    }

    public void f() {
        BufferedReaderImpl bufferedReaderImpl = new BufferedReaderImpl(new InputStreamReader(System.in));
        while (this.g) {
            try {
                String strA = bufferedReaderImpl.a();
                if (strA == null) {
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    a((NetworkConnection) null, "ADMIN", strA, true);
                }
            } catch (IOException e2) {
                if (this.n < 3) {
                    GameEngine.log("Error while reading stdin: " + e2.toString());
                    this.n++;
                    if (this.n == 3) {
                        GameEngine.log("Too many stdin errors, ignoring");
                    }
                }
                try {
                    Thread.sleep(700L);
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public void g() {
        final Semaphore semaphore = new Semaphore(0);
        Thread thread = new Thread(new Runnable() { // from class: com.corrodinggames.rts.java.Main.2
            @Override // java.lang.Runnable
            public void run() {
                GameEngine.setupUncaughtExceptionHandler();
                Looper.a();
                semaphore.release(1);
                Looper.c();
            }
        });
        thread.setDaemon(true);
        thread.start();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void a(String[] strArr) {
        float height;
        float width;
        this.l = strArr;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        boolean z8 = false;
        Integer numValueOf = null;
        Integer numValueOf2 = null;
        GameEngine.log("Reading args");
        String str = null;
        String str2 = null;
        int i = 0;
        while (i < strArr.length) {
            String lowerCase = strArr[i].trim().toLowerCase(Locale.ENGLISH);
            if (str != null) {
                if (str.equals("+connect_lobby")) {
                    a("connect lobby:" + lowerCase);
                    GameEngine.buildVersion = lowerCase;
                    str = null;
                } else if (str.equals("-width")) {
                    numValueOf = Integer.valueOf(Integer.parseInt(lowerCase));
                    str = null;
                } else if (str.equals("-height")) {
                    numValueOf2 = Integer.valueOf(Integer.parseInt(lowerCase));
                    str = null;
                } else {
                    a("Unknown two_part_arg: " + str);
                    str = null;
                }
            } else if (lowerCase.equals("-debug")) {
                i++;
                if (i >= strArr.length) {
                    a("-debug requires parameters");
                    System.exit(1);
                }
                String str3 = strArr[i];
                DebugSocketServer.start(Integer.parseInt(str3.split(":")[0]), str3.split(":")[1]);
            } else if (lowerCase.equals("-debugscript")) {
                i++;
                if (i >= strArr.length) {
                    a("-debugscript requires parameters");
                    System.exit(1);
                }
                DebugSocketServer.addScriptToRun(strArr[i]);
            } else if (lowerCase.equals("-log")) {
                i++;
                if (i >= strArr.length) {
                    a("-log requires parameters");
                    System.exit(1);
                }
                String str4 = strArr[i];
                try {
                    PrintStream printStream = new PrintStream(str4);
                    System.setOut(printStream);
                    System.setErr(printStream);
                    GameEngine.log("File logging started");
                } catch (FileNotFoundException e) {
                    GameEngine.printLog("Cannot open log file:" + str4);
                    e.printStackTrace();
                }
            } else if (!lowerCase.equals("-nologfile")) {
                if (lowerCase.equals("-lang")) {
                    i++;
                    if (i >= strArr.length) {
                        a("-lang requires parameters");
                        System.exit(1);
                    }
                    com.corrodinggames.rts.gameFramework.local.Locale.overrideLanguageCode = strArr[i];
                } else if (lowerCase.equals("-logcolor")) {
                    GameEngine.isPCVersionStatic = true;
                } else if (lowerCase.equals("-nodisplay")) {
                    z = true;
                } else if (lowerCase.equals("-canvasgl")) {
                    GameEngine.isGameStartedStatic = true;
                } else if (lowerCase.equals("-replay_debug")) {
                    GameEngine.isIOSVersionStatic = true;
                } else if (lowerCase.equals("-nopreferipv4")) {
                    z4 = true;
                } else if (lowerCase.equals("-noresources")) {
                    GameEngine.isNetworkServerStatic = true;
                } else if (lowerCase.equals("-nosound")) {
                    z2 = true;
                } else if (lowerCase.equals("-nomusic")) {
                    z3 = true;
                } else if (lowerCase.equals("-safemode")) {
                    GameEngine.isCommandLineMode = true;
                } else if (lowerCase.equals("-extrasafemode")) {
                    GameEngine.isAutomatedTesting = true;
                } else if (lowerCase.equals("-disable_vbos")) {
                    z7 = true;
                } else if (lowerCase.equals("-disable_atlas")) {
                    GameEngine.isDemoVersionStatic = true;
                } else if (lowerCase.equals("-force_vbos")) {
                    z8 = true;
                } else if (lowerCase.equals("-allowsoftwarerender")) {
                    z5 = true;
                } else if (lowerCase.equals("-fullscreen")) {
                    z6 = true;
                } else if (lowerCase.equals("-nobackground")) {
                    GameEngine.isAndroidVersionStatic = true;
                } else if (lowerCase.equals("-nomods")) {
                    GameEngine.isInGameOrLobbyStatic = true;
                } else if (lowerCase.equals("-printunits")) {
                    GameEngine.isUnitImageGenerationMode = true;
                } else if (lowerCase.equals("-outputunitimages")) {
                    GameEngine.isUnitValidationMode = true;
                } else if (lowerCase.equals("-oldreplays")) {
                    GameEngine.isGamePausedOrMinimizedStatic = true;
                } else if (lowerCase.equals("-teamshaders")) {
                    GameEngine.isGameMinimizedStatic2 = true;
                } else if (lowerCase.equals("-noteamshaders")) {
                    GameEngine.isGameMinimizedStatic2 = false;
                } else if (lowerCase.equals("-devdebug")) {
                    i++;
                    if (i >= strArr.length) {
                        a("-debugscript requires parameters");
                        System.exit(1);
                    }
                    GameEngine.platformName = strArr[i];
                } else if (lowerCase.equals("-postprocessing")) {
                    GameEngine.isDedicatedServer = true;
                } else if (lowerCase.equals("-nopostprocessing")) {
                    GameEngine.isDedicatedServer = false;
                } else if (lowerCase.equals("-disabletextureread")) {
                    SlickTexture.F = false;
                } else if (lowerCase.equals("-sandbox")) {
                    GameEngine.isNetworkConnectedStatic = true;
                } else if (lowerCase.equals("-steam")) {
                    GameEngine.isNetworkGameActiveStatic = true;
                } else if (lowerCase.equals("-width") || lowerCase.equals("-height")) {
                    str = lowerCase;
                } else if (lowerCase.startsWith("+")) {
                    if (lowerCase.equals("+connect_lobby")) {
                        str = lowerCase;
                    } else {
                        a("Unknown steam option: " + lowerCase);
                    }
                } else if (lowerCase.trim().length() != 0) {
                    a("Unknown option: " + lowerCase);
                    str2 = "Unknown option: " + lowerCase;
                }
            }
            i++;
        }
        GameEngine.log("Game arguments:");
        for (String str5 : strArr) {
            a("arg: " + str5.trim().toLowerCase(Locale.ENGLISH));
        }
        if (str2 != null) {
            if (GameEngine.isNetworkGameActiveStatic) {
                a("Unknown options but running anyway due to being in steam");
            } else {
                a("Exiting due to unknown option: " + str2);
                System.exit(1);
            }
        }
        GameEngine.isPausedStatic2 = true;
        GameEngine.setupUncaughtExceptionHandler();
        String property = System.getProperty("os.name");
        GameEngine.log("OS name: " + property);
        GameEngine.log("OS version: " + System.getProperty("os.version"));
        GameEngine.log("LWJGL version: " + Sys.getVersion());
        GameEngine.log("Build Number: " + this.e);
        GameEngine.log("Game Version: 1.15");
        GameEngine.log("Game Code: 176");
        GameLogic.isCheatingEnabled = Sys.is64Bit();
        GameEngine.log("Is 64bit: " + GameLogic.isCheatingEnabled);
        GameEngine.log("JVM maxMemory:" + Runtime.getRuntime().maxMemory());
        GameEngine.log("JVM totalMemory:" + Runtime.getRuntime().totalMemory());
        GameEngine.log("JVM freeMemory:" + Runtime.getRuntime().freeMemory());
        if (property != null && property.toLowerCase().contains("mac os")) {
            GameLogic.isSandboxEnabled = true;
        }
        if (z4) {
            GameEngine.log("Skipping preferIPv4Stack=true");
        } else {
            System.setProperty("java.net.preferIPv4Stack", "true");
        }
        if (GameEngine.isNetworkGameActiveStatic) {
            DisabledSteamEngine.a = new JavaSteamEngine();
            GameEngine.log("Early steam init");
            DisabledSteamEngine.a().b();
            GameEngine.log("Early steam init done.");
        } else {
            GameEngine.log("steam not requested");
        }
        g();
        String str6 = c;
        if (z) {
            str6 = VariableScope.nullOrMissingString;
        }
        Input.disableControllers();
        if (a) {
            Renderer.setRenderer(2);
        }
        if (!z8 && GameLogic.isSandboxEnabled) {
            GameEngine.log("Disabling vbo on mac (without force option)");
            z7 = true;
        }
        if (z7) {
            GameEngine.log("disable_vbos requested");
            SGL vBORenderer = Renderer.get();
            if (vBORenderer instanceof VBORenderer) {
                ((VBORenderer)vBORenderer).disableVBOs();
            } else {
                GameEngine.log("Failed to disable VBOs, wrong class");
            }
        }
        SlickGraphicsEngine.c();
        this.j = new SlickGameHandler(str6);
        this.j.b = this;
        this.j.i = z;
        this.j.j = z2;
        this.j.k = z3;
        if (z) {
            GameEngine.printLog("Skipping display mode call");
            height = 800.0f;
            width = 600.0f;
        } else {
            try {
                DisplayMode displayMode = Display.getDisplayMode();
                height = displayMode.getHeight();
                width = displayMode.getWidth();
            } catch (Exception e2) {
                GameEngine.printLog("Failed to get display mode, defaulting to min size");
                e2.printStackTrace();
                height = 800.0f;
                width = 600.0f;
            }
        }
        GameEngine.log("screenHeight:" + height);
        GameEngine.log("screenWidth:" + width);
        int iIntValue = 1000;
        int iIntValue2 = 733;
        if (height > 800.0f) {
            iIntValue = 1000;
            iIntValue2 = 800;
        }
        if (height > 900.0f) {
            iIntValue = 1600;
            iIntValue2 = 900;
        }
        if (z) {
            iIntValue = 10;
            iIntValue2 = 10;
        }
        if (numValueOf != null) {
            GameEngine.log("Overriding width to:" + numValueOf);
            iIntValue = numValueOf.intValue();
        }
        if (numValueOf2 != null) {
            GameEngine.log("Overriding height to:" + numValueOf2);
            iIntValue2 = numValueOf2.intValue();
        }
        if (z5) {
            GameEngine.log("allowSoftwareOpenGL is now on");
            System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
        }
        this.j.l = false;
        boolean z9 = z6;
        try {
            if (this.j.l) {
                this.j.a(iIntValue * 2, iIntValue2 * 2);
                this.k = new SlickGameContainer(new ScalableGame(this.j, iIntValue, iIntValue2), iIntValue, iIntValue2, z9);
            } else {
                this.k = new SlickGameContainer(this.j, iIntValue, iIntValue2, z9);
            }
            this.j.c = this.k;
            Display.setResizable(true);
            this.r = new Thread(new GameStartupRunnable(this));
            this.r.setDaemon(false);
            this.r.start();
        } catch (SlickException e3) {
            throw new RuntimeException((Throwable) e3);
        }
    }

    public void b(String str) {
        this.q.a(str, true);
    }

    public synchronized void h() {
        b("displayModes");
        b("starting controllers");
        this.o = System.nanoTime();
        GameEngine.isPausedStatic2 = true;
        GameEngine.isSandboxModeStatic2 = true;
        if (!GameEngine.isNetworkServerStatic) {
            if (GameEngine.isGameStartedStatic) {
                GameEngine.isIOSVersionStatic2 = true;
                GameEngine.isAndroidVersionStatic2 = true;
                GameEngine.gameEngineClass = SoftwareGraphicsInterface.class;
            } else {
                GameEngine.isIOSVersionStatic2 = true;
                GameEngine.isAndroidVersionStatic2 = true;
                GameEngine.gameEngineClass = SlickGraphicsEngine.class;
            }
        }
        if (this.j != null && !this.j.j) {
            OpenALAudio openALAudio = new OpenALAudio(20, 9, 512);
            GameEngine.log("openALAudio hasDevice:" + openALAudio.hasDevice());
            SoundEngine.soundFactory = new OpenALSoundFactory(openALAudio);
            if (this.j.k) {
                GameEngine.log("Music disabled");
                MusicManager.musicFactory = new NullMusicFactory();
            } else {
                MusicManager.musicFactory = new OpenALMusicFactory(openALAudio);
            }
        } else {
            GameEngine.updatePaintTextSizeIfNeeded("Disabling sound with NullSoundFactory");
            SoundEngine.soundFactory = new NullSound();
            MusicManager.musicFactory = new NullMusicFactory();
        }
        MasterServerClient.httpClientManager = new JavaHttpClientManager();
        InputController.b = new JavaInputHandler();
        long jA = PerformanceProfiler.a();
        b("loading libRocket");
        GameEngine.log("start libRocket setup");
        this.d = new HeadlessGameView();
        this.i = CommonGuiEngine.p();
        this.i.f = this;
        this.p = new SlickLibRocketManager();
        this.i.init(this.p, this.d);
        this.p.debug = false;
        this.p.setup();
        b("libRocket - fonts");
        this.p.loadFont("font/Delicious-Roman.otf");
        this.p.loadFont("font/Delicious-Italic.otf");
        this.p.loadFont("font/Delicious-Bold.otf");
        this.p.loadFont("font/Delicious-BoldItalic.otf");
        this.p.loadFont("font/Roboto-Regular.ttf");
        this.p.loadFont("font/Roboto-Bold.ttf");
        GameEngine.log("NotoSansCJKsc start");
        this.p.loadFont("font/NotoSansCJKsc-Regular.otf", "notoSans");
        this.p.loadFont("font/DroidSansFallback.ttf", "fallback");
        GameEngine.log("NotoSansCJKsc end");
        this.i.setGamePaused2();
        GameEngine.log("end libRocket setup");
        b("GuiEngine");
        PerformanceProfiler.a("libRocket setup took:", jA);
        GameEngine.gameEngineVersion = this.e;
        ServerContext serverContext = new ServerContext();
        b("GameEngine");
        GameEngine.screenSize = new Point(this.j.a.getWidth(), this.j.a.getHeight());
        GameEngine gameEngineCreateGameEngine = GameEngine.createGameEngine(serverContext, this.q);
        b("GameEngine ready");
        GameEngine.log("version: " + gameEngineCreateGameEngine.getVersion() + " " + gameEngineCreateGameEngine.getVersionCode(false) + ":" + this.e);
        this.i.showMainMenu();
        DebugSocketServer.runPendingScripts();
        this.h = gameEngineCreateGameEngine.networkEngine;
        gameEngineCreateGameEngine.settingsEngine.showZoomButton = false;
        gameEngineCreateGameEngine.settingsEngine.showUnitGroups = false;
        this.j.a(this.d);
        this.j.a(1000, 800);
        long jNanoTime = System.nanoTime();
        GameEngine.log("-----------------------------");
        GameEngine.log("----- Game init finished in:" + ((jNanoTime - this.o) / 1000000.0d) + " ms");
        gameEngineCreateGameEngine.networkEngine.callbacks = this;
        gameEngineCreateGameEngine.networkEngine.playerName = "unset";
        if (!GameEngine.isAndroidVersionStatic) {
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    /* JADX INFO: renamed from: b */
    public void onStartGameEvent() {
        this.f.a(new Runnable() { // from class: com.corrodinggames.rts.java.Main.3
            @Override // java.lang.Runnable
            public void run() {
                GameEngine gameEngine = GameEngine.getInstance();
                GameEngine.log("got startGameEvent..");
                MultiplayerBattleroomActivity.setupGame();
                if (gameEngine.tileMap == null || !gameEngine.tileMap.isCursorActive) {
                    GameEngine.log("Not starting multiplayer game because map failed to load");
                    gameEngine.networkEngine.af();
                    return;
                }
                gameEngine.networkEngine.bd = true;
                gameEngine.reloadMap = false;
                gameEngine.isStopped = false;
                Main.this.i.showAbout(false);
                GameMainManager.getInstance().resumeGame();
                Main.this.p.getActiveDocument();
                if (Main.this.p.scriptEngine != null) {
                    Main.this.p.scriptEngine.getRoot().resumeNonMenu();
                } else {
                    GameEngine.log("startGameEvent: scriptEngine==null");
                    GameEngine.printStackTrace();
                }
            }
        });
    }

    public void a(float f) {
        this.f.a();
    }

    public void a(boolean z) {
        this.g = false;
        GameEngine gameEngine = GameEngine.getInstance();
        if (!z) {
            gameEngine.networkEngine.u();
        } else {
            gameEngine.networkEngine.disconnectNetworking("shutdownServer");
        }
        try {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } catch (SecurityException e2) {
            e2.printStackTrace();
        }
    }

    Main() {
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public synchronized boolean a(NetworkConnection networkConnection, String str, String str2) {
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public synchronized void b(NetworkConnection networkConnection, String str, String str2) {
        a(networkConnection, str, str2, false);
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public void c() {
        ScriptEngine scriptEngine;
        if (!GameEngine.getInstance().networkEngine.gameHasBeenStarted && (scriptEngine = ScriptEngine.getInstance()) != null) {
            scriptEngine.addScriptToQueueIfNotAlreadyQueued("mp.refreshUI()");
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public synchronized void a(final int i, final String str, final String str2, final NetworkConnection networkConnection) {
        if (this.p != null && this.p.scriptEngine != null) {
            this.p.scriptEngine.addRunnableToQueue(new Runnable() { // from class: com.corrodinggames.rts.java.Main.4
                @Override // java.lang.Runnable
                public void run() {
                    Main.this.p.scriptEngine.getRoot().receiveChatMessage(i, str, str2, networkConnection);
                }
            });
        } else {
            GameEngine.printStackTrace();
        }
    }

    public synchronized void a(NetworkConnection networkConnection, String str, String str2, boolean z) {
        if (!z) {
            a(str + ": " + str2);
        }
        if (this.s) {
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public String a(NetworkConnection networkConnection, String str) {
        return null;
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public synchronized void c(NetworkConnection networkConnection, String str, String str2) {
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public synchronized void b(NetworkConnection networkConnection, String str) {
    }

    public void i() {
        GameEngine.getInstance();
        this.j.g();
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    public void d() {
        GameMainManager.getInstance().closeBattleroomIfOpen();
    }

    @Override // com.corrodinggames.rts.gameFramework.network.NetworkCallbacks
    /* JADX INFO: renamed from: a */
    public void onPasswordPrompt(PasswordHandler passwordHandler) {
        GameMainManager.getInstance().showPasswordPrompt(passwordHandler);
    }
}
