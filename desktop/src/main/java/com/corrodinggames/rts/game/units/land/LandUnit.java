package com.corrodinggames.rts.game.units.land;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.MovableUnit;
import com.corrodinggames.rts.game.units.UnitMovementType;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.j */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/j.class */
public abstract class LandUnit extends MovableUnit {

    /* JADX INFO: renamed from: dK */
    float fallVelocity;

    /* JADX INFO: renamed from: dL */
    public static Texture landUnitIconTexture = null;

    /* JADX INFO: renamed from: dM */
    public static Texture landUnitIconTextureExp = null;

    /* JADX INFO: renamed from: dN */
    public static Texture[] landUnitIconTextures = new Texture[10];

    /* JADX INFO: renamed from: dO */
    public static Texture[] landUnitIconTexturesExp = new Texture[10];

    public LandUnit(boolean z) {
        super(z);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Texture v() {
        if (this.team.teamId == -1) {
            return null;
        }
        if (isExperimental()) {
            return landUnitIconTexturesExp[this.team.getTeamColorIndex()];
        }
        return landUnitIconTextures[this.team.getTeamColorIndex()];
    }

    /* JADX INFO: renamed from: dt */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        landUnitIconTexture = gameEngine.renderGraphicsEngine.a(R.drawable.unit_icon_land);
        if (landUnitIconTexture == null) {
            throw new RuntimeException("IMAGE_ICON is null");
        }
        landUnitIconTextures = PlayerTeam.getTeamColorTextures(landUnitIconTexture);
        landUnitIconTextureExp = gameEngine.renderGraphicsEngine.a(R.drawable.unit_icon_land_exp);
        if (landUnitIconTextureExp == null) {
            throw new RuntimeException("IMAGE_ICON_EXP is null");
        }
        landUnitIconTexturesExp = PlayerTeam.getTeamColorTextures(landUnitIconTextureExp);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isDead) {
            float f2 = 0.0f;
            if (isOverLiquid()) {
                f2 = -10.0f;
            }
            if (this.posZ > f2) {
                if (this.posZ > 0.0f && this.fallVelocity < 0.4f) {
                    this.fallVelocity = 0.4f;
                }
                this.fallVelocity += 0.002f * f;
                this.posZ -= this.fallVelocity * f;
                if (this.posZ <= f2) {
                    this.posZ = f2;
                }
            }
        }
        if (!isAlive() || this.isDead || (this instanceof HoverLandUnit)) {
            return;
        }
        if (this.posZ < 0.0f) {
            this.posZ += 0.2f * f;
            if (this.posZ >= 0.0f) {
                this.posZ = 0.0f;
            }
        }
        if (this.posZ > 0.0f) {
            this.fallVelocity += 0.03f * f;
            if (this.posZ < 0.0f) {
                this.fallVelocity = Utility.clamp(this.fallVelocity, 0.2f);
            }
            this.posZ -= this.fallVelocity * f;
            if (this.posZ <= 0.0f) {
                if (this.posZ < 0.0f) {
                    this.posZ = 0.0f;
                }
                this.fallVelocity = 0.0f;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public UnitMovementType getMovementType() {
        return UnitMovementType.LAND;
    }
}
