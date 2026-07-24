package com.corrodinggames.rts.gameFramework.ui;

import android.graphics.Point;
import com.corrodinggames.rts.game.map.MapTile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.buildings.BaseBuilding;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.f.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/f/j.class */
public class PlacementFinder {

    /* JADX INFO: renamed from: a */
    static Point tempPoint = new Point();

    /* JADX INFO: renamed from: a */
    public static Point findClosestOpenPlacement(int i, int i2, int i3) {
        MapTile pathingOverrideTileAt;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.tileMap.setCursorTileIndexFromWorldPoint(i, i2);
        int i4 = gameEngine.tileMap.cursorTileX;
        int i5 = gameEngine.tileMap.cursorTileY;
        Point point = null;
        float f = -1.0f;
        for (int i6 = i4 - i3; i6 <= i4 + i3; i6++) {
            for (int i7 = i5 - i3; i7 <= i5 + i3; i7++) {
                if (gameEngine.tileMap.isInBounds(i6, i7) && (pathingOverrideTileAt = gameEngine.tileMap.getPathingOverrideTileAt(i6, i7)) != null && pathingOverrideTileAt.isResourcePool) {
                    BaseUnit baseUnitB = BaseBuilding.b(i6, i7);
                    if (baseUnitB != null && !baseUnitB.isVisibleToLocalPlayer()) {
                        baseUnitB = null;
                    }
                    if (baseUnitB == null) {
                        float fDistanceSq = Utility.distanceSq(i4, i5, i6, i7);
                        if (f == -1.0f || f > fDistanceSq) {
                            gameEngine.tileMap.setCursorTileIndexFromTileIndex(i6, i7);
                            tempPoint.a(gameEngine.tileMap.cursorTileX, gameEngine.tileMap.cursorTileY);
                            point = tempPoint;
                            f = fDistanceSq;
                        }
                    }
                }
            }
        }
        return point;
    }
}
