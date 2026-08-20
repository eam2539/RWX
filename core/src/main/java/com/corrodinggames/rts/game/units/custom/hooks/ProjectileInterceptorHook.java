package com.corrodinggames.rts.game.units.custom.hooks;

import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.buildings.LaserDefense;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.gameFramework.Utility;
import io.github.rwx.geometry.PointF;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.b.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/b/k.class */
public class ProjectileInterceptorHook extends CustomUnitRenderHook {
    public static final CustomUnitRenderHook a = new ProjectileInterceptorHook();
    static final PointF b = new PointF();

    @Override // com.corrodinggames.rts.game.units.custom.hooks.CustomUnitRenderHook
    public void b(CustomUnit customUnit, float f) {
        CustomUnitConfig customUnitConfig = customUnit.unitConfig;
        int techLevel = customUnit.getTechLevel();
        for (int i = 0; i < techLevel; i++) {
            TurretConfig turretConfig = customUnitConfig.turrets[i];
            if (turretConfig.laserDefenceEnergyUse != null && customUnit.currentEnergy > 0.0f && !customUnit.v) {
                float fFloatValue = turretConfig.laserDefenceEnergyUse.floatValue();
                b.a(customUnit.E(i));
                float fM = customUnit.m();
                if (turretConfig.limitingRange < 99999.0f) {
                    fM = turretConfig.limitingRange;
                }
                if (LaserDefense.attackTarget(customUnit, b.x, b.y, customUnit.posZ, fM, fFloatValue)) {
                }
                if (customUnit.currentEnergy < 0.0f) {
                    customUnit.currentEnergy = 0.0f;
                    customUnit.v = true;
                }
            }
            if (turretConfig.interceptProjectilesWithTags != null) {
                a(customUnit, turretConfig);
            }
        }
    }

    public static void a(CustomUnit customUnit, TurretConfig turretConfig) {
        if (!customUnit.a(turretConfig)) {
            return;
        }
        float f = turretConfig.interceptProjectilesAndTargetingGroundUnderDistance;
        float f2 = turretConfig.interceptProjectilesAndUnderDistance;
        float f3 = turretConfig.interceptProjectilesAndOverHeight;
        Projectile projectile = null;
        AnimationSet animationSet = turretConfig.interceptProjectilesWithTags;
        Object[] objArrA = Projectile.a.a();
        int i = Projectile.a.size;
        for (int i2 = 0; i2 < i; i2++) {
            Projectile projectile2 = (Projectile) objArrA[i2];
            if (projectile2.animationSet != null && projectile2.posZ > f3 && AnimationTag.a(projectile2.animationSet, animationSet) && Utility.distanceSq(customUnit.posX, customUnit.posY, projectile2.posX, projectile2.posY) < f2 * f2 && ((Utility.distanceSq(customUnit.posX, customUnit.posY, projectile2.targetX, projectile2.targetY) < f * f || f < 0.0f) && ((projectile2.sourceUnit == null || (!projectile2.sourceUnit.team.d(customUnit.team) && projectile2.sourceUnit.team != customUnit.team)) && projectile2.lifeTimer > 0.0f && !a(projectile2)))) {
                projectile = projectile2;
            }
        }
        if (projectile != null) {
            customUnit.b(turretConfig);
            Projectile projectileA = customUnit.a((BaseUnit) null, projectile.posX, projectile.posY, turretConfig.turretIndex, (CustomProjectileTemplate) null, 0);
            projectileA.fliesToPosition = true;
            projectileA.parentProjectile = projectile;
        }
    }

    public static boolean a(Projectile projectile) {
        Object[] objArrA = Projectile.a.a();
        int i = Projectile.a.size;
        for (int i2 = 0; i2 < i; i2++) {
            Projectile projectile2 = (Projectile) objArrA[i2];
            if (projectile2 != projectile && projectile2.parentProjectile == projectile) {
                return true;
            }
        }
        return false;
    }
}
