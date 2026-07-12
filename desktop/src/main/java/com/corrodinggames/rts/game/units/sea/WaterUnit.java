package com.corrodinggames.rts.game.units.sea;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.MovableUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.h.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/h/f.class */
public abstract class WaterUnit extends MovableUnit {

    /* JADX INFO: renamed from: m */
    float smokeEffectTimer;

    /* JADX INFO: renamed from: n */
    float sinkVelocity;

    /* JADX INFO: renamed from: o */
    boolean hasSunk;

    /* JADX INFO: renamed from: p */
    public static Texture waterUnitIconTexture = null;

    /* JADX INFO: renamed from: q */
    public static Texture[] waterUnitIconTextures = new Texture[10];

    public WaterUnit(boolean z) {
        super(z);
        this.hasSunk = false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject, com.corrodinggames.rts.gameFramework.Serializable
    public void a(GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeFloat(this.sinkVelocity);
        gameOutputStream.writeBoolean(this.hasSunk);
        super.a(gameOutputStream);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.PositionedObject, com.corrodinggames.rts.gameFramework.GameObject
    public void a(GameInputStream gameInputStream) throws IOException {
        this.sinkVelocity = gameInputStream.readFloat();
        this.hasSunk = gameInputStream.readBoolean();
        super.a(gameInputStream);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        return waterUnitIconTextures[this.team.getTeamColorIndex()];
    }

    /* JADX INFO: renamed from: M */
    public static void loadTextures() {
        waterUnitIconTexture = GameEngine.getInstance().renderGraphicsEngine.a(R.drawable.unit_icon_water);
        waterUnitIconTextures = PlayerTeam.getTeamColorTextures(waterUnitIconTexture);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType h() {
        return UnitMovementType.WATER;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cv */
    public boolean getUnitAIPathfindTimeout() {
        return true;
    }

    /* JADX INFO: renamed from: K */
    public boolean shouldCreateSmokeEffect() {
        return true;
    }

    /* JADX INFO: renamed from: s */
    public void adjustZPosition(float f) {
        if (this.posZ != 0.0f) {
            this.posZ = Utility.distanceSq(this.posZ, 0.0f, 0.2f * f);
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isDestroyed) {
            if (this.posZ > -10.0f) {
                this.sinkVelocity += 0.002f * f;
                this.posZ -= this.sinkVelocity * f;
                return;
            } else {
                this.posZ = -10.0f;
                if (!this.hasSunk) {
                    this.hasSunk = true;
                    return;
                }
                return;
            }
        }
        if (!isAlive() || this.isDestroyed) {
            return;
        }
        adjustZPosition(f);
        if (shouldCreateSmokeEffect()) {
            if (this.rotation != 0.0f) {
                this.smokeEffectTimer += f;
            }
            if (this.smokeEffectTimer > 10.0f) {
                this.smokeEffectTimer = 0.0f;
                if (isBuilding()) {
                    GameEngine gameEngine = GameEngine.getInstance();
                    float f2 = this.rotationSpeed + 180.0f;
                    if (this.rotation < 0.0f) {
                        f2 += 180.0f;
                    }
                    float f3 = this.radius - 6.0f;
                    if (f3 < 4.0f) {
                        f3 = 4.0f;
                    }
                    gameEngine.effectManager.createSmokeEffect(this.posX + (Utility.fastCos(f2) * f3), this.posY + (Utility.fastSin(f2) * f3), 0.0f, f2);
                }
            }
        }
    }
}
