package com.corrodinggames.rts.game.units;

import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.path.PathEngine;
import com.corrodinggames.rts.gameFramework.pathfinding.PathCostMap;
import io.github.rwx.geometry.Point;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.aq */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/aq.class */
public final class PathfindingUtils {
    public static final KoolPaint a = new KoolPaint();
    static final Point b = new Point();
    static final Rect c = new Rect();
    static final PointF d = new PointF();
    static final PointF e = new PointF();
    static final PointF f = new PointF();
    static final PointF g = new PointF();
    static final PointF h = new PointF();
    static final PointF i = new PointF();
    static final PointF j = new PointF();

    /* JADX INFO: renamed from: a */
    private static Point findPathObstacle(UnitMovementType unitMovementType, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        PathEngine pathEngine = GameEngine.getInstance().pathfindingEngine;
        PathCostMap pathCostMapA = pathEngine.a(unitMovementType);
        int iAbs = Utility.abs(i4 - i2);
        int iAbs2 = Utility.abs(i5 - i3);
        int i9 = i2;
        int i10 = i3;
        int i11 = 1 + iAbs + iAbs2;
        int i12 = i4 > i2 ? 1 : -1;
        int i13 = i5 > i3 ? 1 : -1;
        int i14 = iAbs - iAbs2;
        int i15 = iAbs * 2;
        int i16 = iAbs2 * 2;
        int i17 = 0;
        while (i11 > 0) {
            int i18 = i9;
            int i19 = i10;
            if (i7 != 0 && pathEngine.c(pathCostMapA, i18, i19) < i7) {
                b.a(i18, i19);
                return b;
            }
            if (i6 != 0) {
                int iB = pathEngine.b(pathCostMapA, i18, i19);
                if (iB == -1) {
                    b.a(i18, i19);
                    return b;
                }
                if (i8 > 0) {
                    i8--;
                } else {
                    i17 += iB;
                }
                if (i17 >= i6) {
                    b.a(i18, i19);
                    return b;
                }
            } else if (pathEngine.a(pathCostMapA, i18, i19)) {
                b.a(i18, i19);
                return b;
            }
            if (i14 > 0) {
                i9 += i12;
                i14 -= i16;
            } else if (i14 < 0) {
                i10 += i13;
                i14 += i15;
            } else if (i14 == 0) {
                i9 += i12;
                i10 += i13;
                i14 = (i14 - i16) + i15;
                i11--;
            }
            i11--;
        }
        return null;
    }

    public static boolean a(UnitMovementType unitMovementType, float f2, float f3, float f4, float f5, int i2, int i3, int i4) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.pathfindingEngine.a(gameEngine.pathfindingEngine.a(unitMovementType), true);
        return canReachTargetPrepared(unitMovementType, f2, f3, f4, f5, i2, i3, i4);
    }

    /* JADX INFO: renamed from: b */
    public static boolean canReachTargetPrepared(UnitMovementType unitMovementType, float f2, float f3, float f4, float f5, int i2, int i3, int i4) {
        TileMap tileMap = GameEngine.getInstance().tileMap;
        tileMap.setCursorTileIndexFromWorldPoint(f2, f3);
        int i5 = tileMap.cursorTileX;
        int i6 = tileMap.cursorTileY;
        tileMap.setCursorTileIndexFromWorldPoint(f4, f5);
        return findPathObstacle(unitMovementType, i5, i6, tileMap.cursorTileX, tileMap.cursorTileY, i2, i3, i4) == null;
    }

    public static PointF a(UnitMovementType unitMovementType, float f2, float f3, float f4, float f5, int i2, int i3, boolean z) {
        PathEngine pathEngine = GameEngine.getInstance().pathfindingEngine;
        c.a(i2, i3, i2 + 1, i3 + 1);
        d.a(f2, f3);
        e.a(f4, f5);
        f.a(e);
        byte b2 = -1;
        g.a(c.a, c.d);
        h.a(c.c, c.b);
        i.a(c.a, c.b);
        j.a(c.c, c.d);
        if (d.y < e.y) {
            if ((z || !pathEngine.a(unitMovementType, i2, i3 - 1)) && Utility.replace(d, e, i, h)) {
                b2 = 3;
            }
        } else {
            if ((z || !pathEngine.a(unitMovementType, i2, i3 + 1)) && Utility.replace(d, e, g, j)) {
                b2 = 1;
            }
        }
        if (d.x < e.x) {
            if ((z || !pathEngine.a(unitMovementType, i2 - 1, i3)) && Utility.replace(d, e, i, g)) {
                b2 = 2;
            }
        } else {
            if ((z || !pathEngine.a(unitMovementType, i2 + 1, i3)) && Utility.replace(d, e, h, j)) {
                b2 = 0;
            }
        }
        if (b2 == -1) {
            return null;
        }
        if (b2 == 0) {
            f.x = i2 + 1 + 0.01f;
        }
        if (b2 == 2) {
            f.x = i2 - 0.01f;
        }
        if (b2 == 1) {
            f.y = i3 + 1 + 0.01f;
        }
        if (b2 == 3) {
            f.y = i3 - 0.01f;
        }
        return f;
    }

    public static boolean a(OrderableUnit orderableUnit, BaseUnit baseUnit) {
        if (baseUnit.unitTransportTarget != null || !orderableUnit.canAttackUnitType(baseUnit) || !baseUnit.d((BaseUnit) orderableUnit)) {
            return false;
        }
        return true;
    }

    public static boolean b(OrderableUnit orderableUnit, BaseUnit baseUnit) {
        if (!a(orderableUnit, baseUnit) || !orderableUnit.isWithinEngagementRange(baseUnit) || !orderableUnit.canEngageTargetNow(baseUnit)) {
            return false;
        }
        return true;
    }
}
