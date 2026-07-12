package com.corrodinggames.rts.game.units.land;

import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.n */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/n.class */
public class TankUnit extends LandUnit {

    /* JADX INFO: renamed from: a */
    static Texture tankTexture = null;

    /* JADX INFO: renamed from: b */
    static Texture tankTexture_alive = null;

    /* JADX INFO: renamed from: c */
    static Texture tankTurretTexture = null;

    /* JADX INFO: renamed from: d */
    static Texture tankShadowTexture = null;

    /* JADX INFO: renamed from: e */
    static Texture[] tankTextures_teamColors = new Texture[10];

    /* JADX INFO: renamed from: f */
    int animationFrame;

    /* JADX INFO: renamed from: g */
    float animationTimer;

    /* JADX INFO: renamed from: h */
    float dustEffectTimer;

    /* JADX INFO: renamed from: i */
    Rect boundingRect;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.tank;
    }

    /* JADX INFO: renamed from: f */
    public static void loadTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        tankTexture_alive = gameEngine.renderGraphicsEngine.a(R.drawable.tank2);
        tankTexture = gameEngine.renderGraphicsEngine.a(R.drawable.tank2_dead);
        tankTurretTexture = gameEngine.renderGraphicsEngine.a(R.drawable.tank2_turret);
        tankShadowTexture = gameEngine.renderGraphicsEngine.a(R.drawable.tank2_shadow);
        tankTextures_teamColors = PlayerTeam.getTeamColorTextures(tankTexture_alive);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return tankTexture;
        }
        return tankTextures_teamColors[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return tankShadowTexture;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && !this.isDestroyed;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 3.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 3.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return tankTurretTexture;
    }

    public TankUnit(boolean z) {
        super(z);
        this.boundingRect = new Rect();
        a(tankTexture_alive, 3);
        this.radius = 11.0f;
        this.displayRadius = this.radius + 1.0f;
        this.maxHealth = 210.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = tankTexture_alive;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        this.baseTexture = tankTexture;
        S(0);
        this.isAlive = false;
        a(UnitSize.small);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!this.isDestroyed && this.rotation != 0.0f) {
            this.animationTimer += f;
            if (this.animationTimer > 1.0f) {
                this.animationTimer = 0.0f;
                this.animationFrame++;
                if (this.animationFrame > 2) {
                    this.animationFrame = 0;
                }
            }
            if (this.rotation > 0.0f && this.flag3) {
                this.dustEffectTimer += f;
                if (this.dustEffectTimer > 9.0f) {
                    this.dustEffectTimer = 0.0f;
                    createDustEffect();
                }
            }
        }
    }

    /* JADX INFO: renamed from: K */
    public void createDustEffect() {
        GameEngine gameEngine = GameEngine.getInstance();
        int i = 0;
        while (i <= 1) {
            float f = i == 0 ? -20 : 20;
            gameEngine.effectManager.createMuzzleFlash(this.posX + (Utility.fastCos(this.rotationSpeed + 180.0f + f) * this.radius), this.posY + (Utility.fastSin(this.rotationSpeed + 180.0f + f) * this.radius), this.posZ, this.rotationSpeed + 180.0f, 0);
            i++;
        }
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
        PointF pointFK = getShadowTexture(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.U = 30.0f;
        projectileA.l = baseUnit;
        projectileA.h = 60.0f;
        projectileA.t = 3.0f;
        projectileA.P = (short) 1;
        projectileA.x = 1.0f;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1127220);
        gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
        gameEngine.soundEngine.playSoundAt(SoundEngine.tankFiringSound, 0.3f, 1.0f + Utility.randomFloatInRange(-0.07f, 0.07f), pointFE.x, pointFE.y);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 130.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 75.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 4.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.25f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        if (!super.c(f)) {
            return false;
        }
        GameViewUtils.a((OrderableUnit) this);
        if (!this.isDestroyed) {
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.07f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.17f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 20.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        if (z) {
            return super.a_(z);
        }
        if (this.isDestroyed) {
            return super.a_(z);
        }
        return super.a(z, this.animationFrame);
    }
}
