package com.corrodinggames.rts.game.map;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.sea.MissileShip;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.GraphicsEngine;
import com.corrodinggames.rts.gameFramework.utility.Log;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import io.github.rwx.geometry.Rect;
import io.github.rwx.geometry.RectF;
import io.github.rwx.render.canvas.KoolPaint;

import java.util.Properties;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.b.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/b/g.class */
public final class MapTile {

    /* JADX INFO: renamed from: a */
    public Tileset tileset;

    /* JADX INFO: renamed from: b */
    public int tilesetLocalIndex;

    /* JADX INFO: renamed from: c */
    public int atlasSlotIndex = -2;

    /* JADX INFO: renamed from: d */
    public short uniqueTileId = -1;

    /* JADX INFO: renamed from: e */
    public boolean isWater;

    /* JADX INFO: renamed from: f */
    public boolean isWaterBridge;

    /* JADX INFO: renamed from: g */
    public boolean isLava;

    /* JADX INFO: renamed from: h */
    public boolean isCliff;

    /* JADX INFO: renamed from: i */
    public boolean isResourcePool;

    /* JADX INFO: renamed from: j */
    public byte movementBlockLevel;

    /* JADX INFO: renamed from: k */
    public boolean hasLargeObject;

    /* JADX INFO: renamed from: l */
    public boolean blocksBuildingPlacement;

    /* JADX INFO: renamed from: m */
    public MapTile[] randomVariants;

    /* JADX INFO: renamed from: n */
    static final Rect sharedTempRect = new Rect();

    /* JADX INFO: renamed from: a */
    public static boolean compareTiles(MapTile mapTile, MapTile mapTile2) {
        if (mapTile == mapTile2) {
            return true;
        }
        return mapTile != null && mapTile2 != null && mapTile.tileset == mapTile2.tileset && mapTile.tilesetLocalIndex == mapTile2.tilesetLocalIndex;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public MapTile clone() {
        MapTile mapTile = new MapTile();
        mapTile.tileset = this.tileset;
        mapTile.tilesetLocalIndex = this.tilesetLocalIndex;
        mapTile.isWater = this.isWater;
        mapTile.isWaterBridge = this.isWaterBridge;
        mapTile.isLava = this.isLava;
        mapTile.isCliff = this.isCliff;
        mapTile.isResourcePool = this.isResourcePool;
        mapTile.movementBlockLevel = this.movementBlockLevel;
        mapTile.hasLargeObject = this.hasLargeObject;
        mapTile.blocksBuildingPlacement = this.blocksBuildingPlacement;
        return mapTile;
    }

    /* JADX INFO: renamed from: a */
    public static void reportMissingUnit(String str) {
        GameEngine.logColored(str);
        GameEngine.getInstance().alert("Missing unit data while loading map: " + str, 1);
        try {
            Thread.sleep(2L);
        } catch (InterruptedException e) {
        }
    }

    /* JADX INFO: renamed from: a */
    public static MapTile createTile(TileMap tileMap, MapLayer mapLayer, Tileset tileset, int i, short s, short s2, boolean z) throws MapLoadException {
        String str;
        PlayerTeam playerTeamK;
        Properties embeddedPngBase64 = tileset.getEmbeddedPngBase64(tileset.firstGid + i);
        if (embeddedPngBase64 != null) {
            String property = embeddedPngBase64.getProperty("showFog");
            if (property != null) {
                int i2 = Integer.parseInt(property);
                GameEngine gameEngine = GameEngine.getInstance();
                tileMap.setCursorTileIndexFromTileIndex((int) s, (int) s2);
                gameEngine.tileMap.updateFogVisibilityForTeamsAtWorldPoint(tileMap.cursorTileX + tileMap.halfTileWorldSizeX, tileMap.cursorTileY + tileMap.halfTileWorldSizeY, i2, gameEngine.playerTeam, false);
                return null;
            }
            String property2 = embeddedPngBase64.getProperty("unit");
            String property3 = embeddedPngBase64.getProperty("customUnit");
            if (property2 != null || property3 != null) {
                String property4 = embeddedPngBase64.getProperty("team");
                if ("none".equalsIgnoreCase(property4)) {
                    playerTeamK = PlayerTeam.k(-1);
                } else {
                    if (property4 == null) {
                        GameEngine.log("map", "warning: unit has no team property:" + property2 + " at: " + ((int) s) + "," + ((int) s2));
                        return null;
                    }
                    playerTeamK = PlayerTeam.k(Integer.valueOf(property4).intValue());
                    if (playerTeamK == null) {
                        GameEngine.log("map", "skipping unit without player:" + property2 + " at: " + ((int) s) + "," + ((int) s2) + " team:" + property4);
                        return null;
                    }
                    if (playerTeamK.isSpectatorTeamColor()) {
                        GameEngine.log("map", "Unit team is marked as spectator:" + property2 + " (skipping unit)");
                        return null;
                    }
                }
                BaseUnit missileShip = null;
                if (property3 != null) {
                    CustomUnitConfig customUnitConfigFindConfigByName = CustomUnitConfig.findConfigByName(property3);
                    if (customUnitConfigFindConfigByName == null) {
                        String str2 = "Could not find custom unit of:" + property3 + " at x:" + ((int) s) + ", y:" + ((int) s2);
                        reportMissingUnit(str2);
                        throw new MapLoadException(str2);
                    }
                    UnitType unitTypeC = CustomUnitConfig.c(customUnitConfigFindConfigByName);
                    if (unitTypeC != null) {
                        if (unitTypeC instanceof CustomUnitConfig) {
                            customUnitConfigFindConfigByName = (CustomUnitConfig) unitTypeC;
                        } else {
                            GameEngine.logColored("replacement not a custom unit:" + unitTypeC.getUnitTypeDescriptionShort());
                        }
                    }
                    missileShip = CustomUnitConfig.a(false, customUnitConfigFindConfigByName);
                    if (missileShip == null) {
                        String str3 = "Metadata unit is null for:" + property3;
                        reportMissingUnit(str3);
                        throw new MapLoadException(str3);
                    }
                } else {
                    UnitType unitTypeByName = UnitTypeEnum.getUnitTypeByName(property2);
                    if (unitTypeByName != null) {
                        missileShip = unitTypeByName.a();
                    }
                    if (missileShip == null && "scoutShip".equalsIgnoreCase(property2)) {
                        missileShip = new MissileShip(false);
                    }
                    if (missileShip == null) {
                        String str4 = "Could not find unit:" + property2 + " at: " + ((int) s) + "," + ((int) s2);
                        reportMissingUnit(str4);
                        throw new MapLoadException(str4);
                    }
                }
                tileMap.setCursorTileIndexFromTileIndex((int) s, (int) s2);
                missileShip.posX = tileMap.cursorTileX + missileShip.getTileOffsetX();
                missileShip.posY = tileMap.cursorTileY + missileShip.getTileOffsetY();
                if (playerTeamK != null) {
                    missileShip.setUnitTeam(playerTeamK);
                    if (embeddedPngBase64.getProperty("type") != null) {
                        missileShip.a_(embeddedPngBase64.getProperty("type"));
                    }
                    if (embeddedPngBase64.getProperty("randomRotate") != null) {
                        missileShip.rotationSpeed = Utility.getDeterministicRandomInt(missileShip, -180, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_STB_INPUT);
                    }
                    missileShip.changeTeam = "builder".equalsIgnoreCase(property2) || "builder".equalsIgnoreCase(property3);
                    missileShip.isTargetable = "commandCenter".equalsIgnoreCase(property2) || "commandCenter".equalsIgnoreCase(property3);
                    missileShip.isActive = true;
                    missileShip.n();
                    PlayerTeam.c(missileShip);
                    GameObject.dL();
                    return null;
                }
                throw new MapLoadException("team has not been set for:" + property2);
            }
            if (mapLayer != null && mapLayer.lowerName.equals("units")) {
                Log.d("RustedWarfare", "non unit on units layer at:" + ((int) s) + "," + ((int) s2));
                return null;
            }
        }
        MapTile mapTile = new MapTile();
        mapTile.tileset = tileset;
        tileset.usedInMap = true;
        if (mapLayer != null && !mapLayer.isGroundLayer) {
            tileset.usedInNonGroundLayer = true;
        }
        if (z) {
            tileset.usedInFogLayer = true;
        }
        mapTile.tilesetLocalIndex = i;
        if (embeddedPngBase64 != null) {
            if (embeddedPngBase64.getProperty("water") != null) {
                mapTile.isWater = true;
            }
            if (embeddedPngBase64.getProperty("water-bridge") != null) {
                mapTile.isWaterBridge = true;
            }
            if (embeddedPngBase64.getProperty("lava") != null || embeddedPngBase64.getProperty("lava-cliff") != null) {
                mapTile.isLava = true;
                if (embeddedPngBase64.getProperty("lava-cliff") != null) {
                    mapTile.isCliff = true;
                }
            }
            if (embeddedPngBase64.getProperty("cliff-soft") != null) {
                mapTile.isCliff = true;
            }
            if (embeddedPngBase64.getProperty("cliff") != null) {
                mapTile.isCliff = true;
            }
            if (embeddedPngBase64.getProperty("large-cliff") != null) {
                mapTile.hasLargeObject = true;
            }
            if (embeddedPngBase64.getProperty("trees") != null) {
                mapTile.hasLargeObject = true;
            }
            if (embeddedPngBase64.getProperty("res_pool") != null) {
                mapTile.isResourcePool = true;
            }
            if (embeddedPngBase64.getProperty("tree") != null) {
            }
            if (embeddedPngBase64.getProperty("small-rock") != null) {
                mapTile.movementBlockLevel = (byte) 40;
            }
            if (embeddedPngBase64.getProperty("large-rock") != null) {
                mapTile.movementBlockLevel = (byte) -1;
            }
            if (embeddedPngBase64.getProperty("block-land") != null) {
                mapTile.movementBlockLevel = (byte) -1;
            }
            if (embeddedPngBase64.getProperty("block-buildings") != null) {
                mapTile.blocksBuildingPlacement = true;
            }
        }
        int i3 = 0;
        int i4 = 0;
        if (mapTile.tileset != null && (str = mapTile.tileset.imageKey) != null) {
            if (mapTile.tilesetLocalIndex == 0 && str.equals("shallowwater.png")) {
                i3 = 5;
            }
            if (mapTile.tilesetLocalIndex == 0 && str.equals("deepwater.png")) {
                i3 = 2;
            }
            if (mapTile.tilesetLocalIndex == 0 && str.equals("water.png")) {
                i3 = 2;
            }
            if (mapTile.tilesetLocalIndex == 0 && str.equals("longgrass.png")) {
                i3 = 3;
            }
            if (mapTile.tilesetLocalIndex == 0 && str.equals("mountain.png")) {
                i3 = 3;
            }
            if (mapTile.tilesetLocalIndex == 7 && str.equals("stone.png")) {
                i3 = 4;
                i4 = 23;
            }
            if (mapTile.tilesetLocalIndex != 0 || str.equals("lava.png")) {
            }
            if (mapTile.tilesetLocalIndex == 0 && str.equals("snow.png")) {
                i3 = 2;
            }
        }
        if (embeddedPngBase64 != null && embeddedPngBase64.getProperty("randomTileBy") != null) {
            try {
                i3 = Integer.parseInt(embeddedPngBase64.getProperty("randomTileBy"));
                if (embeddedPngBase64.getProperty("randomTileFixedOffset") != null) {
                    try {
                        i4 = Integer.parseInt(embeddedPngBase64.getProperty("randomTileFixedOffset"));
                    } catch (NumberFormatException e) {
                        throw new MapLoadException("(x:" + ((int) s) + "y:" + ((int) s2) + ") - randomTileFixedOffset: Unexpected integer value:'" + embeddedPngBase64.getProperty("randomTileBy") + "'");
                    }
                }
            } catch (NumberFormatException e2) {
                throw new MapLoadException("(x:" + ((int) s) + "y:" + ((int) s2) + ") - randomTileBy: Unexpected integer value:'" + embeddedPngBase64.getProperty("randomTileBy") + "'");
            }
        }
        if (i3 > 0) {
            MapTile[] mapTileArr = new MapTile[i3];
            for (int i5 = 0; i5 < i3; i5++) {
                mapTileArr[i5] = mapTile.clone();
                mapTileArr[i5].tilesetLocalIndex += i5 + 1 + i4;
            }
            mapTile.randomVariants = mapTileArr;
        }
        return mapTile;
    }

    MapTile() {
    }

    /* JADX INFO: renamed from: a */
    public void renderTile(GraphicsEngine graphicsEngine, RectF rectF, float f, KoolPaint paint) {
        Tileset tileset = this.tileset;
        graphicsEngine.a(tileset.tilesetBitmap, tileset.freeUnusedImages(this.tilesetLocalIndex), rectF, paint);
    }
}
