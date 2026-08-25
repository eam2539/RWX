package com.corrodinggames.rts.game;

import android.graphics.Color;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.AnimationSet;
import com.corrodinggames.rts.game.units.custom.CustomProjectileTemplate;
import com.corrodinggames.rts.game.units.custom.CustomUnitSpawnList;
import com.corrodinggames.rts.game.units.custom.UnitSpawnList;
import com.corrodinggames.rts.game.units.custom.UnitSpawner;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/g.class */
public class ProjectileTemplate {
    public static final ProjectileTemplate a = new ProjectileTemplate();
    public int b;
    public int c;
        /* JADX INFO: renamed from: d */
    public boolean killParentOnHit;
        /* JADX INFO: renamed from: e */
    public boolean hasSplashDamage;
        /* JADX INFO: renamed from: f */
    public boolean noExplosion;
        /* JADX INFO: renamed from: g */
    public boolean fullSplashDamage;
        /* JADX INFO: renamed from: h */
    public boolean splashRadiusBonus;
    public float u;
    public float v;
    public short y;
    public boolean A;
    public Texture B;
    public Texture C;
    public float D;
    public float E;
    public float F;
    public float G;
    public float H;
    public float Q;
    public float R;
    public float S;
    public boolean T;
    public Texture Y;
    public Texture Z;
    public boolean aa;
    public Texture ab;
    public boolean ac;
    /* JADX INFO: renamed from: ad */
    public float beamImageOffsetRate;
    /* JADX INFO: renamed from: ah */
    public CustomUnitSpawnList trailEffect;
    /* JADX INFO: renamed from: ai */
    public CustomUnitSpawnList effectOnCreate;
    /* JADX INFO: renamed from: aj */
    public UnitSpawnList spawnProjectilesOnExplode;
    /* JADX INFO: renamed from: ak */
    public UnitSpawnList spawnProjectilesOnEndOfLife;
    /* JADX INFO: renamed from: al */
    public UnitSpawnList spawnProjectilesOnCreate;
    /* JADX INFO: renamed from: am */
    public float wobbleAmplitude;
    /* JADX INFO: renamed from: aq */
    public boolean lightCastOnGround;
    /* JADX INFO: renamed from: az */
    public boolean retargetingInFlight;
    /* JADX INFO: renamed from: aD */
    public AnimationSet retargetingInFlightSearchOnlyTags;
    /* JADX INFO: renamed from: aI */
    public boolean flameWeapon;
    /* JADX INFO: renamed from: aK */
    public float targetGroundSpread;
    /* JADX INFO: renamed from: aL */
    public float targetGroundHeightOffset;
    /* JADX INFO: renamed from: aM */
    public float speedSpread;
    /* JADX INFO: renamed from: aN */
    public boolean ignoreParentShootDamageMultiplier;
    /* JADX INFO: renamed from: aO */
    public boolean explodeOnEndOfLife;
    /* JADX INFO: renamed from: aP */
    public float pushForce;
    /* JADX INFO: renamed from: aQ */
    public float pushVelocity;
    /* JADX INFO: renamed from: aV */
    public float armourIgnoreAmount;
    /* JADX INFO: renamed from: aX */
    public CustomUnitSpawnList explodeEffect;
    /* JADX INFO: renamed from: aY */
    public CustomUnitSpawnList explodeEffectOnShield;
    /* JADX INFO: renamed from: aZ */
    public UnitSpawner spawnUnit;
    /* JADX INFO: renamed from: ba */
    public int unloadUpToXUnitsFromSource;
    /* JADX INFO: renamed from: bb */
    public boolean teleportSource;
    /* JADX INFO: renamed from: bc */
    public boolean convertHitToSourceTeam;
    /* JADX INFO: renamed from: bd */
    public AnimationSet tags;
    public int i = 35;
    public float j = -1.0f;
    public boolean k = false;
    public boolean l = false;
    public boolean m = false;
    public boolean n = false;
    public float o = 1.0f;
    public boolean p = false;
    public boolean q = false;
    public boolean r = false;
    public boolean s = false;
    public boolean t = false;
    public float w = 5.0f;
    public short x = -1;
    public short z = -1;
    public boolean I = false;
    public boolean J = false;
    public float K = -1.0f;
    public boolean L = false;
    public boolean M = false;
    public boolean N = false;
    public float O = -1.0f;
    public float P = -1.0f;
    public boolean U = false;
    public boolean V = false;
    public boolean W = false;
    public boolean X = false;
    public boolean ae = false;
    public boolean af = false;
    public float ag = 3.0f;
    /* JADX INFO: renamed from: an */
    public float wobbleFrequency = 5.0f;
    /* JADX INFO: renamed from: ao */
    public int lightColor = -1;
    /* JADX INFO: renamed from: ap */
    public float lightSize = 0.5f;
    /* JADX INFO: renamed from: ar */
    public boolean largeHitEffect = false;
    /* JADX INFO: renamed from: as */
    public float ballisticDelayMoveHeight = -1.0f;
    /* JADX INFO: renamed from: at */
    public float ballisticHeight = -1.0f;
    /* JADX INFO: renamed from: au */
    public float targetSpeed = -1.0f;
    /* JADX INFO: renamed from: av */
    public float targetSpeedAcceleration = 0.1f;
    /* JADX INFO: renamed from: aw */
    public boolean autoTargetingOnDeadTarget = false;
    /* JADX INFO: renamed from: ax */
    public float autoTargetingOnDeadTargetRange = 120.0f;
    /* JADX INFO: renamed from: ay */
    public float autoTargetingOnDeadTargetLead = 15.0f;
    /* JADX INFO: renamed from: aA */
    public float retargetingInFlightSearchDelay = 5.0f;
    /* JADX INFO: renamed from: aB */
    public float retargetingInFlightSearchRange = 120.0f;
    /* JADX INFO: renamed from: aC */
    public float retargetingInFlightSearchLead = 15.0f;
    /* JADX INFO: renamed from: aE */
    public int color = Color.a(255, 255, 255, 255);
    /* JADX INFO: renamed from: aF */
    public float drawSize = 1.0f;
    /* JADX INFO: renamed from: aG */
    public float teamColorRatio = 0.0f;
    /* JADX INFO: renamed from: aH */
    public float teamColorRatioSourceRatio = 1.0f;
    /* JADX INFO: renamed from: aJ */
    public boolean hitSound = true;
    /* JADX INFO: renamed from: aR */
    public float buildingDamageMultiplier = 1.0f;
    /* JADX INFO: renamed from: aS */
    public float shieldDamageMultiplier = 1.0f;
    /* JADX INFO: renamed from: aT */
    public float shieldDeflectionMultiplier = 1.0f;
    /* JADX INFO: renamed from: aU */
    public float hullDamageMultiplier = 1.0f;
    /* JADX INFO: renamed from: aW */
    public float areaExpandTime = -1.0f;
    public FastArrayList be = null;
    public FastArrayList bf = null;
    public FastArrayList bg = null;

    public CustomUnitSpawnList a(BaseUnit baseUnit) {
        FastArrayList<UnitFilter> fastArrayList = this.bg;
        if (fastArrayList != null && fastArrayList.size > 0) {
            for (UnitFilter unitFilter : fastArrayList) {
                if (unitFilter.a(baseUnit) && unitFilter.spawnList != null) {
                    return unitFilter.spawnList;
                }
            }
            return null;
        }
        return null;
    }

    public float a(BaseUnit baseUnit, float f, boolean z) {
        FastArrayList<UnitFilter> fastArrayList;
        float f2;
        if (!z) {
            fastArrayList = this.be;
        } else {
            fastArrayList = this.bf;
        }
        if (fastArrayList != null && fastArrayList.size > 0) {
            for (UnitFilter unitFilter : fastArrayList) {
                if (unitFilter.a(baseUnit)) {
                    if (!z) {
                        if (unitFilter.e != null) {
                            unitFilter.e.h(baseUnit);
                        }
                        f2 = unitFilter.c;
                    } else {
                        if (unitFilter.f != null) {
                            unitFilter.f.h(baseUnit);
                        }
                        f2 = unitFilter.d;
                    }
                    f *= f2;
                }
            }
        }
        return f;
    }

    public static void a(ProjectileTemplate projectileTemplate, GameOutputStream gameOutputStream) throws IOException {
        if (projectileTemplate == a) {
            gameOutputStream.writeByte(0);
        } else if (projectileTemplate instanceof CustomProjectileTemplate) {
            gameOutputStream.writeByte(1);
            CustomProjectileTemplate.a((CustomProjectileTemplate) projectileTemplate, gameOutputStream);
        } else {
            GameEngine.logWarningAndStack("writeOutLink: Unhandled projectile type");
            gameOutputStream.writeByte(0);
        }
    }

    public static ProjectileTemplate a(GameInputStream gameInputStream) throws IOException {
        byte b = gameInputStream.readByte();
        if (b == 0) {
            return a;
        }
        if (b == 1) {
            ProjectileTemplate projectileTemplateB = CustomProjectileTemplate.b(gameInputStream);
            if (projectileTemplateB == null) {
                return a;
            }
            return projectileTemplateB;
        }
        throw new IOException("Unknown projectile type:" + ((int) b));
    }
}
