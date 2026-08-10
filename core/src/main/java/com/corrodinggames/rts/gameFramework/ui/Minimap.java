package com.corrodinggames.rts.gameFramework.ui;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.effects.BuildPreview;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.RenderTargetMode;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.Point;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;
import io.github.rwx.render.canvas.KoolPaint;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.o */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/o.class */
public class Minimap {

    private GraphicsEngine graphicsBackend;

    /* JADX INFO: renamed from: a */
    float x;

    /* JADX INFO: renamed from: b */
    float y;

    /* JADX INFO: renamed from: e */
    public boolean isSetup;

    /* JADX INFO: renamed from: f */
    public boolean isMapReady;

    /* JADX INFO: renamed from: g */
    public int mapWidth;

    /* JADX INFO: renamed from: h */
    public int mapHeight;

    /* JADX INFO: renamed from: i */
    public float scaleX;

    /* JADX INFO: renamed from: j */
    public float scaleY;

    /* JADX INFO: renamed from: k */
    int lastElementUpdateX;

    /* JADX INFO: renamed from: l */
    int lastElementUpdateY;

    /* JADX INFO: renamed from: m */
    boolean elementPositionsDirty;

    /* JADX INFO: renamed from: F */
    Texture backgroundTexture;

    /* JADX INFO: renamed from: G */
    GraphicsEngine backgroundGraphics;

    /* JADX INFO: renamed from: H */
    Texture fogTexture;

    /* JADX INFO: renamed from: I */
    GraphicsEngine fogGraphics;

    /* JADX INFO: renamed from: J */
    public Texture unitsTexture;

    /* JADX INFO: renamed from: K */
    GraphicsEngine unitsGraphics;

    /* JADX INFO: renamed from: M */
    float fogRefreshDelayTimer;

    /* JADX INFO: renamed from: N */
    float fogRefreshStepTimer;

    /* JADX INFO: renamed from: T */
    public Texture pingTexture;

    /* JADX INFO: renamed from: U */
    public Texture buildingTexture;

    /* JADX INFO: renamed from: W */
    GamePaint[] fogAlphaPaints;

    /* JADX INFO: renamed from: X */
    GamePaint shroudFogPaint;

    /* JADX INFO: renamed from: Y */
    float lastPingTime;

    /* JADX INFO: renamed from: ac */
    static ArrayList<LineDrawer> lineDrawerPool = new ArrayList();

    /* JADX INFO: renamed from: c */
    public float width = 120.0f;

    /* JADX INFO: renamed from: d */
    public float height = 120.0f;

    /* JADX INFO: renamed from: n */
    final KoolPaint minimapTexturePaint = new KoolPaint();

    /* JADX INFO: renamed from: o */
    final KoolPaint borderPaint = new KoolPaint();

    /* JADX INFO: renamed from: p */
    final KoolPaint dynamicTeamColorPaint = new KoolPaint();

    /* JADX INFO: renamed from: q */
    float borderPulsePhase = 0.0f;

    /* JADX INFO: renamed from: r */
    float effectPulsePhase = 0.0f;

    /* JADX INFO: renamed from: s */
    final KoolPaint cameraViewportPaint = new GamePaint();

    /* JADX INFO: renamed from: t */
    final KoolPaint alertEffectPaint = new KoolPaint();

    /* JADX INFO: renamed from: u */
    final KoolPaint unitEffectPaint = new KoolPaint();

    /* JADX INFO: renamed from: v */
    final KoolPaint messageEffectPaint = new KoolPaint();

    /* JADX INFO: renamed from: w */
    public final Rect minimapBoundsRect = new Rect();

    /* JADX INFO: renamed from: x */
    final KoolPaint ownUnitPaint = new GamePaint();

    /* JADX INFO: renamed from: y */
    final KoolPaint allyUnitPaint = new GamePaint();

    /* JADX INFO: renamed from: z */
    final KoolPaint enemyUnitPaint = new GamePaint();

    /* JADX INFO: renamed from: A */
    final KoolPaint ownBuildPreviewPaint = new GamePaint();

    /* JADX INFO: renamed from: B */
    final KoolPaint allyBuildPreviewPaint = new GamePaint();

    /* JADX INFO: renamed from: C */
    final KoolPaint enemyBuildPreviewPaint = new GamePaint();

    /* JADX INFO: renamed from: D */
    final KoolPaint strategicPointPaint = new GamePaint();

    /* JADX INFO: renamed from: E */
    final Rect cameraViewportRect = new Rect();

    /* JADX INFO: renamed from: L */
    float elementPositionRefreshTimer = 0.0f;

    /* JADX INFO: renamed from: O */
    public boolean isFogRefreshPending = false;

    /* JADX INFO: renamed from: P */
    public boolean isFogRefreshActive = false;

    /* JADX INFO: renamed from: Q */
    public float fogRefreshProgress = 0.0f;

    /* JADX INFO: renamed from: R */
    int field_R = 30;

    /* JADX INFO: renamed from: S */
    int field_S = -1;

    /* JADX INFO: renamed from: V */
    final Rect tempDrawRect = new Rect();

    /* JADX INFO: renamed from: Z */
    public final ArrayList<MinimapPing> pingMarkers = new ArrayList();

    /* JADX INFO: renamed from: aa */
    public final ArrayList<MinimapEffect> activeEffects = new ArrayList<>();

    /* JADX INFO: renamed from: ag */
    private final ArrayList<StrategicPoint> strategicPoints = new ArrayList();

    /* JADX INFO: renamed from: ab */
    Rect fogUpdateRect = new Rect();

    /* JADX INFO: renamed from: ad */
    Point scratchPoint = new Point();

    /* JADX INFO: renamed from: af */
    ArrayList trackedUnits = new ArrayList();

    /* JADX INFO: renamed from: a */
    public void ping(int i, int i2, float f, BaseUnit baseUnit) {
        boolean z = baseUnit != null && baseUnit.bI();
        for (MinimapPing minimapPing : this.pingMarkers) {
            if (minimapPing.isEnemy == z && Utility.abs(i - minimapPing.x) < 40 && Utility.abs(i2 - minimapPing.y) < 40) {
                minimapPing.radius += f;
                return;
            }
        }
        this.pingMarkers.add(new MinimapPing(this, f, i, i2, z));
    }

    /* JADX INFO: renamed from: a */
    public void init() {
        this.borderPaint.a(KoolPaint.Style.STROKE);
        this.borderPaint.a(1.0f);
        this.cameraViewportPaint.a(255, 255, 255, 255);
        this.cameraViewportPaint.a(KoolPaint.Style.STROKE);
        this.cameraViewportPaint.a(1.0f);
        this.fogAlphaPaints = new GamePaint[11];
        for (int i = 0; i <= 10; i++) {
            this.fogAlphaPaints[i] = new GamePaint();
            this.fogAlphaPaints[i].b(-16777216);
            this.fogAlphaPaints[i].a(KoolPaint.Style.FILL);
            this.fogAlphaPaints[i].c(i * 25);
        }
        this.shroudFogPaint = new GamePaint();
        this.shroudFogPaint.b(-16777216);
        this.shroudFogPaint.a(KoolPaint.Style.FILL);
        this.alertEffectPaint.a(255, 255, 0, 0);
        this.alertEffectPaint.a(KoolPaint.Style.STROKE);
        this.alertEffectPaint.a(2.0f);
        this.unitEffectPaint.a(155, 255, 0, 0);
        this.unitEffectPaint.a(KoolPaint.Style.STROKE);
        this.unitEffectPaint.a(2.0f);
        this.messageEffectPaint.a(200, 12, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_11, 219);
        this.messageEffectPaint.a(KoolPaint.Style.STROKE);
        this.messageEffectPaint.a(2.0f);
        this.ownUnitPaint.b(-16711936);
        this.allyUnitPaint.b(-256);
        this.enemyUnitPaint.b(-65536);
        this.ownBuildPreviewPaint.b(darkenColor(this.ownUnitPaint.e()));
        this.allyBuildPreviewPaint.b(darkenColor(this.allyUnitPaint.e()));
        this.enemyBuildPreviewPaint.b(darkenColor(this.enemyUnitPaint.e()));
        this.strategicPointPaint.a(210, 255, 255, 255);
    }

    /* JADX INFO: renamed from: a */
    public static int darkenColor(int i) {
        return KoolArgbColor.a(KoolArgbColor.a(i), (int) (KoolArgbColor.b(i) * 0.5f), (int) (KoolArgbColor.c(i) * 0.5f), (int) (KoolArgbColor.d(i) * 0.5f));
    }

    /* JADX INFO: renamed from: a */
    public void updateMinimapPosition() {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!GameUI.bR) {
            this.x = (int) (gameEngine.screenWidth - (this.width + 0.0f));
            this.y = 0.0f;
        } else {
            this.x = 0.0f;
            this.y = (int) (gameEngine.screenHeight - (this.height + 0.0f));
        }
    }

    /* JADX INFO: renamed from: b */
    public int getBottomY() {
        return (int) (this.y + this.height);
    }

    /* JADX INFO: renamed from: a */
    public void reset(TileMap tileMap, boolean z) {
        this.trackedUnits.clear();
        if (z) {
            this.elementPositionsDirty = true;
            return;
        }
        this.mapWidth = 1;
        this.mapHeight = 1;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.isMapReady = false;
        this.isSetup = false;
    }

    /* JADX INFO: renamed from: c */
    public void release() {
        if (this.backgroundGraphics != null) {
            this.backgroundGraphics.q();
            this.backgroundGraphics = null;
        }
        if (this.unitsGraphics != null) {
            this.unitsGraphics.q();
            this.unitsGraphics = null;
        }
        if (this.fogGraphics != null) {
            this.fogGraphics.q();
            this.fogGraphics = null;
        }
        if (this.unitsTexture != null) {
            this.unitsTexture.o();
            this.unitsTexture = null;
        }
        if (this.backgroundTexture != null) {
            this.backgroundTexture.o();
            this.backgroundTexture = null;
        }
        if (this.pingTexture != null) {
            this.pingTexture.o();
            this.pingTexture = null;
        }
        if (this.buildingTexture != null) {
            this.buildingTexture.o();
            this.buildingTexture = null;
        }
        if (this.fogTexture != null) {
            this.fogTexture.o();
            this.fogTexture = null;
        }
        this.isSetup = false;
    }

    public void bindGraphicsBackend(GraphicsEngine graphicsEngine) {
        if (graphicsEngine == null) {
            throw new IllegalArgumentException("graphicsEngine cannot be null");
        }
        if (this.graphicsBackend == graphicsEngine) {
            return;
        }
        release();
        this.graphicsBackend = graphicsEngine;
    }

    /* JADX INFO: renamed from: d */
    public float getCameraZoom() {
        return GameEngine.getInstance().sidebarWidth;
    }

    /* JADX INFO: renamed from: e */
    public void createImageBuffers() {
        GraphicsEngine graphicsEngine = this.graphicsBackend;
        if (graphicsEngine == null) {
            throw new IllegalStateException("Minimap graphics backend is not bound");
        }
        updateDimensions();
        GameEngine.log("Creating minimap image buffers..");
        if (this.backgroundTexture == null) {
            this.backgroundTexture = graphicsEngine.a((int) this.width, (int) this.height, false);
            this.backgroundGraphics = graphicsEngine.b(this.backgroundTexture, RenderTargetMode.IMMEDIATE);
        }
        if (this.unitsTexture == null) {
            this.unitsTexture = graphicsEngine.a((int) this.width, (int) this.height, false);
            this.unitsGraphics = graphicsEngine.b(this.unitsTexture, RenderTargetMode.IMMEDIATE);
        }
        if (this.fogTexture == null) {
            this.fogTexture = graphicsEngine.a((int) this.width, (int) this.height, false);
            this.fogGraphics = graphicsEngine.b(this.fogTexture, RenderTargetMode.IMMEDIATE);
        }
    }

    /* JADX INFO: renamed from: f */
    public void updateDimensions() {
        this.width = getCameraZoom();
        this.height = this.width;
        updateMinimapPosition();
    }

    /* JADX INFO: renamed from: g */
    public void setup() {
        long jA = PerformanceProfiler.a();
        GameEngine.log("--setting up minimap--");
        GameEngine gameEngine = GameEngine.getInstance();
        updateDimensions();
        this.mapWidth = gameEngine.tileMap.tileCountX * gameEngine.tileMap.tileWorldSizeX;
        this.mapHeight = gameEngine.tileMap.tileCountY * gameEngine.tileMap.tileWorldSizeY;
        if (this.mapWidth <= 0) {
            this.mapWidth = 1;
        }
        if (this.mapHeight <= 0) {
            this.mapHeight = 1;
        }
        this.scaleX = 1.0f / this.mapWidth;
        this.scaleY = 1.0f / this.mapHeight;
        this.isMapReady = true;
        createImageBuffers();
        this.pingMarkers.clear();
        this.activeEffects.clear();
        this.strategicPoints.clear();
        for (Point point : gameEngine.tileMap.unitObjects) {
            this.strategicPoints.add(new StrategicPoint(this, point.worldX, point.worldY));
        }
        this.backgroundGraphics.b(-16777216);
        this.unitsGraphics.b(-16777216);
        for (int i = 0; i < 2; i++) {
            for (int i2 = 0; i2 < 2; i2++) {
                this.fogGraphics.b(-16777216);
                int i3 = ((int) this.width) / 2;
                int i4 = ((int) this.height) / 2;
                int i5 = this.mapWidth / 2;
                int i6 = this.mapHeight / 2;
                gameEngine.tileMap.groundLayer.renderLayerRegion(this.fogGraphics, i5 * i, i6 * i2, i5 * i, i6 * i2, i5, i6, this.width / i5, this.height / i6, false, false, false);
                this.fogGraphics.p();
                Rect rect2 = new Rect(0, 0, (int) this.width, (int) this.height);
                Rect rect3 = new Rect(i3 * i, i4 * i2, i3 * (i + 1), i4 * (i2 + 1));
                KoolPaint paint2 = new KoolPaint();
                paint2.a(true);
                paint2.d(true);
                paint2.b(true);
                this.unitsGraphics.a(this.fogTexture, rect2, rect3, paint2);
            }
        }
        this.unitsGraphics.p();
        Rect rect4 = new Rect(0, 0, (int) this.width, (int) this.height);
        this.backgroundGraphics.b(-16777216);
        KoolPaint paint3 = new KoolPaint();
        paint3.a(true);
        paint3.d(true);
        paint3.b(true);
        paint3.a(200, 255, 255, 255);
        this.backgroundGraphics.a(this.unitsTexture, rect4, rect4, paint3);
        this.backgroundGraphics.p();
        this.fogGraphics.b(-16777216);
        this.unitsGraphics.b(-16777216);
        this.fogRefreshDelayTimer = 50.0f;
        drawFog(0.0f, 1.0f);
        this.isSetup = true;
        GameEngine.log("Minimap map render took:" + PerformanceProfiler.a(PerformanceProfiler.a(jA)));
    }

    /* JADX INFO: renamed from: a */
    void drawFog(float f, float f2) {
        GamePaint gamePaint;
        GameEngine gameEngine = GameEngine.getInstance();
        this.fogUpdateRect.a(0, (int) (f * this.height), (int) this.width, (int) (f2 * this.height));
        this.fogGraphics.a(this.backgroundTexture, this.fogUpdateRect, this.fogUpdateRect, (KoolPaint) null);
        TileMap tileMap = gameEngine.tileMap;
        if (tileMap.fogEnabled) {
            boolean z = tileMap.fogRenderActive;
            GamePaint gamePaint2 = this.fogAlphaPaints[5];
            GamePaint gamePaint3 = this.fogAlphaPaints[10];
            GamePaint gamePaint4 = this.shroudFogPaint;
            gamePaint4.c(255);
            if (z) {
                gamePaint4.c((int) ((1.0f - ((1.0f - (gamePaint2.f() / 255.0f)) * (1.0f - (this.fogAlphaPaints[7].f() / 255.0f)))) * 255.0f));
            }
            float f3 = this.width / tileMap.tileCountX;
            float f4 = this.height / tileMap.tileCountY;
            int i = ((int) (f * tileMap.tileCountY)) - 1;
            int i2 = ((int) (f2 * tileMap.tileCountY)) + 1;
            if (i < 0) {
                i = 0;
            }
            if (i2 < 0) {
                i2 = 0;
            }
            if (i > gameEngine.tileMap.tileCountY) {
                i = tileMap.tileCountY;
            }
            if (i2 > gameEngine.tileMap.tileCountY) {
                i2 = tileMap.tileCountY;
            }
            int i3 = 0;
            byte[][] bArr = gameEngine.playerTeam.fogOfWarData;
            if (bArr != null) {
                int i4 = tileMap.tileCountX;
                Rect rect = this.tempDrawRect;
                for (int i5 = i; i5 < i2; i5++) {
                    int i6 = 0;
                    while (i6 < i4) {
                        byte b = bArr[i6][i5];
                        if (b != 0) {
                            int i7 = i6;
                            int i8 = i6;
                            while (i8 < i4 - 1 && bArr[i8][i5] == b) {
                                i8++;
                            }
                            i6 = i8;
                            rect.a(0 + ((int) (i7 * f3)), 0 + ((int) (i5 * f4)), 0 + ((int) ((i8 + 1) * f3)), 0 + ((int) ((i5 + 1) * f4)));
                            if (b == 10) {
                                gamePaint = gamePaint4;
                            } else {
                                gamePaint = gamePaint2;
                            }
                            this.fogGraphics.b(rect, gamePaint);
                            i3++;
                            if (i3 > 2) {
                                i3 = 0;
                            }
                        }
                        i6++;
                    }
                }
            }
        }
        this.fogGraphics.p();
        this.unitsGraphics.a(this.fogTexture, this.fogUpdateRect, this.fogUpdateRect, (KoolPaint) null);
        this.unitsGraphics.p();
        if (GameEngine.isGDXVersion) {
        }
    }

    /* JADX INFO: renamed from: a */
    static LineDrawer getLineDrawer(int i, KoolPaint paint) {
        synchronized (lineDrawerPool) {
            LineDrawer lineDrawer = null;
            for (LineDrawer lineDrawer2 : lineDrawerPool) {
                if (lineDrawer2.capacity >= i && (lineDrawer == null || lineDrawer2.capacity < lineDrawer.capacity)) {
                    lineDrawer = lineDrawer2;
                }
            }
            if (lineDrawer != null) {
                lineDrawerPool.remove(lineDrawer);
                lineDrawer.paint = paint;
                return lineDrawer;
            }
            return new LineDrawer(i + 15, paint);
        }
    }

    /* JADX INFO: renamed from: a */
    static void returnLineDrawer(LineDrawer lineDrawer) {
        lineDrawer.paint = null;
        lineDrawer.vertexIndex = 0;
        synchronized (lineDrawerPool) {
            if (lineDrawerPool.size() < 20) {
                lineDrawerPool.add(lineDrawer);
                return;
            }
            Iterator it = lineDrawerPool.iterator();
            while (it.hasNext()) {
                if (((LineDrawer) it.next()).capacity < lineDrawer.capacity) {
                    it.remove();
                    lineDrawerPool.add(lineDrawer);
                    return;
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    void drawUnitsAndBuildings(GraphicsEngine graphicsEngine, int i, int i2, float f, float f2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.width < 50.0f) {
            i3 = 0;
            i4 = 0;
            i5 = 1;
            i6 = 1;
            i7 = 1;
        } else if (this.width < 120.0f) {
            i3 = 0;
            i4 = 0;
            i5 = 2;
            i6 = 2;
            i7 = 2;
        } else {
            i3 = -1;
            i4 = -1;
            i5 = 2;
            i6 = 2;
            i7 = 3;
        }
        int i8 = i4 + i;
        int i9 = i5 + i;
        int i10 = i6 + i2;
        int i11 = i3 + i2;
        boolean z = false;
        if (gameEngine.playerTeam.isSpectatorTeamColor() || gameEngine.replayEngine.j()) {
            z = true;
        }
        for (int i12 = -1; i12 < PlayerTeam.TEAM_NEUTRAL; i12++) {
            PlayerTeam playerTeamK = PlayerTeam.k(i12);
            if (playerTeamK != null) {
                KoolPaint paint = playerTeamK.teamColorPaint;
                if (gameEngine.settingsEngine.useMinimapAllyColors) {
                    if (z) {
                        this.dynamicTeamColorPaint.b(PlayerTeam.i(playerTeamK.teamColorId));
                        paint = this.dynamicTeamColorPaint;
                    } else if (gameEngine.playerTeam == playerTeamK) {
                        paint = this.ownUnitPaint;
                    } else if (gameEngine.playerTeam.d(playerTeamK)) {
                        paint = this.allyUnitPaint;
                    } else if (gameEngine.playerTeam.c(playerTeamK)) {
                        paint = this.enemyUnitPaint;
                    }
                }
                int i13 = 0;
                if (playerTeamK.getUnitCount(true, false) > 0) {
                    BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
                    int size = BaseUnit.bE.size();
                    for (int i14 = 0; i14 < size; i14++) {
                        BaseUnit baseUnit = baseUnitArrA[i14];
                        if (baseUnit.team == playerTeamK && baseUnit.isUnitFull) {
                            i13++;
                        }
                    }
                }
                if (i13 > 0) {
                    paint.a((float) i7);
                    LineDrawer lineDrawer = getLineDrawer(i13, paint);
                    lineDrawer.drawAsPoints = !gameEngine.settingsEngine.renderWithLineWidth;
                    BaseUnit[] baseUnitArrA2 = BaseUnit.bE.a();
                    int size2 = BaseUnit.bE.size();
                    for (int i15 = 0; i15 < size2; i15++) {
                        BaseUnit baseUnit2 = baseUnitArrA2[i15];
                        if (baseUnit2.team == playerTeamK && baseUnit2.isUnitFull) {
                            lineDrawer.addPoint(baseUnit2.unitCargoCount, baseUnit2.unitCargoMax);
                        }
                    }
                    if (lineDrawer.vertexIndex != 0) {
                        gameEngine.renderGraphicsEngine.a(lineDrawer);
                    }
                }
                KoolPaint paint2 = playerTeamK.teamTextPaint;
                if (gameEngine.settingsEngine.useMinimapAllyColors) {
                    if (z) {
                        this.dynamicTeamColorPaint.b(PlayerTeam.i(playerTeamK.teamColorId));
                        KoolPaint paint3 = this.dynamicTeamColorPaint;
                    } else if (gameEngine.playerTeam == playerTeamK) {
                        paint2 = this.ownBuildPreviewPaint;
                    } else if (gameEngine.playerTeam.d(playerTeamK)) {
                        paint2 = this.allyBuildPreviewPaint;
                    } else if (gameEngine.playerTeam.c(playerTeamK)) {
                        paint2 = this.enemyBuildPreviewPaint;
                    }
                }
                int i16 = 0;
                Object[] objArrB = BuildPreview.activePreviews.b();
                int size3 = BuildPreview.activePreviews.size();
                for (int i17 = 0; i17 < size3; i17++) {
                    BuildPreview buildPreview = (BuildPreview) objArrB[i17];
                    if (buildPreview.team == playerTeamK && buildPreview.hasMinimapPosition) {
                        i16++;
                    }
                }
                if (i16 > 0) {
                    paint2.a((float) i7);
                    LineDrawer lineDrawer2 = getLineDrawer(i16, paint2);
                    Object[] objArrB2 = BuildPreview.activePreviews.b();
                    int size4 = BuildPreview.activePreviews.size();
                    for (int i18 = 0; i18 < size4; i18++) {
                        BuildPreview buildPreview2 = (BuildPreview) objArrB2[i18];
                        if (buildPreview2.team == playerTeamK && buildPreview2.hasMinimapPosition) {
                            lineDrawer2.addPoint(buildPreview2.gridX, buildPreview2.gridY);
                        }
                    }
                    if (lineDrawer2.vertexIndex != 0) {
                        gameEngine.renderGraphicsEngine.a(lineDrawer2);
                    }
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void addEffect(int i, int i2, MinimapEffectType minimapEffectType) {
        MinimapEffect minimapEffect = new MinimapEffect(this);
        minimapEffect.x = i;
        minimapEffect.y = i2;
        minimapEffect.type = minimapEffectType;
        minimapEffect.intensity = 0.9f;
        minimapEffect.duration = 0.9f;
        this.activeEffects.add(minimapEffect);
    }

    /* JADX INFO: renamed from: h */
    public void updateElementPositions() {
        GameEngine gameEngine = GameEngine.getInstance();
        this.elementPositionsDirty = false;
        this.lastElementUpdateX = (int) this.x;
        this.lastElementUpdateY = (int) this.y;
        BaseUnit[] baseUnitArrA = BaseUnit.bE.a();
        int size = BaseUnit.bE.size();
        for (int i = 0; i < size; i++) {
            BaseUnit baseUnit = baseUnitArrA[i];
            if (!baseUnit.isDead && baseUnit.unitTransportTarget == null && baseUnit.isVisibleToLocalPlayer() && baseUnit.c_() && !baseUnit.u()) {
                Point pointWorldToScreen = worldToScreen(baseUnit.posX, baseUnit.posY);
                baseUnit.unitCargoCount = pointWorldToScreen.worldX;
                baseUnit.unitCargoMax = pointWorldToScreen.worldY;
                baseUnit.isUnitFull = true;
            } else {
                baseUnit.isUnitFull = false;
            }
        }
        Object[] objArrB = BuildPreview.activePreviews.b();
        int size2 = BuildPreview.activePreviews.size();
        for (int i2 = 0; i2 < size2; i2++) {
            BuildPreview buildPreview = (BuildPreview) objArrB[i2];
            if (!buildPreview.isBuilding && buildPreview.isVisibleOnMinimap) {
                Point pointWorldToScreen2 = worldToScreen(buildPreview.worldX, buildPreview.worldY);
                buildPreview.gridX = pointWorldToScreen2.worldX;
                buildPreview.gridY = pointWorldToScreen2.worldY;
                buildPreview.hasMinimapPosition = true;
            }
        }
        PlayerTeam playerTeam = gameEngine.playerTeam;
        for (StrategicPoint strategicPoint : this.strategicPoints) {
            strategicPoint.isVisible = false;
            if (gameEngine.tileMap.isTileVisibleForTeam(playerTeam, strategicPoint.gridX, strategicPoint.gridY)) {
                strategicPoint.isVisible = true;
                Point pointWorldToScreen3 = worldToScreen(strategicPoint.gridX * gameEngine.tileMap.tileWorldSizeX, strategicPoint.gridY * gameEngine.tileMap.tileWorldSizeY);
                strategicPoint.screenX = pointWorldToScreen3.worldX;
                strategicPoint.screenY = pointWorldToScreen3.worldY;
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void update(float f) {
        if (GameEngine.isNonAndroidVersion && !GameEngine.isPCOrIOSVersion) {
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        this.elementPositionRefreshTimer = Utility.moveTowardsZero(this.elementPositionRefreshTimer, f);
        if (this.elementPositionRefreshTimer == 0.0f) {
            this.elementPositionRefreshTimer = 15.0f;
            updateElementPositions();
        }
        this.lastPingTime += f;
        if (this.lastPingTime > 15.0f) {
            MinimapPing minimapPing = null;
            for (MinimapPing minimapPing2 : this.pingMarkers) {
                if (minimapPing2.e != 0.0f) {
                    minimapPing2.radius = 0.0f;
                } else if (minimapPing2.radius > 15.0f) {
                    minimapPing2.radius = 0.0f;
                    minimapPing2.e = 300.0f;
                    MinimapEffect minimapEffect = new MinimapEffect(this);
                    minimapEffect.x = minimapPing2.x;
                    minimapEffect.y = minimapPing2.y;
                    if (minimapPing2.isEnemy) {
                        minimapEffect.type = MinimapEffectType.base;
                    } else {
                        minimapEffect.type = MinimapEffectType.unit;
                        minimapEffect.intensity = 0.4f;
                        minimapEffect.duration = 0.8f;
                    }
                    this.activeEffects.add(minimapEffect);
                }
                minimapPing2.radius = Utility.moveTowardsZero(minimapPing2.radius, 2.0f * this.lastPingTime);
                minimapPing2.e = Utility.moveTowardsZero(minimapPing2.e, this.lastPingTime);
                if (minimapPing2.radius == 0.0f && minimapPing2.e == 0.0f) {
                    minimapPing = minimapPing2;
                }
            }
            if (minimapPing != null) {
                this.pingMarkers.remove(minimapPing);
            }
            for (MinimapEffect minimapEffect2 : this.activeEffects) {
                if (minimapEffect2.type != MinimapEffectType.message && gameEngine.visibleWorldRect.b(minimapEffect2.x, minimapEffect2.y)) {
                    minimapEffect2.intensity = 0.0f;
                    minimapEffect2.duration = 0.0f;
                }
            }
            this.lastPingTime = 0.0f;
        }
    }

    /* JADX INFO: renamed from: b */
    public float worldToMinimapX(float f) {
        return f * this.scaleX * this.width;
    }

    /* JADX INFO: renamed from: b */
    public Point worldToScreen(float f, float f2) {
        if (!this.isMapReady) {
            this.scratchPoint.a(-1, -1);
            return this.scratchPoint;
        }
        this.scratchPoint.a((int) ((f * this.scaleX * this.width) + this.x), (int) ((f2 * this.scaleY * this.height) + this.y));
        return this.scratchPoint;
    }

    /* JADX INFO: renamed from: c */
    public Point screenToWorld(float f, float f2) {
        if (f < this.x || f2 < this.y || f > this.x + this.width || f2 > this.y + this.height) {
            return null;
        }
        this.scratchPoint.a((int) (((f - this.x) / this.width) * this.mapWidth), (int) (((f2 - this.y) / this.height) * this.mapHeight));
        return this.scratchPoint;
    }

    /* JADX INFO: renamed from: c */
    public float clampX(float f) {
        return f < this.x ? this.x : f > this.x + this.width ? this.x + this.width : f;
    }

    /* JADX INFO: renamed from: d */
    public float clampY(float f) {
        return f < this.y ? this.y : f > this.y + this.height ? this.y + this.height : f;
    }

    /* JADX INFO: renamed from: e */
    public void draw(float f) {
        KoolPaint paint;
        GameEngine gameEngine = GameEngine.getInstance();
        GraphicsEngine graphicsEngine = gameEngine.renderGraphicsEngine;
        updateMinimapPosition();
        if (this.unitsTexture != null && !Utility.isDifferenceWithinTolerance(this.width, getCameraZoom(), 5.0f)) {
            GameEngine.log("minimap", "minimap size has changed, reseting");
            release();
        }
        if (!this.isSetup || this.unitsTexture == null) {
            setup();
        }
        if (this.lastElementUpdateX != ((int) this.x) || this.lastElementUpdateY != ((int) this.y) || this.elementPositionsDirty) {
            updateElementPositions();
        }
        if (gameEngine.tileMap.fogEnabled) {
            if (this.isFogRefreshPending && !this.isFogRefreshActive) {
                this.fogRefreshDelayTimer = Utility.moveTowardsZero(this.fogRefreshDelayTimer, 1.0f);
                if (this.fogRefreshDelayTimer == 0.0f) {
                    this.fogRefreshDelayTimer = 40.0f;
                    this.isFogRefreshPending = false;
                    this.fogRefreshProgress = 0.0f;
                    this.isFogRefreshActive = true;
                }
            }
            if (this.isFogRefreshActive) {
                this.fogRefreshStepTimer = Utility.moveTowardsZero(this.fogRefreshStepTimer, 1.0f);
                if (this.fogRefreshStepTimer == 0.0f) {
                    this.fogRefreshStepTimer = 3.0f;
                    if (this.unitsTexture != null) {
                        float f2 = this.fogRefreshProgress - 0.005f;
                        this.fogRefreshProgress = (float) (((double) this.fogRefreshProgress) + 0.04d);
                        if (f2 < 0.0f) {
                            f2 = 0.0f;
                        }
                        if (this.fogRefreshProgress >= 1.0f) {
                            this.fogRefreshProgress = 1.0f;
                            this.isFogRefreshActive = false;
                        }
                        drawFog(f2, this.fogRefreshProgress);
                    }
                }
            }
        }
        graphicsEngine.b(this.unitsTexture, this.x, this.y, this.minimapTexturePaint);
        this.minimapBoundsRect.a((int) this.x, (int) this.y, (int) (this.x + this.width), (int) (((double) (this.y + this.height)) - 0.4d));
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        Object[] objArrA = Projectile.a.a();
        int i = Projectile.a.size;
        for (int i2 = 0; i2 < i; i2++) {
            if (((Projectile) objArrA[i2]).D) {
                z3 = true;
                z2 = true;
            }
        }
        for (MinimapEffect minimapEffect : this.activeEffects) {
            if (minimapEffect.type != MinimapEffectType.unit) {
                z = true;
                if (minimapEffect.type != MinimapEffectType.message) {
                    z2 = true;
                }
            }
        }
        if (!z && !z3) {
            this.borderPaint.a(255, 100, 100, 100);
            this.borderPaint.a(1.0f);
            if (GameUI.bO) {
                this.borderPaint.a(115, 0, 0, 0);
                this.borderPaint.a(2.0f);
            }
        } else {
            this.borderPulsePhase += 5.0f * f;
            if (this.borderPulsePhase > 180.0f) {
                this.borderPulsePhase -= 180.0f;
            }
            float fFastSin = Utility.fastSin(this.borderPulsePhase);
            if (z3) {
                this.borderPaint.a(255, 0, (int) (0.0f + (fFastSin * 230.0f)), 0);
            } else if (!z2) {
                this.borderPaint.a(255, 12, (int) (0.0f + (fFastSin * 220.0f)), (int) (0.0f + (fFastSin * 220.0f)));
            } else {
                this.borderPaint.a(255, (int) (0.0f + (fFastSin * 230.0f)), 0, 0);
            }
            this.borderPaint.a(2.0f);
        }
        graphicsEngine.b(this.minimapBoundsRect, this.borderPaint);
        for (StrategicPoint strategicPoint : this.strategicPoints) {
            if (strategicPoint.isVisible) {
                this.tempDrawRect.a(strategicPoint.screenX, strategicPoint.screenY, strategicPoint.screenX + 2, strategicPoint.screenY + 2);
                graphicsEngine.b(this.tempDrawRect, this.strategicPointPaint);
            }
        }
        drawUnitsAndBuildings(graphicsEngine, 0, 0, 0.0f, 1.0f);
        if (this.trackedUnits.size() != 0) {
            Iterator it = this.trackedUnits.iterator();
            while (it.hasNext()) {
                MinimapTrackedUnit minimapTrackedUnit = (MinimapTrackedUnit) it.next();
                if (minimapTrackedUnit.unit.isDead) {
                    it.remove();
                } else {
                    BaseUnit baseUnit = minimapTrackedUnit.unit;
                    Point pointWorldToScreen = worldToScreen(baseUnit.posX, baseUnit.posY);
                    if (!baseUnit.a(pointWorldToScreen.worldX, pointWorldToScreen.worldY)) {
                        graphicsEngine.a(pointWorldToScreen.worldX, pointWorldToScreen.worldY, 4.0f, baseUnit.team.teamColorPaint);
                    }
                }
            }
        }
        int i3 = Projectile.a.size;
        for (int i4 = 0; i4 < i3; i4++) {
            Projectile projectile = (Projectile) objArrA[i4];
            if ((projectile.D || (projectile.q != null && projectile.q.D)) && projectile.j != null) {
                Point pointWorldToScreen2 = worldToScreen(projectile.posX, projectile.posY);
                float f3 = 2.0f;
                if (projectile.D) {
                    f3 = 4.0f;
                }
                graphicsEngine.a(pointWorldToScreen2.worldX, pointWorldToScreen2.worldY, f3, projectile.j.team.teamColorPaint);
            }
        }
        Point pointWorldToScreen3 = worldToScreen(gameEngine.viewpointXSnapped, gameEngine.viewpointYSnapped);
        this.cameraViewportRect.a = pointWorldToScreen3.worldX;
        this.cameraViewportRect.b = pointWorldToScreen3.worldY;
        Point pointWorldToScreen4 = worldToScreen(gameEngine.viewpointXSnapped + gameEngine.visibleWorldWidth, gameEngine.viewpointYSnapped + gameEngine.visibleWorldHeight);
        this.cameraViewportRect.c = pointWorldToScreen4.worldX;
        this.cameraViewportRect.d = pointWorldToScreen4.worldY;
        if (this.cameraViewportRect.a < this.minimapBoundsRect.a) {
            this.cameraViewportRect.a = this.minimapBoundsRect.a;
        }
        if (this.cameraViewportRect.c > this.minimapBoundsRect.c) {
            this.cameraViewportRect.c = this.minimapBoundsRect.c;
        }
        if (this.cameraViewportRect.b < this.minimapBoundsRect.b) {
            this.cameraViewportRect.b = this.minimapBoundsRect.b;
        }
        if (this.cameraViewportRect.d > this.minimapBoundsRect.d) {
            this.cameraViewportRect.d = this.minimapBoundsRect.d;
        }
        graphicsEngine.b(this.cameraViewportRect, this.cameraViewportPaint);
        this.effectPulsePhase += 6.0f * f;
        if (this.effectPulsePhase > 180.0f) {
            this.effectPulsePhase -= 180.0f;
        }
        Iterator it2 = this.activeEffects.iterator();
        while (it2.hasNext()) {
            MinimapEffect minimapEffect2 = (MinimapEffect) it2.next();
            Point pointWorldToScreen5 = worldToScreen(minimapEffect2.x, minimapEffect2.y);
            float f4 = minimapEffect2.intensity;
            float f5 = 0.05f;
            if (minimapEffect2.type == MinimapEffectType.unit) {
                paint = this.unitEffectPaint;
                f5 = 0.03f;
                float fFastSin2 = Utility.fastSin(this.effectPulsePhase);
                paint.a((int) (50.0f + (fFastSin2 * 190.0f)), (int) (50.0f + (fFastSin2 * 190.0f)), 0, 0);
            } else if (minimapEffect2.type == MinimapEffectType.message) {
                paint = this.messageEffectPaint;
            } else {
                paint = this.alertEffectPaint;
                float fFastSin3 = Utility.fastSin(this.effectPulsePhase);
                paint.a((int) (50.0f + (fFastSin3 * 190.0f)), (int) (50.0f + (fFastSin3 * 190.0f)), 0, 0);
            }
            float fClampTo255 = Utility.clampTo255(f4, f5, 1.0f);
            if (minimapEffect2.type == MinimapEffectType.unit) {
                float f6 = this.width * fClampTo255;
                float f7 = this.height * fClampTo255;
                graphicsEngine.a(clampX(pointWorldToScreen5.worldX - f6), clampY(pointWorldToScreen5.worldY - f7), clampX(pointWorldToScreen5.worldX + f6), clampY(pointWorldToScreen5.worldY + f7), paint);
                graphicsEngine.a(clampX(pointWorldToScreen5.worldX + f6), clampY(pointWorldToScreen5.worldY - f7), clampX(pointWorldToScreen5.worldX - f6), clampY(pointWorldToScreen5.worldY + f7), paint);
            } else {
                graphicsEngine.a(clampX(pointWorldToScreen5.worldX - (this.width * fClampTo255)), clampY(pointWorldToScreen5.worldY), clampX(pointWorldToScreen5.worldX + (this.width * fClampTo255)), clampY(pointWorldToScreen5.worldY), paint);
                graphicsEngine.a(clampX(pointWorldToScreen5.worldX), clampY(pointWorldToScreen5.worldY - (this.height * fClampTo255)), clampX(pointWorldToScreen5.worldX), clampY(pointWorldToScreen5.worldY + (this.height * fClampTo255)), paint);
            }
            minimapEffect2.intensity = Utility.moveTowardsZero(minimapEffect2.intensity, 0.04f * f);
            if (minimapEffect2.intensity == 0.0f) {
                minimapEffect2.duration = Utility.moveTowardsZero(minimapEffect2.duration, 0.005f * f);
                if (minimapEffect2.duration == 0.0f) {
                    it2.remove();
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void addTrackedUnitMarker(BaseUnit baseUnit) {
        Iterator it = this.trackedUnits.iterator();
        while (it.hasNext()) {
            if (((MinimapTrackedUnit) it.next()).unit == baseUnit) {
                return;
            }
        }
        MinimapTrackedUnit minimapTrackedUnit = new MinimapTrackedUnit(this);
        minimapTrackedUnit.unit = baseUnit;
        this.trackedUnits.add(minimapTrackedUnit);
    }
}
