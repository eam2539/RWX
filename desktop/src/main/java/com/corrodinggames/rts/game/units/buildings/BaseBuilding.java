package com.corrodinggames.rts.game.units.buildings;

import android.graphics.*;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.map.MapTile;
import com.corrodinggames.rts.game.map.TileMap;
import com.corrodinggames.rts.game.units.*;
import com.corrodinggames.rts.game.units.custom.PlacementRules;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.effects.BuildPreview;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/d.class */
public abstract class BaseBuilding extends OrderableUnit {

    /* JADX INFO: renamed from: m */
    Texture overlayTexture;

    /* JADX INFO: renamed from: n */
    public Rect buildingTargetRect;

    /* JADX INFO: renamed from: o */
    public Rect buildingVelocityRect;

    /* JADX INFO: renamed from: p */
    public static Texture buildingIconTexture = null;

    /* JADX INFO: renamed from: q */
    public static Texture[] teamColoredIconTextures = new Texture[10];

    /* JADX INFO: renamed from: r */
    int buildingAnimationState;

    /* JADX INFO: renamed from: s */
    int currentAnimationFrame;

    /* JADX INFO: renamed from: ds */
    public boolean isBuildingActive() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeInt(this.buildingAnimationState);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        if (gameInputStream.getProtocolVersion() >= 15) {
            setBuildingAnimationState(gameInputStream.readInt());
        }
        super.a(gameInputStream);
    }

    /* JADX INFO: renamed from: a */
    public static boolean canBuildingBePlacedAt(UnitType unitType, float f, float f2, PlayerTeam playerTeam) {
        GameEngine gameEngine = GameEngine.getInstance();
        OrderableUnit orderableUnit = (OrderableUnit) BaseUnit.findTurretPosition(unitType);
        gameEngine.tileMap.updateCursorTileIndexFromWorldPoint(f, f2);
        orderableUnit.posX = gameEngine.tileMap.cursorTileX + orderableUnit.getTileOffsetX();
        orderableUnit.posY = gameEngine.tileMap.cursorTileY + orderableUnit.getTileOffsetX();
        orderableUnit.setUnitTeam(playerTeam);
        return orderableUnit.canPlaceAtCurrentPosition((PlayerTeam) null);
    }

    /* JADX INFO: renamed from: R */
    public void setBuildingAnimationState(int i) {
        this.buildingAnimationState = i;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return teamColoredIconTextures[this.team.getTeamColorIndex()];
    }

    public static void dt() {
        buildingIconTexture = GameEngine.getInstance().renderGraphicsEngine.a(R.drawable.unit_icon_building);
        teamColoredIconTextures = PlayerTeam.getTeamColorTextures(buildingIconTexture);
    }

    public BaseBuilding(boolean z) {
        super(z);
        this.buildingTargetRect = new Rect();
        this.buildingVelocityRect = new Rect();
        this.buildingAnimationState = 1;
        this.currentAnimationFrame = 0;
        this.rotationSpeed = -90.0f;
        this.isAlive = false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public void f_() {
        this.isAlive = false;
    }

    /* JADX INFO: renamed from: L */
    public boolean onDeath() {
        a(UnitSize.large);
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().pathfindingEngine.a(this);
        if (this.buildProgress < 1.0f) {
            a(UnitSize.verysmall);
            return false;
        }
        this.currentAnimationFrame = 0;
        return onDeath();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect cd() {
        return this.buildingVelocityRect;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect cc() {
        return this.buildingTargetRect;
    }

    public static boolean a(OrderableUnit orderableUnit, UnitType unitType, UnitMovementType unitMovementType, int i, int i2, int i3) {
        GameEngine gameEngine = GameEngine.getInstance();
        TileMap tileMap = gameEngine.tileMap;
        if (!tileMap.isInBounds(i, i2)) {
            return false;
        }
        boolean z = false;
        if (tileMap.fogEnabled && gameEngine.playerTeam.fogOfWarData != null) {
            if (!tileMap.fogRenderActive && gameEngine.playerTeam.fogOfWarData[i][i2] == 10) {
                return false;
            }
            z = gameEngine.playerTeam.fogOfWarData[i][i2] >= 5;
        }
        if (a(orderableUnit, unitType, unitMovementType, i, i2, z)) {
            if (unitType.p()) {
                MapTile pathingOverrideTileAt = tileMap.getPathingOverrideTileAt(i, i2);
                if (pathingOverrideTileAt != null && pathingOverrideTileAt.isResourcePool) {
                    return true;
                }
                return false;
            }
            if (BuildPreview.isTileOverBlueprint(gameEngine.playerTeam, i, i2, i3)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean a(OrderableUnit orderableUnit, UnitType unitType, UnitMovementType unitMovementType, int i, int i2, boolean z) {
        return a(orderableUnit, unitType, unitMovementType, i, i2, z, null) == null;
    }

    public static String a(OrderableUnit orderableUnit, UnitType unitType, UnitMovementType unitMovementType, int i, int i2, boolean z, PlayerTeam playerTeam) {
        String strA;
        GameEngine gameEngine = GameEngine.getInstance();
        if (!gameEngine.tileMap.isInBounds(i, i2)) {
            return "{0}";
        }
        PlacementRules placementRulesQ = unitType.q();
        if (placementRulesQ != null && (strA = placementRulesQ.a(orderableUnit, i, i2)) != null) {
            return strA;
        }
        if (unitType == UnitTypeEnum.seaFactory || unitMovementType == UnitMovementType.WATER) {
            if (!gameEngine.pathfindingEngine.a(gameEngine.pathfindingEngine.A, i, i2)) {
                return null;
            }
            return "{3}";
        }
        MapTile pathingOverrideTileAt = gameEngine.tileMap.getPathingOverrideTileAt(i, i2);
        if (pathingOverrideTileAt != null && pathingOverrideTileAt.isResourcePool) {
            if (unitType.p()) {
                return null;
            }
            return "{0}";
        }
        if (unitMovementType == UnitMovementType.AIR) {
            return null;
        }
        if (unitMovementType == UnitMovementType.HOVER) {
            if (!gameEngine.pathfindingEngine.a(gameEngine.pathfindingEngine.C, i, i2)) {
                return null;
            }
            return "{0}";
        }
        if (unitMovementType == UnitMovementType.OVER_CLIFF) {
            if (!gameEngine.pathfindingEngine.a(gameEngine.pathfindingEngine.D, i, i2)) {
                return null;
            }
            return "{0}";
        }
        if (unitMovementType == UnitMovementType.OVER_CLIFF_WATER) {
            if (!gameEngine.pathfindingEngine.a(gameEngine.pathfindingEngine.E, i, i2)) {
                return null;
            }
            return "{0}";
        }
        if (gameEngine.pathfindingEngine.a(gameEngine.pathfindingEngine.z, i, i2, z)) {
            boolean z2 = false;
            if (playerTeam != null && !gameEngine.tileMap.isTileVisibleForTeam(i, i2, playerTeam)) {
                z2 = true;
            }
            if (!z2) {
                return "{0}";
            }
            return null;
        }
        return null;
    }

    public static BaseUnit b(int i, int i2) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.tileMap.setCursorTileIndexFromTileIndex(i, i2);
        float f = gameEngine.tileMap.cursorTileX + gameEngine.tileMap.halfTileWorldSizeX;
        float f2 = gameEngine.tileMap.cursorTileY + gameEngine.tileMap.halfTileWorldSizeY;
        for (BaseUnit baseUnit : gameEngine.unitSpatialIndex.b(f, f2, 0.0f)) {
            if (baseUnit.bI() && !baseUnit.isDead && baseUnit.checkAttackCooldown(f, f2, 0.0f)) {
                return baseUnit;
            }
        }
        return null;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(int i) {
    }

    public static BaseUnit g(UnitType unitType) {
        if (unitType == null) {
            throw new RuntimeException("type is null");
        }
        return unitType.a();
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType getMovementType() {
        return UnitMovementType.NONE;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean Q() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: b_ */
    public boolean requiresFacingForActions() {
        return false;
    }

    /* JADX INFO: renamed from: f */
    public Paint getBuildingPaint() {
        int iA;
        GameEngine gameEngine = GameEngine.getInstance();
        PorterDuffColorFilter porterDuffColorFilter = null;
        if (this.buildProgress < 1.0f) {
            iA = Color.a((int) (40.0f + (this.buildProgress * 200.0f)), 140, 255, 140);
            porterDuffColorFilter = overlayFilterLightGreen;
        } else {
            iA = Color.a(255, 255, 255, 255);
        }
        if (this.isUnitParalyzed) {
            if (this.isUnitDisabled) {
                iA = Color.a(200, 20, 255, 20);
                porterDuffColorFilter = overlayFilterGreen;
            }
            if (this.isUnitCapturable) {
                iA = Color.a(200, 255, 20, 20);
                porterDuffColorFilter = overlayFilterRed;
            }
            if (this.isUnitInvulnerable) {
                iA = Color.a(70, 70, 70, 245);
                porterDuffColorFilter = overlayFilterBlue;
                if (this.isUnitCapturable) {
                    iA = Color.a(70, 255, 20, 20);
                    porterDuffColorFilter = overlayFilterRed;
                }
            }
            if (this.isUnitUntargetable) {
                iA = Color.a(150, 100, 100, 100);
            }
        }
        boolean z = gameEngine.settingsEngine.renderAntiAlias;
        if (!dk()) {
            z = false;
            if (gameEngine.zoom < 1.0f) {
                z = true;
            }
        }
        if (this.isUnitStunned) {
            z = UnitTypeEnum.ag;
        }
        return a(iA, porterDuffColorFilter, z);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        GameEngine gameEngine = GameEngine.getInstance();
        int i = this.currentAnimationFrame * this.es;
        RectF rectFCF = getUnitBounds();
        dv.a(i, 0, i + this.es, 0 + this.et);
        gameEngine.renderGraphicsEngine.a(this.baseTexture, dv, rectFCF, getBuildingPaint());
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void d(float f) {
        super.d(f);
        if (this.overlayTexture == null) {
            return;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        if (isBuildingActive()) {
            gameEngine.renderGraphicsEngine.b(this.overlayTexture, (this.posX - ((int) (this.overlayTexture.t + 0.1f))) - gameEngine.viewpointXSnapped, (this.posY - ((int) (this.overlayTexture.u + 0.1f))) - gameEngine.viewpointYSnapped, getBuildingPaint());
            return;
        }
        RectF rectFCF = getUnitBounds();
        dv.a(0, 0, 0 + this.es, 0 + this.et);
        gameEngine.renderGraphicsEngine.a(this.overlayTexture, dv, rectFCF, getBuildingPaint());
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean bI() {
        return true;
    }
}
