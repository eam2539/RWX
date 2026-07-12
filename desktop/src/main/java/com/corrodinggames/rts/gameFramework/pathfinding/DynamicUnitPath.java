package com.corrodinggames.rts.gameFramework.pathfinding;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import com.corrodinggames.rts.game.map.MapLayer;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.path.PathEngine;

import java.util.LinkedList;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.k.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/k/f.class */
public class DynamicUnitPath extends Path {
    PathPositionProvider a;
    FlowField b;
    static Paint c = new Paint();
    static Point d = new Point();

    public DynamicUnitPath(PathEngine pathEngine, boolean z) {
        super(pathEngine, z);
        this.a = new FlowFieldPathPositionProvider(this);
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.Path
    public PathPositionProvider a(BaseUnit baseUnit) {
        if (a() != null) {
            return this.a;
        }
        return null;
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.Path
    public LinkedList a() {
        return super.a();
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.Path
    public boolean b() {
        return true;
    }

    @Override // com.corrodinggames.rts.gameFramework.pathfinding.Path
    public boolean a(Path path) {
        if (this == path) {
            return true;
        }
        if (!(path instanceof DynamicUnitPath)) {
            return false;
        }
        DynamicUnitPath dynamicUnitPath = (DynamicUnitPath) path;
        if (this.l != dynamicUnitPath.l || this.m != dynamicUnitPath.m || this.o != dynamicUnitPath.o) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.corrodinggames.rts.gameFramework.pathfinding.Path
    public boolean c() {
        return this.x != null;
    }

    public final byte a(int i, int i2) {
        if (this.b == null) {
            return (byte) -1;
        }
        return this.b.a(i, i2);
    }

    public static final void a(byte b, Point point) {
        int i = 0;
        int i2 = 0;
        byte b2 = (byte) (b - 1);
        if (b2 == 0) {
            i = 0 + 1;
        }
        if (b2 == 1) {
            i++;
            i2 = 0 + 1;
        }
        if (b2 == 2) {
            i2++;
        }
        if (b2 == 3) {
            i2++;
            i--;
        }
        if (b2 == 4) {
            i--;
        }
        if (b2 == 5) {
            i--;
            i2--;
        }
        if (b2 == 6) {
            i2--;
        }
        if (b2 == 7) {
            i2--;
            i++;
        }
        point.worldX = i;
        point.worldY = i2;
    }

    public void d() {
        int i;
        int i2;
        int i3;
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        Rect rect = new Rect();
        float f = gameEngine.viewpointXSnapped;
        float f2 = gameEngine.viewpointYSnapped;
        float f3 = gameEngine.visibleWorldWidth;
        float f4 = gameEngine.visibleWorldHeight;
        MapLayer mapLayer = gameEngine.tileMap.groundLayer;
        int i4 = (int) ((f * tileMap.tileScaleX) - 1.0f);
        if (i4 < 0) {
            i4 = 0;
        }
        int i5 = (int) ((f2 * tileMap.tileScaleY) - 1.0f);
        if (i5 < 0) {
            i5 = 0;
        }
        int i6 = (int) (((f + f3) * tileMap.tileScaleX) + 1.0f);
        if (i6 > tileMap.tileCountX - 1) {
            i6 = tileMap.tileCountX - 1;
        }
        int i7 = (int) (((f2 + f4) * tileMap.tileScaleY) + 1.0f);
        if (i7 > tileMap.tileCountY - 1) {
            i7 = tileMap.tileCountY - 1;
        }
        for (int i8 = i4; i8 < i6 + 1; i8++) {
            for (int i9 = i5; i9 < i7 + 1; i9++) {
                if (mapLayer.getTileAt(i8, i9) != null) {
                    int i10 = i8 * tileMap.tileWorldSizeX;
                    int i11 = i9 * tileMap.tileWorldSizeY;
                    rect.a(i10, i11, i10 + tileMap.tileWorldSizeX, i11 + tileMap.tileWorldSizeY);
                    rect.a((int) (-f), (int) (-f2));
                    boolean zB = rect.b((int) (gameEngine.gameUI.selectionBoxStartX / gameEngine.zoom), (int) (gameEngine.gameUI.selectionBoxStartY / gameEngine.zoom));
                    int i12 = 0;
                    if (50 == -1) {
                        i = 255;
                    } else {
                        i = 50 * 2;
                    }
                    if (0 == -1) {
                        i2 = 255;
                    } else {
                        i2 = 0 * 2;
                    }
                    if (0 == -1) {
                        i3 = 255;
                    } else {
                        if (0 != 0) {
                            i12 = 0 + 30;
                        }
                        i3 = i12 * 2;
                    }
                    c.a(128, i, i2, i3);
                    a(a(i8, i9), d);
                    float f5 = (i10 + tileMap.halfTileWorldSizeX) - f;
                    float f6 = (i11 + tileMap.halfTileWorldSizeY) - f2;
                    gameEngine.renderGraphicsEngine.a(f5, f6, f5 + (d.worldX * (tileMap.tileWorldSizeX - 3)) + 1.0f, f6 + (d.worldY * (tileMap.tileWorldSizeY - 3)) + 1.0f, c);
                    if (zB) {
                    }
                }
            }
        }
    }
}
