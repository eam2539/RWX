package com.corrodinggames.rts.game.units.custom;

import android.graphics.Color;
import android.graphics.PointF;
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
        customProjectileTemplate.ad = iniFile.getFloat(str, "beamImageOffsetRate", Float.valueOf(customProjectileTemplate.ad)).floatValue();
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
        if (customProjectileTemplate.M) {
            if (!customProjectileTemplate.L) {
                throw new RuntimeException("[" + str + "]instantReuseLast_alsoChangeTurretAim also requires instantReuseLast");
            }
            customUnitConfig.moveYAxisScaleInverted = true;
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
                customProjectileTemplate.ah = customUnitConfig.addConfigExtension(string, (CustomUnitSpawnList) null);
            }
        }
        String string2 = iniFile.getString(str, "effectOnCreate", (String) null);
        if (string2 != null) {
            customProjectileTemplate.ai = customUnitConfig.addConfigExtension(string2, (CustomUnitSpawnList) null);
        }
        customProjectileTemplate.ag = iniFile.getFloat(str, "trailEffectRate", Float.valueOf(customProjectileTemplate.ag)).floatValue();
        if (customProjectileTemplate.af) {
            customProjectileTemplate.ao = -1118720;
        }
        customProjectileTemplate.am = iniFile.getFloat(str, "wobbleAmplitude", Float.valueOf(customProjectileTemplate.am)).floatValue();
        customProjectileTemplate.an = iniFile.getTime(str, "wobbleFrequency", Float.valueOf(customProjectileTemplate.an)).floatValue();
        if (customProjectileTemplate.an <= 0.0f) {
            throw new RuntimeException("wobbleFrequency must be greater than 0");
        }
        customProjectileTemplate.ak = UnitSpawnList.a(customUnitConfig, iniFile, str, "spawnProjectilesOnEndOfLife", (UnitSpawnList) null);
        customProjectileTemplate.aj = UnitSpawnList.a(customUnitConfig, iniFile, str, "spawnProjectilesOnExplode", (UnitSpawnList) null);
        customProjectileTemplate.al = UnitSpawnList.a(customUnitConfig, iniFile, str, "spawnProjectilesOnCreate", (UnitSpawnList) null);
        customProjectileTemplate.ao = iniFile.getColorAsInt(str, "lightColor", Integer.valueOf(customProjectileTemplate.ao)).intValue();
        customProjectileTemplate.ap = iniFile.getFloat(str, "lightSize", Float.valueOf(customProjectileTemplate.ap)).floatValue();
        customProjectileTemplate.aq = iniFile.getBoolean(str, "lightCastOnGround", Boolean.valueOf(customProjectileTemplate.aq)).booleanValue();
        customProjectileTemplate.ar = iniFile.getBoolean(str, "largeHitEffect", Boolean.valueOf(customProjectileTemplate.ar)).booleanValue();
        customProjectileTemplate.O = iniFile.getFloat(str, "turnSpeed", Float.valueOf(customProjectileTemplate.O)).floatValue();
        customProjectileTemplate.P = iniFile.getFloat(str, "turnSpeedWhenNear", Float.valueOf(customProjectileTemplate.P)).floatValue();
        customProjectileTemplate.Q = iniFile.getFloat(str, "sweepSpeed", Float.valueOf(customProjectileTemplate.Q)).floatValue();
        customProjectileTemplate.R = iniFile.getFloat(str, "sweepOffset", Float.valueOf(customProjectileTemplate.R)).floatValue();
        customProjectileTemplate.S = iniFile.getFloat(str, "sweepOffsetFromTargetRadius", Float.valueOf(customProjectileTemplate.S)).floatValue();
        customProjectileTemplate.U = iniFile.getBoolean(str, "drawUnderUnits", Boolean.valueOf(customProjectileTemplate.U)).booleanValue();
        customProjectileTemplate.V = iniFile.getBoolean(str, "lightingEffect", Boolean.valueOf(customProjectileTemplate.V)).booleanValue();
        customProjectileTemplate.W = iniFile.getBoolean(str, "laserEffect", Boolean.valueOf(customProjectileTemplate.W)).booleanValue();
        if (customProjectileTemplate.W && customProjectileTemplate.Y == null) {
            customProjectileTemplate.aE = Color.a(80, 255, 0, 0);
        }
        if (customProjectileTemplate.V && customProjectileTemplate.s) {
            throw new RuntimeException("lightingEffect must be targeted, cannot be targetGround");
        }
        if (customProjectileTemplate.W && customProjectileTemplate.s) {
            throw new RuntimeException("laserEffect must be targeted, cannot be targetGround");
        }
        customProjectileTemplate.as = iniFile.getFloat(str, "ballistic_delaymove_height", Float.valueOf(customProjectileTemplate.as)).floatValue();
        customProjectileTemplate.at = iniFile.getFloat(str, "ballistic_height", Float.valueOf(customProjectileTemplate.at)).floatValue();
        customProjectileTemplate.au = iniFile.getFloat(str, "targetSpeed", Float.valueOf(customProjectileTemplate.au)).floatValue();
        customProjectileTemplate.av = iniFile.getFloat(str, "targetSpeedAcceleration", Float.valueOf(customProjectileTemplate.av)).floatValue();
        customProjectileTemplate.aw = iniFile.getBoolean(str, "autoTargetingOnDeadTarget", Boolean.valueOf(customProjectileTemplate.aw)).booleanValue();
        customProjectileTemplate.ax = iniFile.getFloat(str, "autoTargetingOnDeadTargetRange", Float.valueOf(customProjectileTemplate.ax)).floatValue();
        customProjectileTemplate.ay = iniFile.getFloat(str, "autoTargetingOnDeadTargetLead", Float.valueOf(customProjectileTemplate.ay)).floatValue();
        customProjectileTemplate.az = iniFile.getBoolean(str, "retargetingInFlight", Boolean.valueOf(customProjectileTemplate.az)).booleanValue();
        customProjectileTemplate.aA = iniFile.getFloat(str, "retargetingInFlightSearchDelay", Float.valueOf(customProjectileTemplate.aA)).floatValue();
        customProjectileTemplate.aB = iniFile.getFloat(str, "retargetingInFlightSearchRange", Float.valueOf(customProjectileTemplate.aB)).floatValue();
        customProjectileTemplate.aC = iniFile.getFloat(str, "retargetingInFlightSearchLead", Float.valueOf(customProjectileTemplate.aC)).floatValue();
        customProjectileTemplate.aD = iniFile.getAnimationSet(customUnitConfig, str, "retargetingInFlightSearchOnlyTags", (AnimationSet) null);
        if (customProjectileTemplate.ax > 1500.0f) {
            throw new RuntimeException("for performance autoTargetingOnDeadTargetRange cannot be >1500");
        }
        if (customProjectileTemplate.aB > 1500.0f) {
            throw new RuntimeException("for performance retargetingInFlightSearchRange cannot be >1500");
        }
        customProjectileTemplate.aE = iniFile.getColorAsInt(str, "color", Integer.valueOf(customProjectileTemplate.aE)).intValue();
        customProjectileTemplate.aG = iniFile.getFloat(str, "teamColorRatio", Float.valueOf(customProjectileTemplate.aG)).floatValue();
        if (customProjectileTemplate.aG < 0.0f || customProjectileTemplate.aG > 1.0f) {
            throw new RuntimeException("teamColorRatio should be between 0-1 got:" + customProjectileTemplate.aG);
        }
        customProjectileTemplate.aH = iniFile.getFloat(str, "teamColorRatio_sourceRatio", Float.valueOf(1.0f - customProjectileTemplate.aG)).floatValue();
        if (customProjectileTemplate.aH < 0.0f || customProjectileTemplate.aH > 1.0f) {
            throw new RuntimeException("teamColorRatio_sourceRatio should be between 0-1 got:" + customProjectileTemplate.aH);
        }
        if (customProjectileTemplate.aG == 0.0f && customProjectileTemplate.aH != 1.0f) {
            throw new RuntimeException("teamColorRatio_sourceRatio requires teamColorRatio");
        }
        customProjectileTemplate.aF = iniFile.getFloat(str, "drawSize", Float.valueOf(customProjectileTemplate.aF)).floatValue();
        customProjectileTemplate.aI = iniFile.getBoolean(str, "flameWeapon", Boolean.valueOf(customProjectileTemplate.aI)).booleanValue();
        customProjectileTemplate.aJ = iniFile.getBoolean(str, "hitSound", Boolean.valueOf(customProjectileTemplate.aJ)).booleanValue();
        customProjectileTemplate.aL = iniFile.getFloat(str, "targetGroundHeightOffset", Float.valueOf(customProjectileTemplate.aL)).floatValue();
        customProjectileTemplate.aK = iniFile.getFloat(str, "targetGroundSpread", Float.valueOf(customProjectileTemplate.aK)).floatValue();
        customProjectileTemplate.aM = iniFile.getFloat(str, "speedSpread", Float.valueOf(customProjectileTemplate.aM)).floatValue();
        customProjectileTemplate.aO = iniFile.getBoolean(str, "explodeOnEndOfLife", Boolean.valueOf(customProjectileTemplate.aO)).booleanValue();
        customProjectileTemplate.aN = iniFile.getBoolean(str, "ignoreParentShootDamageMultiplier", Boolean.valueOf(customProjectileTemplate.aN)).booleanValue();
        customProjectileTemplate.aP = iniFile.getFloat(str, "pushForce", Float.valueOf(customProjectileTemplate.aP)).floatValue();
        customProjectileTemplate.aQ = iniFile.getFloat(str, "pushVelocity", Float.valueOf(customProjectileTemplate.aQ)).floatValue();
        customProjectileTemplate.aR = iniFile.getFloat(str, "buildingDamageMultiplier", Float.valueOf(customProjectileTemplate.aR)).floatValue();
        customProjectileTemplate.aS = iniFile.getFloat(str, "shieldDamageMultiplier", Float.valueOf(customProjectileTemplate.aS)).floatValue();
        customProjectileTemplate.aT = iniFile.getFloat(str, "shieldDefectionMultiplier", Float.valueOf(customProjectileTemplate.aT)).floatValue();
        customProjectileTemplate.aU = iniFile.getFloat(str, "hullDamageMultiplier", Float.valueOf(customProjectileTemplate.aU)).floatValue();
        customProjectileTemplate.aV = iniFile.getFloat(str, "armourIgnoreAmount", Float.valueOf(customProjectileTemplate.aV)).floatValue();
        customProjectileTemplate.aW = iniFile.getFloat(str, "areaExpandTime", Float.valueOf(customProjectileTemplate.aW)).floatValue();
        String string3 = iniFile.getString(str, "explodeEffect", (String) null);
        if (string3 != null) {
            customProjectileTemplate.aX = customUnitConfig.addConfigExtension(string3, (CustomUnitSpawnList) null);
        }
        String string4 = iniFile.getString(str, "explodeEffectOnShield", (String) null);
        if (string4 != null) {
            customProjectileTemplate.aY = customUnitConfig.addConfigExtension(string4, (CustomUnitSpawnList) null);
        }
        UnitSpawner unitSpawnerA = UnitSpawner.a(customUnitConfig, iniFile, str, "spawnUnit");
        if (unitSpawnerA != null && !unitSpawnerA.b()) {
            customProjectileTemplate.aZ = unitSpawnerA;
        }
        customProjectileTemplate.ba = iniFile.getLogicBooleanUnit(str, "unloadUpToXUnitsFromSource", Integer.valueOf(customProjectileTemplate.ba)).intValue();
        customProjectileTemplate.bb = iniFile.getBoolean(str, "teleportSource", Boolean.valueOf(customProjectileTemplate.bb)).booleanValue();
        customProjectileTemplate.bc = iniFile.getBoolean(str, "convertHitToSourceTeam", Boolean.valueOf(customProjectileTemplate.bc)).booleanValue();
        customProjectileTemplate.bd = AnimationTag.a(iniFile.getString(str, "tags", (String) null));
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
            if (!Utility.isLessThan(unitFilter.c, 1.0f)) {
                z = true;
            }
            if (!Utility.isLessThan(unitFilter.d, 1.0f) && customProjectileTemplate.c != 0 && customProjectileTemplate.i > 0) {
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
        if ((customProjectileTemplate.aP != 0.0f || customProjectileTemplate.aQ != 0.0f) && customProjectileTemplate.i > 0) {
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
            if (this.aK != 0.0f) {
                projectile.n += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.aK) * 100.0f), (int) (this.aK * 100.0f), 2) / 100.0f;
                baseUnit.unitFlags4 = (int) (baseUnit.unitFlags4 + projectile.n);
                projectile.o += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.aK) * 100.0f), (int) (this.aK * 100.0f), 3) / 100.0f;
                baseUnit.unitFlags4 = (int) (baseUnit.unitFlags4 + projectile.o);
            }
            projectile.p = 0.0f;
            projectile.p += this.aL;
            return;
        }
        if (projectile.m) {
            projectile.aC = true;
            if (!this.J) {
                float f4 = projectile.t;
                if (this.au != -1.0f) {
                    f4 = this.au;
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
            projectile.p += this.aL;
            if (this.aK != 0.0f) {
                projectile.n += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.aK) * 100.0f), (int) (this.aK * 100.0f), 2) / 100.0f;
                projectile.o += Utility.getDeterministicRandomInt((GameObject) baseUnit, (int) ((-this.aK) * 100.0f), (int) (this.aK * 100.0f), 7) / 100.0f;
                return;
            }
            return;
        }
        projectile.l = baseUnit2;
    }
}
