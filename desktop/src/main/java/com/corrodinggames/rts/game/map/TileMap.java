package com.corrodinggames.rts.game.map;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.ScorchMark;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.Tree;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.buildings.BaseBuilding;
import com.corrodinggames.rts.game.units.custom.hooks.AttachmentSlotDefinition;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.mission.MissionEngine;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FileLoaderFactory;
import com.corrodinggames.rts.gameFramework.utility.IFileLoader;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/b.class */
public final class TileMap {

    /* JADX INFO: renamed from: f */
    static boolean fogAtlasLockDisabled;

    /* JADX INFO: renamed from: l */
    public static TileAtlasCache fogTileAtlasCacheFullScale;

    /* JADX INFO: renamed from: m */
    public static TileAtlasCache fogTileAtlasCacheHalfScale;

    /* JADX INFO: renamed from: n */
    public int tileWorldSizeX;

    /* JADX INFO: renamed from: o */
    public int tileWorldSizeY;

    /* JADX INFO: renamed from: p */
    public int halfTileWorldSizeX;

    /* JADX INFO: renamed from: q */
    public int halfTileWorldSizeY;

    /* JADX INFO: renamed from: r */
    public float tileScaleX;

    /* JADX INFO: renamed from: s */
    public float tileScaleY;

    /* JADX INFO: renamed from: x */
    public MapLayer groundOverlayLayer;

    /* JADX INFO: renamed from: C */
    public int tileCountX;

    /* JADX INFO: renamed from: D */
    public int tileCountY;

    /* JADX INFO: renamed from: K */
    public static Texture fogSmoothAtlasTexture;

    /* JADX INFO: renamed from: L */
    public static GraphicsEngine fogAtlasRenderer;

    /* JADX INFO: renamed from: M */
    public byte[][] fogOfWarCurrent;

    /* JADX INFO: renamed from: N */
    public byte[][] fogOfWarNext;

    /* JADX INFO: renamed from: Q */
    public MapObjectLayer objectsLayer;

    /* JADX INFO: renamed from: R */
    public boolean editorActive;

    /* JADX INFO: renamed from: S */
    public boolean editorSelectionActive;

    /* JADX INFO: renamed from: T */
    public int cursorTileX;

    /* JADX INFO: renamed from: U */
    public int cursorTileY;

    /* JADX INFO: renamed from: W */
    public boolean isCursorActive;

    /* JADX INFO: renamed from: X */
    public boolean isCursorSelectionActive;

    /* JADX INFO: renamed from: Y */
    public int cursorStartTileX;

    /* JADX INFO: renamed from: Z */
    public int cursorStartTileY;

    /* JADX INFO: renamed from: ab */
    Paint debugRedStrokePaint;

    /* JADX INFO: renamed from: ac */
    Paint placementValidStrokePaint;

    /* JADX INFO: renamed from: ad */
    Paint placementValidHoverStrokePaint;

    /* JADX INFO: renamed from: ae */
    Paint placementInvalidStrokePaint;

    /* JADX INFO: renamed from: af */
    Paint placementInvalidFillPaint;

    /* JADX INFO: renamed from: ag */
    Paint clearXferPaint;

    /* JADX INFO: renamed from: ah */
    HashMap gidToMapTileCache;

    /* JADX INFO: renamed from: ai */
    float fogFadeSpeed;

    /* JADX INFO: renamed from: ap */
    long fogUpdateTimeAccumulated;

    /* JADX INFO: renamed from: aq */
    float fogProfilingSecondsAccumulator;

    /* JADX INFO: renamed from: ar */
    float fogMaintenanceElapsedSeconds;

    /* JADX INFO: renamed from: a */
    static final boolean fogProfilingEnabled = false;
    static final boolean b = false;
    static final boolean c = false;

    /* JADX INFO: renamed from: d */
    public static boolean fogDebugGlobalFlag = false;

    /* JADX INFO: renamed from: e */
    static ReentrantLock fogAtlasLock = new ReentrantLock();

    /* JADX INFO: renamed from: g */
    static Paint fogAtlasDebugWhiteStrokePaint = new Paint();

    /* JADX INFO: renamed from: h */
    static Paint fogAtlasDebugRedStrokePaint = new Paint();

    /* JADX INFO: renamed from: i */
    static Paint fogAtlasDebugGreenStrokePaint = new Paint();

    /* JADX INFO: renamed from: j */
    static Paint fogAtlasDebugRedStrokePaintAlt = new Paint();

    /* JADX INFO: renamed from: H */
    public static boolean softFogFadingInitialized = false;

    /* JADX INFO: renamed from: I */
    public static boolean softFogFadingEnabled = false;

    /* JADX INFO: renamed from: J */
    public static boolean softFogFadingForced = false;

    /* JADX INFO: renamed from: al */
    public static LayerBufferManager layerBufferManager = new LayerBufferManager();

    /* JADX INFO: renamed from: k */
    boolean[] androidKeyStates = new boolean[SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_CONTENTS_MENU];

    /* JADX INFO: renamed from: t */
    public ArrayList<Tileset> tilesets = new ArrayList();

    /* JADX INFO: renamed from: u */
    public MapLayer groundLayer = null;

    /* JADX INFO: renamed from: v */
    public MapLayer groundDetailsLayer = null;

    /* JADX INFO: renamed from: w */
    public MapLayer groundDetails2Layer = null;

    /* JADX INFO: renamed from: y */
    public MapLayer pathingOverrideLayer = null;

    /* JADX INFO: renamed from: z */
    public ArrayList<MapLayer> mapLayers = new ArrayList();

    /* JADX INFO: renamed from: A */
    public ArrayList<Point> unitObjects = new ArrayList();

    /* JADX INFO: renamed from: as */
    private int nextUniqueTileIndex = 1;

    /* JADX INFO: renamed from: B */
    public MapTile[] uniqueTiles = new MapTile[0];

    /* JADX INFO: renamed from: E */
    public boolean fogEnabled = true;

    /* JADX INFO: renamed from: F */
    public boolean fogPeriodicMaintenanceEnabled = false;

    /* JADX INFO: renamed from: G */
    public boolean fogRenderActive = false;

    /* JADX INFO: renamed from: O */
    Rect tempRect = new Rect();

    /* JADX INFO: renamed from: P */
    protected ArrayList<MapObjectLayer> objectLayers = new ArrayList();

    /* JADX INFO: renamed from: V */
    public PointF tempWorldPoint = new PointF();

    /* JADX INFO: renamed from: aa */
    float fogBlendAlpha = 0.0f;

    /* JADX INFO: renamed from: aj */
    float fogScale = 1.0f;

    /* JADX INFO: renamed from: ak */
    int fogFadeStep = 0;

    /* JADX INFO: renamed from: am */
    Paint fogOverlayPaint = new Paint();

    /* JADX INFO: renamed from: an */
    Rect tempTileRect = new Rect();

    /* JADX INFO: renamed from: ao */
    Rect tempRectTile = new Rect();

    /* JADX INFO: renamed from: a */
    public static void acquireFogAtlasLock() {
        if (fogAtlasLockDisabled) {
            return;
        }
        fogAtlasLock.lock();
    }

    /* JADX INFO: renamed from: b */
    public static void releaseFogAtlasLock() {
        if (fogAtlasLockDisabled) {
            return;
        }
        fogAtlasLock.unlock();
    }

    /* JADX INFO: renamed from: c */
    public static void buildFogSmoothAtlas() {
        GameEngine gameEngine = GameEngine.getInstance();
        fogAtlasDebugWhiteStrokePaint.a(150, 255, 255, 255);
        fogAtlasDebugWhiteStrokePaint.a(Paint.Style.STROKE);
        fogAtlasDebugWhiteStrokePaint.a(1.0f);
        gameEngine.updatePaintTextSize(fogAtlasDebugWhiteStrokePaint, 16.0f);
        fogAtlasDebugRedStrokePaint.a(150, 255, 0, 0);
        fogAtlasDebugRedStrokePaint.a(Paint.Style.STROKE);
        fogAtlasDebugRedStrokePaint.a(1.0f);
        fogAtlasDebugGreenStrokePaint.a(150, 0, 255, 0);
        fogAtlasDebugGreenStrokePaint.a(Paint.Style.STROKE);
        fogAtlasDebugGreenStrokePaint.a(1.0f);
        fogAtlasDebugRedStrokePaintAlt.a(150, 255, 0, 0);
        long jA = PerformanceProfiler.a();
        Texture textureA = gameEngine.renderGraphicsEngine.a(R.drawable.fog_smooth);
        fogSmoothAtlasTexture = gameEngine.renderGraphicsEngine.b(((20 + 2) * 16) + 1, ((20 + 2) * 16) + 1, true);
        fogSmoothAtlasTexture.m = true;
        fogSmoothAtlasTexture.b(true);
        fogAtlasRenderer = gameEngine.renderGraphicsEngine.b(fogSmoothAtlasTexture);
        Texture textureB = gameEngine.renderGraphicsEngine.b(20 + 1, 20 + 1, true);
        GraphicsEngine graphicsEngineB = gameEngine.renderGraphicsEngine.b(textureB);
        composeFogPattern(singletonMask(1), 2, 5, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(singletonMask(2), 0, 5, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(singletonMask(4), 0, 3, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(singletonMask(8), 2, 3, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations(16, 1, 2), 1, 0, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations(32, 2, 4), 2, 1, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations(64, 8, 4), 1, 2, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations(-128, 1, 8), 0, 1, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations(16 + 32, 2, 1, 4), 2, 0, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations(32 + 64, 4, 8, 2), 2, 2, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations(64 - 128, 8, 4, 1), 0, 2, true, textureB, graphicsEngineB, textureA);
        composeFogPattern(enumerateMaskCombinations((-128) + 16, 1, 8, 2), 0, 0, true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(1 + 2), getByteOrDefault(2, 5, 0, 5), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(2 + 4), getByteOrDefault(0, 5, 0, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(4 + 8), getByteOrDefault(0, 3, 2, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(8 + 1), getByteOrDefault(2, 3, 2, 5), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(16 + 32 + 64, 1, 2, 4, 8), getByteOrDefault(2, 0, 2, 2), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations((32 + 64) - 128, 1, 2, 4, 8), getByteOrDefault(2, 2, 0, 2), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations((64 - 128) + 16, 1, 2, 4, 8), getByteOrDefault(0, 2, 0, 0), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations((-128) + 16 + 32, 1, 2, 4, 8), getByteOrDefault(0, 0, 2, 0), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(16 + 64), getByteOrDefault(1, 0, 1, 2), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask((-128) + 32), getByteOrDefault(0, 1, 2, 1), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(1 + 4), getByteOrDefault(2, 5, 0, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(2 + 8), getByteOrDefault(0, 5, 2, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(16 + 4, 2, 1), getByteOrDefault(1, 0, 0, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(64 + 2, 4, 8), getByteOrDefault(1, 2, 0, 5), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations((-128) + 2, 1, 8), getByteOrDefault(0, 1, 0, 5), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(32 + 1, 2, 4), getByteOrDefault(2, 1, 2, 5), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(16 + 8, 2, 1), getByteOrDefault(1, 0, 2, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(64 + 1, 4, 8), getByteOrDefault(1, 2, 2, 5), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations((-128) + 4, 1, 8), getByteOrDefault(0, 1, 0, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(32 + 8, 2, 4), getByteOrDefault(2, 1, 2, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(16 + 4 + 8, 2, 1), getByteOrDefault(1, 0, 0, 3, 2, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(64 + 2 + 1, 4, 8), getByteOrDefault(1, 2, 0, 5, 2, 5), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations((-128) + 2 + 4, 1, 8), getByteOrDefault(0, 1, 2, 5, 2, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(enumerateMaskCombinations(32 + 1 + 8, 2, 4), getByteOrDefault(2, 1, 0, 5, 0, 3), true, textureB, graphicsEngineB, textureA);
        blitFogTiles(singletonMask(-1), getByteOrDefault(1, 4), true, textureB, graphicsEngineB, textureA);
        fogAtlasRenderer.p();
        fogAtlasRenderer.q();
        fogAtlasRenderer = null;
        graphicsEngineB.q();
        PerformanceProfiler.a("smoothFog load took:", jA);
        initSoftFogFading();
        fogTileAtlasCacheFullScale = new TileAtlasCache(1.0f, false);
        fogTileAtlasCacheFullScale.initializeAtlasTexture();
        fogTileAtlasCacheHalfScale = new TileAtlasCache(0.5f, false);
        fogTileAtlasCacheHalfScale.initializeAtlasTexture();
    }

    /* JADX INFO: renamed from: d */
    public static void initSoftFogFading() {
        if (softFogFadingInitialized) {
            return;
        }
        softFogFadingInitialized = true;
        softFogFadingEnabled = GameEngine.getInstance().settingsEngine.softFogFading;
        if (GameEngine.isAndroidPlatform() && Build.VERSION.SDK_INT > 26) {
            long jMaxMemory = Runtime.getRuntime().maxMemory() / 1048576;
            GameEngine.log("MaxHeapSizeInMB:" + jMaxMemory);
            if (jMaxMemory > 200) {
                GameEngine.log("enabling softFades");
                softFogFadingEnabled = true;
            }
        }
    }

    /* JADX INFO: renamed from: b */
    private static int[] singletonMask(int i) {
        return new int[]{i};
    }

    /* JADX INFO: renamed from: a */
    private static int[] enumerateMaskCombinations(int i, int... iArr) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(i));
        if (iArr.length == 1) {
            arrayList.add(Integer.valueOf(i + iArr[0]));
        } else if (iArr.length == 2) {
            arrayList.add(Integer.valueOf(i + iArr[0]));
            arrayList.add(Integer.valueOf(i + iArr[1]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[1]));
        } else if (iArr.length == 3) {
            arrayList.add(Integer.valueOf(i + iArr[0]));
            arrayList.add(Integer.valueOf(i + iArr[1]));
            arrayList.add(Integer.valueOf(i + iArr[2]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[1]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[2]));
            arrayList.add(Integer.valueOf(i + iArr[1] + iArr[2]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[1] + iArr[2]));
        } else if (iArr.length == 4) {
            arrayList.add(Integer.valueOf(i + iArr[0]));
            arrayList.add(Integer.valueOf(i + iArr[1]));
            arrayList.add(Integer.valueOf(i + iArr[2]));
            arrayList.add(Integer.valueOf(i + iArr[3]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[1] + iArr[2] + iArr[3]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[1] + iArr[2]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[1] + iArr[3]));
            arrayList.add(Integer.valueOf(i + iArr[1] + iArr[2] + iArr[3]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[1]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[2]));
            arrayList.add(Integer.valueOf(i + iArr[0] + iArr[3]));
            arrayList.add(Integer.valueOf(i + iArr[1] + iArr[2]));
            arrayList.add(Integer.valueOf(i + iArr[1] + iArr[3]));
            arrayList.add(Integer.valueOf(i + iArr[2] + iArr[3]));
        } else {
            throw new RuntimeException("unhandled:" + iArr.length);
        }
        int[] iArr2 = new int[arrayList.size()];
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            if (arrayList.get(i2) != null) {
                iArr2[i2] = ((Integer) arrayList.get(i2)).intValue();
            }
        }
        return iArr2;
    }

    /* JADX INFO: renamed from: a */
    private static int[] getByteOrDefault(int... iArr) {
        return iArr;
    }

    /* JADX INFO: renamed from: a */
    private static void composeFogPattern(int[] iArr, int i, int i2, boolean z, Texture texture, GraphicsEngine graphicsEngine, Texture texture2) {
        blitFogTiles(iArr, getByteOrDefault(i, i2), z, texture, graphicsEngine, texture2);
    }

    /* JADX INFO: renamed from: a */
    private static void blitFogTiles(int[] iArr, int[] iArr2, boolean z, Texture texture, GraphicsEngine graphicsEngine, Texture texture2) {
        if (z) {
            graphicsEngine.o();
        }
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        Rect rect3 = new Rect();
        Rect rect4 = new Rect();
        rect.a(0, 0, 20, 20);
        for (int i = 0; i < iArr2.length; i += 2) {
            int i2 = iArr2[i + 0] * 20;
            int i3 = iArr2[i + 1] * 20;
            rect2.a(i2, i3, i2 + 20, i3 + 20);
            graphicsEngine.a(texture2, rect2, rect, (Paint) null);
            rect4.a(rect2.c - 1, rect2.b, rect2.c, rect2.d);
            rect3.a(rect.c, rect.b, rect.c + 1, rect.d);
            graphicsEngine.a(texture2, rect4, rect3, (Paint) null);
            rect4.a(rect2.a, rect2.d - 1, rect2.c, rect2.d);
            rect3.a(rect.a, rect.d, rect.c, rect.d + 1);
            graphicsEngine.a(texture2, rect4, rect3, (Paint) null);
        }
        graphicsEngine.p();
        for (int i4 : iArr) {
            renderFogAtlasTile(i4 + 128, texture);
        }
    }

    /* JADX INFO: renamed from: a */
    public static void renderFogAtlasTile(int i, Texture texture) {
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        rect2.a(0, 0, 20, 20);
        computeFogAtlasTileRect(i, rect);
        TileAtlasCache.blitPaddingEdges(fogAtlasRenderer, texture, rect2, rect, (Paint) null);
    }

    /* JADX INFO: renamed from: a */
    public static void computeFogAtlasTileRect(int i, Rect rect) {
        int i2 = ((i % 16) * (20 + 2)) + 1;
        int i3 = (((int) (i * 0.0625f)) * (20 + 2)) + 1;
        rect.a = i2;
        rect.b = i3;
        rect.c = i2 + 20;
        rect.d = i3 + 20;
    }

    /* JADX INFO: renamed from: a */
    public final short registerUniqueTile(MapTile mapTile) {
        if (this.nextUniqueTileIndex >= this.uniqueTiles.length) {
            MapTile[] mapTileArr = new MapTile[Utility.min(this.uniqueTiles.length + 100, 32767)];
            System.arraycopy(this.uniqueTiles, 0, mapTileArr, 0, this.uniqueTiles.length);
            this.uniqueTiles = mapTileArr;
        }
        int i = this.nextUniqueTileIndex;
        if (this.nextUniqueTileIndex < 32766) {
            this.nextUniqueTileIndex++;
        } else {
            GameEngine.logColored("Max unique tile limit reached at: " + this.nextUniqueTileIndex);
        }
        this.uniqueTiles[i] = mapTile;
        return (short) i;
    }

    /* JADX INFO: renamed from: a */
    public final MapTile getUniqueTile(short s) {
        return this.uniqueTiles[s];
    }

    /* JADX INFO: renamed from: a */
    public MapTile getTileVariant(MapTile mapTile, int i, int i2) {
        int length = 0;
        if (mapTile != null && mapTile.randomVariants != null && (((i * 13) + (i2 * 1313)) % (mapTile.randomVariants.length + 1)) - 1 >= 0) {
            return mapTile.randomVariants[length];
        }
        return mapTile;
    }

    /* JADX INFO: renamed from: a */
    public boolean isWorldPointVisibleForTeam(float f, float f2, PlayerTeam playerTeam) {
        if (this.fogEnabled) {
            int i = (int) (f * this.tileScaleX);
            int i2 = (int) (f2 * this.tileScaleY);
            if (playerTeam.fogOfWarData != null && isInBounds(i, i2) && playerTeam.fogOfWarData[i][i2] >= 5) {
                return false;
            }
            return true;
        }
        return true;
    }

    /* JADX INFO: renamed from: a */
    public boolean isTileVisibleForTeam(int i, int i2, PlayerTeam playerTeam) {
        if (this.fogEnabled && playerTeam.fogOfWarData != null && isInBounds(i, i2) && playerTeam.fogOfWarData[i][i2] >= 5) {
            return false;
        }
        return true;
    }

    /* JADX INFO: renamed from: a */
    public void setCursorTileIndexFromWorldPoint(float f, float f2) {
        this.cursorTileX = (int) (f * this.tileScaleX);
        this.cursorTileY = (int) (f2 * this.tileScaleY);
    }

    /* JADX INFO: renamed from: a */
    public void setCursorTileIndexFromTileIndex(int i, int i2) {
        this.cursorTileX = i * this.tileWorldSizeX;
        this.cursorTileY = i2 * this.tileWorldSizeY;
    }

    /* JADX INFO: renamed from: b */
    public void setCursorTileIndexFromTileIndexCentered(int i, int i2) {
        this.cursorTileX = (i * this.tileWorldSizeX) + this.halfTileWorldSizeX;
        this.cursorTileY = (i2 * this.tileWorldSizeY) + this.halfTileWorldSizeY;
    }

    /* JADX INFO: renamed from: a */
    public PointF tileToWorldPoint(Point point) {
        this.tempWorldPoint.a(point.worldX * this.tileWorldSizeX, point.worldY * this.tileWorldSizeY);
        return this.tempWorldPoint;
    }

    /* JADX INFO: renamed from: b */
    public void updateCursorTileIndexFromWorldPoint(float f, float f2) {
        setCursorTileIndexFromWorldPoint(f, f2);
        setCursorTileIndexFromTileIndex(this.cursorTileX, this.cursorTileY);
    }

    /* JADX INFO: renamed from: a */
    public float clampWorldX(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f > getWorldWidth()) {
            f = getWorldWidth();
        }
        return f;
    }

    /* JADX INFO: renamed from: b */
    public float clampWorldY(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f > getWorldHeight()) {
            f = getWorldHeight();
        }
        return f;
    }

    /* JADX INFO: renamed from: c */
    public final boolean isInBounds(int i, int i2) {
        return i >= 0 && i < this.tileCountX && i2 >= 0 && i2 < this.tileCountY;
    }

    /* JADX INFO: renamed from: c */
    public MapTile getTileAtWorldPoint(float f, float f2) {
        int i = (int) (f * this.tileScaleX);
        int i2 = (int) (f2 * this.tileScaleY);
        if (i < 0 || i >= this.tileCountX || i2 < 0 || i2 >= this.tileCountY) {
            return null;
        }
        return this.groundLayer.getTileAt(i, i2);
    }

    /* JADX INFO: renamed from: d */
    public MapTile getTileAt(int i, int i2) {
        if (isInBounds(i, i2)) {
            return this.groundLayer.getTileAt(i, i2);
        }
        return null;
    }

    /* JADX INFO: renamed from: e */
    public MapTile getPathingOverrideTileAt(int i, int i2) {
        if (!isInBounds(i, i2) || this.pathingOverrideLayer == null) {
            return null;
        }
        return this.pathingOverrideLayer.getTileAt(i, i2);
    }

    /* JADX INFO: renamed from: a */
    void convertWorldRectToTileRect(RectF rectF) {
        if (GameEngine.isSpaceGame()) {
            rectF.a *= this.tileWorldSizeX / 20;
            rectF.c *= this.tileWorldSizeX / 20;
            rectF.b *= this.tileWorldSizeY / 20;
            rectF.d *= this.tileWorldSizeY / 20;
        }
    }

    public TileMap() {
        this.tileWorldSizeX = 20;
        this.tileWorldSizeY = 20;
        if (GameEngine.isSpaceGame()) {
            this.tileWorldSizeX = 60;
            this.tileWorldSizeY = 60;
        }
        this.halfTileWorldSizeX = this.tileWorldSizeX / 2;
        this.halfTileWorldSizeY = this.tileWorldSizeY / 2;
        this.tileScaleX = 1.0f / this.tileWorldSizeX;
        this.tileScaleY = 1.0f / this.tileWorldSizeY;
        this.debugRedStrokePaint = new GamePaint();
        this.debugRedStrokePaint.a(100, 255, 0, 0);
        this.debugRedStrokePaint.b(16.0f);
        this.placementValidStrokePaint = new GamePaint();
        this.placementValidStrokePaint.a(Paint.Style.STROKE);
        this.placementValidStrokePaint.a(1.0f);
        this.placementValidStrokePaint.a(255, 0, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PAIRING, 0);
        this.placementValidHoverStrokePaint = new GamePaint();
        this.placementValidHoverStrokePaint.a(Paint.Style.STROKE);
        this.placementValidHoverStrokePaint.a(1.0f);
        this.placementValidHoverStrokePaint.a(100, 0, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_PROG_YELLOW, 0);
        this.placementInvalidStrokePaint = new GamePaint();
        this.placementInvalidStrokePaint.a(Paint.Style.STROKE);
        this.placementInvalidStrokePaint.a(1.0f);
        this.placementInvalidStrokePaint.a(255, 175, 0, 0);
        this.placementInvalidFillPaint = new GamePaint();
        this.placementInvalidFillPaint.a(155, 175, 0, 0);
        this.clearXferPaint = new GamePaint();
        this.clearXferPaint.a(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    /* JADX INFO: renamed from: a */
    public static void writeMapStreamToOutput(String str, GameOutputStream gameOutputStream) throws IOException {
        InputStream inputStreamOpenMapInputStreamWithMovedFallback = openMapInputStreamWithMovedFallback(str);
        if (inputStreamOpenMapInputStreamWithMovedFallback == null) {
            throw new IOException("writeMapStream: Could not find map:" + str);
        }
        int mapFileSize = (int) getMapFileSize(str);
        if (mapFileSize == -1) {
            new IOException("writeMapStream: Failed to get map size");
        }
        if (mapFileSize == 0) {
            new IOException("writeMapStream: Got empty map size");
        }
        GameEngine.log("Sending map stream of size: " + mapFileSize);
        gameOutputStream.writeInputStreamWithLength(inputStreamOpenMapInputStreamWithMovedFallback, mapFileSize);
    }

    /* JADX INFO: renamed from: a */
    public static long getMapFileSize(String str) {
        String str2 = VariableScope.nullOrMissingString + str;
        String strConvertAbstractPath = FileHelper.convertAbstractPath(str2);
        IFileLoader fileLoaderForPath = FileLoaderFactory.getFileLoaderForPath(strConvertAbstractPath);
        if (fileLoaderForPath != null && !strConvertAbstractPath.endsWith(".rwmod")) {
            long size = fileLoaderForPath.getSize(strConvertAbstractPath, false);
            if (size == -1) {
            }
            return size;
        }
        if (FileHelper.isAbstractPath(str2)) {
            try {
                return GameEngine.getInstance().appContext.d().b(strConvertAbstractPath).getLength();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new File(strConvertAbstractPath).length();
    }

    /* JADX INFO: renamed from: b */
    public static InputStream openMapInputStreamWithMovedFallback(String str) throws IOException {
        InputStream inputStreamOpenAssetStream;
        InputStream inputStreamOpenAssetStream2 = openAssetStream(str);
        if (inputStreamOpenAssetStream2 == null && (inputStreamOpenAssetStream = openAssetStream(str.replace(".tmx", VariableScope.nullOrMissingString) + "_moved")) != null) {
            String strTrim = Utility.readStreamToString(inputStreamOpenAssetStream).trim();
            GameEngine.log("Found moved map at:" + strTrim);
            inputStreamOpenAssetStream2 = openAssetStream(strTrim);
        }
        return inputStreamOpenAssetStream2;
    }

    /* JADX INFO: renamed from: c */
    public static String resolveAbstractPathIfNotNull(String str) {
        if (str == null) {
            return null;
        }
        return FileHelper.convertAbstractPath(str);
    }

    /* JADX INFO: renamed from: d */
    public static InputStream openAssetStream(String str) {
        String strResolveAbstractPathIfNotNull = resolveAbstractPathIfNotNull(VariableScope.nullOrMissingString + str);
        GameEngine.log("Mapfile: " + strResolveAbstractPathIfNotNull);
        return FileHelper.openFileByPath(strResolveAbstractPathIfNotNull);
    }

    /* JADX INFO: renamed from: a */
    public void writeTmxDocumentToOutput(Document document, OutputStream outputStream) throws TransformerException {
        Transformer transformerNewTransformer = TransformerFactory.newInstance().newTransformer();
        transformerNewTransformer.setOutputProperty("indent", "yes");
        transformerNewTransformer.transform(new DOMSource(document), new StreamResult(outputStream));
    }

    /* JADX INFO: renamed from: a */
    public void rewriteTmxWithUnitObjects(InputStream inputStream, OutputStream outputStream) throws MapLoadException, TransformerException, ParserConfigurationException, SAXException, IOException {
        float f;
        DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
        documentBuilderFactoryNewInstance.setValidating(false);
        DocumentBuilder documentBuilderNewDocumentBuilder = documentBuilderFactoryNewInstance.newDocumentBuilder();
        documentBuilderNewDocumentBuilder.setEntityResolver(new EntityResolver() { // from class: com.corrodinggames.rts.game.b.b.1
            @Override // org.xml.sax.EntityResolver
            public InputSource resolveEntity(String str, String str2) {
                return new InputSource(new ByteArrayInputStream(new byte[0]));
            }
        });
        Document document = documentBuilderNewDocumentBuilder.parse(inputStream);
        Element documentElement = document.getDocumentElement();
        String attribute = documentElement.getAttribute("orientation");
        if (!attribute.equals("orthogonal")) {
            throw new MapLoadException("Only orthogonal maps are supported, found: " + attribute);
        }
        NodeList elementsByTagName = documentElement.getElementsByTagName("SOMETHING");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
        }
        NodeList elementsByTagName2 = documentElement.getElementsByTagName("layer");
        for (int i2 = 0; i2 < elementsByTagName2.getLength(); i2++) {
            Element element = (Element) elementsByTagName2.item(i2);
            if ("units".equalsIgnoreCase(element.getAttribute("name"))) {
                element.getParentNode().removeChild(element);
            }
        }
        NodeList elementsByTagName3 = documentElement.getElementsByTagName("objectgroup");
        for (int i3 = 0; i3 < elementsByTagName3.getLength(); i3++) {
            Element element2 = (Element) elementsByTagName3.item(i3);
            if ("UnitObjects".equalsIgnoreCase(element2.getAttribute("name"))) {
                element2.getParentNode().removeChild(element2);
            }
        }
        Element elementCreateElement = document.createElement("objectgroup");
        elementCreateElement.setAttribute("name", "UnitObjects");
        for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
            if ((baseUnit instanceof BaseUnit) && (!(baseUnit instanceof Tree) || !((Tree) baseUnit).isActive)) {
                if (!baseUnit.isDead && !baseUnit.u()) {
                    AttachmentSlotDefinition attachmentSlotDefinitionDn = baseUnit.dn();
                    if (baseUnit.parentEntity != null && attachmentSlotDefinitionDn != null) {
                        if (!attachmentSlotDefinitionDn.D) {
                        }
                    } else {
                        Element elementCreateElement2 = document.createElement("object");
                        int i4 = 20;
                        if (20 < baseUnit.radius) {
                            i4 = (int) baseUnit.radius;
                        }
                        elementCreateElement2.setAttribute("name", baseUnit.r().getUnitTypeDescriptionShort() + " (t:" + baseUnit.team.teamId + ")");
                        elementCreateElement2.setAttribute("x", VariableScope.nullOrMissingString + (baseUnit.posX - (i4 / 2)));
                        elementCreateElement2.setAttribute("y", VariableScope.nullOrMissingString + (baseUnit.posY - (i4 / 2)));
                        elementCreateElement2.setAttribute("width", VariableScope.nullOrMissingString + i4);
                        elementCreateElement2.setAttribute("height", VariableScope.nullOrMissingString + i4);
                        if (baseUnit.bI()) {
                            f = baseUnit.rotationSpeed;
                        } else {
                            f = baseUnit.rotationSpeed + 90.0f;
                        }
                        elementCreateElement2.setAttribute("rotation", VariableScope.nullOrMissingString + f);
                        Integer numIsTileVisibleForTeam = findTileIdForUnitType(baseUnit.r());
                        if (numIsTileVisibleForTeam != null) {
                            elementCreateElement2.setAttribute("gid", VariableScope.nullOrMissingString + numIsTileVisibleForTeam);
                        }
                        Element elementCreateElement3 = document.createElement("properties");
                        Element elementCreateElement4 = document.createElement("property");
                        elementCreateElement4.setAttribute("name", "unit");
                        elementCreateElement4.setAttribute("value", baseUnit.r().getUnitTypeDescriptionShort());
                        elementCreateElement3.appendChild(elementCreateElement4);
                        Element elementCreateElement5 = document.createElement("property");
                        elementCreateElement5.setAttribute("name", "team");
                        elementCreateElement5.setAttribute("value", VariableScope.nullOrMissingString + baseUnit.team.teamId);
                        elementCreateElement3.appendChild(elementCreateElement5);
                        elementCreateElement2.appendChild(elementCreateElement3);
                        elementCreateElement.appendChild(elementCreateElement2);
                    }
                }
            }
        }
        documentElement.appendChild(elementCreateElement);
        writeTmxDocumentToOutput(document, outputStream);
    }

    /* JADX INFO: renamed from: a */
    public boolean exportMap(String str, String str2) {
        GameEngine gameEngine = GameEngine.getInstance();
        try {
            exportMapToPath(str, str2);
            gameEngine.gameUI.messageManager.addMessage((String) null, "Map exported.");
            return true;
        } catch (MapLoadException e) {
            gameEngine.showMessageBox("Error exporting map", "Failed to export map. error: " + e.getMessage());
            return false;
        } catch (IOException e2) {
            e2.printStackTrace();
            gameEngine.showMessageBox("Error exporting map", "Failed to export map. IO error: " + e2.getMessage());
            return false;
        } catch (NoClassDefFoundError e3) {
            e3.printStackTrace();
            gameEngine.showMessageBox("Error exporting map", "Failed to export map. Class not found: " + e3.getMessage());
            return false;
        }
    }

    /* JADX INFO: renamed from: b */
    public void exportMapToPath(String str, String str2) throws MapLoadException, IOException {
        GameEngine.log(" --- Saving map:" + str + " to: " + str2);
        InputStream inputStreamOpenMapInputStreamWithMovedFallback = openMapInputStreamWithMovedFallback(str);
        if (inputStreamOpenMapInputStreamWithMovedFallback == null) {
            throw new IOException("Could not find orginal map: " + str);
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamOpenMapInputStreamWithMovedFallback);
        String strConvertAbstractPath = FileHelper.convertAbstractPath(str2);
        File parentFile = new File(strConvertAbstractPath).getParentFile();
        if (!FileHelper.fileExists(parentFile.getAbsolutePath())) {
            FileHelper.createDirectory(parentFile.getAbsolutePath());
        }
        if (!FileHelper.isDirectoryNonZip(parentFile.getAbsolutePath())) {
            GameEngine.logColored("Save Map: Could not create parent directory");
        }
        try {
            OutputStream outputStreamOpenOutputStreamByPath = FileHelper.openOutputStreamByPath(strConvertAbstractPath, false);
            if (outputStreamOpenOutputStreamByPath == null) {
                throw new IOException("Failed to get save target:" + strConvertAbstractPath);
            }
            try {
                rewriteTmxWithUnitObjects(bufferedInputStream, outputStreamOpenOutputStreamByPath);
                try {
                    outputStreamOpenOutputStreamByPath.close();
                    bufferedInputStream.close();
                    inputStreamOpenMapInputStreamWithMovedFallback.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                throw new IOException(e2);
            } catch (ParserConfigurationException e3) {
                throw new IOException(e3);
            } catch (TransformerException e4) {
                throw new IOException(e4);
            } catch (SAXException e5) {
                throw new IOException(e5);
            }
        } catch (FileNotFoundException e6) {
            throw new IOException("Failed to open save target:" + strConvertAbstractPath);
        }
    }

    /* JADX INFO: renamed from: a */
    public void loadMap(String str, boolean z) throws MapLoadException, IOException {
        GameEngine.log(" --- Loading map ---");
        InputStream inputStreamOpenMapInputStreamWithMovedFallback = openMapInputStreamWithMovedFallback(str);
        if (inputStreamOpenMapInputStreamWithMovedFallback == null) {
            throw new MapLoadException("Could not find map: " + FileHelper.getFileName(resolveAbstractPathIfNotNull(str)));
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamOpenMapInputStreamWithMovedFallback);
        loadMapFromStream(bufferedInputStream, z);
        try {
            bufferedInputStream.close();
            inputStreamOpenMapInputStreamWithMovedFallback.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: renamed from: e */
    public Tileset getOrCreateTilesetBySource(String str) throws MapLoadException {
        Tileset tileset = null;
        for (Tileset tileset2 : this.tilesets) {
            if (str.equals(tileset2.tilesetSource)) {
                tileset = tileset2;
            }
        }
        if (tileset == null) {
            int i = 1;
            if (this.tilesets.size() > 0) {
                Tileset tileset3 = (Tileset) this.tilesets.get(this.tilesets.size() - 1);
                i = tileset3.firstGid + 100;
                tileset3.setLastGid(i);
            }
            Tileset tileset4 = new Tileset(this, str, i + 1);
            this.tilesets.add(tileset4);
            tileset = tileset4;
        }
        if (tileset.tilesetBitmap == null) {
            tileset.initTextureMetrics();
        }
        return tileset;
    }

    /* JADX INFO: renamed from: a */
    public MapTile getOrCreateTileFromTilesetIndex(String str, int i, int i2) throws MapLoadException {
        Tileset orCreateTilesetBySource = getOrCreateTilesetBySource(str);
        if (this.gidToMapTileCache == null) {
            this.gidToMapTileCache = new HashMap();
        }
        int indexOffsetByPosition = orCreateTilesetBySource.firstGid + orCreateTilesetBySource.getIndexOffsetByPosition(i, i2);
        MapTile mapTile = (MapTile) this.gidToMapTileCache.get(Integer.valueOf(indexOffsetByPosition));
        if (mapTile != null) {
            return mapTile;
        }
        MapTile mapTileCreateTile = MapTile.createTile(this, this.groundLayer, orCreateTilesetBySource, indexOffsetByPosition - orCreateTilesetBySource.firstGid, (short) 0, (short) 0, true);
        this.gidToMapTileCache.put(Integer.valueOf(indexOffsetByPosition), mapTileCreateTile);
        return mapTileCreateTile;
    }

    /* JADX INFO: renamed from: a */
    public void loadMapFromStream(InputStream inputStream, boolean z) throws MapLoadException {
        int iAllocateSlotForTile;
        NodeList elementsByTagName;
        this.unitObjects.clear();
        fogTileAtlasCacheFullScale.clearAtlas();
        fogTileAtlasCacheHalfScale.clearAtlas();
        try {
            GameEngine.log("---- Loading map data ----");
            DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
            documentBuilderFactoryNewInstance.setValidating(false);
            DocumentBuilder documentBuilderNewDocumentBuilder = documentBuilderFactoryNewInstance.newDocumentBuilder();
            documentBuilderNewDocumentBuilder.setEntityResolver(new EntityResolver() { // from class: com.corrodinggames.rts.game.b.b.2
                @Override // org.xml.sax.EntityResolver
                public InputSource resolveEntity(String str, String str2) {
                    return new InputSource(new ByteArrayInputStream(new byte[0]));
                }
            });
            Element documentElement = documentBuilderNewDocumentBuilder.parse(inputStream).getDocumentElement();
            String attribute = documentElement.getAttribute("orientation");
            if (!attribute.equals("orthogonal")) {
                throw new MapLoadException("Only orthogonal maps are supported, found: " + attribute);
            }
            int i = Integer.parseInt(documentElement.getAttribute("width"));
            int i2 = Integer.parseInt(documentElement.getAttribute("height"));
            this.tileCountX = i;
            this.tileCountY = i2;
            GameEngine.log("Map size: " + this.tileCountX + ", " + this.tileCountY);
            this.fogMaintenanceElapsedSeconds = 150.0f;
            if (this.fogEnabled) {
                GameEngine.log("Setting up team fog..");
                for (int i3 = 0; i3 < PlayerTeam.TEAM_NEUTRAL; i3++) {
                    PlayerTeam playerTeamK = PlayerTeam.k(i3);
                    if (playerTeamK != null) {
                        playerTeamK.fogOfWarWidth = this.tileCountX;
                        playerTeamK.fogOfWarHeight = this.tileCountY;
                        playerTeamK.fogOfWarData = new byte[this.tileCountX][this.tileCountY];
                        for (int i4 = 0; i4 < this.tileCountX; i4++) {
                            for (int i5 = 0; i5 < this.tileCountY; i5++) {
                                playerTeamK.fogOfWarData[i4][i5] = 10;
                            }
                        }
                    }
                }
            } else {
                GameEngine.log("No team fog on this map..");
                for (int i6 = 0; i6 < PlayerTeam.TEAM_NEUTRAL; i6++) {
                    PlayerTeam playerTeamK2 = PlayerTeam.k(i6);
                    if (playerTeamK2 != null) {
                        playerTeamK2.fogOfWarData = (byte[][]) null;
                    }
                }
            }
            Element element = (Element) documentElement.getElementsByTagName("properties").item(0);
            if (element != null && (elementsByTagName = element.getElementsByTagName("property")) != null) {
                Properties properties = new Properties();
                for (int i7 = 0; i7 < elementsByTagName.getLength(); i7++) {
                    Element element2 = (Element) elementsByTagName.item(i7);
                    properties.setProperty(element2.getAttribute("name"), element2.getAttribute("value"));
                }
            }
            Tileset tileset = null;
            NodeList elementsByTagName2 = documentElement.getElementsByTagName("tileset");
            for (short s = 0; s < elementsByTagName2.getLength(); s = (short) (s + 1)) {
                Tileset tileset2 = new Tileset(this, (Element) elementsByTagName2.item(s));
                tileset2.firstGlobalTileIndex = s;
                if (tileset != null) {
                    tileset.setLastGid(tileset2.firstGid - 1);
                }
                tileset = tileset2;
                this.tilesets.add(tileset2);
            }
            NodeList elementsByTagName3 = documentElement.getElementsByTagName("layer");
            for (int i8 = 0; i8 < elementsByTagName3.getLength(); i8++) {
                Element element3 = (Element) elementsByTagName3.item(i8);
                String attribute2 = element3.getAttribute("name");
                if (!"set".equalsIgnoreCase(attribute2) && !"set-disabled".equalsIgnoreCase(attribute2)) {
                    MapLayer mapLayer = new MapLayer(this, element3);
                    mapLayer.layerIndex = i8;
                    this.mapLayers.add(mapLayer);
                }
            }
            for (MapLayer mapLayer2 : this.mapLayers) {
                if (mapLayer2.isGroundLayer) {
                    this.groundLayer = mapLayer2;
                }
                if (mapLayer2.name.equalsIgnoreCase("grounddetails")) {
                    this.groundDetailsLayer = mapLayer2;
                }
                if (mapLayer2.name.equalsIgnoreCase("grounddetails2")) {
                    this.groundDetails2Layer = mapLayer2;
                }
                if (mapLayer2.name.equalsIgnoreCase("Items") || mapLayer2.name.equalsIgnoreCase("Objects")) {
                    this.pathingOverrideLayer = mapLayer2;
                }
                if (mapLayer2.name.equalsIgnoreCase("PathingOverride")) {
                    this.groundOverlayLayer = mapLayer2;
                }
            }
            if (this.groundLayer == null) {
                throw new MapLoadException("'Ground' layer was not found in map, this layer is required");
            }
            if (this.uniqueTiles == null || this.uniqueTiles.length == 0) {
                throw new MapLoadException("Invalid map, no tiles have been set");
            }
            if (!GameEngine.isSpaceGame() && !GameEngine.isMapDebugMode()) {
                for (int i9 = 0; i9 < this.tileCountX; i9++) {
                    for (int i10 = 0; i10 < this.tileCountY; i10++) {
                        if (this.groundLayer.getTileAt(i9, i10) == null) {
                            throw new MapLoadException("An empty tile on the Ground layer at " + i9 + "," + i10 + " all tiles must be filled");
                        }
                    }
                }
            }
            if (this.pathingOverrideLayer == null) {
                throw new MapLoadException("'Items' layer was not found in map, this layer is required");
            }
            NodeList elementsByTagName4 = documentElement.getElementsByTagName("objectgroup");
            for (int i11 = 0; i11 < elementsByTagName4.getLength(); i11++) {
                MapObjectLayer mapObjectLayer = new MapObjectLayer((Element) elementsByTagName4.item(i11), this);
                mapObjectLayer.layerIndex = i11;
                this.objectLayers.add(mapObjectLayer);
            }
            Tileset.markAllDescriptorsUnused();
            for (Tileset tileset3 : this.tilesets) {
                if (tileset3.usedInFogLayer) {
                    tileset3.initTextureMetrics();
                }
            }
            Tileset.freeUnusedImages();
            int i12 = 0;
            while (i12 <= 1) {
                for (MapLayer mapLayer3 : this.mapLayers) {
                    if ((mapLayer3 == this.groundLayer) == (i12 == 0)) {
                        mapLayer3.hasAlpha = false;
                        if (mapLayer3.isTerrainLayer) {
                            for (int i13 = 0; i13 < this.tileCountX; i13++) {
                                for (int i14 = 0; i14 < this.tileCountY; i14++) {
                                    MapTile tileAt = mapLayer3.getTileAt(i13, i14);
                                    if (tileAt != null && tileAt.atlasSlotIndex == -2) {
                                        tileAt.atlasSlotIndex = fogTileAtlasCacheFullScale.allocateSlotForTile(tileAt.tileset, tileAt.tilesetLocalIndex);
                                        if (tileAt.atlasSlotIndex >= 0 && (iAllocateSlotForTile = fogTileAtlasCacheHalfScale.allocateSlotForTile(tileAt.tileset, tileAt.tilesetLocalIndex)) != tileAt.atlasSlotIndex) {
                                            throw new RuntimeException("Meta index mismatch: " + iAllocateSlotForTile + " vs " + tileAt.atlasSlotIndex);
                                        }
                                        if (tileAt.atlasSlotIndex < 0) {
                                            mapLayer3.hasAlpha = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                i12++;
            }
            fogTileAtlasCacheFullScale.finalizeAtlas();
            fogTileAtlasCacheHalfScale.finalizeAtlas();
            this.objectsLayer = getObjectLayerByName("triggers");
            MapObject mapObjectFindObjectByName = null;
            if (this.objectsLayer != null) {
                mapObjectFindObjectByName = this.objectsLayer.findObjectByName("map_info");
            }
            boolean z2 = false;
            boolean z3 = false;
            GameEngine gameEngine = GameEngine.getInstance();
            gameEngine.missionEngine = null;
            String str = null;
            String description = null;
            if (mapObjectFindObjectByName != null) {
                String description2 = mapObjectFindObjectByName.getDescription("type");
                description = mapObjectFindObjectByName.getDescription("fog");
                if ("mission".equalsIgnoreCase(description2) || "survival".equalsIgnoreCase(description2) || "challenge".equalsIgnoreCase(description2) || "skirmish".equalsIgnoreCase(description2)) {
                    str = description2;
                } else {
                    GameEngine.logColored("Unknown map type:" + description2);
                }
            } else {
                GameEngine.logColored("Map type not found on mapInfo");
            }
            if (str == null) {
                GameEngine.logColored("Defaulting to skirmish map type");
                str = "skirmish";
            } else {
                GameEngine.logColored("Map type: " + str);
            }
            gameEngine.missionEngine = new MissionEngine();
            gameEngine.missionEngine.a(z);
            if (description != null && !VariableScope.nullOrMissingString.equals(description)) {
                if (!description.equalsIgnoreCase("none")) {
                    z2 = true;
                    if (description.equalsIgnoreCase("los")) {
                        z3 = true;
                    } else if (!description.equalsIgnoreCase("map")) {
                        GameEngine.log("Unknown map fog type: " + description);
                    }
                }
            } else if (GameEngine.isPC() && !gameEngine.isNetworkConnected()) {
                z2 = true;
                if (str != null && str.equalsIgnoreCase("skirmish")) {
                    z3 = true;
                }
            }
            if (!z2) {
                this.fogEnabled = false;
            }
            if (z2 && z3) {
                this.fogPeriodicMaintenanceEnabled = true;
            }
            this.isCursorActive = true;
        } catch (IOException e) {
            throw new MapLoadException("Failed to parse map", e);
        } catch (ParserConfigurationException e2) {
            throw new RuntimeException("Failed to parse map", e2);
        } catch (SAXException e3) {
            GameEngine.log(" --- SAXException: Failed to parse map - " + e3.getMessage() + " ---");
            try {
                GameEngine.log("available:" + inputStream.available());
                inputStream.reset();
                GameEngine.log("after reset:" + inputStream.available());
            } catch (IOException e4) {
                GameEngine.log("-- error writing debug info --");
                e4.printStackTrace();
            }
            throw new MapLoadException("Failed to parse map - " + e3.getMessage(), e3);
        }
    }

    /* JADX INFO: renamed from: e */
    public void noop() {
    }

    /* JADX INFO: renamed from: a */
    public void enqueueScorchMark(ScorchMark scorchMark) {
        if (GameEngine.isNonAndroidVersion && !GameEngine.isPCOrIOSVersion) {
            return;
        }
        layerBufferManager.applyScorchToCells(scorchMark);
    }

    /* JADX INFO: renamed from: a */
    public void renderBuildPlacementOverlay(OrderableUnit orderableUnit, int i, int i2, int i3, int i4, int i5, int i6, GraphicsEngine graphicsEngine, boolean z, int i7) {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        UnitType unitType = gameEngine.gameUI.currentAction.getUnitType();
        UnitMovementType unitMovementTypeO = unitType.o();
        for (int i8 = i; i8 <= i3; i8++) {
            for (int i9 = i2; i9 <= i4; i9++) {
                boolean zA = BaseBuilding.a(orderableUnit, unitType, unitMovementTypeO, i8, i9, i7);
                int i10 = (i8 * tileMap.tileWorldSizeX) - i5;
                int i11 = (i9 * tileMap.tileWorldSizeY) - i6;
                this.tempTileRect.a(i10, i11, (i10 + tileMap.tileWorldSizeX) - 1, (i11 + tileMap.tileWorldSizeY) - 1);
                if (z) {
                    if (zA) {
                        graphicsEngine.b(this.tempTileRect, tileMap.placementValidHoverStrokePaint);
                    } else {
                        graphicsEngine.b(this.tempTileRect, tileMap.placementInvalidFillPaint);
                        graphicsEngine.b(this.tempTileRect, tileMap.placementInvalidStrokePaint);
                    }
                } else if (zA) {
                    graphicsEngine.b(this.tempTileRect, tileMap.placementValidStrokePaint);
                } else {
                    graphicsEngine.b(this.tempTileRect, tileMap.placementInvalidStrokePaint);
                }
            }
        }
    }

    /* JADX INFO: renamed from: f */
    public static void updateLayerBuffers() {
        layerBufferManager.update();
    }

    /* JADX INFO: renamed from: c */
    public void renderFogOverlayAndCursorSelection(float f) {
        layerBufferManager.setRenderScale(f);
    }

    /* JADX INFO: renamed from: g */
    public void invalidateAllLayerCells() {
        layerBufferManager.invalidateAllCells();
    }

    /* JADX INFO: renamed from: d */
    public void updateFogRenderPass(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        boolean zIsAndroidPlatform = GameEngine.isAndroidPlatform();
        if (zIsAndroidPlatform) {
            gameEngine.renderGraphicsEngine.a(fogAtlasLock);
        }
        renderFogOverlayAndCursorSelection(f);
        if (zIsAndroidPlatform) {
            gameEngine.renderGraphicsEngine.b(fogAtlasLock);
        }
        if (this.isCursorSelectionActive) {
            new Rect();
            Rect rect = new Rect();
            int i = this.cursorStartTileX * this.tileWorldSizeX;
            int i2 = this.cursorStartTileY * this.tileWorldSizeY;
            rect.a(i, i2, i + this.tileWorldSizeX, i2 + this.tileWorldSizeY);
            rect.a(-GameEngine.getInstance().viewpointXInt, -GameEngine.getInstance().viewpointYInt);
        }
    }

    /* JADX INFO: renamed from: e */
    public void updateFogLogicFrame(float f) {
        GameEngine.getInstance();
        advanceFogVisibility(f);
    }

    /* JADX INFO: renamed from: h */
    public void clearAllMapData() {
        Iterator it = this.tilesets.iterator();
        while (it.hasNext()) {
            ((Tileset) it.next()).cleanup();
        }
        this.tilesets.clear();
        Iterator it2 = this.mapLayers.iterator();
        while (it2.hasNext()) {
            ((MapLayer) it2.next()).resetLayer();
        }
        this.mapLayers.clear();
        this.objectLayers.clear();
        this.objectsLayer = null;
        layerBufferManager.invalidateAllCells();
    }

    /* JADX INFO: renamed from: a */
    public Tileset findTilesetByGlobalTileId(int i) {
        for (int i2 = 0; i2 < this.tilesets.size(); i2++) {
            Tileset tileset = (Tileset) this.tilesets.get(i2);
            if (tileset.containsGid(i)) {
                return tileset;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: a */
    public Integer findTileIdForUnitType(UnitType unitType) {
        String unitTypeDescriptionShort = unitType.getUnitTypeDescriptionShort();
        Integer numC = c("unit", unitTypeDescriptionShort);
        if (numC == null) {
            numC = c("customUnit", unitTypeDescriptionShort);
        }
        return numC;
    }

    public Integer c(String str, String str2) {
        for (int i = 0; i < this.tilesets.size(); i++) {
            Integer numFindTileIdByProperty = ((Tileset) this.tilesets.get(i)).findTileIdByProperty(str, str2);
            if (numFindTileIdByProperty != null) {
                return numFindTileIdByProperty;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: f */
    public MapObjectLayer getObjectLayerByName(String str) {
        for (MapObjectLayer mapObjectLayer : this.objectLayers) {
            if (str.equalsIgnoreCase(mapObjectLayer.name)) {
                return mapObjectLayer;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: i */
    public float getWorldWidth() {
        return this.tileCountX * this.tileWorldSizeX;
    }

    /* JADX INFO: renamed from: j */
    public float getWorldHeight() {
        return this.tileCountY * this.tileWorldSizeY;
    }

    /* JADX INFO: renamed from: a */
    public void updateFogVisibilityForTeamsAtWorldPoint(float f, float f2, int i, PlayerTeam playerTeam, boolean z) {
        MissionEngine missionEngine;
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.fogEnabled) {
            long jA = 0;
            if (fogProfilingEnabled) {
                jA = PerformanceProfiler.a();
            }
            boolean z2 = true;
            boolean z3 = playerTeam.isTeamVictory;
            if (!gameEngine.isInNetworkOrReplay() && (missionEngine = gameEngine.missionEngine) != null && !missionEngine.a() && !missionEngine.b()) {
                z2 = false;
            }
            if (!z2) {
                updateFogVisibilityForTeamCircle(f, f2, i, playerTeam, z);
            } else {
                for (int i2 = 0; i2 < PlayerTeam.TEAM_NEUTRAL; i2++) {
                    PlayerTeam playerTeamK = PlayerTeam.k(i2);
                    if (playerTeamK != null && (playerTeamK == playerTeam || (!playerTeamK.isTeamSpectator && (playerTeamK.d(playerTeam) || z3)))) {
                        updateFogVisibilityForTeamCircle(f, f2, i, playerTeamK, z);
                    }
                }
            }
            if (fogProfilingEnabled) {
                this.fogUpdateTimeAccumulated += PerformanceProfiler.a() - jA;
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public byte calculateFogNeighborMask(int i, int i2, byte[][] bArr, byte b2) {
        byte b3 = 0;
        int i3 = this.tileCountX;
        int i4 = this.tileCountY;
        if (i >= 1) {
            if (bArr[i - 1][i2] >= b2) {
                b3 = (byte) (0 - 128);
            }
            if (i2 >= 1 && bArr[i - 1][i2 - 1] >= b2) {
                b3 = (byte) (b3 + 1);
            }
            if (i2 < i4 - 1 && bArr[i - 1][i2 + 1] >= b2) {
                b3 = (byte) (b3 + 8);
            }
        }
        if (i2 >= 1) {
            if (bArr[i][i2 - 1] >= b2) {
                b3 = (byte) (b3 + 16);
            }
            if (i < i3 - 1 && bArr[i + 1][i2 - 1] >= b2) {
                b3 = (byte) (b3 + 2);
            }
        }
        if (i < i3 - 1 && bArr[i + 1][i2] >= b2) {
            b3 = (byte) (b3 + 32);
        }
        if (i2 < i4 - 1) {
            if (bArr[i][i2 + 1] >= b2) {
                b3 = (byte) (b3 + 64);
            }
            if (i < i3 - 1 && bArr[i + 1][i2 + 1] >= b2) {
                b3 = (byte) (b3 + 4);
            }
        }
        if (b3 == 127) {
            b3 = -1;
        }
        return b3;
    }

    /* JADX INFO: renamed from: k */
    public void resetFogToInvisible() {
        ensureFogCacheAllocated();
        for (int i = 0; i < this.tileCountX; i++) {
            for (int i2 = 0; i2 < this.tileCountY; i2++) {
                this.fogOfWarCurrent[i][i2] = 0;
                this.fogOfWarNext[i][i2] = 0;
            }
        }
    }

    /* JADX INFO: renamed from: f */
    public void invalidateLayerCellAt(int i, int i2) {
        this.fogOfWarCurrent[i][i2] = 0;
        this.fogOfWarNext[i][i2] = 0;
    }

    /* JADX INFO: renamed from: g */
    public void invalidateLayerCellsAround(int i, int i2) {
        int i3 = i - 1;
        int i4 = i2 - 1;
        if (i3 < 0) {
            i3 = 0;
        }
        if (i4 < 0) {
            i4 = 0;
        }
        int i5 = i + 1;
        int i6 = i2 + 1;
        if (i5 > this.tileCountX - 1) {
            i5 = this.tileCountX - 1;
        }
        if (i6 > this.tileCountY - 1) {
            i6 = this.tileCountY - 1;
        }
        for (int i7 = i3; i7 <= i5; i7++) {
            for (int i8 = i4; i8 <= i6; i8++) {
                if (this.fogOfWarCurrent[i7][i8] != 0) {
                    this.fogOfWarCurrent[i7][i8] = 127;
                }
                if (this.fogOfWarNext[i7][i8] != 0) {
                    this.fogOfWarNext[i7][i8] = 127;
                }
            }
        }
    }

    /* JADX INFO: renamed from: l */
    public void ensureFogCacheAllocated() {
        boolean z = false;
        if (this.fogOfWarCurrent == null) {
            z = true;
        } else if (this.fogOfWarCurrent.length != this.tileCountX || this.fogOfWarCurrent[0].length != this.tileCountY) {
            GameEngine.log("smoothFog_cache: Size mismatch");
            z = true;
        }
        if (z) {
            GameEngine.log("Building smoothFog_cache");
            this.fogOfWarCurrent = new byte[this.tileCountX][this.tileCountY];
            this.fogOfWarNext = new byte[this.tileCountX][this.tileCountY];
            for (int i = 0; i < this.tileCountX; i++) {
                for (int i2 = 0; i2 < this.tileCountY; i2++) {
                    this.fogOfWarCurrent[i][i2] = 127;
                    this.fogOfWarNext[i][i2] = 127;
                }
            }
        }
    }

    /* JADX INFO: renamed from: b */
    public void updateFogVisibilityForTeamCircle(float f, float f2, int i, PlayerTeam playerTeam, boolean z) {
        byte b2;
        GameEngine gameEngine = GameEngine.getInstance();
        if (this.fogEnabled && playerTeam.fogOfWarData != null) {
            ensureFogCacheAllocated();
            float f3 = (i - 5) * (i - 5);
            float f4 = (i - 3) * (i - 3);
            float f5 = i * i;
            float f6 = (1.0f / (f5 - f4)) * 10.0f;
            setCursorTileIndexFromWorldPoint(f, f2);
            int i2 = this.cursorTileX;
            int i3 = this.cursorTileY;
            float f7 = f * this.tileScaleX;
            float f8 = f2 * this.tileScaleY;
            byte[][] bArr = playerTeam.fogOfWarData;
            int i4 = i - 1;
            int i5 = i2 - i4;
            int i6 = i3 - i4;
            if (i5 < 0) {
                i5 = 0;
            }
            if (i6 < 0) {
                i6 = 0;
            }
            int i7 = i2 + i4;
            int i8 = i3 + i4;
            if (i7 > this.tileCountX - 1) {
                i7 = this.tileCountX - 1;
            }
            if (i8 > this.tileCountY - 1) {
                i8 = this.tileCountY - 1;
            }
            LayerBufferManager layerBufferManager2 = layerBufferManager;
            boolean z2 = false;
            boolean isCurrentPlayerTeam = playerTeam.isCurrentPlayerTeam();
            for (int i9 = i5; i9 <= i7; i9++) {
                for (int i10 = i6; i10 <= i8; i10++) {
                    byte b3 = bArr[i9][i10];
                    if (b3 != 0) {
                        float fDistanceSq = Utility.distanceSq(f7, f8, i9, i10);
                        if (fDistanceSq <= f4) {
                            if (b3 > 0) {
                                bArr[i9][i10] = 0;
                                if (isCurrentPlayerTeam) {
                                    layerBufferManager2.invalidateTileArea(i9, i10, true);
                                    z2 = true;
                                    if (fDistanceSq <= f3 && z) {
                                        invalidateLayerCellAt(i9, i10);
                                    } else {
                                        invalidateLayerCellsAround(i9, i10);
                                    }
                                }
                            }
                        } else if (fDistanceSq <= f5 && b3 > (b2 = (byte) ((fDistanceSq - f4) * f6))) {
                            bArr[i9][i10] = b2;
                            if (isCurrentPlayerTeam) {
                                layerBufferManager2.invalidateTileArea(i9, i10, true);
                                z2 = true;
                                invalidateLayerCellsAround(i9, i10);
                            }
                        }
                    }
                }
            }
            if (z2) {
                gameEngine.minimap.isFogRefreshPending = true;
            }
        }
    }

    /* JADX INFO: renamed from: f */
    public void advanceFogVisibility(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (fogProfilingEnabled) {
            this.fogProfilingSecondsAccumulator += f;
            if (this.fogProfilingSecondsAccumulator > 60.0f) {
                this.fogProfilingSecondsAccumulator = 0.0f;
                if (this.fogUpdateTimeAccumulated > 0) {
                    GameEngine.log("seeThoughFogOfWarTimes: " + PerformanceProfiler.b(this.fogUpdateTimeAccumulated));
                    this.fogUpdateTimeAccumulated = 0L;
                }
                if (this.fogUpdateTimeAccumulated < 0) {
                    GameEngine.log("seeThoughFogOfWarTimes negative: " + PerformanceProfiler.b(this.fogUpdateTimeAccumulated));
                    this.fogUpdateTimeAccumulated = 0L;
                }
            }
        }
        if (this.fogEnabled && this.fogPeriodicMaintenanceEnabled) {
            ensureFogCacheAllocated();
            this.fogMaintenanceElapsedSeconds += f;
            if (this.fogMaintenanceElapsedSeconds > 260.0f) {
                this.fogMaintenanceElapsedSeconds = 0.0f;
                GameObject[] gameObjectArrA = BaseUnit.fastGameObjectList.a();
                int size = GameObject.fastGameObjectList.size();
                boolean z = false;
                for (int i = 0; i < PlayerTeam.TEAM_NEUTRAL; i++) {
                    PlayerTeam playerTeamK = PlayerTeam.k(i);
                    if (playerTeamK != null && !playerTeamK.isTeamWipedOut) {
                        z = true;
                        for (int i2 = 0; i2 < size; i2++) {
                            GameObject gameObject = gameObjectArrA[i2];
                            if (gameObject instanceof OrderableUnit) {
                                OrderableUnit orderableUnit = (OrderableUnit) gameObject;
                                if (orderableUnit.bI()) {
                                    orderableUnit.g(playerTeamK);
                                }
                            }
                        }
                        if (playerTeamK.fogOfWarData == null) {
                            GameEngine.logColored("fogOfWar_map==null for:" + i);
                        }
                        boolean z2 = false;
                        boolean isCurrentPlayerTeam = playerTeamK.isCurrentPlayerTeam();
                        byte[][] bArr = playerTeamK.fogOfWarData;
                        byte[][] bArr2 = this.fogOfWarNext;
                        for (int i3 = 0; i3 < this.tileCountX; i3++) {
                            for (int i4 = 0; i4 < this.tileCountY; i4++) {
                                if (bArr[i3][i4] < 5) {
                                    bArr[i3][i4] = 5;
                                    if (isCurrentPlayerTeam) {
                                        layerBufferManager.invalidateTileArea(i3, i4, true);
                                        z2 = true;
                                        bArr2[i3][i4] = 127;
                                    }
                                }
                            }
                        }
                        if (z2) {
                            gameEngine.minimap.isFogRefreshPending = true;
                        }
                    }
                }
                for (int i5 = 0; i5 < size; i5++) {
                    GameObject gameObject2 = gameObjectArrA[i5];
                    if (gameObject2 instanceof OrderableUnit) {
                        OrderableUnit orderableUnit2 = (OrderableUnit) gameObject2;
                        if (!orderableUnit2.isDead) {
                            orderableUnit2.c(false);
                        }
                    }
                }
                if (z) {
                    for (int i6 = 0; i6 < size; i6++) {
                        GameObject gameObject3 = gameObjectArrA[i6];
                        if (gameObject3 instanceof OrderableUnit) {
                            OrderableUnit orderableUnit3 = (OrderableUnit) gameObject3;
                            if (orderableUnit3.bI()) {
                                orderableUnit3.updateFogOfWarPreview();
                            }
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void writeCursorSelectionPresenceFlag(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeBoolean(false);
    }

    /* JADX INFO: renamed from: a */
    public void readCursorSelectionBlockFromStream(GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.readBoolean()) {
            int i = gameInputStream.readInt();
            int i2 = gameInputStream.readInt();
            for (int i3 = 0; i3 < i; i3++) {
                for (int i4 = 0; i4 < i2; i4++) {
                    gameInputStream.readByte();
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    private InputStream openAssetStreamFromSuffixSegments(String str, String str2, int i) {
        String[] strArrSplit = str2.split("/");
        if (strArrSplit.length >= i) {
            StringBuilder str3 = new StringBuilder(VariableScope.nullOrMissingString);
            boolean z = true;
            for (int length = strArrSplit.length - i; length < strArrSplit.length; length++) {
                if (!z) {
                    str3.append("/");
                }
                z = false;
                str3.append(strArrSplit[length]);
            }
            return FileHelper.openAsset(str + str3);
        }
        return null;
    }

    /* JADX INFO: renamed from: d */
    public InputStream openAssetStreamFromPair(String str, String str2) throws IOException {
        InputStream inputStreamOpenAsset = FileHelper.openAsset(str + str2);
        if (inputStreamOpenAsset == null) {
            inputStreamOpenAsset = openAssetStreamFromSuffixSegments(str, str2, 3);
        }
        if (inputStreamOpenAsset == null) {
            inputStreamOpenAsset = openAssetStreamFromSuffixSegments(str, str2, 2);
        }
        if (inputStreamOpenAsset == null) {
            inputStreamOpenAsset = openAssetStreamFromSuffixSegments(str, str2, 1);
        }
        if (inputStreamOpenAsset == null) {
            throw new IOException("File could not be found:" + str + str2);
        }
        return inputStreamOpenAsset;
    }

    /* JADX INFO: renamed from: a */
    public boolean isTileVisibleForTeam(PlayerTeam playerTeam, int i, int i2) {
        if (!this.fogRenderActive && this.fogEnabled && playerTeam.fogOfWarData != null && isInBounds(i, i2) && playerTeam.fogOfWarData[i][i2] == 10) {
            return false;
        }
        return true;
    }
}
