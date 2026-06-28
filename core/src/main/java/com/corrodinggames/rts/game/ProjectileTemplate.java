package com.corrodinggames.rts.game;

import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.custom.*;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import io.github.rwx.render.canvas.KoolArgbColor;

import java.io.IOException;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.g */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/g.class */
public class ProjectileTemplate {
    public static final ProjectileTemplate a = new ProjectileTemplate();
    public int b;
    public int c;
    public boolean d;
    public boolean e;
    public boolean f;
    public boolean g;
    public boolean h;
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
    public float beamImageOffsetRate;
    public CustomUnitSpawnList trailEffect;
    public CustomUnitSpawnList effectOnCreate;
    public UnitSpawnList spawnProjectilesOnExplode;
    public UnitSpawnList spawnProjectilesOnEndOfLife;
    public UnitSpawnList spawnProjectilesOnCreate;
    public float wobbleAmplitude;
    public boolean lightCastOnGround;
    public boolean retargetingInFlight;
    public AnimationSet retargetingInFlightSearchOnlyTags;
    public boolean flameWeapon;
    public float targetGroundSpread;
    public float targetGroundHeightOffset;
    public float speedSpread;
    public boolean ignoreParentShootDamageMultiplier;
    public boolean explodeOnEndOfLife;
    public float pushForce;
    public float pushVelocity;
    public float armourIgnoreAmount;
    public CustomUnitSpawnList explodeEffect;
    public CustomUnitSpawnList explodeEffectOnShield;
    public UnitSpawner spawnUnit;
    public int unloadUpToXUnitsFromSource;
    public boolean teleportSource;
    public boolean convertHitToSourceTeam;
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
    public float wobbleFrequency = 5.0f;
    public int lightColor = -1;
    public float lightSize = 0.5f;
    public boolean largeHitEffect = false;
    public float ballisticDelayMoveHeight = -1.0f;
    public float ballisticHeight = -1.0f;
    public float targetSpeed = -1.0f;
    public float targetSpeedAcceleration = 0.1f;
    public boolean autoTargetingOnDeadTarget = false;
    public float autoTargetingOnDeadTargetRange = 120.0f;
    public float autoTargetingOnDeadTargetLead = 15.0f;
    public float retargetingInFlightSearchDelay = 5.0f;
    public float retargetingInFlightSearchRange = 120.0f;
    public float retargetingInFlightSearchLead = 15.0f;
    public int color = KoolArgbColor.a(255, 255, 255, 255);
    public float drawSize = 1.0f;
    public float teamColorRatio = 0.0f;
    public float teamColorRatioSourceRatio = 1.0f;
    public boolean hitSound = true;
    public float buildingDamageMultiplier = 1.0f;
    public float shieldDamageMultiplier = 1.0f;
    public float shieldDeflectionMultiplier = 1.0f;
    public float hullDamageMultiplier = 1.0f;
    public float areaExpandTime = -1.0f;
    public FastArrayList be = null;
    public FastArrayList bf = null;
    public FastArrayList bg = null;

    public CustomUnitSpawnList a(BaseUnit baseUnit) {
        FastArrayList<UnitFilter> fastArrayList = this.bg;
        if (fastArrayList != null && fastArrayList.size > 0) {
            for (UnitFilter unitFilter : fastArrayList) {
                if (unitFilter.a(baseUnit) && unitFilter.g != null) {
                    return unitFilter.g;
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
