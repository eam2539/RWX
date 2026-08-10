package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.Projectile;
import com.corrodinggames.rts.game.ProjectileTemplate;
import com.corrodinggames.rts.game.UnitFilter;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.GameObject;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.network.GameInputStream;
import com.corrodinggames.rts.gameFramework.network.GameOutputStream;
import com.corrodinggames.rts.gameFramework.utility.FastArrayList;
import com.corrodinggames.rts.gameFramework.utility.IniFile;
import io.github.rwx.geometry.PointF;
import io.github.rwx.render.canvas.KoolArgbColor;

import java.io.IOException;
import java.util.Iterator;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bh */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bh.class */
public class CustomProjectileTemplate extends ProjectileTemplate {

    /* JADX INFO: renamed from: bh */
    public String projectileName;

    /* JADX INFO: renamed from: bi */
    public int projectileId;

    /* JADX INFO: renamed from: bj */
    public CustomUnitConfig customUnitConfig;

    public String renderExtensionId;
    public String renderExtensionVariant;
    public String projectileObserverId;
    public String projectileObserverVariant;
    public boolean reuseAcrossTurrets;

    public static void a(CustomProjectileTemplate customProjectileTemplate, CustomUnitConfig customUnitConfig, IniFile iniFile, String str) throws ConfigParseException {
        Integer logicBooleanUnit = iniFile.getLogicBooleanUnit(str, "directDamage", (Integer) null);
        Integer logicBooleanUnit2 = iniFile.getLogicBooleanUnit(str, "areaDamage", (Integer) null);
        if (logicBooleanUnit == null && logicBooleanUnit2 == null) {
            throw new RuntimeException("[" + str + "]: directDamage or areaDamage must be set");
        }
        customProjectileTemplate.s = iniFile.getBoolean(str, "targetGround", Boolean.valueOf(customProjectileTemplate.s)).booleanValue();
        customProjectileTemplate.t = iniFile.getBoolean(str, "targetGround_includeTargetHeight", Boolean.valueOf(customProjectileTemplate.t)).booleanValue();
        Integer logicBooleanUnit3 = iniFile.getLogicBooleanUnit(str, "areaRadius", (Integer) null);
        if (logicBooleanUnit3 != null) {
            customProjectileTemplate.i = logicBooleanUnit3.intValue();
        }
        customProjectileTemplate.b = iniFile.getLogicBooleanUnit(str, "directDamage", Integer.valueOf(customProjectileTemplate.b)).intValue();
        customProjectileTemplate.c = iniFile.getLogicBooleanUnit(str, "areaDamage", Integer.valueOf(customProjectileTemplate.c)).intValue();
        customProjectileTemplate.d = iniFile.getBoolean(str, "interceptProjectile_removeTargetLifeOnly", Boolean.valueOf(customProjectileTemplate.d)).booleanValue();
        customProjectileTemplate.g = iniFile.getBoolean(str, "areaDamageNoFalloff", Boolean.valueOf(customProjectileTemplate.g)).booleanValue();
        customProjectileTemplate.j = iniFile.getFloat(str, "areaIgnoreUnitsCloserThan", Float.valueOf(customProjectileTemplate.j)).floatValue();
        customProjectileTemplate.h = iniFile.getBoolean(str, "areaRadiusFromEdge", Boolean.valueOf(customProjectileTemplate.h)).booleanValue();
        if ("only-ignoreEnemy".equalsIgnoreCase(iniFile.getString(str, "friendlyFire", (String) null))) {
            customProjectileTemplate.l = true;
        } else {
            Boolean bool = iniFile.getBoolean(str, "friendlyFire", (Boolean) null);
            if (bool != null) {
                customProjectileTemplate.l = false;
                customProjectileTemplate.k = bool.booleanValue();
            }
        }
        customProjectileTemplate.m = iniFile.getBoolean(str, "areaHitAirAndLandAtSameTime", Boolean.valueOf(customProjectileTemplate.m)).booleanValue();
        customProjectileTemplate.n = iniFile.getBoolean(str, "areaHitUnderwaterAlways", Boolean.valueOf(customProjectileTemplate.n)).booleanValue();
        customProjectileTemplate.o = iniFile.getFloat(str, "deflectionPower", Float.valueOf(customProjectileTemplate.o)).floatValue();
        customProjectileTemplate.p = iniFile.getBoolean(str, "nukeWeapon", Boolean.valueOf(customProjectileTemplate.p)).booleanValue();
        customProjectileTemplate.q = iniFile.getBoolean(str, "shouldRevealFog", Boolean.valueOf(customProjectileTemplate.q)).booleanValue();
        customProjectileTemplate.r = iniFile.getBoolean(str, "alwaysVisibleInFog", Boolean.valueOf(customProjectileTemplate.r)).booleanValue();
        customProjectileTemplate.v = iniFile.getFloatStrict(str, "life").floatValue();
        customProjectileTemplate.u = iniFile.getTime(str, "delayedStartTimer", Float.valueOf(0.0f)).floatValue();
        customProjectileTemplate.w = iniFile.getFloat(str, "speed", Float.valueOf(customProjectileTemplate.w)).floatValue();
        customProjectileTemplate.x = iniFile.getShort(str, "frame", Short.valueOf(customProjectileTemplate.x)).shortValue();
        customProjectileTemplate.y = iniFile.getShort(str, "drawType", Short.valueOf(customProjectileTemplate.y)).shortValue();
        customProjectileTemplate.z = iniFile.getShort(str, "shadowFrame", Short.valueOf(customProjectileTemplate.z)).shortValue();
        Texture textureA = customUnitConfig.a(iniFile, str, "image");
        if (textureA != null) {
            customProjectileTemplate.B = textureA;
        }
        Texture textureA2 = customUnitConfig.a(iniFile, str, "shadowImage");
        if (textureA2 != null) {
            customProjectileTemplate.C = textureA2;
        }
        customProjectileTemplate.beamImageOffsetRate = iniFile.getFloat(str, "beamImageOffsetRate", Float.valueOf(customProjectileTemplate.beamImageOffsetRate)).floatValue();
        Texture textureA3 = customUnitConfig.a(iniFile, str, "beamImage");
        if (textureA3 != null) {
            customProjectileTemplate.Y = textureA3;
            customProjectileTemplate.X = true;
            if (textureA3.q < 20 && !GameEngine.isDedicatedServer()) {
                throw new RuntimeException("beamImage height must currently be 20 pixels or greater (performance when tiling)");
            }
        }
        Texture textureA4 = customUnitConfig.a(iniFile, str, "beamImageStart");
        if (textureA4 != null) {
            customProjectileTemplate.Z = textureA4;
            if (textureA3 == null) {
                throw new RuntimeException("beamImageStart requires beamImage to be set");
            }
        }
        customProjectileTemplate.aa = iniFile.getBoolean(str, "beamImageStartRotated", (Boolean) false).booleanValue();
        Texture textureA5 = customUnitConfig.a(iniFile, str, "beamImageEnd");
        if (textureA5 != null) {
            customProjectileTemplate.ab = textureA5;
            if (textureA3 == null) {
                throw new RuntimeException("beamImageEnd requires beamImage to be set");
            }
        }
        customProjectileTemplate.ac = iniFile.getBoolean(str, "beamImageEndRotated", (Boolean) false).booleanValue();
        customProjectileTemplate.A = iniFile.getBoolean(str, "invisible", Boolean.valueOf(customProjectileTemplate.A)).booleanValue();
        customProjectileTemplate.D = iniFile.getFloat(str, "initialUnguidedSpeedHeight", Float.valueOf(customProjectileTemplate.D)).floatValue();
        customProjectileTemplate.E = iniFile.getFloat(str, "initialUnguidedSpeedX", Float.valueOf(customProjectileTemplate.E)).floatValue();
        customProjectileTemplate.F = iniFile.getFloat(str, "initialUnguidedSpeedY", Float.valueOf(customProjectileTemplate.F)).floatValue();
        customProjectileTemplate.G = iniFile.getFloat(str, "gravity", Float.valueOf(customProjectileTemplate.G)).floatValue();
        customProjectileTemplate.H = iniFile.getFloat(str, "trueGravity", Float.valueOf(customProjectileTemplate.H)).floatValue();
        customProjectileTemplate.I = iniFile.getBoolean(str, "instant", Boolean.valueOf(customProjectileTemplate.I)).booleanValue();
        customProjectileTemplate.L = iniFile.getBoolean(str, "instantReuseLast", Boolean.valueOf(customProjectileTemplate.L)).booleanValue();
        customProjectileTemplate.M = iniFile.getBoolean(str, "instantReuseLast_alsoChangeTurretAim", Boolean.valueOf(customProjectileTemplate.M)).booleanValue();
        customProjectileTemplate.reuseAcrossTurrets = iniFile.getBoolean(
                str,
                "instantReuseLast_acrossTurrets",
                Boolean.valueOf(customProjectileTemplate.reuseAcrossTurrets)
        ).booleanValue();
        if (customProjectileTemplate.M) {
            if (!customProjectileTemplate.L) {
                throw new RuntimeException("[" + str + "]instantReuseLast_alsoChangeTurretAim also requires instantReuseLast");
            }
            customUnitConfig.moveYAxisScaleInverted = true;
        }
        if (customProjectileTemplate.reuseAcrossTurrets && !customProjectileTemplate.L) {
            throw new RuntimeException("[" + str + "]instantReuseLast_acrossTurrets also requires instantReuseLast");
        }
        customProjectileTemplate.N = iniFile.getBoolean(str, "instantReuseLast_keepAreaDamageList", Boolean.valueOf(customProjectileTemplate.N)).booleanValue();
        customProjectileTemplate.T = iniFile.getBoolean(str, "moveWithParent", Boolean.valueOf(customProjectileTemplate.T)).booleanValue();
        customProjectileTemplate.J = iniFile.getBoolean(str, "disableLeadTargeting", Boolean.valueOf(customProjectileTemplate.J)).booleanValue();
        customProjectileTemplate.K = iniFile.getFloat(str, "leadTargetingSpeedCalculation", Float.valueOf(customProjectileTemplate.K)).floatValue();
        customProjectileTemplate.ae = iniFile.getBoolean(str, "ballistic", Boolean.valueOf(customProjectileTemplate.ae)).booleanValue();
        String string = iniFile.getString(str, "trailEffect", (String) null);
        if (string != null) {
            if (string.equalsIgnoreCase("true")) {
                customProjectileTemplate.af = true;
            } else if (string.equalsIgnoreCase("false")) {
                customProjectileTemplate.af = false;
            } else {
                customProjectileTemplate.af = false;
                customProjectileTemplate.trailEffect = customUnitConfig.addConfigExtension(string, (CustomUnitSpawnList) null);
            }
        }
        String string2 = iniFile.getString(str, "effectOnCreate", (String) null);
        if (string2 != null) {
            customProjectileTemplate.effectOnCreate = customUnitConfig.addConfigExtension(string2, (CustomUnitSpawnList) null);
        }
        customProjectileTemplate.ag = iniFile.getFloat(str, "trailEffectRate", Float.valueOf(customProjectileTemplate.ag)).floatValue();
        if (customProjectileTemplate.af) {
            customProjectileTemplate.lightColor = -1118720;
        }
        customProjectileTemplate.wobbleAmplitude = iniFile.getFloat(str, "wobbleAmplitude", Float.valueOf(customProjectileTemplate.wobbleAmplitude)).floatValue();
        customProjectileTemplate.wobbleFrequency = iniFile.getTime(str, "wobbleFrequency", Float.valueOf(customProjectileTemplate.wobbleFrequency)).floatValue();
        if (customProjectileTemplate.wobbleFrequency <= 0.0f) {
            throw new RuntimeException("wobbleFrequency must be greater than 0");
        }
        customProjectileTemplate.spawnProjectilesOnEndOfLife = UnitSpawnList.a(customUnitConfig, iniFile, str, "spawnProjectilesOnEndOfLife", (UnitSpawnList) null);
        customProjectileTemplate.spawnProjectilesOnExplode = UnitSpawnList.a(customUnitConfig, iniFile, str, "spawnProjectilesOnExplode", (UnitSpawnList) null);
        customProjectileTemplate.spawnProjectilesOnCreate = UnitSpawnList.a(customUnitConfig, iniFile, str, "spawnProjectilesOnCreate", (UnitSpawnList) null);
        customProjectileTemplate.lightColor = iniFile.getColorAsInt(str, "lightColor", Integer.valueOf(customProjectileTemplate.lightColor)).intValue();
        customProjectileTemplate.lightSize = iniFile.getFloat(str, "lightSize", Float.valueOf(customProjectileTemplate.lightSize)).floatValue();
        customProjectileTemplate.lightCastOnGround = iniFile.getBoolean(str, "lightCastOnGround", Boolean.valueOf(customProjectileTemplate.lightCastOnGround)).booleanValue();
        customProjectileTemplate.largeHitEffect = iniFile.getBoolean(str, "largeHitEffect", Boolean.valueOf(customProjectileTemplate.largeHitEffect)).booleanValue();
        customProjectileTemplate.O = iniFile.getFloat(str, "turnSpeed", Float.valueOf(customProjectileTemplate.O)).floatValue();
        customProjectileTemplate.P = iniFile.getFloat(str, "turnSpeedWhenNear", Float.valueOf(customProjectileTemplate.P)).floatValue();
        customProjectileTemplate.Q = iniFile.getFloat(str, "sweepSpeed", Float.valueOf(customProjectileTemplate.Q)).floatValue();
        customProjectileTemplate.R = iniFile.getFloat(str, "sweepOffset", Float.valueOf(customProjectileTemplate.R)).floatValue();
        customProjectileTemplate.S = iniFile.getFloat(str, "sweepOffsetFromTargetRadius", Float.valueOf(customProjectileTemplate.S)).floatValue();
        customProjectileTemplate.U = iniFile.getBoolean(str, "drawUnderUnits", Boolean.valueOf(customProjectileTemplate.U)).booleanValue();
        customProjectileTemplate.V = iniFile.getBoolean(str, "lightingEffect", Boolean.valueOf(customProjectileTemplate.V)).booleanValue();
        customProjectileTemplate.W = iniFile.getBoolean(str, "laserEffect", Boolean.valueOf(customProjectileTemplate.W)).booleanValue();
        if (customProjectileTemplate.W && customProjectileTemplate.Y == null) {
            customProjectileTemplate.color = KoolArgbColor.a(80, 255, 0, 0);
        }
        if (customProjectileTemplate.V && customProjectileTemplate.s) {
            throw new RuntimeException("lightingEffect must be targeted, cannot be targetGround");
        }
        if (customProjectileTemplate.W && customProjectileTemplate.s) {
            throw new RuntimeException("laserEffect must be targeted, cannot be targetGround");
        }
        customProjectileTemplate.ballisticDelayMoveHeight = iniFile.getFloat(str, "ballistic_delaymove_height", Float.valueOf(customProjectileTemplate.ballisticDelayMoveHeight)).floatValue();
        customProjectileTemplate.ballisticHeight = iniFile.getFloat(str, "ballistic_height", Float.valueOf(customProjectileTemplate.ballisticHeight)).floatValue();
        customProjectileTemplate.targetSpeed = iniFile.getFloat(str, "targetSpeed", Float.valueOf(customProjectileTemplate.targetSpeed)).floatValue();
        customProjectileTemplate.targetSpeedAcceleration = iniFile.getFloat(str, "targetSpeedAcceleration", Float.valueOf(customProjectileTemplate.targetSpeedAcceleration)).floatValue();
        customProjectileTemplate.autoTargetingOnDeadTarget = iniFile.getBoolean(str, "autoTargetingOnDeadTarget", Boolean.valueOf(customProjectileTemplate.autoTargetingOnDeadTarget)).booleanValue();
        customProjectileTemplate.autoTargetingOnDeadTargetRange = iniFile.getFloat(str, "autoTargetingOnDeadTargetRange", Float.valueOf(customProjectileTemplate.autoTargetingOnDeadTargetRange)).floatValue();
        customProjectileTemplate.autoTargetingOnDeadTargetLead = iniFile.getFloat(str, "autoTargetingOnDeadTargetLead", Float.valueOf(customProjectileTemplate.autoTargetingOnDeadTargetLead)).floatValue();
        customProjectileTemplate.retargetingInFlight = iniFile.getBoolean(str, "retargetingInFlight", Boolean.valueOf(customProjectileTemplate.retargetingInFlight)).booleanValue();
        customProjectileTemplate.retargetingInFlightSearchDelay = iniFile.getFloat(str, "retargetingInFlightSearchDelay", Float.valueOf(customProjectileTemplate.retargetingInFlightSearchDelay)).floatValue();
        customProjectileTemplate.retargetingInFlightSearchRange = iniFile.getFloat(str, "retargetingInFlightSearchRange", Float.valueOf(customProjectileTemplate.retargetingInFlightSearchRange)).floatValue();
        customProjectileTemplate.retargetingInFlightSearchLead = iniFile.getFloat(str, "retargetingInFlightSearchLead", Float.valueOf(customProjectileTemplate.retargetingInFlightSearchLead)).floatValue();
        customProjectileTemplate.retargetingInFlightSearchOnlyTags = iniFile.getAnimationSet(customUnitConfig, str, "retargetingInFlightSearchOnlyTags", (AnimationSet) null);
        if (customProjectileTemplate.autoTargetingOnDeadTargetRange > 1500.0f) {
            throw new RuntimeException("for performance autoTargetingOnDeadTargetRange cannot be >1500");
        }
        if (customProjectileTemplate.retargetingInFlightSearchRange > 1500.0f) {
            throw new RuntimeException("for performance retargetingInFlightSearchRange cannot be >1500");
        }
        customProjectileTemplate.color = iniFile.getColorAsInt(str, "color", Integer.valueOf(customProjectileTemplate.color)).intValue();
        customProjectileTemplate.teamColorRatio = iniFile.getFloat(str, "teamColorRatio", Float.valueOf(customProjectileTemplate.teamColorRatio)).floatValue();
        if (customProjectileTemplate.teamColorRatio < 0.0f || customProjectileTemplate.teamColorRatio > 1.0f) {
            throw new RuntimeException("teamColorRatio should be between 0-1 got:" + customProjectileTemplate.teamColorRatio);
        }
        customProjectileTemplate.teamColorRatioSourceRatio = iniFile.getFloat(str, "teamColorRatio_sourceRatio", Float.valueOf(1.0f - customProjectileTemplate.teamColorRatio)).floatValue();
        if (customProjectileTemplate.teamColorRatioSourceRatio < 0.0f || customProjectileTemplate.teamColorRatioSourceRatio > 1.0f) {
            throw new RuntimeException("teamColorRatio_sourceRatio should be between 0-1 got:" + customProjectileTemplate.teamColorRatioSourceRatio);
        }
        if (customProjectileTemplate.teamColorRatio == 0.0f && customProjectileTemplate.teamColorRatioSourceRatio != 1.0f) {
            throw new RuntimeException("teamColorRatio_sourceRatio requires teamColorRatio");
        }
        customProjectileTemplate.drawSize = iniFile.getFloat(str, "drawSize", Float.valueOf(customProjectileTemplate.drawSize)).floatValue();
        customProjectileTemplate.flameWeapon = iniFile.getBoolean(str, "flameWeapon", Boolean.valueOf(customProjectileTemplate.flameWeapon)).booleanValue();
        customProjectileTemplate.hitSound = iniFile.getBoolean(str, "hitSound", Boolean.valueOf(customProjectileTemplate.hitSound)).booleanValue();
        customProjectileTemplate.targetGroundHeightOffset = iniFile.getFloat(str, "targetGroundHeightOffset", Float.valueOf(customProjectileTemplate.targetGroundHeightOffset)).floatValue();
        customProjectileTemplate.targetGroundSpread = iniFile.getFloat(str, "targetGroundSpread", Float.valueOf(customProjectileTemplate.targetGroundSpread)).floatValue();
        customProjectileTemplate.speedSpread = iniFile.getFloat(str, "speedSpread", Float.valueOf(customProjectileTemplate.speedSpread)).floatValue();
        customProjectileTemplate.explodeOnEndOfLife = iniFile.getBoolean(str, "explodeOnEndOfLife", Boolean.valueOf(customProjectileTemplate.explodeOnEndOfLife)).booleanValue();
        customProjectileTemplate.ignoreParentShootDamageMultiplier = iniFile.getBoolean(str, "ignoreParentShootDamageMultiplier", Boolean.valueOf(customProjectileTemplate.ignoreParentShootDamageMultiplier)).booleanValue();
        customProjectileTemplate.pushForce = iniFile.getFloat(str, "pushForce", Float.valueOf(customProjectileTemplate.pushForce)).floatValue();
        customProjectileTemplate.pushVelocity = iniFile.getFloat(str, "pushVelocity", Float.valueOf(customProjectileTemplate.pushVelocity)).floatValue();
        customProjectileTemplate.buildingDamageMultiplier = iniFile.getFloat(str, "buildingDamageMultiplier", Float.valueOf(customProjectileTemplate.buildingDamageMultiplier)).floatValue();
        customProjectileTemplate.shieldDamageMultiplier = iniFile.getFloat(str, "shieldDamageMultiplier", Float.valueOf(customProjectileTemplate.shieldDamageMultiplier)).floatValue();
        customProjectileTemplate.shieldDeflectionMultiplier = iniFile.getFloat(str, "shieldDefectionMultiplier", Float.valueOf(customProjectileTemplate.shieldDeflectionMultiplier)).floatValue();
        customProjectileTemplate.hullDamageMultiplier = iniFile.getFloat(str, "hullDamageMultiplier", Float.valueOf(customProjectileTemplate.hullDamageMultiplier)).floatValue();
        customProjectileTemplate.armourIgnoreAmount = iniFile.getFloat(str, "armourIgnoreAmount", Float.valueOf(customProjectileTemplate.armourIgnoreAmount)).floatValue();
        customProjectileTemplate.areaExpandTime = iniFile.getFloat(str, "areaExpandTime", Float.valueOf(customProjectileTemplate.areaExpandTime)).floatValue();
        String string3 = iniFile.getString(str, "explodeEffect", (String) null);
        if (string3 != null) {
            customProjectileTemplate.explodeEffect = customUnitConfig.addConfigExtension(string3, (CustomUnitSpawnList) null);
        }
        String string4 = iniFile.getString(str, "explodeEffectOnShield", (String) null);
        if (string4 != null) {
            customProjectileTemplate.explodeEffectOnShield = customUnitConfig.addConfigExtension(string4, (CustomUnitSpawnList) null);
        }
        UnitSpawner unitSpawnerA = UnitSpawner.a(customUnitConfig, iniFile, str, "spawnUnit");
        if (unitSpawnerA != null && !unitSpawnerA.b()) {
            customProjectileTemplate.spawnUnit = unitSpawnerA;
        }
        customProjectileTemplate.unloadUpToXUnitsFromSource = iniFile.getLogicBooleanUnit(str, "unloadUpToXUnitsFromSource", Integer.valueOf(customProjectileTemplate.unloadUpToXUnitsFromSource)).intValue();
        customProjectileTemplate.teleportSource = iniFile.getBoolean(str, "teleportSource", Boolean.valueOf(customProjectileTemplate.teleportSource)).booleanValue();
        customProjectileTemplate.convertHitToSourceTeam = iniFile.getBoolean(str, "convertHitToSourceTeam", Boolean.valueOf(customProjectileTemplate.convertHitToSourceTeam)).booleanValue();
        customProjectileTemplate.tags = AnimationTag.a(iniFile.getString(str, "tags", (String) null));
        FastArrayList keysStartingWith = iniFile.getKeysStartingWith(str, "mutator");
        FastArrayList<String> fastArrayList = new FastArrayList();
        Iterator it = keysStartingWith.iterator();
        while (it.hasNext()) {
            String[] strArrSplit = ((String) it.next()).split("_");
            if (strArrSplit.length > 1) {
                String str2 = strArrSplit[0];
                String str3 = str2 + "_";
                if (!fastArrayList.contains(str3) && str2.length() > "mutator".length()) {
                    fastArrayList.add(str3);
                }
            }
        }
        for (String str4 : fastArrayList) {
            UnitFilter unitFilter = new UnitFilter();
            unitFilter.a = AnimationTag.a(iniFile.getString(str, str4 + "ifUnitWithTags", (String) null));
            unitFilter.b = AnimationTag.a(iniFile.getString(str, str4 + "ifUnitWithoutTags", (String) null));
            if (unitFilter.a == null && unitFilter.b == null) {
                throw new RuntimeException("[" + str + "]" + str4 + " requires: unitWithTags and/or unitWithoutTags");
            }
            unitFilter.c = iniFile.getFloat(str, str4 + "directDamageMultiplier", Float.valueOf(1.0f)).floatValue();
            unitFilter.d = iniFile.getFloat(str, str4 + "areaDamageMultiplier", Float.valueOf(1.0f)).floatValue();
            UnitPrice unitPriceA = UnitPrice.a(customUnitConfig, iniFile, str, str4 + "addResourcesDirectHit", true);
            if (unitPriceA != null && unitPriceA.d()) {
                unitFilter.e = unitPriceA;
                if (customProjectileTemplate.s) {
                    throw new RuntimeException("[" + str + "]" + str4 + "addResourcesDirectHit doesn't work with targetGround, as it will never get direct hits (use addResourcesAreaHit)");
                }
            }
            UnitPrice unitPriceA2 = UnitPrice.a(customUnitConfig, iniFile, str, str4 + "addResourcesAreaHit", true);
            if (unitPriceA2 != null && unitPriceA2.d()) {
                unitFilter.f = unitPriceA2;
                if (logicBooleanUnit3 == null) {
                    throw new RuntimeException("[" + str + "]" + str4 + "addResourcesAreaHit requires areaRadius to be set");
                }
            }
            String string5 = iniFile.getString(str, str4 + "changedExplodeEffect", (String) null);
            if (string5 != null) {
                unitFilter.g = customUnitConfig.addConfigExtension(string5, (CustomUnitSpawnList) null);
                if (unitFilter.g != null && !unitFilter.g.a()) {
                    unitFilter.g = null;
                }
            }
            boolean z = false;
            boolean z2 = false;
            if (!Utility.approximatelyEqualStrict(unitFilter.c, 1.0f)) {
                z = true;
            }
            if (!Utility.approximatelyEqualStrict(unitFilter.d, 1.0f) && customProjectileTemplate.c != 0 && customProjectileTemplate.i > 0) {
                z2 = true;
            }
            if (unitFilter.e != null) {
                z = true;
            }
            if (unitFilter.f != null) {
                z2 = true;
            }
            if (z) {
                if (customProjectileTemplate.be == null) {
                    customProjectileTemplate.be = new FastArrayList();
                }
                customProjectileTemplate.be.add(unitFilter);
            }
            if (z2) {
                if (customProjectileTemplate.bf == null) {
                    customProjectileTemplate.bf = new FastArrayList();
                }
                customProjectileTemplate.e = true;
                customProjectileTemplate.bf.add(unitFilter);
            }
            if (unitFilter.g != null) {
                if (customProjectileTemplate.bg == null) {
                    customProjectileTemplate.bg = new FastArrayList();
                }
                customProjectileTemplate.bg.add(unitFilter);
            }
        }
        if (customProjectileTemplate.c != 0 && customProjectileTemplate.i > 0) {
            customProjectileTemplate.e = true;
        }
        if ((customProjectileTemplate.pushForce != 0.0f || customProjectileTemplate.pushVelocity != 0.0f) && customProjectileTemplate.i > 0) {
            customProjectileTemplate.e = true;
        }
        customProjectileTemplate.f = !customProjectileTemplate.e;
        customUnitConfig.projectileTemplates.add(customProjectileTemplate);
    }

    public static void a(CustomProjectileTemplate customProjectileTemplate, GameOutputStream gameOutputStream) throws IOException {
        gameOutputStream.writeUnitTypeId(customProjectileTemplate.customUnitConfig);
        gameOutputStream.writeStringUTF(customProjectileTemplate.projectileName);
    }

    public static ProjectileTemplate b(GameInputStream gameInputStream) throws IOException {
        UnitType unitTypeQ = gameInputStream.q();
        String utf = gameInputStream.readUTF();
        if (unitTypeQ == null) {
            return null;
        }
        if (!(unitTypeQ instanceof CustomUnitConfig)) {
            GameEngine.logColored("ProjectileTemplate:readInLinkCustom: Got non CustomUnitMetadata object of:" + unitTypeQ.getUnitTypeDescriptionShort() + " loading real_meta");
            return null;
        }
        CustomProjectileTemplate customProjectileTemplateFindProjectileTemplateByName = ((CustomUnitConfig) unitTypeQ).findProjectileTemplateByName(utf);
        if (customProjectileTemplateFindProjectileTemplateByName == null) {
            GameEngine.logColored("ProjectileTemplate:readInLinkCustom: Could not find projectile with name:" + utf);
            return null;
        }
        return customProjectileTemplateFindProjectileTemplateByName;
    }

    public void a(BaseUnit baseUnit, Projectile projectile, BaseUnit baseUnit2, float f, float f2, float f3) {
        if (baseUnit2 == null) {
            projectile.aC = true;
            projectile.n = f;
            projectile.o = f2;
            if (this.targetGroundSpread != 0.0f) {
                projectile.n += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.targetGroundSpread) * 100.0f), (int) (this.targetGroundSpread * 100.0f), 2) / 100.0f;
                baseUnit.unitFlags4 = (int) (baseUnit.unitFlags4 + projectile.n);
                projectile.o += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.targetGroundSpread) * 100.0f), (int) (this.targetGroundSpread * 100.0f), 3) / 100.0f;
                baseUnit.unitFlags4 = (int) (baseUnit.unitFlags4 + projectile.o);
            }
            projectile.p = 0.0f;
            projectile.p += this.targetGroundHeightOffset;
            return;
        }
        if (projectile.m) {
            projectile.aC = true;
            if (!this.J) {
                float f4 = projectile.t;
                if (this.targetSpeed != -1.0f) {
                    f4 = this.targetSpeed;
                }
                if (this.K >= 0.0f) {
                    f4 = this.K;
                }
                PointF pointFA = baseUnit2.a(projectile.posX, projectile.posY, f4, projectile.h, f3);
                projectile.n = pointFA.x;
                projectile.o = pointFA.y;
            } else {
                projectile.n = baseUnit2.posX;
                projectile.o = baseUnit2.posY;
            }
            if (this.t) {
                projectile.p = baseUnit2.posZ;
            } else {
                projectile.p = 0.0f;
            }
            projectile.p += this.targetGroundHeightOffset;
            if (this.targetGroundSpread != 0.0f) {
                projectile.n += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.targetGroundSpread) * 100.0f), (int) (this.targetGroundSpread * 100.0f), 2) / 100.0f;
                projectile.o += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.targetGroundSpread) * 100.0f), (int) (this.targetGroundSpread * 100.0f), 7) / 100.0f;
                return;
            }
            return;
        }
        projectile.l = baseUnit2;
    }
}
