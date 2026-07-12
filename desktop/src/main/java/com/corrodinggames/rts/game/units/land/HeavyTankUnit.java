package com.corrodinggames.rts.game.units.land;

import android.graphics.Color;
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
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/f.class */
public class HeavyTankUnit extends LandUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture[] c = new Texture[10];
    static Texture d = null;

    /* JADX INFO: renamed from: e */
    int turretFrameIndex;

    /* JADX INFO: renamed from: f */
    float turretAnimationTimer;

    /* JADX INFO: renamed from: g */
    float smokeTimer;

    /* JADX INFO: renamed from: h */
    Rect turretBounds;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.heavyTank;
    }

    /* JADX INFO: renamed from: f */
    public static void loadHeavyTankTextures() {
        GameEngine gameEngine = GameEngine.getInstance();
        Texture textureA = gameEngine.renderGraphicsEngine.a(R.drawable.heavy_tank);
        c = PlayerTeam.getTeamColorTextures(textureA);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.heavy_tank_dead);
        b = gameEngine.renderGraphicsEngine.a(R.drawable.heavy_tank_turret);
        d = attackUnit(textureA, textureA.m() / 3, textureA.l());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDestroyed) {
            return a;
        }
        return c[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return d;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return b;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && !this.isDestroyed && this.deceleration >= 1.0f && !this.isUnitInvulnerable;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 2.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 2.0f;
    }

    public HeavyTankUnit(boolean z) {
        super(z);
        this.turretBounds = new Rect();
        a(c[7], 3);
        this.radius = 15.0f;
        this.displayRadius = this.radius + 1.0f;
        this.maxHealth = 600.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = c[7];
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        a(UnitSize.normal);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!this.isDestroyed && this.rotation != 0.0f) {
            this.turretAnimationTimer += f;
            if (this.turretAnimationTimer > 1.4d) {
                this.turretAnimationTimer = 0.0f;
                this.turretFrameIndex++;
                if (this.turretFrameIndex > 2) {
                    this.turretFrameIndex = 0;
                }
            }
            if (this.flag3) {
                this.smokeTimer += f;
                if (this.smokeTimer > 9.0f) {
                    this.smokeTimer = 0.0f;
                    spawnHeavyTankSmoke();
                }
            }
        }
    }

    /* JADX INFO: renamed from: K */
    public void spawnHeavyTankSmoke() {
        GameEngine gameEngine = GameEngine.getInstance();
        float f = this.rotationSpeed;
        if (this.rotation < 0.0f) {
            f += 180.0f;
        }
        int i = 0;
        while (i <= 1) {
            float f2 = i == 0 ? -20 : 20;
            gameEngine.effectManager.createMuzzleFlash(this.posX + (Utility.fastCos(f + 180.0f + f2) * this.radius), this.posY + (Utility.fastSin(f + 180.0f + f2) * this.radius), this.posZ, f + 180.0f, 0);
            i++;
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 7000.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float q(int i) {
        return 50.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        GameEngine gameEngine = GameEngine.getInstance();
        if (!baseUnit.i()) {
            PointF pointFE = E(i);
            Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
            PointF pointFK = getShadowTexture(i);
            projectileA.K = pointFK.x;
            projectileA.L = pointFK.y;
            projectileA.ar = Color.a(SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_TERRESTRIAL_ANALOG, 150, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 40);
            projectileA.U = q(i);
            projectileA.l = baseUnit;
            projectileA.h = 60.0f;
            projectileA.t = 4.0f;
            projectileA.x = 2.0f;
            projectileA.aQ = true;
            projectileA.z = true;
            gameEngine.effectManager.createLightEffect(projectileA, -16716288);
            gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1127220);
            gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
            gameEngine.soundEngine.playSound(SoundEngine.gunFire4Sound, 0.3f, this.posX, this.posY);
            return;
        }
        PointF pointFE2 = E(i);
        pointFE2.a(this.posX, this.posY);
        Projectile projectileA2 = Projectile.a(this, this.posX, this.posY);
        projectileA2.ar = Color.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 50);
        projectileA2.U = q(i);
        projectileA2.l = baseUnit;
        projectileA2.h = 190.0f;
        projectileA2.t = 0.5f;
        projectileA2.r = 5.0f;
        projectileA2.aH = true;
        projectileA2.aI = 10.0f;
        projectileA2.aJ = 15.0f;
        projectileA2.aM = true;
        projectileA2.aQ = true;
        projectileA2.aG = true;
        gameEngine.soundEngine.playSound(SoundEngine.missileFireSound, 0.2f, this.posX, this.posY);
        gameEngine.effectManager.createLightEffect(projectileA2, -1118720);
        gameEngine.effectManager.createLightEffect(pointFE2.x, pointFE2.y, this.posZ, -1127220);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 160.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 70.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.9f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float w(int i) {
        return 0.12f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 3.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.05f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f) {
        if (!super.c(f)) {
            return false;
        }
        GameViewUtils.a((OrderableUnit) this);
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: l */
    public boolean canAttack() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: af */
    public boolean canAttackFlyingUnits() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float g(int i) {
        return 21.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        if (z) {
            return super.a_(z);
        }
        if (this.isDestroyed) {
            return super.a_(z);
        }
        return super.a(z, this.turretFrameIndex);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float H(int i) {
        return -2.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float I(int i) {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float J(int i) {
        return 12.0f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        super.e(f);
        GameViewUtils.a(this, m());
    }
}
