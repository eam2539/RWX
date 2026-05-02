package com.corrodinggames.rts.game.units.buildings.turrets;

import android.graphics.Color;
import android.graphics.PointF;
import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.audio.SoundEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.a.h */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/a/h.class */
class GunTurret extends TurretImplementation {

    /* JADX INFO: renamed from: b */
    final /* synthetic */ TurretFactory turretFactory;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    GunTurret(TurretFactory turretFactory) {
        super(turretFactory);
        this.turretFactory = turretFactory;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    public String c() {
        return TurretFactory.GUN_TURRET_TYPE;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    public int d() {
        return UnitTypeEnum.turret.c();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: d */
    public Texture getTurretTopTexture(int i) {
        return TurretFactory.turretTopL1Texture;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    float getAttackRange() {
        return 165.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: b */
    public float getAttackDamage(int i) {
        return 41.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public float getAttackDelay(int i) {
        return 30.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: g */
    public float getTurretHeight(int i) {
        return 21.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void fireProjectile(BaseUnit baseUnit, int i) {
        PointF pointFC = getTurretType(i);
        Projectile projectileA = Projectile.a(this.turretFactory, pointFC.x, pointFC.y);
        PointF pointFK = this.turretFactory.getShadowTexture(i);
        projectileA.K = pointFK.x;
        projectileA.L = pointFK.y;
        projectileA.l = baseUnit;
        projectileA.h = 60.0f;
        projectileA.t = 5.0f;
        projectileA.ar = Color.a(255, 100, 30, 30);
        projectileA.U = getAttackDamage(i);
        projectileA.P = (short) 5;
        projectileA.x = 1.0f;
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.effectManager.createLightEffect(pointFC.x, pointFC.y, this.turretFactory.posZ, -1127220);
        gameEngine.effectManager.createFlameEffect(pointFC.x, pointFC.y, this.turretFactory.posZ, this.turretFactory.movementLevels[i].targetX);
        gameEngine.soundEngine.playSoundAt(SoundEngine.gunFire3Sound, 0.3f, 1.0f + Utility.randomFloatInRange(-0.07f, 0.07f), pointFC.x, pointFC.y);
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: b */
    public int getTechLevel() {
        return 1;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void copyFrom(TurretImplementation turretImplementation) {
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        this.turretFactory.updateTurretRotation(f);
    }
}
