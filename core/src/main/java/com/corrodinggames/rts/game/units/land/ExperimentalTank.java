package com.corrodinggames.rts.game.units.land;

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
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.d */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/d.class */
public class ExperimentalTank extends LandUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture[] d = new Texture[10];
    int e;
    float f;
    Rect g;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.experimentalTank;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        Texture textureA = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_tank);
        d = PlayerTeam.getTeamColorTextures(textureA);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_tank_dead);
        b = gameEngine.renderGraphicsEngine.a(R.drawable.experimental_tank_turret);
        c = attackUnit(textureA, textureA.m() / 2, textureA.l());
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return d[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && this.posZ > -2.0f && this.buildProgress >= 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: G */
    public float getShadowOffsetX() {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: H */
    public float getShadowOffsetY() {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        if (R(i)) {
            return null;
        }
        return b;
    }

    public ExperimentalTank(boolean z) {
        super(z);
        this.g = new Rect();
        a(d[7], 2);
        this.radius = 37.0f;
        this.displayRadius = this.radius + 1.0f;
        this.maxHealth = 6000.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = d[7];
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance();
        a(UnitSize.largeUnit);
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        return true;
    }

    @Override
    // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (!this.isDead) {
            if (this.spawnExitLockTimer != 0.0f) {
                S(2);
            } else {
                S(4);
            }
        }
        if (this.isMoving) {
            this.f += f;
            if (this.f > 5.0f) {
                this.f = 0.0f;
                this.e = 1 - this.e;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bN */
    public float getPushMass() {
        return 80000.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        if (!R(i)) {
            PointF pointFE = E(i);
            Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
            PointF pointFK = getShadowOffsetForLevel(i);
            projectileA.K = pointFK.x;
            projectileA.L = pointFK.y;
            projectileA.ar = KoolArgbColor.a(255, 247, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_EISU, 129);
            projectileA.h = 120.0f;
            projectileA.t = 5.0f;
            projectileA.l = baseUnit;
            projectileA.Y = 60.0f;
            projectileA.U = 40.0f;
            projectileA.Z = 45.0f;
            projectileA.aa = true;
            projectileA.x = 2.0f;
            projectileA.aQ = true;
            projectileA.P = (short) 9;
            projectileA.x = 1.0f;
            projectileA.drawLayer = this.drawLayer;
            GameEngine gameEngine = GameEngine.getInstance();
            gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, 16745216);
            gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
            gameEngine.effectManager.createLightEffect(projectileA, -1127220);
            gameEngine.soundEngine.playSound(SoundEngine.largeGunFire1Sound, 0.3f, this.posX, this.posY);
            return;
        }
        PointF pointFE2 = E(i);
        pointFE2.a(this.posX, this.posY);
        Projectile projectileA2 = Projectile.a(this, this.posX, this.posY);
        projectileA2.ar = KoolArgbColor.a(255, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 50);
        projectileA2.U = 60.0f;
        projectileA2.l = baseUnit;
        projectileA2.h = 190.0f;
        projectileA2.t = 3.0f;
        projectileA2.r = 6.0f;
        projectileA2.aH = true;
        projectileA2.aI = 10.0f;
        projectileA2.aJ = 15.0f;
        projectileA2.aM = true;
        projectileA2.aQ = true;
        projectileA2.aG = true;
        projectileA2.drawLayer = this.drawLayer;
        GameEngine gameEngine2 = GameEngine.getInstance();
        gameEngine2.soundEngine.playSound(SoundEngine.missileFireSound, 0.2f, this.posX, this.posY);
        gameEngine2.effectManager.createLightEffect(projectileA2, -1118720);
        gameEngine2.effectManager.createLightEffect(pointFE2.x, pointFE2.y, this.posZ, -1127220);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean a(int i, BaseUnit baseUnit, boolean z, boolean z2) {
        if (!z && z2 && !isWithinEngagementRange(baseUnit)) {
            return false;
        }
        if (R(i)) {
            if (!baseUnit.i()) {
                return false;
            }
            return true;
        }
        if (baseUnit.i()) {
            return false;
        }
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 310.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        if (R(i)) {
            i -= 4;
        }
        return 110 - (i * 20);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        if (R(i)) {
            i -= 4;
        }
        return i * 20;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.4f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float bc() {
        return 1.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public int bh() {
        return 1;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 0.8f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.04f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float w(int i) {
        if (R(i)) {
            return 1.0f;
        }
        return 0.08f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        if (R(i)) {
            return 4.5f;
        }
        return 2.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.03f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.08f;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public Rect a_(boolean z) {
        if (this.isDead && !z) {
            return super.a_(z);
        }
        if (z) {
            return super.a_(z);
        }
        int i = 0 + (this.e * this.es);
        this.g.a(i, 0, i + this.es, 0 + this.et);
        return this.g;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
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
        return 20.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF G(int i) {
        PointF pointFG = super.G(i);
        float fFastCos = pointFG.x;
        float fFastSin = pointFG.y;
        if (!R(i)) {
            if (i <= 1) {
                fFastCos += Utility.fastCos(this.rotationSpeed) * 5.0f;
                fFastSin += Utility.fastSin(this.rotationSpeed) * 5.0f;
            }
            float f = (-45) + (90 * i);
            fFastCos += Utility.fastCos(this.rotationSpeed + f) * 18.0f;
            fFastSin += Utility.fastSin(this.rotationSpeed + f) * 18.0f;
        }
        tempPointF3.a(fFastCos, fFastSin);
        return tempPointF3;
    }

    public boolean R(int i) {
        if (i >= 4) {
            return true;
        }
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bl */
    public int getTechLevel() {
        return 6;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public void e(float f) {
        super.e(f);
        GameViewUtils.a(this, m());
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: cw */
    public int getTransportSlotsNeeded() {
        return 5;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: dd */
    public boolean isExperimental() {
        return true;
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
}
