package com.corrodinggames.rts.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.*;
import android.os.Debug;
import android.util.DisplayMetrics;
import android.util.Log;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.appFramework.GameView;
import com.corrodinggames.rts.appFramework.InGameActivity;
import com.corrodinggames.rts.game.ai.AIController;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.*;
import com.corrodinggames.rts.game.units.custom.ConfigParseException;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfigParser;
import com.corrodinggames.rts.game.units.management.UnitSpatialIndex;
import com.corrodinggames.rts.gameFramework.*;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.debug.DebugServer;
import com.corrodinggames.rts.gameFramework.effects.BuildPreview;
import com.corrodinggames.rts.gameFramework.effects.CloudRenderer;
import com.corrodinggames.rts.gameFramework.effects.EffectEmitter;
import com.corrodinggames.rts.gameFramework.effects.EffectManager;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.graphics.*;
import com.corrodinggames.rts.gameFramework.local.Locale;
import com.corrodinggames.rts.gameFramework.mod.ModManager;
import com.corrodinggames.rts.gameFramework.network.NetworkEngine;
import com.corrodinggames.rts.gameFramework.path.PathEngine;
import com.corrodinggames.rts.gameFramework.stats.StatGroup;
import com.corrodinggames.rts.gameFramework.stats.StatType;
import com.corrodinggames.rts.gameFramework.steam.DisabledSteamEngine;
import com.corrodinggames.rts.gameFramework.ui.GameUI;
import com.corrodinggames.rts.gameFramework.ui.Minimap;
import com.corrodinggames.rts.gameFramework.utility.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.i */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/i.class */
public class GameLogic extends GameEngine {

    /* JADX INFO: renamed from: a */
    public static String gameVersionName;

    /* JADX INFO: renamed from: b */
    public static boolean isCheatingEnabled;

    /* JADX INFO: renamed from: c */
    public static boolean isSandboxEnabled;

    /* JADX INFO: renamed from: d */
    int allUnitsChecksum;

    /* JADX INFO: renamed from: e */
    public float densityScaleMultiplier;

    /* JADX INFO: renamed from: f */
    public static String safeModeReason = null;

    /* JADX INFO: renamed from: g */
    JpegFrameWriterTask[] someKArray;

    /* JADX INFO: renamed from: h */
    String someString;

    /* JADX INFO: renamed from: i */
    public boolean someBoolean;

    /* JADX INFO: renamed from: j */
    public int cleanupCounter;

    /* JADX INFO: renamed from: k */
    public ConcurrentLinkedQueue gameThreadRunnableQueue;

    /* JADX INFO: renamed from: l */
    Paint paintL;

    /* JADX INFO: renamed from: m */
    Paint fpsPaint;

    /* JADX INFO: renamed from: n */
    Paint paintN;

    /* JADX INFO: renamed from: o */
    Paint paintO;

    /* JADX INFO: renamed from: p */
    Paint paintP;

    /* JADX INFO: renamed from: q */
    int fpsAccumulator;

    /* JADX INFO: renamed from: r */
    int fpsFrameCounter;

    /* JADX INFO: renamed from: s */
    int fps;

    /* JADX INFO: renamed from: t */
    float averageFrameTime;

    /* JADX INFO: renamed from: u */
    public String fpsString;

    /* JADX INFO: renamed from: v */
    Rect rectV;

    /* JADX INFO: renamed from: w */
    public ArrayList arrayListW;

    /* JADX INFO: renamed from: x */
    Paint paintX;

    /* JADX INFO: renamed from: y */
    Paint paintY;

    /* JADX INFO: renamed from: z */
    Paint paintZ;

    /* JADX INFO: renamed from: A */
    public Paint paintA;

    /* JADX INFO: renamed from: B */
    public GameStateData gameStateData;

    /* JADX INFO: renamed from: C */
    public GameStateManager gameStateManager;

    /* JADX INFO: renamed from: D */
    public CloudRenderer cloudRenderer;

    /* JADX INFO: renamed from: E */
    GameObject gameObjectE;

    /* JADX INFO: renamed from: F */
    boolean hasCheckedSafeMode;

    /* JADX INFO: renamed from: G */
    float accumulator;

    /* JADX INFO: renamed from: H */
    public float speedMultiplier;

    /* JADX INFO: renamed from: I */
    public float floatI;

    /* JADX INFO: renamed from: J */
    public float lastDelta;

    /* JADX INFO: renamed from: K */
    FrameBufferHelper postBaseShader;

    /* JADX INFO: renamed from: L */
    FrameBufferHelper postDisplacementShader;

    /* JADX INFO: renamed from: M */
    boolean postProcessingFailed;

    /* JADX INFO: renamed from: N */
    GraphicsEngine graphicsEngine;

    /* JADX INFO: renamed from: O */
    Texture waterCloudTexture;

    /* JADX INFO: renamed from: P */
    Texture waterLayer1Texture;

    /* JADX INFO: renamed from: Q */
    Texture waterLayer2Texture;

    /* JADX INFO: renamed from: R */
    float waterAnimationTimer;

    /* JADX INFO: renamed from: S */
    Rect waterRect;

    /* JADX INFO: renamed from: T */
    RectF waterRectF;

    /* JADX INFO: renamed from: U */
    public Texture textureU;

    /* JADX INFO: renamed from: V */
    public Texture textureV;

    /* JADX INFO: renamed from: W */
    GameObjectArrayList renderList;

    /* JADX INFO: renamed from: X */
    GameObjectArrayList renderListBuffer;

    /* JADX INFO: renamed from: Y */
    Matrix matrixY;

    /* JADX INFO: renamed from: Z */
    public ArrayList arrayListZ;

    /* JADX INFO: renamed from: aa */
    public ArrayList arrayListAA;

    /* JADX INFO: renamed from: ab */
    Timer gameTimer;

    /* JADX INFO: renamed from: ac */
    boolean booleanAC;

    /* JADX INFO: renamed from: ad */
    Object initLock;

    /* JADX INFO: renamed from: ae */
    int menuLoadFailureCount;

    /* JADX INFO: renamed from: af */
    BaseUnit cameraFocusUnit;

    /* JADX INFO: renamed from: ag */
    BaseUnit nextCameraFocusUnit;

    /* JADX INFO: renamed from: ah */
    float cameraFocusTransition;

    /* JADX INFO: renamed from: ai */
    boolean isCameraFocusing;

    public GameLogic(Context context) {
        super(context);
        this.densityScaleMultiplier = 1.0f;
        this.someKArray = new JpegFrameWriterTask[6];
        this.someBoolean = false;
        this.cleanupCounter = 0;
        this.gameThreadRunnableQueue = new ConcurrentLinkedQueue();
        this.fpsAccumulator = 0;
        this.fpsFrameCounter = 0;
        this.fps = 0;
        this.averageFrameTime = 16.0f;
        this.fpsString = "0fps";
        this.rectV = new Rect();
        this.arrayListW = new ArrayList();
        this.paintA = new Paint();
        this.cloudRenderer = new CloudRenderer();
        this.accumulator = 0.0f;
        this.speedMultiplier = 1.0f;
        this.waterAnimationTimer = 0.0f;
        this.waterRect = new Rect();
        this.waterRectF = new RectF();
        this.textureU = null;
        this.textureV = null;
        this.renderList = new GameObjectArrayList("allOnScreenObjects");
        this.renderListBuffer = new GameObjectArrayList("allOnScreenObjectsDirty");
        this.matrixY = new Matrix();
        this.arrayListZ = new ArrayList();
        this.arrayListAA = new ArrayList();
        this.initLock = new Object();
        this.menuLoadFailureCount = 0;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: a */
    public boolean createInstance() {
        if (this.gameUI.isDraggingSelection) {
            return true;
        }
        if (this.missionEngine2 != null && this.missionEngine2.b()) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: a */
    public boolean shouldSkipUpdate(boolean z) {
        if (!z || this.replayEngine.j()) {
            if (this.gameUI.isDraggingSelection || this.isShowingDialog) {
                return true;
            }
            if (this.isStopped && !this.reloadMap) {
                return true;
            }
            if (this.exitGameThread && this.missionEngine2 != null && this.missionEngine2.b()) {
                return true;
            }
        }
        if ((z && !this.networkEngine.gameHasBeenStarted) || this.networkEngine.I()) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: b */
    public int getFps() {
        return this.fps;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: c */
    public boolean isCustomGameMode() {
        return this.isExtraSafeMode;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: d */
    public boolean isExperimental() {
        return this.isExtraSafeMode2;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: a */
    public synchronized void init(Context context) {
        Log.d("RustedWarfare", "--- ----------------- ----");
        Log.d("RustedWarfare", "--- GameEngine:init() ----");
        Log.d("RustedWarfare", "--- ----------------- ----");
        if (this.isInitialized) {
            Log.d("RustedWarfare", "GameEngine init has already been called");
            return;
        }
        GameEngine.log("Version:" + getVersionNameWithSuffix());
        if (printLog() && getClass().equals(GameLogic.class)) {
            throw new RuntimeException("inSpace but class is:" + getClass());
        }
        System.gc();
        loadLevel("Asset Index");
        this.assetIndex = new AssetIndex(context);
        long jA = PerformanceProfiler.a();
        this.performanceProfiler = new PerformanceProfiler(this);
        this.performanceProfiler.a(ProfilerSection.init_total);
        if (isPausedStatic2) {
            this.densityScaleRaw = 1.0f;
        } else {
            DisplayMetrics displayMetrics = context.e().getDisplayMetrics();
            this.densityScaleRaw = context.e().getDisplayMetrics().density;
            GameEngine.log("densityScaleRaw: " + this.densityScaleRaw);
            updateDensity(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
        this.densityScaleRaw *= this.densityScaleMultiplier;
        GameEngine.log("densityScaleRaw*densityScaleMultiplier: " + this.densityScaleRaw);
        if (GameEngine.centerCameraOnPosition(context)) {
            this.isDemo = true;
        }
        //this.gameObjectE = new Unit();
        this.isServer = false;
        loadLevel("InputController");
        this.inputController = new InputController();
        this.inputController.a();
        loadLevel("SettingsEngine");
        this.settingsEngine = SettingsEngine.getInstance(context);
        this.settingsEngine.loadMainExternalFolder(true);
        FileHelper.initialize();
        int i = 3;
        if (isDebugVersionStatic2) {
            i = 1;
        }
        if (this.settingsEngine.numIncompleteLoadAttempts > 1 || this.settingsEngine.numLoadsSinceRunningGameOrNormalExit > i) {
            this.isSafeMode = true;
            if (this.settingsEngine.numIncompleteLoadAttempts > 2 || this.settingsEngine.numLoadsSinceRunningGameOrNormalExit > 4) {
                this.settingsEngine.forceEnglish = true;
                this.forceEnglish = true;
            }
            if (this.settingsEngine.numIncompleteLoadAttempts > 3) {
                this.settingsEngine.newRender = false;
            }
            if (this.settingsEngine.numIncompleteLoadAttempts > 4 || this.settingsEngine.numLoadsSinceRunningGameOrNormalExit > 5) {
                GameEngine.log("Extra safe mode");
                this.isExtraSafeMode = true;
            }
            if (this.settingsEngine.numIncompleteLoadAttempts > 5) {
                GameEngine.log("Extra safe mode x2");
                this.isExtraSafeMode2 = true;
            }
            if (this.settingsEngine.numIncompleteLoadAttempts > 6) {
                GameEngine.log("Extra safe mode x3");
                this.settingsEngine.newRender = false;
                this.settingsEngine.shaderEffects = false;
                this.settingsEngine.teamShaders = false;
            }
            if (this.settingsEngine.newRender && this.settingsEngine.numLoadsSinceRunningGameOrNormalExit > 15) {
                GameEngine.log("Disabling opengl mode");
                this.settingsEngine.newRender = false;
            }
            GameEngine.log("starting game in safe mode, numIncompleteLoadAttempts:" + this.settingsEngine.numIncompleteLoadAttempts + " numLoadsSinceRunningGameOrNormalExit:" + this.settingsEngine.numLoadsSinceRunningGameOrNormalExit);
        }
        if (isCommandLineMode) {
            this.isSafeMode = true;
            this.safeModeReason = "<forced by command line>";
        }
        if (isAutomatedTesting) {
            this.isSafeMode = true;
            this.isExtraSafeMode = true;
            this.isExtraSafeMode2 = true;
            this.safeModeReason = "<forced by command line>";
        }
        this.settingsEngine.numLoadsSinceRunningGameOrNormalExit++;
        this.settingsEngine.numIncompleteLoadAttempts++;
        if (!this.settingsEngine.save() && isDebugVersionStatic2) {
            GameEngine.log("starting game in safe mode, failed to save settings");
            this.safeModeReason = "failing to write preferences data";
            this.isSafeMode = true;
        }
        DebugServer.a();
        this.screenScale = getScreenScale();
        GameEngine.log("densityScale(): " + this.screenScale);
        long jA2 = PerformanceProfiler.a();
        Locale.initialize();
        PerformanceProfiler.a("Locale.init took:", jA2);
        PlayerTeam.updateAllTeamData();
        this.paintL = new Paint();
        this.fpsPaint = new Paint();
        this.fpsPaint.a(255, 255, 255, 255);
        this.fpsPaint.a(true);
        updatePaintTextSize(this.fpsPaint, 16.0f);
        this.paintN = new Paint();
        this.paintN.a(255, 255, 255, 255);
        this.paintN.a(true);
        updatePaintTextSize(this.paintN, 16.0f);
        this.paintO = new Paint();
        this.paintO.a(100, 255, 0, 0);
        updatePaintTextSize(this.paintO, 16.0f);
        this.paintP = new Paint();
        this.paintP.a(100, 0, 255, 0);
        updatePaintTextSize(this.paintP, 16.0f);
        this.teamInfoPaint = new Paint();
        this.centeredPaint = new Paint();
        this.centeredPaint.a(Paint.Align.CENTER);
        this.centeredPaint.a(true);
        this.centeredPaint.a(Typeface.a(Typeface.c, 0));
        updatePaintTextSize(this.centeredPaint, 16.0f);
        this.loadingPaint = new Paint();
        this.loadingPaint.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE);
        this.loadingPaint.a(true);
        this.loadingPaint.a(Paint.Align.CENTER);
        updatePaintTextSize(this.loadingPaint, 18.0f);
        this.paintX = new Paint();
        this.paintX.b(-1);
        this.paintX.c(100);
        this.paintY = new Paint();
        this.paintY.b(-7829368);
        this.paintY.c(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_SATELLITE_SERVICE);
        this.paintY.a(Paint.Style.STROKE);
        this.paintY.a(1.0f);
        long jA3 = PerformanceProfiler.a();
        loadLevel("AudioEngine");
        SoundEngine.noop();
        this.soundEngine = new SoundEngine();
        this.soundEngine.loadSounds(context);
        PerformanceProfiler.a("AudioEngine took:", jA3);
        loadLevel("MusicController");
        this.musicManager = new MusicManager();
        this.musicManager.init(context);
        if (graphicsEngine != null) {
            log("init(): using Graphics instance");
            this.graphicsEngine2 = graphicsEngine;
        } else if (gameEngineClass != null) {
            log("init(): using GraphicsSlick2d");
            try {
                this.graphicsEngine2 = (GraphicsEngine) gameEngineClass.newInstance();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e2) {
                throw new RuntimeException(e2);
            }
        } else if (isPausedStatic2) {
            this.graphicsEngine2 = new NullGraphicsInterface();
        } else {
            this.graphicsEngine2 = new SoftwareGraphicsInterface();
        }
        loadLevel("graphics.init");
        this.graphicsEngine2.a(context);
        this.graphicsEngine2.b();
        FileChangeEngine.a();
        loadLevel("Fonts");
        updateGraphics();
        loadLevel("effects.init");
        this.effectManager = new EffectManager();
        this.effectManager.loadContent(context);
        loadLevel("minimapHandler");
        this.minimap = new Minimap();
        this.minimap.init(context);
        if (screenSize != null) {
            GameEngine.log("We have an initial screen size, can do early setup of image buffers");
            loadLevel("Map Buffers");
            updateWindowResolution(screenSize.worldX, screenSize.worldY);
            updateCameraSystem();
            TileMap.initSoftFogFading();
            TileMap.updateLayerBuffers();
            this.minimap.createImageBuffers();
            if (GameEngine.isPostProcessingSupported()) {
                loadLevel("Setting up postprocessing");
                if (!setupPostProcessing()) {
                    GameEngine.log("Failed to setup postprocessing");
                }
            }
        }
        loadLevel("PathEngine");
        this.pathfindingEngine = new PathEngine();
        loadLevel("GroupController");
        this.groupController = new FormationEngine();
        loadLevel("CollisionEngine");
        this.gameRenderer = new CollisionEngine();
        loadLevel("InterfaceEngine");
        this.gameUI = new GameUI();
        this.gameUI.initializeUIResources(context);
        this.gameStateManager = GameStateManager.c(context);
        loadLevel("NetworkEngine");
        this.networkEngine = new NetworkEngine();
        this.networkEngine.updateAIDifficulty();
        loadLevel("StatsHandler");
        this.gameStatistics = new GameStatistics();
        loadLevel("ModEngine");
        this.modManager = new ModManager();
        this.modManager.loadAndApply();
        if (this.isSafeMode) {
            this.modManager.disableAllMods();
        }
        loadLevel("CommandController");
        this.commandController = new CommandController();
        loadLevel("GameSaver");
        this.gameSaver = new GameSaver();
        loadLevel("ReplayEngine");
        this.replayEngine = new ReplayEngine();
        this.replayEngine.a(context);
        loadLevel("UnitGeoIndex");
        this.unitSpatialIndex = new UnitSpatialIndex();
        loadLevel("Precalculating map fog");
        TileMap.buildFogSmoothAtlas();
        loadLevel("ScorchMark.load");
        ScorchMark.b();
        loadLevel("Projectile.load");
        Projectile.c();
        loadLevel("Emitter.load");
        EffectEmitter.b();
        loadLevel("Unit.loadAllUnits");
        long jA4 = PerformanceProfiler.a();
        BaseUnit.loadAllUnits();
        PerformanceProfiler.a("loadAllUnits took:", jA4);
        loadLevel("Loading custom unit data");
        long jA5 = PerformanceProfiler.a();
        CustomUnitConfigParser.loadAllCustomUnitsAndMods();
        loadLevel("getAllUnitsChecksum");
        PerformanceProfiler.a("CustomUnits took:", jA5);
        long jA6 = PerformanceProfiler.a();
        this.allUnitsChecksum = BaseUnit.bM();
        PerformanceProfiler.a("allUnitsChecksum took:", jA6);
        this.paintZ = new Paint();
        this.paintZ.a(50, 255, 255, 255);
        logMessageWithTime();
        System.gc();
        this.isInitialized = true;
        GameEngine.log("Init completed");
        PerformanceProfiler.a("Loading took:", jA);
        this.performanceProfiler.b(ProfilerSection.init_total);
        this.performanceProfiler.a(true, true);
        long jA7 = PerformanceProfiler.a();
        loadLevel("Loading map data");
        if (!GameEngine.isAndroidVersionStatic) {
            loadMenuBackground();
        }
        PerformanceProfiler.a("loadAMenuMap took:", jA7);
        loadLevel("Last setup");
        setupANRWatchDog();
        this.networkEngine.m();
        loadLevel("init complete");
        if (isUnitImageGenerationMode) {
            UnitTypeEnum.loadUnitTypeImages();
            System.exit(0);
        }
        if (isUnitValidationMode) {
            UnitTypeEnum.loadAllUnitTypes();
            System.exit(0);
        }
        this.isGameMinimized = true;
    }

    /* JADX INFO: renamed from: a */
    public void updateDensity(int i, int i2) {
        float fDistance = Utility.distance(0.0f, 0.0f, i, i2) / 1131.0f;
        GameEngine.log("defaultViewpointZoomDensity: " + fDistance);
        if (fDistance < 0.5f) {
            fDistance = 0.5f;
        }
        if (fDistance > 3.0f) {
            fDistance = 3.0f;
        }
        GameEngine.log("defaultViewpointZoomDensity after limit: " + fDistance);
        this.cameraIsDragging = 1.0f;
        if (Utility.abs(fDistance - 1.0f) > 0.1d) {
            this.cameraIsDragging = fDistance;
            if (this.cameraIsDragging > 2.0f) {
                this.cameraIsDragging = 2.0f;
            }
            if (this.cameraIsDragging < 0.5f) {
                this.cameraIsDragging = 0.5f;
            }
            this.zoom = this.cameraEdgeScrollZone * this.cameraIsDragging;
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: e */
    public void stopAndReset() {
        stopGameThreadIfNotInGameThread();
        resetFlags();
    }

    /* JADX INFO: renamed from: f */
    public void resetFlags() {
        resetGame(false);
        this.loadNewGame = false;
        this.reloadMap = false;
        this.exitGameThread = false;
        this.isShowingDialog = false;
        this.gameUI.isDraggingSelection = false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: a */
    public synchronized void loadGame(boolean z, GameMode gameMode)  {
        stopGameThreadIfNotInGameThread();
        loadLevel(z, false, gameMode);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: a */
    public void loadLevel(boolean z, boolean z2, GameMode gameMode)  {
        InGameActivity surfaceHolder;
        this.lastTimeMillis = this.settingsEngine.teamUnitCapSinglePlayer;
        if (this.lastTimeMillis < 1) {
            this.lastTimeMillis = 1;
        }
        this.currentTimeMillis = this.lastTimeMillis;
        resetGame(z2);
        PlayerTeam.staticUpdateAllTeamColors();
        this.isServer = false;
        System.gc();
        this.fullReload = true;
        this.loadNewGame = false;
        this.isShowingDialog = false;
        this.exitGameThread = false;
        this.lastTick = 0;
        this.isGameEngineReady = false;
        this.networkEngine.a(1L);
        this.currentTick = 0;
        this.globalSeed = 0;
        Utility.resetSharedRandomSeed();
        this.networkEngine.t();
        if (!z2) {
            this.isTouchDown = false;
            this.isTouchMoving = false;
            this.touchStartX = 0.0f;
            this.isPinching = false;
            this.touchStartY = false;
        }
        this.cleanupCounter = 0;
        if (!z2) {
            this.cameraEdgeScrollZone = 1.0f;
        }
        this.pinchStartZoom = 0.0f;
        if (!this.replayEngine.j()) {
            if (!this.networkEngine.B) {
                CustomUnitConfigParser.enableAllCustomUnits(true);
            } else {
                CustomUnitConfigParser.applyPendingNetworkUnits();
            }
        }
        if (!this.networkEngine.B) {
            if (!this.replayEngine.j() && z) {
                this.playerTeam = new GameTeam(0);
                this.playerTeam.teamName = "Player";
                for (int i = 1; i < 8; i++) {
                    new AIController(i);
                }
                this.networkEngine.aq();
            }
        } else {
            this.playerTeam = this.networkEngine.localPlayerTeam;
            if (this.playerTeam == null) {
                throw new RuntimeException("cannot find player's team");
            }
            if (this.playerTeam != PlayerTeam.k(this.playerTeam.teamId)) {
                GameEngine.logWarningAndStack("Stale playerTeam");
            }
        }
        this.missionEngine = null;
        this.tileMap = new TileMap();
        try {
            if (this.remoteMapStream != null) {
                InputStream activeInputStream = this.remoteMapStream.getActiveInputStream();
                try {
                    activeInputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.tileMap.isWorldPointVisibleForTeam(activeInputStream, z2);
            } else {
                this.tileMap.clampWorldX(getCurrentMapPath(), z2);
            }
            if (!this.tileMap.isCursorActive) {
                log("map did not load, returning");
                this.fullReload = false;
                return;
            }
            this.tileMap.fogRenderActive = false;
            PlayerTeam.getTeamStatistics();
            for (int i2 = 0; i2 < PlayerTeam.TEAM_NEUTRAL; i2++) {
                PlayerTeam playerTeamK = PlayerTeam.k(i2);
                if (playerTeamK != null) {
                    playerTeamK.updateTeamConnectionStatus();
                }
            }
            if (!z2) {
                CustomUnitConfig.spawnOnNewMapAccordingToTeamColors();
            }
            if (!this.networkEngine.B && !this.replayEngine.j()) {
                this.networkEngine.roomSettings.incomeMultiplier = 1.0f;
                this.networkEngine.roomSettings.randomSeed = Utility.getRandomIntInRange(1, 1000000000);
            }
            this.globalSeed = this.networkEngine.roomSettings.randomSeed;
            log("global Seed: " + this.globalSeed);
            if (this.networkEngine.B || this.replayEngine.j()) {
                if (!this.networkEngine.F) {
                    this.currentTimeMillis = this.networkEngine.aw;
                    this.lastTimeMillis = this.networkEngine.ax;
                }
                GameEngine.log("Unit cap is now: " + this.lastTimeMillis);
                if (this.networkEngine.roomSettings.fodMode == 0) {
                    this.tileMap.fogEnabled = false;
                    this.tileMap.fogPeriodicMaintenanceEnabled = false;
                } else if (this.networkEngine.roomSettings.fodMode == 1) {
                    this.tileMap.fogEnabled = true;
                    this.tileMap.fogPeriodicMaintenanceEnabled = false;
                } else if (this.networkEngine.roomSettings.fodMode == 2) {
                    this.tileMap.fogEnabled = true;
                    this.tileMap.fogPeriodicMaintenanceEnabled = true;
                }
                this.tileMap.fogRenderActive = this.networkEngine.roomSettings.revealedMap;
                byte b = 10;
                if (this.networkEngine.roomSettings.revealedMap) {
                    b = 10;
                }
                for (int i3 = 0; i3 < PlayerTeam.TEAM_NEUTRAL; i3++) {
                    PlayerTeam playerTeamK2 = PlayerTeam.k(i3);
                    if (playerTeamK2 != null) {
                        if (playerTeamK2.fogOfWarData == null) {
                            GameEngine.log("Fog null for team: " + playerTeamK2.teamId);
                        } else {
                            for (int i4 = 0; i4 < this.tileMap.tileCountX; i4++) {
                                for (int i5 = 0; i5 < this.tileMap.tileCountY; i5++) {
                                    playerTeamK2.fogOfWarData[i4][i5] = b;
                                }
                            }
                        }
                    }
                }
                int iK = this.networkEngine.k();
                for (int i6 = 0; i6 < PlayerTeam.TEAM_NEUTRAL; i6++) {
                    PlayerTeam playerTeamK3 = PlayerTeam.k(i6);
                    if (playerTeamK3 != null) {
                        playerTeamK3.credits = iK;
                        if (playerTeamK3.isTeamSpectator) {
                            if (!playerTeamK3.isTeamLocked) {
                                if (playerTeamK3.teamAIDifficultyOverride != null) {
                                    playerTeamK3.teamPingTime = playerTeamK3.teamAIDifficultyOverride.intValue();
                                } else {
                                    playerTeamK3.teamPingTime = this.networkEngine.roomSettings.aiDifficulty;
                                }
                            } else {
                                playerTeamK3.c("aiDifficulty is locked");
                            }
                        }
                        playerTeamK3.isTeamConnectionActive = this.networkEngine.roomSettings.sharedControl;
                        boolean z3 = false;
                        boolean z4 = false;
                        int iIntValue = this.networkEngine.roomSettings.startingUnits;
                        if (playerTeamK3.teamAIBehaviourOverride != null) {
                            iIntValue = playerTeamK3.teamAIBehaviourOverride.intValue();
                        }
                        if (iIntValue != 1) {
                            boolean z5 = true;
                            boolean z6 = true;
                            Float fValueOf = null;
                            Float fValueOf2 = null;
                            Float fValueOf3 = null;
                            Float fValueOf4 = null;
                            if (iIntValue == 5 || iIntValue == 4 || iIntValue > 10) {
                                z6 = false;
                            }
                            if (iIntValue == 5 || iIntValue == 4 || iIntValue == 3 || iIntValue > 10) {
                                z5 = false;
                            }
                            if (iIntValue == 9) {
                                z6 = false;
                                z5 = false;
                            }
                            for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
                                if ((baseUnit instanceof BaseUnit) && !baseUnit.isDestroyed && baseUnit.team == playerTeamK3) {
                                    if (baseUnit.isSelectable && !z3) {
                                        z3 = true;
                                        fValueOf = Float.valueOf(baseUnit.posX);
                                        fValueOf2 = Float.valueOf(baseUnit.posY);
                                        if (!z5) {
                                            baseUnit.getUnitAICondition();
                                        }
                                    }
                                    if (baseUnit.isTargetable && !z4) {
                                        z4 = true;
                                        fValueOf3 = Float.valueOf(baseUnit.posX);
                                        fValueOf4 = Float.valueOf(baseUnit.posY);
                                        if (!z6) {
                                            baseUnit.getUnitAICondition();
                                        }
                                    }
                                }
                            }
                            if (fValueOf == null) {
                                fValueOf = fValueOf3;
                                fValueOf2 = fValueOf4;
                            }
                            if (fValueOf == null) {
                                GameEngine.log("placementLocation==null for team:" + playerTeamK3.teamId);
                            } else {
                                float fFloatValue = fValueOf.floatValue();
                                float fFloatValue2 = fValueOf2.floatValue();
                                if (iIntValue == 2) {
                                    for (int i7 = 0; i7 <= 2; i7++) {
                                        if (i7 != 1) {
                                            BaseUnit baseUnitA = UnitTypeEnum.builder.a();
                                            baseUnitA.setUnitTeam(playerTeamK3);
                                            baseUnitA.posX = (fFloatValue - 50.0f) + (i7 * 50);
                                            baseUnitA.posY = fFloatValue2;
                                            PlayerTeam.c(baseUnitA);
                                        }
                                    }
                                    for (int i8 = 0; i8 <= 2; i8++) {
                                        BaseUnit baseUnitA2 = UnitTypeEnum.heavyTank.a();
                                        baseUnitA2.setUnitTeam(playerTeamK3);
                                        baseUnitA2.posX = (fFloatValue - 50.0f) + (i8 * 50);
                                        baseUnitA2.posY = fFloatValue2 + 50.0f;
                                        PlayerTeam.c(baseUnitA2);
                                    }
                                } else if (iIntValue == 3 || iIntValue == 4) {
                                    for (int i9 = 0; i9 <= 2; i9++) {
                                        UnitType unitTypeByName = UnitTypeEnum.getUnitTypeByName("combatEngineer");
                                        if (unitTypeByName == null) {
                                            NetworkEngine.g("Could not find: combatEngineer on network.setup.startingUnits==3");
                                        } else {
                                            BaseUnit baseUnitA3 = unitTypeByName.a();
                                            baseUnitA3.setUnitTeam(playerTeamK3);
                                            baseUnitA3.posX = (fFloatValue - 50.0f) + (i9 * 50);
                                            baseUnitA3.posY = fFloatValue2 + 50.0f;
                                            PlayerTeam.c(baseUnitA3);
                                        }
                                    }
                                } else if (iIntValue == 5) {
                                    UnitType unitTypeByName2 = UnitTypeEnum.getUnitTypeByName("experimentalSpider");
                                    if (unitTypeByName2 == null) {
                                        NetworkEngine.g("Could not find: experimentalSpider on network.setup.startingUnits==5");
                                    } else {
                                        BaseUnit baseUnitA4 = unitTypeByName2.a();
                                        baseUnitA4.setUnitTeam(playerTeamK3);
                                        baseUnitA4.posX = fFloatValue;
                                        baseUnitA4.posY = fFloatValue2;
                                        baseUnitA4.rotationSpeed = 90.0f;
                                        baseUnitA4.posZ = 2.0f;
                                        baseUnitA4.getUnitAICombatState();
                                        PlayerTeam.c(baseUnitA4);
                                    }
                                } else if (iIntValue != 9 && iIntValue > 10) {
                                    CustomUnitConfig customUnitConfigC = CustomUnitConfig.c(iIntValue);
                                    if (customUnitConfigC == null) {
                                        NetworkEngine.g("Could not find starting unit on startingUnits==" + iIntValue);
                                    } else {
                                        BaseUnit baseUnitA5 = customUnitConfigC.a();
                                        baseUnitA5.setUnitTeam(playerTeamK3);
                                        baseUnitA5.posX = fFloatValue;
                                        baseUnitA5.posY = fFloatValue2;
                                        if (!baseUnitA5.bI()) {
                                            baseUnitA5.rotationSpeed = 90.0f;
                                        }
                                        if (customUnitConfigC.startFallingWhenStartingUnit) {
                                            baseUnitA5.getUnitAICombatState();
                                            if (baseUnitA5 instanceof CustomUnit) {
                                                ((CustomUnit) baseUnitA5).dB();
                                            }
                                        }
                                        PlayerTeam.c(baseUnitA5);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!z2 && (this.missionEngine == null || !this.missionEngine.q)) {
                setViewpoint(0.0f, 0.0f);
                int i10 = 0;
                int i11 = 0;
                boolean z7 = false;
                for (BaseUnit baseUnit2 : BaseUnit.bE) {
                    if (baseUnit2 instanceof Tree) {
                        i11++;
                    } else {
                        i10++;
                    }
                    if (baseUnit2.team == this.playerTeam && baseUnit2.isTargetable) {
                        centerViewpoint(baseUnit2.posX, baseUnit2.posY);
                        z7 = true;
                    }
                }
                if (!z7) {
                    for (BaseUnit baseUnit3 : BaseUnit.bE) {
                        if (baseUnit3.team == this.playerTeam && !baseUnit3.t() && !baseUnit3.u()) {
                            centerViewpoint(baseUnit3.posX, baseUnit3.posY);
                        }
                    }
                }
                log("there are " + i10 + " units on this map and " + i11 + " trees");
            }
            this.gameStateData = GameStateManager.c(this.appContext).b(getCurrentMapPath());
            this.pathfindingEngine.a(this.tileMap, z2);
            this.minimap.reset(this.tileMap, z2);
            this.commandController.clearAllCommands();
            this.groupController.a();
            if (!z2) {
                BuildPreview.clearAll();
            }
            this.gameSaver.readSaveGame(z2);
            this.gameUI.toggleGameAndUIState(z2);
            if (!z2) {
                this.gameUI.clearSelection();
                selectAnyOnScreenBuilder();
                if (this.isGameStarted) {
                    this.gameUI.clearSelection();
                }
            } else {
                this.gameUI.clearSelection();
            }
            this.unitSpatialIndex.a(this.tileMap);
            if (!z2) {
                this.musicManager.onNewGame();
            }
            this.gameStatistics.a();
            for (BaseUnit baseUnit4 : BaseUnit.bE) {
                if (baseUnit4 instanceof OrderableUnit) {
                    ((OrderableUnit) baseUnit4).c(false);
                }
            }
            this.gameStateData.e = true;
            this.gameStateManager.a(this.appContext);
            this.loadNewGame = true;
            this.reloadMap = false;
            this.fullReload = false;
            if (gameMode != GameMode.menu && !this.settingsEngine.hasPlayedGameOrSeenHelp) {
                this.settingsEngine.hasPlayedGameOrSeenHelp = true;
                this.settingsEngine.save();
            }
            for (int i12 = 0; i12 < 5; i12++) {
                System.gc();
            }
            if (!GameEngine.isPausedStatic2) {
                Log.a("RustedWarfare", "getNativeHeapSize" + String.valueOf(Debug.getNativeHeapSize()));
                Log.a("RustedWarfare", "getNativeHeapAllocatedSize" + String.valueOf(Debug.getNativeHeapAllocatedSize()));
                Log.a("RustedWarfare", "getNativeHeapFreeSize" + String.valueOf(Debug.getNativeHeapFreeSize()));
                Log.a("RustedWarfare", "Runtime.getRuntime().maxMemory()" + String.valueOf(Runtime.getRuntime().maxMemory()));
            }
            if (this.gameThread != null) {
                this.gameThread.a();
            }
            this.accumulator = 0.0f;
            if (this.networkEngine.F && this.networkEngine.B) {
                GameEngine.log("Disabling network for singleplayer");
                this.networkEngine.B = false;
            }
            if (!isDebug()) {
                if (gameMode == GameMode.normalSave) {
                    GameEngine.log("Not starting replay recording as we are loading a save");
                } else {
                    this.replayEngine.a(z2);
                }
            }
            if (PathEngine.m) {
            }
        } catch (MapLoadException e2) {
            e2.printStackTrace();
            alert("Error loading map: " + e2.getMessage(), 1);
            if (isNetworkServerStatic2) {
                GameEngine.log("Crashing on allowed map error because automated testing is active");
                throw new RuntimeException(e2);
            }
            if (!this.networkEngine.B && this.activity != null && (surfaceHolder = this.activity.getSurfaceHolder()) != null) {
                surfaceHolder.m();
            }
            writeCrashToFile("Map Load Warning", getStackTrace(e2));
            this.fullReload = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: aG */
    private void selectAnyOnScreenBuilder() {
        this.gameUI.clearSelection();
        for (BaseUnit baseUnit : BaseUnit.bE) {
            if (baseUnit.team == this.playerTeam && (baseUnit instanceof OrderableUnit) && baseUnit.canMove() && baseUnit.isBuilding() && baseUnit.isAlive() && !baseUnit.u() && !baseUnit.t()) {
                GameEngine.log("selectAnyOnScreenBuilder: found builder");
                this.gameUI.selectUnit(baseUnit);
                return;
            }
        }
        GameEngine.log("selectAnyOnScreenBuilder: no builder found");
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: g */
    public void clearAllObjects() {
        TransactionalArrayList<GameObject> transactionalArrayListDK = GameObject.dK();
        Iterator it = transactionalArrayListDK.iterator();
        while (it.hasNext()) {
            ((GameObject) it.next()).remove();
        }
        BaseUnit.getGlobalUnitList();
        GameObject.dK();
        int size = transactionalArrayListDK.size();
        if (size != 0) {
            GameEngine.printLog("SHOULD_NOT_HAPPEN: we still had " + size + " objects in gameObjectListForLogic after removeAll");
            for (GameObject gameObject : transactionalArrayListDK) {
                String unitShortName = "Object: " + gameObject.objectId;
                if (gameObject instanceof BaseUnit) {
                    unitShortName = ((BaseUnit) gameObject).getUnitShortName();
                }
                GameEngine.printLog("Remaining object: " + unitShortName);
            }
            if (GameEngine.getInstance().isMissionActive()) {
                throw new RuntimeException("We still had " + size + " objects in gameObjectListForLogic after removeAll");
            }
        }
        BaseUnit.getGlobalUnitList().clear();
        GameObject.dK().clear();
        CustomUnit.dD();
        this.renderList.clear();
    }

    /* JADX INFO: renamed from: b */
    public void resetGame(boolean z) {
        synchronized (this.gameLoopLock) {
            if (this.activity != null) {
                this.activity.onSizeChanged();
            }
            this.isLoading = false;
            if (!z) {
                this.replayEngine.g();
            }
            this.pathfindingEngine.c();
            clearAllObjects();
            if (!isPC()) {
                this.musicManager.pause();
            }
            this.effectManager.setBitmapQuality(z);
            if (this.tileMap != null) {
                this.tileMap.clearAllMapData();
                this.tileMap = null;
            }
            if (this.missionEngine != null) {
                this.missionEngine = null;
            }
            if (this.unitSpatialIndex != null) {
                this.unitSpatialIndex.b();
            }
            this.cameraFocusUnit = null;
            this.nextCameraFocusUnit = null;
            this.cleanupCounter = 0;
            PlayerTeam.staticUpdateTeamColors();
            setupTeamStats(StatType.none, StatGroup.player);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: a */
    public void gameLoop(float f, int i) throws ConfigParseException, IOException {
        synchronized (this.gameLoopLock) {
            processEvent(f, i);
        }
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:314:0x0948 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX INFO: renamed from: b */
    public void processEvent(float float1, int integer) throws IOException, ConfigParseException {
        if (this.currentTick == 2) {
            this.checkLowMemory();
        } else if (this.currentTick % 10000 == 0 && this.currentTick != 0) {
            this.checkLowMemory();
        }

        if (isGameThreadRunningStatic && !this.isInGameOrLobby && isDesktop() && Debug.getNativeHeapAllocatedSize() > 209715200L) {
            GameEngine.log("getNativeHeapAllocatedSize: " + Utility.formatMilliseconds((int) Debug.getNativeHeapAllocatedSize()));
            this.isInGameOrLobby = true;
        }

        this.checkMemory();
        this.taskQueue1.a();
        this.gameModeTimer2.b();
        this.performanceProfiler.a(ProfilerSection.total);
        this.networkEngine.b(float1);
        this.activity = this.activity2;
        if (this.activity.isPaused()) {
            this.performanceProfiler.a(ProfilerSection.update);

            while (this.gameThreadRunnableQueue.peek() != null) {
                Runnable var3 = (Runnable)this.gameThreadRunnableQueue.poll();
                var3.run();
            }

            if (!this.loadNewGame) {
                if (!this.isStopped) {
                    Log.d("RustedWarfare", "game running without a loaded level!!!");
                    this.stopAndClose();

                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException var21) {
                        var21.printStackTrace();
                    }
                }
            } else {
                this.isLoading = true;
                if (!this.hasCheckedSafeMode && this.currentTick > 5) {
                    this.hasCheckedSafeMode = true;
                    boolean var25 = false;
                    if (this.settingsEngine.numIncompleteLoadAttempts > 1) {
                        var25 = true;
                    }

                    this.settingsEngine.numIncompleteLoadAttempts = 0;
                    if (this.isSafeMode) {
                        this.settingsEngine.numLoadsSinceRunningGameOrNormalExit = 0;
                    }

                    this.settingsEngine.save();
                    if (this.isSafeMode && (this.forceEnglish || this.modManager.getStorageModsCount() > 0)) {
                        if (this.safeModeReason != null) {
                            this.showMessageBox("Safe mode", "Started game in safe mode due to " + this.safeModeReason + ". Mods have been disabled.");
                        } else if (var25) {
                            this.showMessageBox("Safe mode", "Started game in safe mode due to failed loading attempts. Mods have been disabled.");
                        } else {
                            this.showMessageBox("Safe mode", "Started game in safe mode due to multiple loads without starting a game or exiting. Mods have been disabled.");
                        }
                    }
                }

                if (!this.reloadMap && this.loadNewGame && this.settingsEngine.numLoadsSinceRunningGameOrNormalExit != 0) {
                    this.settingsEngine.numLoadsSinceRunningGameOrNormalExit = 0;
                    this.settingsEngine.save();
                }

                this.gameSaver.deleteSave();
                float var26 = this.cameraEdgeScrollZone * this.cameraIsDragging;
                if (var26 != this.zoom) {
                    float var4 = this.mouseX / this.zoom + this.viewpointX;
                    float var5 = this.mouseY / this.zoom + this.viewpointY;
                    this.zoom = var26;
                    this.updateCameraSystem();
                    if (this.recomputeViewpoint) {
                        float var6 = this.mouseX / this.zoom + this.viewpointX;
                        float var7 = this.mouseY / this.zoom + this.viewpointY;
                        this.setViewpoint(this.viewpointX - (var6 - var4), this.viewpointY - (var7 - var5));
                        this.recomputeViewpoint = false;
                    }
                }

                if (this.scrollDeltaX != 0.0F || this.scrollDeltaY != 0.0F) {
                    float var27 = 3.0F * float1;
                    float var34 = 0.0F;
                    if (this.scrollDeltaX > 0.0F) {
                        var34 = Utility.min(this.scrollDeltaX, var27);
                    }

                    if (this.scrollDeltaX < 0.0F) {
                        var34 = Utility.max(this.scrollDeltaX, -var27);
                    }

                    var34 += 0.15F * this.scrollDeltaX;
                    float var37 = 0.0F;
                    if (this.scrollDeltaY > 0.0F) {
                        var37 = Utility.min(this.scrollDeltaY, var27);
                    }

                    if (this.scrollDeltaY < 0.0F) {
                        var37 = Utility.max(this.scrollDeltaY, -var27);
                    }

                    var37 += 0.15F * this.scrollDeltaY;
                    if (Utility.abs(this.scrollDeltaX) <= var27) {
                        var34 = this.scrollDeltaX;
                        this.scrollDeltaX = 0.0F;
                    } else {
                        this.scrollDeltaX -= var34;
                    }

                    if (Utility.abs(this.scrollDeltaY) <= var27) {
                        var37 = this.scrollDeltaY;
                        this.scrollDeltaY = 0.0F;
                    } else {
                        this.scrollDeltaY -= var37;
                    }

                    this.viewpointX += var34;
                    this.viewpointY += var37;
                    this.setViewpoint(this.viewpointX, this.viewpointY);
                    this.clampCameraPosition();
                }

                if (this.wasPaused != this.isPaused) {
                    this.updateCameraSystem();
                }

                if (float1 > 3.0F) {
                    float1 = 3.0F;
                }

                if (float1 < 0.0F) {
                    float1 = 0.0F;
                }

                if (this.gameSpeedMultiplier >= 0.0F) {
                    float1 = this.gameSpeedMultiplier;
                }

                this.lastTickTime = (int)(this.lastTickTime + float1 * 16.666666F);
                this.updateCameraFocus(float1);
                this.fpsAccumulator += integer;
                this.fpsFrameCounter++;
                if (this.fpsFrameCounter >= 40) {
                    if (this.fpsAccumulator == 0) {
                        this.fpsAccumulator = 1;
                    }

                    this.fps = (int)(this.fpsFrameCounter * 1000 / this.fpsAccumulator + 0.5F);
                    this.averageFrameTime = (float)this.fpsAccumulator / this.fpsFrameCounter;
                    this.fpsAccumulator = 0;
                    this.fpsFrameCounter = 0;
                    if (this.settingsEngine.showFps) {
                        this.fpsString = this.fps + "fps";
                    }
                }

                this.processKeyEvents();

                for (int var28 = 0; var28 < this.gameModeEnabled.length; var28++) {
                    this.gameModeEnabled[var28] = true;
                }

                this.mouseRightPressed = Utility.moveTowardsZero(this.mouseRightPressed, 0.1F * float1);
                this.mouseMiddlePressed = Utility.moveTowardsZero(this.mouseMiddlePressed, 0.1F * float1);
                this.mouseRightPressed = Utility.clamp(this.mouseRightPressed, 5.0F);
                this.mouseMiddlePressed = Utility.clamp(this.mouseMiddlePressed, 5.0F);
                this.gameUI.updateInput(float1);
                this.clampCameraPosition();
                TileMap.updateLayerBuffers();
                if (this.networkEngine.B) {
                    float var29 = float1;
                    if (this.replayEngine.v != 1) {
                        var29 = float1 * this.replayEngine.v;
                    }

                    this.networkEngine.a(var29);
                    if (!this.shouldSkipUpdate(true) && !this.networkEngine.Y) {
                        this.accumulator += var29;

                        while (this.accumulator > this.networkEngine.getDifficultyString()) {
                            if (this.networkEngine.I()) {
                                this.networkEngine.Y = true;
                                break;
                            }

                            this.accumulator = this.accumulator - this.networkEngine.getDifficultyString();
                            this.networkEngine.a(this.networkEngine.getDifficultyString(), false);
                            if (this.networkEngine.Y) {
                                break;
                            }

                            this.update(this.networkEngine.getDifficultyString());
                        }

                        if (!this.networkEngine.isServer) {
                            if (this.networkEngine.af || this.networkEngine.ad) {
                                if (this.networkEngine.af && this.networkEngine.ad && this.currentTick < this.networkEngine.X - this.networkEngine.Q - 5) {
                                    this.networkEngine.d("nearly within frame range");
                                    this.networkEngine.af = false;
                                }

                                if (this.currentTick > this.networkEngine.X - 6) {
                                    this.networkEngine.d("we have back within frame range");
                                    this.networkEngine.af = false;
                                    this.networkEngine.ad = false;
                                }
                            }

                            if (!this.networkEngine.ad && this.currentTick < this.networkEngine.X - this.networkEngine.Q - 10) {
                                this.networkEngine.d("we are slightly out of frame range, speeding up");
                                this.networkEngine.ad = true;
                            }

                            if (!this.networkEngine.af && this.currentTick < this.networkEngine.X - this.networkEngine.Q - 30) {
                                this.networkEngine.d("we are out of frame range, fast forwarding (" + this.currentTick + "->" + this.networkEngine.X + ")");
                                this.networkEngine.af = true;
                            }

                            if (!this.networkEngine.af && this.networkEngine.ad) {
                                this.networkEngine.ae += float1;
                                if (this.networkEngine.ae > this.networkEngine.getDifficultyString() * 3.0F) {
                                    this.networkEngine.ae = 0.0F;
                                    this.networkEngine.a(this.networkEngine.getDifficultyString(), true);
                                    if (!this.networkEngine.Y) {
                                        this.update(this.networkEngine.getDifficultyString());
                                    }
                                }
                            }

                            if (this.networkEngine.af) {
                                this.networkEngine.a(this.networkEngine.getDifficultyString(), true);
                                if (!this.networkEngine.Y) {
                                    this.update(this.networkEngine.getDifficultyString());
                                }
                            }

                            if (this.currentTick < this.networkEngine.X - 90) {
                                this.networkEngine.a(this.networkEngine.getDifficultyString(), true);
                                if (!this.networkEngine.Y) {
                                    this.update(this.networkEngine.getDifficultyString());
                                }
                            }

                            if (this.currentTick < this.networkEngine.X - 120) {
                                this.networkEngine.a(this.networkEngine.getDifficultyString(), true);
                                if (!this.networkEngine.Y) {
                                    this.update(this.networkEngine.getDifficultyString());
                                }
                            }

                            if (this.currentTick < this.networkEngine.X - 600) {
                                this.networkEngine.a(this.networkEngine.getDifficultyString(), true);
                                if (!this.networkEngine.Y) {
                                    this.update(this.networkEngine.getDifficultyString());
                                }
                            }
                        }
                    }
                } else if (this.replayEngine.i()) {
                    float var30 = float1;
                    if (this.replayEngine.v != 1) {
                        var30 = float1 * this.replayEngine.v;
                    }

                    if (this.gameSpeed != 1.0F) {
                        var30 *= this.gameSpeed;
                    }

                    if (!this.shouldSkipUpdate(false)) {
                        this.accumulator += var30;

                        while (this.accumulator > this.networkEngine.getDifficultyString()) {
                            this.accumulator = this.accumulator - this.networkEngine.getDifficultyString();
                            if (this.networkEngine.I()) {
                                break;
                            }

                            this.update(this.networkEngine.getDifficultyString());
                        }
                    }

                    if (this.accumulator > 100.0F) {
                        this.accumulator = 100.0F;
                    }

                    if (this.accumulator < 0.0F) {
                        this.accumulator = 0.0F;
                    }
                } else if (!this.shouldSkipUpdate(false)) {
                    this.update(float1);
                }

                if (this.shouldSkipUpdate(false)) {
                    try {
                        Thread.sleep(2L);
                    } catch (Exception var22) {
                    }
                }

                this.pathfindingEngine.a(float1);
                this.soundEngine.clearPlayingSounds(float1);
                this.musicManager.update(float1);
                this.inputController.b();
                DisabledSteamEngine.a().a(float1);
                this.performanceProfiler.b(ProfilerSection.update);
                this.performanceProfiler.a(ProfilerSection.draw);
                if (!this.pinchDistance) {
                    if (this.graphicsEngine2.a()) {
                        this.drawThreadSafe(null, float1);
                    } else if (this.activity.hasBeenStarted()) {
                        GraphicsInterface var31 = this.activity.lockCanvas(true);
                        this.drawThreadSafe(var31, float1);
                    } else {
                        GameView var32 = this.activity;
                        this.activity.onDraw(float1, integer);
                        if (var32.isSurfaceViewReady() && !var32.isContinuousRendering()) {
                            synchronized (var32.getHolder()) {
                                if (var32.isSurfaceViewReady() && !var32.isContinuousRendering()) {
                                    this.performanceProfiler.a(ProfilerSection.update_waiting_on_draw);
                                    GraphicsInterface var39 = var32.lockCanvas(true);
                                    this.performanceProfiler.b(ProfilerSection.update_waiting_on_draw);

                                    try {
                                        if (!var32.isContinuousRendering()) {
                                            if (var39 != null) {
                                                if (var39.c()) {
                                                    GameEngine.log("gameengine draw: bufferedCanvas drawn on");
                                                }

                                                var39.a(true);
                                            }

                                            if (var39 == null) {
                                                GameEngine.logWithTime("GameEngine gameViewCanvas is null after lockCanvas - " + var32.hashCode());
                                            }

                                            this.drawThreadSafe(var39, float1);
                                            this.graphicsEngine2.a((GraphicsInterface) null);
                                        }
                                    } finally {
                                        if (var39 != null) {
                                            try {
                                                var32.unlockCanvasAndPost(var39, true);
                                            } catch (IllegalArgumentException var19) {
                                                var19.printStackTrace();
                                                GameEngine.logWithTime("GameEngine catch currentGameView - " + var32.hashCode());
                                                GameEngine.logWithTime("GameEngine catch currentGameView.gameThreadSync - " + var32.getHolder().hashCode());
                                                var32.onPause();
                                            } catch (IllegalStateException var20) {
                                                var20.printStackTrace();
                                                GameEngine.logWithTime("GameEngine catch currentGameView - " + var32.hashCode());
                                                GameEngine.logWithTime("GameEngine catch currentGameView.gameThreadSync - " + var32.getHolder().hashCode());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        this.activity.onUpdate(float1, integer);
                    }
                }

                this.pinchDistance = false;
                this.clearLevelConfig();
                this.performanceProfiler.b(ProfilerSection.draw);
                if (this.isPinching) {
                    this.isPinching = false;
                    Integer var33 = getMapLevel(this.currentMapPath);
                    String var36 = null;
                    if (var33 != null) {
                        var36 = findNextLevel(this.currentMapPath);
                    }

                    if (this.networkEngine.B) {
                        var36 = null;
                        // java.lang.Thread, java.lang.Runnable
                        new Thread(() -> GameLogic.this.networkEngine.disconnectNetworking("gotoNextLevel")).start();
                    }

                    if (var36 != null) {
                        GameEngine.log("gotoNextLevel: Loading next level: " + var36);
                        this.currentMapPath = var36;
                        this.gameUI.messageManager.clear();
                        this.loadLevel(true, false, GameMode.normal);
                    } else {
                        GameEngine.log("gotoNextLevel: No next level, finishing");
                        this.loadNewGame = false;
                        InGameActivity var40 = this.activity.getSurfaceHolder();
                        if (var40 != null) {
                            var40.b();
                            var40.m();
                        } else {
                            GameEngine.log("gotoNextLevel: Error getInGameActivity==null");
                        }
                    }
                }

                if (!this.isStopped && this.isBenchmarking && !this.someBoolean) {
                    log("starting method trace");
                    Debug.startMethodTracing("lukeTrace", 110000000);
                    this.someBoolean = true;
                }

                this.exitGameThread = true;
                this.gameModeTimer3.a();
                this.performanceProfiler.b(ProfilerSection.total);
                this.performanceProfiler.b();
            }
        }
    }

    /* JADX INFO: renamed from: com.corrodinggames.rts.game.i$a */
    /* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/i$a.class */
    class a extends Thread {
        a() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            GameLogic.this.networkEngine.disconnectNetworking("gotoNextLevel");
        }
    }

    /* JADX INFO: renamed from: h */
    public void stopAndClose() {
        InGameActivity surfaceHolder = this.activity.getSurfaceHolder();
        if (surfaceHolder != null) {
            if (!surfaceHolder.c()) {
                surfaceHolder.b();
                return;
            } else {
                GameEngine.updatePaintTextSizeIfNeeded("stopAndClose: inGameActivity is isFinishing");
                return;
            }
        }
        GameEngine.updatePaintTextSizeIfNeeded("stopAndClose: Error getInGameActivity==null");
    }

    /* JADX INFO: renamed from: a */
    public void update(float deltaSpeed) throws IOException {
        if (isInNetworkOrReplay() && deltaSpeed < 0.1f) {
            NetworkEngine.g("updateAllGame1: deltaSpeed:" + deltaSpeed + " frame:" + this.currentTick + " network.currentStepRate:" + this.networkEngine.getDifficultyString());
        }
        if (this.gameSpeed != 1.0f && !this.networkEngine.B && !this.replayEngine.i()) {
            deltaSpeed *= this.gameSpeed;
        }
        float f = deltaSpeed * this.speedMultiplier;
        this.floatI = f + 2.0f;
        this.lastDelta = f;
        this.networkEngine.update(f);
        this.lastTick = (int) (this.lastTick + (f * 16.666666f));
        this.commandController.executeAllCommands();
        this.replayEngine.update(f);
        this.currentTick++;
        PlayerTeam.g(f);
        if (this.tileMap != null) {
            this.tileMap.updateFogLogicFrame(f);
        }
        if (isInNetworkOrReplay() && f < 0.1f) {
            NetworkEngine.g("updateAllGame2: deltaSpeed:" + f + " frame:" + this.currentTick);
        }
        BaseUnit.getGlobalUnitList();
        TransactionalArrayList transactionalArrayListDK = GameObject.dK();
        Object[] objArrB = transactionalArrayListDK.b();
        int size = transactionalArrayListDK.size();
        boolean zAy = isInNetworkOrReplay();
        for (int i = 0; i < size; i++) {
            GameObject gameObject = (GameObject) objArrB[i];
            if (zAy && f != this.lastDelta) {
                NetworkEngine.h("JIT bug detected, attempting to correct. before object:" + gameObject.objectId + " frame:" + this.currentTick + " deltaSpeed:" + f);
                f = this.lastDelta;
            }
            gameObject.update(f);
        }
        if (isInNetworkOrReplay() && f < 0.1f) {
            NetworkEngine.g("updateAllGame3: deltaSpeed:" + f + " frame:" + this.currentTick);
        }
        int size2 = transactionalArrayListDK.a.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ListOperationEntry listOperationEntry = (ListOperationEntry) transactionalArrayListDK.a.get(i2);
            if (listOperationEntry.a == ListOperation.add) {
                GameObject gameObject2 = (GameObject) listOperationEntry.b;
                if (!gameObject2.isDestroyed) {
                    gameObject2.update(f);
                }
            }
        }
        this.performanceProfiler.a(ProfilerSection.update_geo_indexes);
        this.unitSpatialIndex.a();
        this.performanceProfiler.b(ProfilerSection.update_geo_indexes);
        OrderableUnit.updateAllUnitCollisions(f);
        CustomUnit.s(f);
        CustomUnit.a(f, 0);
        this.cleanupCounter++;
        if (this.cleanupCounter >= 1000) {
            this.cleanupCounter = 0;
            int i3 = 0;
            for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
                if (baseUnit.isDestroyed && !(baseUnit instanceof Tree)) {
                    i3++;
                }
            }
            if (i3 > 70) {
                for (BaseUnit baseUnit2 : BaseUnit.getGlobalUnitList()) {
                    if ((baseUnit2 instanceof BaseUnit) && baseUnit2.isDestroyed && !(baseUnit2 instanceof Tree) && baseUnit2.unitCreationTime < this.lastTick - 30000 && i3 > 70) {
                        baseUnit2.remove();
                        i3--;
                    }
                }
            }
        }
        this.performanceProfiler.a(ProfilerSection.update_all_team_and_ai);
        PlayerTeam.update2(f);
        this.performanceProfiler.b(ProfilerSection.update_all_team_and_ai);
        BuildPreview.updateAll(f);
        this.effectManager.update(f);
        this.cloudRenderer.update(f);
        GameViewUtils.a(f);
        if (this.missionEngine != null) {
            this.missionEngine.c(f);
        }
        this.performanceProfiler.a(ProfilerSection.update_groupcontroller);
        this.groupController.a(f);
        this.performanceProfiler.b(ProfilerSection.update_groupcontroller);
        this.performanceProfiler.a(ProfilerSection.update_minimap);
        this.minimap.update(f);
        this.performanceProfiler.b(ProfilerSection.update_minimap);
        this.pathfindingEngine.b(f);
        if (this.teamStats != null) {
            this.teamStats.update();
        }
        this.gameStatistics.b();
    }

    /* JADX INFO: renamed from: a */
    public void drawThreadSafe(GraphicsInterface graphicsInterface, float f) {
        synchronized (this.gameLoopLock2) {
            draw(graphicsInterface, f);
        }
    }

    /* JADX INFO: renamed from: i */
    public boolean setupPostProcessing() {
        if (this.postBaseShader == null) {
            this.postBaseShader = new FrameBufferHelper("assets/shaders/post_base.frag");
        }
        if (this.postDisplacementShader == null) {
            this.postDisplacementShader = new FrameBufferHelper("assets/shaders/post_displacement.frag");
        }
        this.postBaseShader.a(this.graphicsEngine2);
        this.postDisplacementShader.a(this.graphicsEngine2);
        if (this.postBaseShader.g || this.postDisplacementShader.g) {
            if (!this.postProcessingFailed) {
                this.postProcessingFailed = true;
                GameEngine.log("setupPostprocessing: failed");
                return false;
            }
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: a */
    public void beginPostProcessing(FrameBufferHelper frameBufferHelper) {
        if (this.graphicsEngine != null) {
            throw new RuntimeException("Layer already enabled");
        }
        this.graphicsEngine = this.graphicsEngine2;
        this.graphicsEngine2 = frameBufferHelper.b;
        this.graphicsEngine2.i();
        this.graphicsEngine2.a(new Rect(0, 0, this.graphicsEngine2.m(), this.graphicsEngine2.n()));
        this.graphicsEngine2.b(frameBufferHelper.f, frameBufferHelper.e);
    }

    /* JADX INFO: renamed from: b */
    public void endPostProcessing(FrameBufferHelper frameBufferHelper) {
        if (this.graphicsEngine == null) {
            throw new RuntimeException("Layer not enabled");
        }
        this.graphicsEngine2.j();
        this.graphicsEngine2.p();
        this.graphicsEngine2 = this.graphicsEngine;
        this.graphicsEngine = null;
        this.graphicsEngine2.b(frameBufferHelper.f, frameBufferHelper.e);
    }

    /* JADX INFO: renamed from: b */
    public void draw(GraphicsInterface graphicsInterface, float f) {
        if (graphicsInterface == null) {
            log("drawAll", "canvas is null, not may not be available yet");
            return;
        }
        if (isNetworkServerStatic) {
            return;
        }
        this.graphicsEngine2.a(graphicsInterface);
        this.graphicsEngine2.a(this.activity.getAudioRenderer());
        this.graphicsEngine2.g();
        this.tickDelta++;
        TeamColorTexture.G = 0.0f;
        if (this.isPinching) {
            this.graphicsEngine2.b(Color.a(0, 0, 0));
            this.graphicsEngine2.a("Loading..", this.halfScreenWidth, this.halfScreenHeight, this.loadingPaint);
            return;
        }
        float f2 = 1.0f;
        if (f2 != 1.0f) {
            this.graphicsEngine2.i();
            this.graphicsEngine2.a(f2, f2);
        }
        boolean zIsPostProcessingSupported = GameEngine.isPostProcessingSupported();
        if (zIsPostProcessingSupported && isKeyPressed(113) && isKeyPressed(44)) {
            zIsPostProcessingSupported = false;
        }
        if (zIsPostProcessingSupported && !setupPostProcessing()) {
            zIsPostProcessingSupported = false;
        }
        if (zIsPostProcessingSupported) {
            beginPostProcessing(this.postBaseShader);
            try {
                this.graphicsEngine2.b(Color.a(0, 0, 0));
                this.performanceProfiler.a(ProfilerSection.draw_game);
                drawGame((GraphicsInterface) null, f);
                this.performanceProfiler.b(ProfilerSection.draw_game);
                endPostProcessing(this.postBaseShader);
                this.postBaseShader.b();
                if (!this.postDisplacementShader.a()) {
                    beginPostProcessing(this.postDisplacementShader);
                    try {
                        this.graphicsEngine2.b(Color.a(128, 128, 255));
                        applyZoomTransform();
                        int iDrawEffect = this.effectManager.drawEffect(f, 3);
                        this.effectManager.texture = null;
                        endPostProcessing(this.postDisplacementShader);
                        if (iDrawEffect > 0) {
                            float fS = this.graphicsEngine2.s();
                            this.postDisplacementShader.d.a("screenBase", this.postBaseShader.a);
                            this.postDisplacementShader.d.b("screenBaseSize", this.postBaseShader.a);
                            this.postDisplacementShader.d.a("u_resolution", this.screenWidth, this.viewpointWidthRaw);
                            this.postDisplacementShader.d.a("u_offsetBy", 0.2f * this.zoom);
                            this.postDisplacementShader.d.a("u_uiScaling", fS);
                            this.postDisplacementShader.b();
                        }
                    } catch (Throwable th) {
                        endPostProcessing(this.postDisplacementShader);
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                endPostProcessing(this.postBaseShader);
                throw th2;
            }
        } else {
            this.performanceProfiler.a(ProfilerSection.draw_game);
            drawGame(graphicsInterface, f);
            this.performanceProfiler.b(ProfilerSection.draw_game);
        }
        if (!isGamePaused()) {
            this.performanceProfiler.a(ProfilerSection.draw_gui);
            drawUI(graphicsInterface, f);
            this.performanceProfiler.b(ProfilerSection.draw_gui);
        }
        if (this.settingsEngine.showFps && this.pauseTransition == 0.0f && !this.isMenuOpen && !this.isPaused) {
            this.graphicsEngine2.a(this.fpsString, 100.0f, 35.0f, this.fpsPaint);
        }
        if (safeModeReason != null) {
            this.graphicsEngine2.a(safeModeReason, 100.0f, 85.0f, this.fpsPaint);
        }
        if (!this.isStopped && (this.graphicsEngine2.d() != null || GameEngine.isAndroidVersionStatic2)) {
            this.gameUI.handleTouchGestures(f);
        }
        if (!isGamePaused()) {
            this.effectManager.drawEffect(f, 4);
        }
        CustomUnit.dE();
        this.graphicsEngine2.h();
        if (f2 != 1.0f) {
            graphicsInterface.a();
        }
    }

    /* JADX INFO: renamed from: j */
    public boolean shouldDrawUnitIcons() {
        if (!this.settingsEngine.showUnitIcons) {
            return false;
        }
        if (this.zoom >= 0.7d || this.viewpointWidth < this.tileMap.getWorldWidth() - 5.0f || this.viewpointHeight < this.tileMap.getWorldHeight() - 5.0f) {
            return printLog() ? ((double) this.zoom) < 0.1d : isPC() ? ((double) this.zoom) < 0.27d : ((double) this.zoom) < 0.4d;
        }
        return true;
    }

    /* JADX INFO: renamed from: b */
    public void updateCameraSmoothing(float f) {
        boolean z = false;
        if (this.cameraSmoothing.a < 0 || this.cameraSmoothing.b < 0 || this.cameraSmoothing.c > this.tileMap.getWorldWidth() || this.cameraSmoothing.d > this.tileMap.getWorldHeight()) {
            z = true;
        }
        if (z) {
            this.graphicsEngine2.b(Color.a(0, 0, 0));
        }
    }

    /* JADX INFO: renamed from: c */
    public void handleFloat(float f) {
    }

    /* JADX WARN: Removed duplicated region for block: B:96:0x044a  */
    /* JADX INFO: renamed from: c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */

    public void drawGame(com.corrodinggames.rts.gameFramework.graphics.GraphicsInterface l, float float2) {
        if (this.loadNewGame) {
            this.performanceProfiler.a(ProfilerSection.update_game_shouldDraw);
            this.renderListBuffer.b();
            this.lastPinchDistance = 0;
            boolean var3 = false;
            GameObject[] var4 = BaseUnit.fastGameObjectList.a();
            int var5 = GameObject.fastGameObjectList.size();

            for (int var6 = 0; var6 < var5; var6++) {
                GameObject var7 = var4[var6];
                boolean var8 = var7.flag3;
                boolean var9 = var7.a(this);
                var7.flag3 = var9;
                if (var8 != var9) {
                    var3 = true;
                }

                if (var9) {
                    this.renderListBuffer.add(var7);
                }
            }

            if (this.renderList.size() != this.renderListBuffer.size()) {
                var3 = true;
            }

            this.performanceProfiler.b(ProfilerSection.update_game_shouldDraw);
            this.performanceProfiler.a(ProfilerSection.update_game_sortRender);
            if (var3) {
                GameObjectArrayList var12 = this.renderList;
                this.renderList = this.renderListBuffer;
                this.renderListBuffer = var12;
            }

            if (!this.shouldDrawUnitIcons()) {
                Collections.sort(this.renderList, GameObject.ei);
            }

            this.performanceProfiler.b(ProfilerSection.update_game_sortRender);
            this.performanceProfiler.a(ProfilerSection.draw_setup);
            this.performanceProfiler.a(ProfilerSection.draw_setup_clip);
            this.graphicsEngine2.i();
            this.graphicsEngine2.a(this.cameraBoundsEnabled);
            this.performanceProfiler.b(ProfilerSection.draw_setup_clip);
            this.performanceProfiler.a(ProfilerSection.draw_setup_fill);
            this.updateCameraSmoothing(float2);
            this.performanceProfiler.b(ProfilerSection.draw_setup_fill);
            if (this.settingsEngine.renderFancyWater) {
                if (this.waterCloudTexture == null) {
                    this.waterCloudTexture = this.graphicsEngine2.a(R.drawable.water_cloud);
                }

                if (this.waterLayer1Texture == null) {
                    this.waterLayer1Texture = this.graphicsEngine2.a(R.drawable.water_layer1);
                }

                if (this.waterLayer2Texture == null) {
                    this.waterLayer2Texture = this.graphicsEngine2.a(R.drawable.water_layer2);
                }

                this.waterRect.a(this.cameraBoundsEnabled);
                this.waterAnimationTimer += 0.05F * float2;
                if (this.waterAnimationTimer > 100.0F) {
                    this.waterAnimationTimer -= 100.0F;
                }

                this.graphicsEngine2.a(this.waterCloudTexture, this.waterRect, null, this.cameraBoundsMaxY / 6, this.mapWidth / 6, 1, 1);
                this.waterRect.a(this.cameraBoundsBuffer);
                this.waterRectF.a(this.cameraBoundsBuffer);
                this.graphicsEngine2.i();
                this.applyZoomTransform();
                this.graphicsEngine2.a(this.waterLayer2Texture, this.waterRectF, null, this.cameraBoundsMaxY + this.waterAnimationTimer, this.mapWidth + this.waterAnimationTimer, 0, 0);
                this.graphicsEngine2.a(this.waterLayer1Texture, this.waterRectF, null, (float)this.cameraBoundsMaxY, (float)this.mapWidth, 0, 0);
                this.graphicsEngine2.j();
            }

            this.performanceProfiler.a(ProfilerSection.draw_setup_drawMap);
            if (this.tileMap != null && this.isNetworkServer()) {
                this.tileMap.updateFogRenderPass(float2);
            }

            this.performanceProfiler.b(ProfilerSection.draw_setup_drawMap);
            this.applyZoomTransform();
            this.graphicsEngine2.a(this.cameraBoundsBuffer);
            boolean var13 = this.shouldDrawUnitIcons();
            this.pathfindingEngine.c(float2);
            this.performanceProfiler.b(ProfilerSection.draw_setup);
            GameObject[] var14 = this.renderList.a();
            int var15 = this.renderList.size();
            this.mouseScreenX = true;
            this.mouseScreenY = true;
            this.mouseWorldX = true;
            this.mouseWorldY = true;
            this.mousePressed = true;
            if (this.zoom < 0.45) {
                this.mouseWorldX = false;
                this.mouseScreenX = false;
                this.mousePressed = false;
            }

            if (this.zoom < 0.3) {
                this.mouseWorldY = false;
                this.mouseScreenY = false;
            }

            if (!var13) {
                for (int var16 = 0; var16 < var15; var16++) {
                    GameObject var10 = var14[var16];
                    if (var10.syncType == 0) {
                        var10.c(float2);
                    }
                }
            }

            BuildPreview.drawAll(float2);
            this.performanceProfiler.a(ProfilerSection.draw_game_effects);
            this.effectManager.getEffectCount(float2);
            this.effectManager.drawEffect(float2, 1);
            this.performanceProfiler.b(ProfilerSection.draw_game_effects);
            this.performanceProfiler.a(ProfilerSection.draw_game_unit);
            if (var13) {
                if (this.gameUI.getSelectedUnitCount() == 0) {
                    BaseUnit.bI.a(255, 195, 195, 195);
                    BaseUnit.bJ.a(255, 255, 255, 255);
                } else {
                    BaseUnit.bI.a(175, 175, 175, 175);
                    BaseUnit.bJ.a(255, 255, 255, 255);
                }

                for (int var17 = 0; var17 < var15; var17++) {
                    GameObject var24 = var14[var17];
                    if (!var24.f(float2)) {
                        var24.c(float2);
                    }
                }

                for (int var18 = 0; var18 < var15; var18++) {
                    GameObject var25 = var14[var18];
                    var25.a(float2, true);
                    var25.p(float2);
                }
            } else {
                for (int var19 = 0; var19 < var15; var19++) {
                    GameObject var26 = var14[var19];
                    var26.d(float2);
                }

                for (int var20 = 0; var20 < var5; var20++) {
                    GameObject var27 = var4[var20];
                    if (!var27.flag3) {
                        if (!(var27 instanceof BaseUnit)) {
                            continue;
                        }

                        BaseUnit var11 = (BaseUnit)var27;
                        if (!var11.isSelected || var11.team != this.playerTeam && !var11.getWeight()) {
                            continue;
                        }
                    }

                    var27.e(float2);
                    if (!var27.flag3) {
                        var27.p(float2);
                    }
                }

                for (int var21 = 0; var21 < var15; var21++) {
                    GameObject var28 = var14[var21];
                    if (var28.syncType != 0 && var28.syncType != 10) {
                        var28.c(float2);
                    }
                }

                for (int var22 = 0; var22 < var15; var22++) {
                    GameObject var29 = var14[var22];
                    var29.a(float2, false);
                    var29.p(float2);
                }

                PlayerTeam.h(float2);
            }

            this.mouseWorldX = true;
            this.mouseWorldY = true;
            this.performanceProfiler.b(ProfilerSection.draw_game_unit);
            this.performanceProfiler.a(ProfilerSection.draw_game_effects);
            this.effectManager.drawEffect(float2, 2);
            this.performanceProfiler.b(ProfilerSection.draw_game_effects);

            for (int var23 = 0; var23 < var15; var23++) {
                GameObject var30 = var14[var23];
                if (var30.syncType == 10) {
                    var30.c(float2);
                }
            }

            this.cloudRenderer.draw(float2);
            if (this.missionEngine != null) {
                this.missionEngine.a(float2);
            }

            this.handleFloat(float2);
            GameViewUtils.b(float2);
            this.unitSpatialIndex.c(float2);
            this.performanceProfiler.a(ProfilerSection.draw_end);
            this.graphicsEngine2.j();
            this.performanceProfiler.b(ProfilerSection.draw_end);
        }
    }


    /* JADX INFO: renamed from: d */
    public void drawUI(GraphicsInterface graphicsInterface, float f) {
        this.gameUI.processTouchInput(f);
        if (this.missionEngine != null) {
            this.missionEngine.b(f);
        }
        this.minimap.draw(f);
        if (this.settingsEngine.showFps && this.pauseTransition == 0.0f) {
            this.performanceProfiler.c();
        }
        if (this.isGameEngineReady) {
            this.graphicsEngine2.a("Look Mode", this.halfScreenWidth, this.halfScreenHeight, this.loadingPaint);
        }
        if (this.isNetworkConnected) {
            int i = 20;
            for (int i2 = 0; i2 < PlayerTeam.TEAM_NEUTRAL; i2++) {
                PlayerTeam playerTeamK = PlayerTeam.k(i2);
                if (playerTeamK != null && (playerTeamK instanceof AIController)) {
                    AIController aIController = (AIController) playerTeamK;
                    this.graphicsEngine2.a(aIController.teamId + "| c:" + aIController.credits, 20.0f, i, this.teamInfoPaint);
                    i += 20;
                }
            }
        }
    }

    /* JADX INFO: renamed from: k */
    public void updateCameraSystem() {
        this.screenScale = getScreenScale();
        updateDensity();
        this.halfScreenWidth = this.screenWidth / 2.0f;
        this.halfScreenHeight = this.viewpointWidthRaw / 2.0f;
        this.sidebarWidth = (int) (this.viewpointWidthRaw / 3.0f);
        if (isPC()) {
            this.sidebarWidth = (int) (this.viewpointWidthRaw / 2.5f);
        }
        float f = (int) (this.screenWidth / 3.0f);
        if (this.sidebarWidth > f) {
            this.sidebarWidth = f;
        }
        this.sidebarWidth = Utility.clampTo255(this.sidebarWidth, 60.0f, (int) (250.0f * this.screenScale));
        float f2 = this.viewpointX + this.halfViewpointWidth;
        float f3 = this.viewpointY + this.halfViewpointHeight;
        if (this.isPaused) {
            this.currentScreenWidthPixels = this.screenWidth;
            this.currentViewpointWidthPixels = this.screenWidth;
        } else {
            this.currentViewpointWidthPixels = (this.screenWidth - this.sidebarWidth) + 1.0f;
            if (GameUI.bO) {
                this.currentScreenWidthPixels = this.screenWidth;
            } else {
                this.currentScreenWidthPixels = this.currentViewpointWidthPixels;
            }
        }
        if (this.currentScreenWidthPixels < 1.0f) {
            this.currentScreenWidthPixels = 1.0f;
        }
        if (this.currentViewpointWidthPixels < 1.0f) {
            this.currentViewpointWidthPixels = 1.0f;
        }
        if (this.wasPaused != this.isPaused) {
            if (!this.isPaused) {
                f2 -= (this.sidebarWidth / 2.0f) / this.zoom;
            } else {
                f2 += (this.sidebarWidth / 2.0f) / this.zoom;
            }
        }
        this.wasPaused = this.isPaused;
        this.currentScreenHeightPixels = this.viewpointWidthRaw;
        this.screenHeight = this.currentScreenWidthPixels / this.zoom;
        this.viewpointHeight = this.currentScreenHeightPixels / this.zoom;
        this.viewpointWidth = this.currentViewpointWidthPixels / this.zoom;
        this.halfViewpointWidth = this.screenHeight / 2.0f;
        this.halfViewpointHeight = this.viewpointHeight / 2.0f;
        this.cameraBoundsEnabled.a(0, 0, (int) this.currentScreenWidthPixels, (int) this.currentScreenHeightPixels);
        this.cameraBoundsBuffer.a(0, 0, ((int) this.screenHeight) + 1, ((int) this.viewpointHeight) + 1);
        this.cameraFollowMode.a(0.0f, 0.0f, this.screenHeight + 1.0f, this.viewpointHeight + 1.0f);
        setViewpoint(f2 - this.halfViewpointWidth, f3 - this.halfViewpointHeight);
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: b */
    public void updateWindowResolution(int i, int i2) {
        updateViewpoint(i, i2, 1.0f);
    }

    /* JADX INFO: renamed from: a */
    public void updateViewpoint(int i, int i2, float f) {
        this.screenWidth = i;
        this.viewpointWidthRaw = i2;
        updateCameraSystem();
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: l */
    public String getPackageName() {
        if (GameEngine.isIOSVersionStatic2) {
            return "com.corrodinggames.rts.java";
        }
        if (GameEngine.isPCVersionStatic2) {
            return "com.corrodinggames.rts.gdx";
        }
        if (isPausedStatic2) {
            return "com.corrodinggames.rts.server";
        }
        if (this.appContext == null) {
            return "<null context>";
        }
        return this.appContext.h();
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: m */
    public String getInstallerPackageName() {
        if (GameEngine.isIOSVersionStatic2) {
            return "java";
        }
        if (GameEngine.isPCVersionStatic2) {
            return "java-gdx";
        }
        if (isPausedStatic2) {
            return "dedicatedServer";
        }
        if (this.appContext == null) {
            return "<null context>";
        }
        try {
            return this.appContext.f().getInstallerPackageName(getPackageName());
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException: " + e.getMessage();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: n */
    public boolean isBetaOrPreview() {
        if (getVersion2().contains("p")) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: c */
    public int getVersionCode(boolean z) {
        if (isPausedStatic2 || z) {
            return 176;
        }
        try {
            return this.appContext.f().getPackageInfo(this.appContext.h(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: o */
    public String getSignature() {
        if (!isDesktop()) {
            return null;
        }
        try {
            Signature[] signatureArr = this.appContext.f().getPackageInfo(this.appContext.h(), 64).signatures;
            if (0 < signatureArr.length) {
                return Utility.formatDouble(signatureArr[0].toByteArray());
            }
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: p */
    public boolean isModdingEnabled() {
        if (!GameEngine.isDebugVersionStatic2) {
            if (isNotObfuscated() || isDesktopVersionStatic2) {
                return true;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: renamed from: q */
    public boolean isNotObfuscated() {
        if (OrderableUnit.class.getSimpleName().equals("OrderableUnit")) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: r */
    public String getVersionNameWithSuffix() {
        String versionName = getVersionName();
        return versionName;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: s */
    public void refreshVersionName() {
        gameVersionName = null;
        getVersionName();
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: t */
    public String getVersionName() {
        if (gameVersionName != null) {
            return gameVersionName;
        }
        String str = "v" + getVersion();
        if (!GameEngine.isGameBetaStatic || isDesktopVersionStatic2) {
            str = "DEBUG BUILD - " + str;
        } else if (GameEngine.isSandboxModeStatic) {
            str = "TESTING BUILD - " + str;
        } else if (str.contains("p")) {
            str = "BETA VERSION - " + str;
        }
        if (!GameEngine.isDebugVersionStatic2 && isNotObfuscated()) {
            str = "RAW - " + str;
        }
        gameVersionName = str;
        return gameVersionName;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: u */
    public String getVersion() {
        return "1.15";
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: v */
    public String getVersion2() {
        return "1.15";
    }

    /* JADX INFO: renamed from: w */
    public synchronized void synchronizedMethod() {
        this.booleanAC = false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: a */
    public synchronized void colorizeLogMessage(Activity activity, GameView gameView, boolean z) {
        synchronized (this.initLock) {
            if (!isPausedStatic2) {
                gameView.pause();
            }
            this.context = activity;
            this.isStopped = z;
            this.isPaused = this.isStopped;
            if (z && !this.loadNewGame && !this.fullReload && !GameEngine.isAndroidVersionStatic && !this.networkEngine.B) {
                loadMenuBackground();
            }
            GameView gameView2 = this.activity2;
            if (this.activity == null) {
                this.activity = gameView;
            }
            this.activity2 = gameView;
            if (gameView2 != null && gameView2 != gameView) {
                gameView2.onResume();
            }
            if (gameView != null) {
                gameView.stopRender();
            }
            if (this.gameUI != null) {
                this.gameUI.initializeLocalizedStrings();
            }
            synchronizedMethod();
            startGameThread();
        }
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: x */
    public synchronized void loadMenuBackground() {
        if (this.menuLoadFailureCount > 20) {
            return;
        }
        int i = this.settingsEngine.nextBackgroundMap;
        this.settingsEngine.nextBackgroundMap++;
        if (this.settingsEngine.nextBackgroundMap > 3) {
            this.settingsEngine.nextBackgroundMap = 1;
        }
        this.settingsEngine.save();
        int iDistance = Utility.distance(i, 1, 3);
        this.remoteMapStream = null;
        this.currentMapPath = "maps/menu_background/menu" + iDistance + ".tmx";
        try {
            PlayerTeam.getResourceCost(10, true);
            for (int i2 = 0; i2 < PlayerTeam.TEAM_NEUTRAL; i2++) {
                AIController aIController = new AIController(i2);
                if (i2 == 0) {
                    this.playerTeam = aIController;
                }
            }
            loadGame(false, GameMode.menu);
            this.reloadMap = true;
            this.gameUI.clearSelection();
            if (!this.loadNewGame) {
                GameEngine.logWarningAndStack("Menu load failed");
                this.menuLoadFailureCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: renamed from: d */
    void updateCameraFocus(float f) {
        if (this.isStopped && !this.reloadMap) {
            if (this.nextCameraFocusUnit == null) {
                this.nextCameraFocusUnit = findCameraFocusUnit();
                if (this.cameraFocusUnit == this.nextCameraFocusUnit) {
                    this.nextCameraFocusUnit = null;
                }
            }
            if (this.cameraFocusUnit == null) {
                this.cameraFocusUnit = this.nextCameraFocusUnit;
                this.nextCameraFocusUnit = null;
            }
            if (this.cameraFocusTransition != 0.0f && this.nextCameraFocusUnit != null) {
                moveCameraTo(f, this.nextCameraFocusUnit.posX, this.nextCameraFocusUnit.posY, this.cameraFocusTransition * 0.5f);
            }
            if (this.cameraFocusUnit != null) {
                boolean zMoveCameraTo = moveCameraTo(f, this.cameraFocusUnit.posX, this.cameraFocusUnit.posY, (1.0f - this.cameraFocusTransition) * 0.5f);
                if (Utility.distanceSq(this.viewpointX + this.halfViewpointWidth, this.viewpointY + this.halfViewpointHeight, this.cameraFocusUnit.posX, this.cameraFocusUnit.posY) < 6400.0f) {
                    zMoveCameraTo = true;
                }
                if (zMoveCameraTo) {
                    this.isCameraFocusing = true;
                }
            }
            if (this.isCameraFocusing) {
                this.cameraFocusTransition += 0.01f * f;
                if (this.cameraFocusTransition >= 1.0f) {
                    this.cameraFocusTransition = 0.0f;
                    this.cameraFocusUnit = null;
                    this.isCameraFocusing = false;
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    BaseUnit findRandomUnit(PlayerTeam playerTeam) {
        int i = 0;
        for (BaseUnit baseUnit : BaseUnit.bE) {
            if (!baseUnit.u() && (baseUnit.team == playerTeam || playerTeam == null)) {
                i++;
            }
        }
        if (i > 0) {
            int randomIntInRange = Utility.getRandomIntInRange(0, i - 1);
            int i2 = 0;
            for (BaseUnit baseUnit2 : BaseUnit.bE) {
                if (!baseUnit2.u() && (baseUnit2.team == playerTeam || playerTeam == null)) {
                    if (i2 == randomIntInRange) {
                        return baseUnit2;
                    }
                    i2++;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: renamed from: y */
    BaseUnit findCameraFocusUnit() {
        BaseUnit baseUnitFindRandomUnit = findRandomUnit(this.playerTeam);
        if (baseUnitFindRandomUnit != null) {
            return baseUnitFindRandomUnit;
        }
        return findRandomUnit((PlayerTeam) null);
    }

    /* JADX INFO: renamed from: a */
    public boolean moveCameraTo(float f, float f2, float f3, float f4) {
        float angleBetweenPoints = Utility.getAngleBetweenPoints(this.viewpointX + this.halfViewpointWidth, this.viewpointY + this.halfViewpointHeight, f2, f3);
        float fDistanceSq = Utility.distanceSq(this.viewpointX + this.halfViewpointWidth, this.viewpointY + this.halfViewpointHeight, f2, f3);
        float f5 = f4 * f;
        float f6 = 15.0f;
        if (15.0f < f5 + 1.0f) {
            f6 = f5 + 1.0f;
        }
        if (fDistanceSq < f6 * f6 || this.stoppedScrolling) {
            return true;
        }
        this.cameraMovementX += Utility.fastCos(angleBetweenPoints) * f5;
        this.cameraMovementY += Utility.fastSin(angleBetweenPoints) * f5;
        if (Utility.abs(this.cameraMovementX) >= 1.0f || Utility.abs(this.cameraMovementY) >= 1.0f) {
            this.viewpointX += this.cameraMovementX;
            this.viewpointY += this.cameraMovementY;
            this.cameraMovementX = 0.0f;
            this.cameraMovementY = 0.0f;
            setViewpoint(this.viewpointX, this.viewpointY);
            return false;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.gameFramework.GameEngine
    /* JADX INFO: renamed from: z */
    public int getAllUnitsChecksum() {
        return this.allUnitsChecksum;
    }
}
