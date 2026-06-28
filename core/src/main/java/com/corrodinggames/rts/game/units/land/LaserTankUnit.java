package com.corrodinggames.rts.game.units.land;

import com.corrodinggames.rts.R;
import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.UnitSize;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.GameViewUtils;
import io.github.rwx.geometry.PointF;
import io.github.rwx.geometry.Rect;
import io.github.rwx.render.canvas.KoolArgbColor;
import io.github.rwx.render.canvas.KoolPaint;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.e.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/e/k.class */
public class LaserTankUnit extends LandUnit {
    static Texture a = null;
    static Texture b = null;
    static Texture c = null;
    static Texture d = null;
    static Texture[] e = new Texture[10];
    static Texture f = null;
    Rect g;

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: b, reason: merged with bridge method [inline-methods] */
    public UnitTypeEnum r() {
        return UnitTypeEnum.laserTank;
    }

    public static void f() {
        GameEngine gameEngine = GameEngine.getInstance();
        b = gameEngine.renderGraphicsEngine.a(R.drawable.laser_tank_base);
        a = gameEngine.renderGraphicsEngine.a(R.drawable.laser_tank_dead);
        c = gameEngine.renderGraphicsEngine.a(R.drawable.laser_tank_turrent);
        d = gameEngine.renderGraphicsEngine.a(R.drawable.laser_tank_charge);
        e = PlayerTeam.getTeamColorTextures(b);
        f = attackUnit(b, b.m(), b.l());
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
        return f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: F */
    public boolean canDrawShadow() {
        return GameEngine.getInstance().settingsEngine.renderExtraShadows && !this.isDead;
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

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public Texture d(int i) {
        return c;
    }

    public LaserTankUnit(boolean z) {
        super(z);
        this.g = new Rect();
        a(b, 1);
        this.radius = 14.0f;
        this.displayRadius = this.radius + 2.0f;
        this.maxHealth = 300.0f;
        this.currentHealth = this.maxHealth;
        this.baseTexture = b;
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    public boolean e() {
        GameEngine.getInstance();
        this.baseTexture = a;
        S(0);
        this.isAlive = false;
        a(UnitSize.small);
        return true;
    }

    @Override
    // com.corrodinggames.rts.game.units.land.LandUnit, com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    /* JADX INFO: renamed from: a */
    public void update(float f2) {
        super.update(f2);
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float q(int i) {
        return 450.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public void a(BaseUnit baseUnit, int i) {
        PointF pointFE = E(i);
        Projectile projectileA = Projectile.a(this, pointFE.x, pointFE.y);
        projectileA.U = q(i);
        projectileA.l = baseUnit;
        projectileA.h = 8.0f;
        projectileA.B = true;
        projectileA.A = true;
        projectileA.aQ = true;
        projectileA.ar = KoolArgbColor.a(80, 255, 0, 0);
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createLightEffect(pointFE.x, pointFE.y, this.posZ, -1127220);
        gameEngine.effectManager.createFlameEffect(pointFE.x, pointFE.y, this.posZ, this.movementLevels[i].targetX);
        gameEngine.soundEngine.playSound(SoundEngine.plasmaFireSound, 0.3f, pointFE.x, pointFE.y);
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bW */
    public float getSecondaryBarProgress() {
        if (this.movementLevels[0].rotation > 0.0f) {
            return 1.0f - (this.movementLevels[0].rotation / b(0));
        }
        if (this.movementLevels[0].speed != 0.0f) {
            return this.movementLevels[0].speed / e(0);
        }
        return super.getSecondaryBarProgress();
    }

    @Override // com.corrodinggames.rts.game.units.BaseUnit
    /* JADX INFO: renamed from: bX */
    public boolean isSecondaryBarRecharging() {
        return this.movementLevels[0].rotation > 0.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float m() {
        return 190.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float b(int i) {
        return 450.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float e(int i) {
        return 80.0f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: z */
    public float getMoveSpeed() {
        return 0.7f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    /* JADX INFO: renamed from: A */
    public float getMaxTurnSpeed() {
        return 1.5f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float B() {
        return 0.1f;
    }

    @Override // com.corrodinggames.rts.game.units.OrderableUnit
    public float c(int i) {
        return 3.0f;
    }

    @Override
    // com.corrodinggames.rts.game.units.OrderableUnit, com.corrodinggames.rts.game.units.BaseUnit, com.corrodinggames.rts.gameFramework.GameObject
    public boolean c(float f2) {
        if (!super.c(f2)) {
            return false;
        }
        GameEngine gameEngine = GameEngine.getInstance();
        GameViewUtils.a((OrderableUnit) this);
        if (!this.isDead) {
            float fE = this.movementLevels[0].speed / e(0);
            if (fE != 0.0f) {
                PointF pointFE = E(0);
                gameEngine.renderGraphicsEngine.i();
                gameEngine.renderGraphicsEngine.b(pointFE.x - gameEngine.viewpointXSnapped, pointFE.y - gameEngine.viewpointYSnapped);
                gameEngine.renderGraphicsEngine.a(fE, fE);
                gameEngine.renderGraphicsEngine.a(d, 0.0f, 0.0f, (KoolPaint) null);
                gameEngine.renderGraphicsEngine.j();
                return true;
            }
            return true;
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
        return 0.12f;
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
        return 19.0f;
    }
}
