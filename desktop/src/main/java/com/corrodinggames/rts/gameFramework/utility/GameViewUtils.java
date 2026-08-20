package com.corrodinggames.rts.gameFramework.utility;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapTile;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.game.units.air.AirUnit;
import com.corrodinggames.rts.game.units.sea.WaterUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GamePaint;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.path.PathEngine;
import com.corrodinggames.rts.gameFramework.pathfinding.PathCostMap;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.utility.y */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/utility/y.class */
public final class GameViewUtils {
    static final Paint a = new Paint();
    static final RectF b = new RectF();
    static ArrayList<DebugDrawItem> c = new ArrayList();
    static final Rect d;
    static final RectF e;
    static Paint f;
    static PaintCache[] g;
    static boolean h;

    static {
        a.a(205, 255, 0, 0);
        a.a(Paint.Style.STROKE);
        d = new Rect();
        e = new RectF();
        f = new Paint();
        g = new PaintCache[30];
        h = false;
    }

    public static void a(BaseUnit baseUnit, float f2) {
        a(baseUnit, f2, false, false);
    }

    public static void a(BaseUnit baseUnit, float f2, boolean z) {
        a(baseUnit, f2, z, false);
    }

    public static boolean a(BaseUnit baseUnit) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (baseUnit.isSelected && gameEngine.gameUI.getSelectedUnitCount() == 1 && !gameEngine.gameUI.interfaceRenderer.isSelecting) {
            return true;
        }
        return false;
    }

    public static void a(BaseUnit baseUnit, float f2, boolean z, boolean z2) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (a(baseUnit) || z) {
            float f3 = baseUnit.posX - gameEngine.viewpointXSnapped;
            float f4 = baseUnit.posY - gameEngine.viewpointYSnapped;
            Paint paint = BaseUnit.dg;
            if (z2) {
                paint = BaseUnit.dh;
            }
            gameEngine.renderGraphicsEngine.a(f3, f4, f2, paint);
        }
    }

    public static void a(BaseUnit baseUnit, float f2, int i, int i2, boolean z) {
        GameEngine gameEngine = GameEngine.getInstance();
        if ((baseUnit.isSelected && gameEngine.gameUI.getSelectedUnitCount() < 10) || z) {
            float f3 = baseUnit.posX - gameEngine.viewpointXSnapped;
            float f4 = baseUnit.posY - gameEngine.viewpointYSnapped;
            Paint paint = BaseUnit.dk;
            paint.b(i);
            paint.a(i2);
            gameEngine.renderGraphicsEngine.a(f3, f4, f2, paint);
        }
    }

    public static void b(BaseUnit baseUnit, float f2, boolean z) {
        a(baseUnit, f2, z, BaseUnit.di);
    }

    public static void a(BaseUnit baseUnit, float f2, boolean z, Paint paint) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (a(baseUnit) || z) {
            gameEngine.renderGraphicsEngine.a(baseUnit.posX - gameEngine.viewpointXSnapped, baseUnit.posY - gameEngine.viewpointYSnapped, f2, paint);
        }
    }

    public static void a(Texture texture, float f2, float f3, float f4, float f5, float f6, Paint paint, int i, int i2, int i3) {
        GameEngine gameEngine = GameEngine.getInstance();
        int i4 = 0 + (i3 * i);
        d.a(i4, 0, i4 + i, 0 + i2);
        float f7 = f6 * 0.5f;
        float f8 = f3 - f4;
        float f9 = i * f7;
        float f10 = i2 * f7;
        e.a(f2 - f9, f8 - f10, f2 + f9, f8 + f10);
        GraphicsEngine graphicsEngine = gameEngine.renderGraphicsEngine;
        graphicsEngine.k();
        graphicsEngine.a(f5 + 90.0f, f2, f8);
        if (f6 != 1.0f) {
            graphicsEngine.a(f6, f6, f2, f8);
        }
        graphicsEngine.a(texture, d, e, paint);
        graphicsEngine.l();
    }

    public static boolean a(BaseUnit baseUnit, boolean z, boolean z2) {
        if (baseUnit.canTransportUnits() && z2) {
            return false;
        }
        if ((z && ((baseUnit instanceof AirUnit) || (baseUnit instanceof WaterUnit))) || baseUnit.bI()) {
            return false;
        }
        if ((z && (baseUnit.isWaterUnit() || baseUnit.isAirborne())) || baseUnit.P() || baseUnit.transportContainer != null || baseUnit.parentEntity != null) {
            return false;
        }
        return true;
    }

    public static Paint a() {
        GamePaint gamePaint = new GamePaint();
        if (GameEngine.getInstance().settingsEngine.renderAntiAlias) {
            gamePaint.a(true);
            gamePaint.d(true);
            gamePaint.b(true);
        } else {
            gamePaint.a(false);
            gamePaint.d(false);
            gamePaint.b(false);
        }
        return gamePaint;
    }

    public static GamePaint b() {
        GamePaint gamePaint = new GamePaint();
        gamePaint.a(false);
        gamePaint.d(false);
        gamePaint.b(false);
        return gamePaint;
    }

    public static void a(OrderableUnit orderableUnit) {
        if (!orderableUnit.isDead) {
            int techLevel = orderableUnit.getTechLevel();
            for (int i = 0; i < techLevel; i++) {
                a(orderableUnit, i);
            }
        }
    }

    public static void a(OrderableUnit orderableUnit, Texture texture, float f2, int i) {
        if (!orderableUnit.isDead && f2 != 0.0f) {
            GameEngine gameEngine = GameEngine.getInstance();
            Vector3D vector3DD = orderableUnit.D(i);
            gameEngine.renderGraphicsEngine.k();
            gameEngine.renderGraphicsEngine.b(vector3DD.a - gameEngine.viewpointXSnapped, ((vector3DD.b - vector3DD.c) - orderableUnit.posZ) - gameEngine.viewpointYSnapped);
            gameEngine.renderGraphicsEngine.a(f2, f2);
            gameEngine.renderGraphicsEngine.a(texture, 0.0f, 0.0f, (Paint) null);
            gameEngine.renderGraphicsEngine.l();
        }
    }

    public static void a(OrderableUnit orderableUnit, int i) {
        Texture textureD = orderableUnit.d(i);
        if (textureD == null) {
            return;
        }
        float fP = orderableUnit.p(i);
        Paint renderPaint = orderableUnit.getRenderPaint();
        GameEngine gameEngine = GameEngine.getInstance();
        Vector3D vector3DF = orderableUnit.F(i);
        float f2 = vector3DF.a - GameEngine.getInstance().viewpointXSnapped;
        float f3 = ((vector3DF.b - GameEngine.getInstance().viewpointYSnapped) - orderableUnit.posZ) - vector3DF.c;
        GraphicsEngine graphicsEngine = gameEngine.renderGraphicsEngine;
        graphicsEngine.k();
        if (fP != 1.0f) {
            graphicsEngine.a(fP, fP, f2, f3);
        }
        graphicsEngine.a(orderableUnit.movementLevels[i].targetX + 90.0f, f2, f3);
        graphicsEngine.b(textureD, (f2 - textureD.t) - orderableUnit.h(i), (f3 - textureD.u) - orderableUnit.i(i), renderPaint);
        graphicsEngine.l();
    }

    public static boolean a(BaseUnit baseUnit, float f2, float f3) {
        return !a(f2, f3, baseUnit.getMovementType());
    }

    public static boolean a(float f2, float f3, UnitMovementType unitMovementType) {
        PathEngine pathEngine = GameEngine.getInstance().pathfindingEngine;
        TileMap tileMap = GameEngine.getInstance().tileMap;
        tileMap.setCursorTileIndexFromWorldPoint(f2, f3);
        return pathEngine.isTileBlockedForMovement(unitMovementType, tileMap.cursorTileX, tileMap.cursorTileY);
    }

    public static short b(float f2, float f3, UnitMovementType unitMovementType) {
        PathEngine pathEngine = GameEngine.getInstance().pathfindingEngine;
        TileMap tileMap = GameEngine.getInstance().tileMap;
        PathCostMap pathCostMapA = pathEngine.a(unitMovementType);
        if (pathCostMapA.g == null) {
            return (short) -3;
        }
        tileMap.setCursorTileIndexFromWorldPoint(f2, f3);
        int i = tileMap.cursorTileX;
        int i2 = tileMap.cursorTileY;
        if (!tileMap.isInBounds(i, i2)) {
            return (short) -2;
        }
        return pathCostMapA.g[(i * pathCostMapA.c) + i2];
    }

    public static int c(float f2, float f3, UnitMovementType unitMovementType) {
        short sB = b(f2, f3, unitMovementType);
        if (sB == -3 || sB == -2 || sB == -1 || sB == 0) {
            return 0;
        }
        Integer num = (Integer) GameEngine.getInstance().pathfindingEngine.a(unitMovementType).h.get(Short.valueOf(sB));
        if (num == null) {
            GameEngine.logColored("Could not find groupSize for:" + ((int) sB) + " at X:" + f2 + " y:" + f3);
            return 0;
        }
        return num.intValue();
    }

    public static boolean a(float f2, float f3) {
        TileMap tileMap = GameEngine.getInstance().tileMap;
        if (tileMap == null) {
            GameEngine.log("isInMap called without map loaded");
            return false;
        }
        return tileMap.isInBounds((int) (f2 * tileMap.tileScaleX), (int) (f3 * tileMap.tileScaleY));
    }

    public static boolean b(float f2, float f3) {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        if (tileMap == null) {
            GameEngine.log("isOverClift called without map loaded");
            return false;
        }
        return gameEngine.pathfindingEngine.b((int) (f2 * tileMap.tileScaleX), (int) (f3 * tileMap.tileScaleY));
    }

    public static boolean c(float f2, float f3) {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        if (tileMap == null) {
            GameEngine.log("isOverWater called without map loaded");
            return false;
        }
        return gameEngine.pathfindingEngine.a((int) (f2 * tileMap.tileScaleX), (int) (f3 * tileMap.tileScaleY));
    }

    public static boolean d(float f2, float f3) {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        if (tileMap == null) {
            GameEngine.log("isOverLiquid called without map loaded");
            return false;
        }
        MapTile tileAtWorldPoint = tileMap.getTileAtWorldPoint(f2, f3);
        if (tileAtWorldPoint == null) {
            return false;
        }
        if (tileAtWorldPoint.isWater || tileAtWorldPoint.isLava) {
            return true;
        }
        return gameEngine.pathfindingEngine.a((int) (f2 * tileMap.tileScaleX), (int) (f3 * tileMap.tileScaleY));
    }

    public static final Paint a(int i, int i2, int i3, int i4, Paint.Style style) {
        return a(Utility.packArgb(i, i2, i3, i4), style);
    }

    public static final Paint a(int i, Paint.Style style) {
        for (int i2 = 0; i2 < g.length; i2++) {
            if (g[i2] == null) {
                PaintCache paintCache = new PaintCache(i, style);
                g[i2] = paintCache;
                return paintCache.c;
            }
            PaintCache paintCache2 = g[i2];
            if (paintCache2.a == i && paintCache2.b == style) {
                return paintCache2.c;
            }
        }
        if (!h) {
            h = true;
            GameEngine.logColored("----- getCachingPaint --- Paint fallback was needed!!");
        }
        f.b(i);
        f.a(style);
        return f;
    }

    public static void a(float f2) {
        if (c.size() == 0) {
            return;
        }
        Iterator it = c.iterator();
        while (it.hasNext()) {
            DebugDrawItem debugDrawItem = (DebugDrawItem) it.next();
            if (debugDrawItem.e <= 0.0f) {
                it.remove();
            } else {
                debugDrawItem.e -= f2;
                if (f2 == 0.0f && debugDrawItem.e < 1.0f) {
                    debugDrawItem.e = -1.0f;
                }
            }
        }
    }

    public static void b(float f2) {
        if (c.size() == 0) {
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        for (DebugDrawItem debugDrawItem : c) {
            float f3 = debugDrawItem.b.a;
            float f4 = debugDrawItem.b.b;
            float f5 = debugDrawItem.b.c;
            float f6 = debugDrawItem.b.d;
            if (debugDrawItem.d) {
                f3 -= GameEngine.getInstance().viewpointXSnapped;
                f4 -= GameEngine.getInstance().viewpointYSnapped;
                f5 -= GameEngine.getInstance().viewpointXSnapped;
                f6 -= GameEngine.getInstance().viewpointYSnapped;
            }
            if (debugDrawItem.c) {
                gameEngine.renderGraphicsEngine.a(f3, f4, f5, f6, debugDrawItem.a);
            } else {
                if (debugDrawItem.d) {
                }
                gameEngine.renderGraphicsEngine.a(debugDrawItem.b, debugDrawItem.a);
            }
            if (debugDrawItem.f != null) {
                gameEngine.renderGraphicsEngine.i();
                gameEngine.restoreZoomTransform();
                float f7 = f5;
                float f8 = f6;
                if (debugDrawItem.d) {
                    f7 *= gameEngine.zoom;
                    f8 *= gameEngine.zoom;
                }
                gameEngine.renderGraphicsEngine.a(debugDrawItem.f, f7, f8, debugDrawItem.a);
                gameEngine.renderGraphicsEngine.j();
            }
        }
    }

    public static final boolean a(int i, int i2) {
        int i3 = GameEngine.getInstance().gameTimeMillis;
        if (i + i2 < i3 || i3 < i - 1000) {
            return true;
        }
        return false;
    }

    public static final boolean b(int i, int i2) {
        int i3 = GameEngine.getInstance().gameTimeMillis;
        if (i >= 0 && i + i2 >= i3 && i <= i3) {
            return true;
        }
        return false;
    }

    public static boolean a(float f2, float f3, float f4, float f5, UnitMovementType unitMovementType) {
        if (unitMovementType == UnitMovementType.AIR || unitMovementType == UnitMovementType.NONE) {
            return true;
        }
        short sB = b(f2, f3, unitMovementType);
        short sB2 = b(f4, f5, unitMovementType);
        if (sB == -3 || sB2 == -3) {
            String strName = "null";
            if (unitMovementType != null) {
                strName = unitMovementType.name();
            }
            GameEngine.logWarningAndStack("pathPossible: no isolatedGroups found! (" + strName + ")");
        }
        if (sB != -1 && sB2 != -1 && sB != -2 && sB2 != -2 && sB == sB2) {
            return true;
        }
        return false;
    }

    public static boolean b(BaseUnit baseUnit, float f2, float f3) {
        return a(baseUnit.posX, baseUnit.posY, f2, f3, baseUnit.getMovementType());
    }

    public static void a(PlayerTeam playerTeam, PointF pointF) {
        GameEngine gameEngine = GameEngine.getInstance();
        for (int i = 0; i <= 2; i++) {
            for (BaseUnit baseUnit : BaseUnit.getGlobalUnitList()) {
                if ((baseUnit instanceof BaseUnit) && !baseUnit.isDead && baseUnit.team == playerTeam) {
                    if (i == 0 && baseUnit.changeTeam) {
                        pointF.a(baseUnit.posX, baseUnit.posY);
                        return;
                    } else if (i == 1 && baseUnit.isTargetable) {
                        pointF.a(baseUnit.posX, baseUnit.posY);
                        return;
                    } else if (i == 2) {
                        pointF.a(baseUnit.posX, baseUnit.posY);
                        return;
                    }
                }
            }
        }
        pointF.a(gameEngine.tileMap.getWorldWidth() / 2.0f, gameEngine.tileMap.getWorldHeight() / 2.0f);
    }

    public static void a(BaseUnit baseUnit, OrderableUnit orderableUnit) {
        baseUnit.transportContainer = null;
        if (baseUnit instanceof OrderableUnit) {
            OrderableUnit orderableUnit2 = (OrderableUnit) baseUnit;
            if (orderableUnit2.parentEntity == orderableUnit) {
                if (orderableUnit2.dn() == null) {
                    GameEngine.log("Unload, attachment data is null");
                }
                orderableUnit2.bx();
            }
        }
    }
}
