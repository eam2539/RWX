package com.corrodinggames.rts.game.units;

import android.graphics.Point;
import android.graphics.PointF;
import com.corrodinggames.rts.game.map.MapLayer;
import com.corrodinggames.rts.game.map.MapLoadException;
import com.corrodinggames.rts.game.map.MapTile;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import java.util.ArrayList;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/p.class */
public class TerrainAutotiler {
    static TileCoordinate[] a;
    static int b;
    static int c;
    static MapTile d;

    public static void a(EditorTerrainType editorTerrainType, PointF pointF) {
        float f = pointF.x;
        float f2 = pointF.y;
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        if (tileMap == null) {
            GameEngine.log("setTerrainType called without map loaded");
            return;
        }
        int i = (int) (f * tileMap.tileScaleX);
        int i2 = (int) (f2 * tileMap.tileScaleY);
        if (!tileMap.isInBounds(i, i2)) {
            GameEngine.log("setTerrainType out of map range");
            return;
        }
        if (tileMap.groundLayer == null) {
            GameEngine.log("setTerrainType mainLayer missing");
            return;
        }
        if (!tileMap.editorActive) {
            if (tileMap.editorSelectionActive) {
                return;
            }
            try {
                if (tileMap.groundDetailsLayer == null) {
                    tileMap.groundDetailsLayer = new MapLayer(tileMap, "grounddetails", tileMap.tileCountX, tileMap.tileCountY);
                    tileMap.mapLayers.add(tileMap.groundDetailsLayer);
                }
                if (tileMap.groundDetails2Layer == null) {
                    tileMap.groundDetails2Layer = new MapLayer(tileMap, "grounddetails2", tileMap.tileCountX, tileMap.tileCountY);
                    tileMap.mapLayers.add(tileMap.groundDetails2Layer);
                }
                tileMap.groundLayer.hasAlpha = true;
                tileMap.groundDetailsLayer.hasAlpha = true;
                tileMap.groundDetails2Layer.hasAlpha = true;
            } catch (Exception /*MapLoadException*/ e) {
                e.printStackTrace();
                gameEngine.showMessageBox("Failed to edit map", e.getMessage());
                tileMap.editorSelectionActive = true;
                return;
            }
        }
        try {
            MapTile orCreateTileFromTilesetIndex = tileMap.getOrCreateTileFromTilesetIndex(editorTerrainType.b(), 0, 0);
            if (orCreateTileFromTilesetIndex == null) {
                GameEngine.log("setTerrainType mapTile==null");
                return;
            }
            MapTile tileAt = tileMap.groundLayer.getTileAt(i, i2);
            if (b == i && c == i2 && MapTile.compareTiles(d, orCreateTileFromTilesetIndex)) {
                return;
            }
            GameEngine.log("setTerrainType changing " + tileAt.tilesetLocalIndex + " to " + orCreateTileFromTilesetIndex.tilesetLocalIndex + " at:" + i2 + "," + i2);
            tileMap.groundLayer.setTileAt(i, i2, orCreateTileFromTilesetIndex, false);
            tileMap.groundDetailsLayer.setTileAt(i, i2, null, false);
            tileMap.groundDetails2Layer.setTileAt(i, i2, null, false);
            b = i;
            c = i2;
            d = orCreateTileFromTilesetIndex;
            ArrayList<Point> arrayList = new ArrayList();
            arrayList.add(new Point(i, i2));
            for (int i3 = 0; i3 <= 4; i3++) {
                ArrayList arrayList2 = new ArrayList();
                for (Point point : arrayList) {
                    a(editorTerrainType, orCreateTileFromTilesetIndex, point.worldX, point.worldY, arrayList2);
                }
                arrayList = arrayList2;
            }
            tileMap.invalidateAllLayerCells();
            gameEngine.pathfindingEngine.a(tileMap, false);
        } catch (MapLoadException e2) {
            e2.printStackTrace();
        }
    }

    public static void a(EditorTerrainType editorTerrainType, MapTile mapTile, int i, int i2, ArrayList arrayList) {
        String strA;
        TileMap tileMap = GameEngine.getInstance().tileMap;
        for (int i3 = -1; i3 <= 1; i3++) {
            for (int i4 = -1; i4 <= 1; i4++) {
                int i5 = i + i3;
                int i6 = i2 + i4;
                if (tileMap.isInBounds(i5, i6) && ((i3 != 0 || i4 != 0) && (strA = editorTerrainType.a()) != null && a(editorTerrainType, mapTile, i5, i6, i3, i4, strA))) {
                    arrayList.add(new Point(i5, i6));
                }
            }
        }
    }

    public static void a(int[] iArr, TileCoordinate tileCoordinate) {
        for (int i : iArr) {
            a[i + 128] = tileCoordinate;
        }
    }

    public static void a() {
        a = new TileCoordinate[SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_CONTENTS_MENU];
        a(a(1), new TileCoordinate(2, 2));
        a(a(2), new TileCoordinate(0, 2));
        a(a(4), new TileCoordinate(0, 0));
        a(a(8), new TileCoordinate(2, 0));
        a(a(16, 1, 2), new TileCoordinate(1, 2));
        a(a(32, 2, 4), new TileCoordinate(0, 1));
        a(a(64, 8, 4), new TileCoordinate(1, 0));
        a(a(-128, 1, 8), new TileCoordinate(2, 1));
        a(a(16 + 32, 2, 1, 4), new TileCoordinate(0, 6));
        a(a(32 + 64, 4, 8, 2), new TileCoordinate(0, 4));
        a(a(64 - 128, 8, 4, 1), new TileCoordinate(2, 4));
        a(a((-128) + 16, 1, 8, 2), new TileCoordinate(2, 6));
        a(a(1 + 2), new TileCoordinate(1, 1));
        a(a(2 + 4), new TileCoordinate(1, 1));
        a(a(4 + 8), new TileCoordinate(1, 1));
        a(a(8 + 1), new TileCoordinate(1, 1));
        a(a(16 + 32 + 64, 1, 2, 4, 8), new TileCoordinate(1, 1));
        a(a((32 + 64) - 128, 1, 2, 4, 8), new TileCoordinate(1, 1));
        a(a((64 - 128) + 16, 1, 2, 4, 8), new TileCoordinate(1, 1));
        a(a((-128) + 16 + 32, 1, 2, 4, 8), new TileCoordinate(1, 1));
        a(a(16 + 64, 1, 2, 8, 4), new TileCoordinate(1, 1));
        a(a((-128) + 32, 1, 2, 8, 4), new TileCoordinate(1, 1));
        a(a(1 + 4), new TileCoordinate(1, 1));
        a(a(2 + 8), new TileCoordinate(1, 1));
        a(a(16 + 4, 2, 1), new TileCoordinate(1, 1));
        a(a(64 + 2, 4, 8), new TileCoordinate(1, 1));
        a(a((-128) + 2, 1, 8), new TileCoordinate(1, 1));
        a(a(32 + 1, 2, 4), new TileCoordinate(1, 1));
        a(a(16 + 4 + 8, 2, 1), new TileCoordinate(1, 1));
        a(a(64 + 2 + 1, 4, 8), new TileCoordinate(1, 1));
        a(a((-128) + 2 + 4, 1, 8), new TileCoordinate(1, 1));
        a(a(32 + 1 + 8, 2, 4), new TileCoordinate(1, 1));
        a(a(-1), new TileCoordinate(1, 1));
    }

    private static int[] a(int i) {
        return new int[]{i};
    }

    private static int[] a(int i, int... iArr) {
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

    public static boolean a(EditorTerrainType editorTerrainType, MapTile mapTile, int i, int i2, int i3, int i4, String str) {
        MapTile orCreateTileFromTilesetIndex;
        boolean z = false;
        TileMap tileMap = GameEngine.getInstance().tileMap;
        byte b2 = b(editorTerrainType, mapTile, i, i2);
        if (a == null) {
            a();
        }
        TileCoordinate tileCoordinate = a[b2 + 128];
        if (tileCoordinate == null) {
            return false;
        }
        if (tileCoordinate.a == 1 && tileCoordinate.b == 1) {
            tileMap.groundLayer.setTileAt(i, i2, mapTile, false);
            orCreateTileFromTilesetIndex = null;
            z = true;
        } else {
            try {
                orCreateTileFromTilesetIndex = tileMap.getOrCreateTileFromTilesetIndex(str, tileCoordinate.a, tileCoordinate.b);
            } catch (MapLoadException e) {
                e.printStackTrace();
                return false;
            }
        }
        a(editorTerrainType, mapTile, orCreateTileFromTilesetIndex, i, i2);
        return z;
    }

    public static void a(EditorTerrainType editorTerrainType, MapTile mapTile, MapTile mapTile2, int i, int i2) {
        TileMap tileMap = GameEngine.getInstance().tileMap;
        MapTile tileAt = tileMap.groundLayer.getTileAt(i, i2);
        MapTile tileAt2 = tileMap.groundDetailsLayer.getTileAt(i, i2);
        MapTile tileAt3 = tileMap.groundDetails2Layer.getTileAt(i, i2);
        EditorTerrainType editorTerrainTypeA = a(tileAt2);
        EditorTerrainType editorTerrainTypeA2 = a(tileAt3);
        if (editorTerrainTypeA == editorTerrainType) {
            tileMap.groundDetailsLayer.setTileAt(i, i2, null, false);
            tileAt2 = null;
        }
        if (editorTerrainTypeA2 == editorTerrainType) {
            tileMap.groundDetails2Layer.setTileAt(i, i2, null, false);
            tileAt3 = null;
            editorTerrainTypeA2 = null;
        }
        if (tileAt2 == null && tileAt3 != null) {
            tileMap.groundDetailsLayer.setTileAt(i, i2, tileAt3, false);
            tileMap.groundDetails2Layer.setTileAt(i, i2, null, false);
            tileAt2 = tileAt3;
            tileAt3 = null;
        }
        if (MapTile.compareTiles(tileAt, mapTile) || mapTile2 == null) {
            return;
        }
        if (tileAt2 != null) {
            if (tileAt3 != null) {
                tileMap.groundDetailsLayer.setTileAt(i, i2, tileAt3, false);
            }
            tileMap.groundDetails2Layer.setTileAt(i, i2, mapTile2, false);
            return;
        }
        tileMap.groundDetailsLayer.setTileAt(i, i2, mapTile2, false);
    }

    public static EditorTerrainType a(MapTile mapTile) {
        if (mapTile == null) {
            return null;
        }
        TileMap tileMap = GameEngine.getInstance().tileMap;
        for (EditorTerrainType editorTerrainType : EditorTerrainType.values()) {
            String str = mapTile.tileset.tilesetSource;
            if (str != null && str.equals(editorTerrainType.b())) {
                return editorTerrainType;
            }
            if (str != null && str.equals(editorTerrainType.a())) {
                return editorTerrainType;
            }
        }
        return null;
    }

    public static boolean a(EditorTerrainType editorTerrainType, MapTile mapTile, int i, int i2) {
        TileMap tileMap = GameEngine.getInstance().tileMap;
        if (tileMap.isInBounds(i, i2) && MapTile.compareTiles(tileMap.groundLayer.getTileAt(i, i2), mapTile)) {
            return true;
        }
        return false;
    }

    public static byte b(EditorTerrainType editorTerrainType, MapTile mapTile, int i, int i2) {
        byte b2 = 0;
        TileMap tileMap = GameEngine.getInstance().tileMap;
        int i3 = tileMap.tileCountX;
        int i4 = tileMap.tileCountY;
        if (i >= 1) {
            if (a(editorTerrainType, mapTile, i - 1, i2)) {
                b2 = (byte) (0 - 128);
            }
            if (i2 >= 1 && a(editorTerrainType, mapTile, i - 1, i2 - 1)) {
                b2 = (byte) (b2 + 1);
            }
            if (i2 < i4 - 1 && a(editorTerrainType, mapTile, i - 1, i2 + 1)) {
                b2 = (byte) (b2 + 8);
            }
        }
        if (i2 >= 1) {
            if (a(editorTerrainType, mapTile, i, i2 - 1)) {
                b2 = (byte) (b2 + 16);
            }
            if (i < i3 - 1 && a(editorTerrainType, mapTile, i + 1, i2 - 1)) {
                b2 = (byte) (b2 + 2);
            }
        }
        if (i < i3 - 1 && a(editorTerrainType, mapTile, i + 1, i2)) {
            b2 = (byte) (b2 + 32);
        }
        if (i2 < i4 - 1) {
            if (a(editorTerrainType, mapTile, i, i2 + 1)) {
                b2 = (byte) (b2 + 64);
            }
            if (i < i3 - 1 && a(editorTerrainType, mapTile, i + 1, i2 + 1)) {
                b2 = (byte) (b2 + 4);
            }
        }
        return b2;
    }
}
