package com.corrodinggames.rts.game.units.air;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.b.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/b/e.class */
public class Gunship extends AirUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture d = null;
    static Texture[] e = new Texture[10];
    float f;
    Rect g;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.gunShip;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.renderGraphicsEngine.a(R.drawable.gunship);
        c = gameEngine.renderGraphicsEngine.a(R.drawable.gunship_shadow);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.gunship_dead);
        e = PlayerTeam.getTeamColorTextures(b);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d() {
        if (this.isDead) {
            return a;
        }
        return e[this.team.getTeamColorIndex()];
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture k() {
        return c;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return null;
    }

    public Gunship(boolean z) {
        super(z);
        this.f = 0.0f;
        this.g = new Rect();
        T(25);
        U(35);
        this.radius = 15.0f;
        this.displayRadius = this.radius + 0.0f;
        this.maxHealth = 260.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
        this.shadowTexture = c;
        this.posZ = 0.0f;
        S(5);
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance().effectManager.createSmallExplosion(this.posX, this.posY, this.posZ);
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.OrderableUnit
    public boolean I() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.MovableUnit, com.corrodinggames.rts.game.units.BaseUnit
    public boolean i() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.air.AirUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        super.update(f);
        if (this.isDead) {
            return;
        }
        this.f += 2.0f * f;
        if (this.f > 360.0f) {
            this.f -= 360.0f;
        }
        this.posZ = Utility.distanceSq(this.posZ, 20.0f + (Utility.fastSin(this.f) * 1.5f), 0.1f * f);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public PointF E(int i) {
        float fG = g(i);
        float f = this.rotationSpeed;
        tempPointF2.a(this.posX + (Utility.fastCos(f) * fG), this.posY + (Utility.fastSin(f) * fG));
        return tempPointF2;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float q(int i) {
        return 35.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y, this.posZ, i);
        PointF pointFK = getShadowTexture(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.ar = Color.a(255, 150, SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_DATA_SERVICE, 40);
        projectileA.U = q(i);
        projectileA.l = baseUnit;
        projectileA.h = 80.0f;
        projectileA.t = 4.0f;
        projectileA.x = 2.0f;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1127220);
        gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
        gameEngine.soundEngine.playSound(SoundEngine.gunFire4Sound, 0.3f, this.posX, this.posY);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 140.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 40.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        if (this.posZ < 15.0f) {
            return 0.0f;
        }
        return 1.4f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.4f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: bi */
    public boolean isSlidingMovement() {
        return true;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 99.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public boolean E() {
        return false;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: C */
    public float getMoveAccelerationSpeed() {
        return 0.2f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float D() {
        return 0.1f;
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
        return 15.0f;
    }
}
