package com.corrodinggames.rts.game.units.buildings.turrets;

import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.PointF;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.a.f */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/a/f.class */
public class GunTurretT2 extends TurretImplementation {

    /* JADX INFO: renamed from: b */
    final /* synthetic */ TurretFactory turretFactory;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    GunTurretT2(TurretFactory turretFactory) {
        super(turretFactory);
        this.turretFactory = turretFactory;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    public String c() {
        return TurretFactory.GUN_TURRET_T2_TYPE;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    public int d() {
        return UnitTypeEnum.turret.c() + TurretFactory.upgradeToT2Action.getCostAmount();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: d */
    public Texture getTurretTopTexture(int i) {
        return TurretFactory.turretTopL2Texture;
    }

    @Override
        // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
        /* JADX INFO: renamed from: a */
    float getAttackRange() {
        return 185.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public float getAttackDelay(int i) {
        return 20.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: b */
    public float getAttackDamage(int i) {
        return 44.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: g */
    public float getTurretHeight(int i) {
        return 21.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: c */
    public PointF getTurretType(final int integer) {
        final PointF b = this.turretFactory.E(integer);
        float n = this.turretFactory.E() ? this.turretFactory.rotationSpeed : this.turretFactory.movementLevels[integer].targetX;
        n += ((this.turretFactory.turretType == 1) ? -90 : 90);
        final PointF pointF = b;
        pointF.x += Utility.fastCos(n) * 4.0f;
        final PointF pointF2 = b;
        pointF2.y += Utility.fastSin(n) * 4.0f;
        return b;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void fireProjectile(BaseUnit baseUnit, int i) {
        PointF turretType = getTurretType(i);
        Projectile projectileA = Projectile.a(this.turretFactory, turretType.x, turretType.y);
        PointF pointFK = this.turretFactory.getShadowTexture(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.l = baseUnit;
        projectileA.h = 60.0f;
        projectileA.t = 6.0f;
        projectileA.ar = KoolArgbColor.a(255, 40, 30, 110);
        projectileA.U = getAttackDamage(i);
        projectileA.P = (short) 5;
        projectileA.x = 1.0f;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createLightEffect(turretType.x, turretType.y, this.turretFactory.posZ, -1127220);
        gameEngine.effectManager.createFlameEffect(turretType.x, turretType.y, this.turretFactory.posZ, this.turretFactory.movementLevels[i].targetX);
        gameEngine.soundEngine.playSoundAt(SoundEngine.gunFire3Sound, 0.3f, 1.0f + Utility.randomFloatInRange(-0.07f, 0.07f), turretType.x, turretType.y);
        this.turretFactory.turretType = this.turretFactory.turretType == 1 ? 0 : 1;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: b */
    public int getTechLevel() {
        return 2;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void copyFrom(TurretImplementation turretImplementation) {
        this.turretFactory.maxHealth += 400.0f;
        this.turretFactory.currentHealth += 400.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        this.turretFactory.updateTurretRotation(f);
    }
}
