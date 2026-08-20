package com.corrodinggames.rts.game.units.buildings.turrets;

import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import io.github.rwx.geometry.PointF;
import io.github.rwx.render.canvas.KoolArgbColor;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.d.a.e */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/d/a/e.class */
class FlamethrowerTurret extends TurretImplementation {

    /* JADX INFO: renamed from: b */
    final /* synthetic */ TurretFactory turretFactory;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    FlamethrowerTurret(TurretFactory turretFactory) {
        super(turretFactory);
        this.turretFactory = turretFactory;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    public String c() {
        return TurretFactory.FLAMETHROWER_TURRET_TYPE;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    public int d() {
        return UnitTypeEnum.turret.c() + TurretFactory.upgradeToFlamethrowerAction.getCostAmount();
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: d */
    public Texture getTurretTopTexture(int i) {
        return TurretFactory.turretTopFlameTexture;
    }

    @Override
        // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
        /* JADX INFO: renamed from: a */
    float getAttackRange() {
        return 155.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public float getAttackDelay(int i) {
        return 5.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: b */
    public float getAttackDamage(int i) {
        return 4.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void fireProjectile(BaseUnit baseUnit, int i) {
        PointF pointFC = getTurretType(i);
        Projectile projectileA = Projectile.a(this.turretFactory, pointFC.x, pointFC.y);
        projectileA.lifeTimer = 60.0f;
        projectileA.speed = 3.0f + ((this.turretFactory.turretType * 13) % 2.0f);
        projectileA.playsHitSound = false;
        projectileA.spawnEmitterOnHit = true;
        projectileA.color = KoolArgbColor.a(105, 255, 255, 255);
        projectileA.textureFrame = (short) 3;
        projectileA.renderScale = 1.3f;
        PointF pointFA = baseUnit.a(pointFC.x, pointFC.y, projectileA.speed, projectileA.lifeTimer, getAttackRange());
        projectileA.fliesToPosition = true;
        projectileA.hasFixedTarget = true;
        projectileA.targetX = pointFA.x;
        projectileA.targetY = pointFA.y;
        projectileA.targetX += (-15) + ((this.turretFactory.turretType * 13) % 30);
        projectileA.targetY += (-15) + ((63 + (this.turretFactory.turretType * 33)) % 30);
        projectileA.drawLayer = 3;
        projectileA.splashDamage = getAttackDamage(i);
        projectileA.explosionRadius = 65.0f;
        projectileA.damageEnemiesOnly = true;
        projectileA.C = true;
        GameEngine gameEngine = GameEngine.getInstance();
        this.turretFactory.turretType++;
        if (this.turretFactory.turretType > 10) {
            this.turretFactory.turretType = 0;
            gameEngine.effectManager.createFlameEffect(pointFC.x, pointFC.y, this.turretFactory.posZ, this.turretFactory.movementLevels[i].targetX);
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: b */
    public int getTechLevel() {
        return 2;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void copyFrom(TurretImplementation turretImplementation) {
        this.turretFactory.maxHealth += 900.0f;
        this.turretFactory.currentHealth += 900.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: a */
    public void update(float f) {
        if (this.turretFactory.currentHealth < this.turretFactory.maxHealth) {
            this.turretFactory.currentHealth += 0.15f * f;
            if (this.turretFactory.currentHealth > this.turretFactory.maxHealth) {
                this.turretFactory.currentHealth = this.turretFactory.maxHealth;
            }
        }
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: e */
    public float getTurretRestingRotationSpeed(int i) {
        return 11.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: f */
    public float getTurretTurnSpeed(int i) {
        return 2.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: g */
    public float getTurretHeight(int i) {
        return 18.0f;
    }

    @Override // com.corrodinggames.rts.game.units.buildings.turrets.TurretImplementation
    /* JADX INFO: renamed from: h */
    public float getTurretOffset(int i) {
        return 0.0f;
    }
}
