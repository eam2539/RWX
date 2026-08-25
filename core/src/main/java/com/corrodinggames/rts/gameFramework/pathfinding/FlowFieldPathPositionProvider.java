package com.corrodinggames.rts.gameFramework.pathfinding;

import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.PositionData;
import com.corrodinggames.rts.gameFramework.GameEngine;
import io.github.rwx.geometry.Point;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/h.class */
public class FlowFieldPathPositionProvider extends PathPositionProvider {
    DynamicUnitPath a;
    PositionData b = new PositionData();
    static Point c = new Point();

    public FlowFieldPathPositionProvider(DynamicUnitPath dynamicUnitPath) {
        this.a = dynamicUnitPath;
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.PathPositionProvider
    public PositionData a(BaseUnit baseUnit) {
        PositionData positionDataA = a(baseUnit.posX, baseUnit.posY);
        if (positionDataA == null) {
            return null;
        }
        PositionData positionDataA2 = a(positionDataA.posX, positionDataA.posY);
        if (positionDataA2 == null) {
            return positionDataA;
        }
        PositionData positionDataA3 = a(positionDataA2.posX, positionDataA2.posY);
        if (positionDataA3 == null) {
            return positionDataA2;
        }
        return positionDataA3;
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.PathPositionProvider
    public void d(BaseUnit baseUnit) {
        if (this.a != null) {
            this.a.d();
        }
        GameEngine gameEngine = GameEngine.getInstance();
        float f = gameEngine.viewpointXSnapped;
        float f2 = gameEngine.viewpointYSnapped;
        PositionData positionDataE = e(baseUnit);
        if (positionDataE != null) {
            float f3 = positionDataE.posX;
            float f4 = positionDataE.posY;
            DynamicUnitPath.c.b(-16776961);
            gameEngine.renderGraphicsEngine.a(baseUnit.posX - f, baseUnit.posY - f2, f3 - f, f4 - f2, DynamicUnitPath.c);
            PositionData positionDataB = b(baseUnit);
            if (positionDataB != null) {
                DynamicUnitPath.c.b(-7829368);
                gameEngine.renderGraphicsEngine.a(f3 - f, f4 - f2, positionDataB.posX - f, positionDataB.posY - f2, DynamicUnitPath.c);
            }
        }
        PositionData positionDataA = a(baseUnit);
        if (positionDataA != null) {
            float f5 = positionDataA.posX;
            float f6 = positionDataA.posY;
            DynamicUnitPath.c.b(-256);
            gameEngine.renderGraphicsEngine.a(baseUnit.posX - f, baseUnit.posY - f2, f5 - f, f6 - f2, DynamicUnitPath.c);
        }
    }

    public PositionData e(BaseUnit baseUnit) {
        return a(baseUnit.posX, baseUnit.posY);
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.PathPositionProvider
    public PositionData b(BaseUnit baseUnit) {
        PositionData positionDataA = a(baseUnit.posX, baseUnit.posY);
        if (positionDataA == null) {
            return null;
        }
        return a(positionDataA.posX, positionDataA.posY);
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.PathPositionProvider
    public void c(BaseUnit baseUnit) {
    }

    public PositionData a(float f, float f2) {
        byte bA;
        if (this.a.flowField == null) {
            return null;
        }
        TileMap tileMap = GameEngine.getInstance().tileMap;
        int i = (int) (f * tileMap.tileScaleX);
        int i2 = (int) (f2 * tileMap.tileScaleY);
        if (!tileMap.isInBounds(i, i2) || (bA = this.a.a(i, i2)) == 0) {
            return null;
        }
        DynamicUnitPath.a(bA, c);
        int i3 = i - c.worldX;
        int i4 = i2 - c.worldY;
        this.b.posX = (i3 * tileMap.tileWorldSizeX) + tileMap.halfTileWorldSizeX;
        this.b.posY = (i4 * tileMap.tileWorldSizeY) + tileMap.halfTileWorldSizeY;
        return this.b;
    }
}
