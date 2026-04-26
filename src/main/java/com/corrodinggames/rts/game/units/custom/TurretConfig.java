package com.corrodinggames.rts.game.units.custom;

import com.corrodinggames.rts.game.units.custom.hooks.ProjectileInterceptorHook;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.price.UnitPrice;
import com.corrodinggames.rts.gameFramework.graphics.Texture;
import com.corrodinggames.rts.gameFramework.utility.IniFile;

/* JADX INFO: renamed from: com.corrodinggames.rts.game.units.custom.bn */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/game/units/custom/bn.class */
public class TurretConfig {

    /* JADX INFO: renamed from: a */
    public String name;

    /* JADX INFO: renamed from: b */
    public String copyFrom;

    /* JADX INFO: renamed from: c */
    public boolean turretMultiTargeting;

    /* JADX INFO: renamed from: d */
    public boolean canRotate;

    /* JADX INFO: renamed from: e */
    public int rotationSpeed;

    /* JADX INFO: renamed from: f */
    public float range;

    /* JADX INFO: renamed from: g */
    public float minRange;

    /* JADX INFO: renamed from: h */
    public float shootDelay;

    /* JADX INFO: renamed from: j */
    public float shootSpeed;

    /* JADX INFO: renamed from: k */
    public float shootAccuracy;

    /* JADX INFO: renamed from: l */
    public boolean canTargetAir;

    /* JADX INFO: renamed from: t */
    public float shootAngle;

    /* JADX INFO: renamed from: v */
    public UnitPrice shootEffect;

    /* JADX INFO: renamed from: y */
    public TurretConfig parentTurret;

    /* JADX INFO: renamed from: z */
    public TurretConfig childTurret;

    /* JADX INFO: renamed from: A */
    public boolean autoTarget;

    /* JADX INFO: renamed from: C */
    public LogicBoolean logicCondition;

    /* JADX INFO: renamed from: D */
    public SoundList sound;

    /* JADX INFO: renamed from: E */
    public CustomUnitSpawnList spawnList;

    /* JADX INFO: renamed from: F */
    public CustomUnitSpawnList spawnList2;

    /* JADX INFO: renamed from: G */
    public Integer spawnCount;

    /* JADX INFO: renamed from: H */
    public boolean canSpawn;

    /* JADX INFO: renamed from: I */
    public boolean canSpawn2;

    /* JADX INFO: renamed from: J */
    public LogicBoolean spawnLogicCondition;

    /* JADX INFO: renamed from: K */
    public LogicBoolean spawnLogicCondition2;

    /* JADX INFO: renamed from: L */
    public LogicBoolean spawnLogicCondition3;

    /* JADX INFO: renamed from: M */
    public LogicBoolean spawnLogicCondition4;

    /* JADX INFO: renamed from: N */
    public LogicBoolean spawnLogicCondition5;

    /* JADX INFO: renamed from: O */
    public AnimationSet animationSet;

    /* JADX INFO: renamed from: P */
    public AnimationSet animationSet2;

    /* JADX INFO: renamed from: T */
    public LogicBoolean logicCondition2;

    /* JADX INFO: renamed from: U */
    public Float rotationSpeed2;

    /* JADX INFO: renamed from: X */
    public float shootSpeed2;

    /* JADX INFO: renamed from: Y */
    public float shootAccuracy2;

    /* JADX INFO: renamed from: aa */
    public float shootAngle2;

    /* JADX INFO: renamed from: ac */
    public Boolean canTargetAir2;

    /* JADX INFO: renamed from: ad */
    public float range2;

    /* JADX INFO: renamed from: ae */
    public float minRange2;

    /* JADX INFO: renamed from: af */
    public float shootDelay2;

    /* JADX INFO: renamed from: aj */
    public Float shootSpeed3;

    /* JADX INFO: renamed from: ak */
    public AnimationSet animationSet3;

    /* JADX INFO: renamed from: ao */
    public TurretConfig linkedTurret;

    /* JADX INFO: renamed from: au */
    public float rotationSpeed3;

    /* JADX INFO: renamed from: av */
    public LogicBoolean logicCondition3;

    /* JADX INFO: renamed from: ax */
    public float shootSpeed4;

    /* JADX INFO: renamed from: ay */
    public boolean canTargetAir3;

    /* JADX INFO: renamed from: az */
    public int rotationSpeed4;

    /* JADX INFO: renamed from: aA */
    public CustomUnitAnimationReference animationReference;

    /* JADX INFO: renamed from: aB */
    public float shootSpeed5;

    /* JADX INFO: renamed from: aC */
    public CustomUnitActionHandler actionHandler;

    /* JADX INFO: renamed from: aD */
    public Texture texture;

    /* JADX INFO: renamed from: aE */
    public Texture[] textureArray;

    /* JADX INFO: renamed from: aF */
    public Texture texture2;

    /* JADX INFO: renamed from: aG */
    public float shootSpeed6;

    /* JADX INFO: renamed from: aH */
    public float shootAccuracy3;

    /* JADX INFO: renamed from: aI */
    public int rotationSpeed5;

    /* JADX INFO: renamed from: aJ */
    public AnimationSet animationSet4;
    public float i = 1.0f;
    public float m = -1.0f;
    public float n = 0.0f;
    public float o = 4.0f;
    public float p = 0.0f;
    public float q = 4.0f;
    public float r = 7.0f;
    public boolean s = false;
    public float u = 0.0f;
    public int w = -1;
    public int x = -1;
    public boolean B = true;
    public float Q = 5.0f;
    public int R = 0;
    public int S = -1;
    public float V = -1.0f;
    public float W = -1.0f;
    public float Z = 0.0f;
    public float ab = 99999.0f;
    public float ag = -1.0f;
    public float ah = -1.0f;
    public float ai = -1.0f;
    public float al = -1.0f;
    public float am = 2000.0f;
    public float an = -999.0f;
    public int ap = -1;
    public boolean aq = true;
    public float ar = 0.0f;
    public float as = 0.0f;
    public float at = 10.0f;
    public float aw = -1.0f;

    public int a(CustomUnit customUnit) {
        if (this.S >= 0 && this.logicCondition2.read(customUnit)) {
            return this.S;
        }
        return this.R;
    }

    public void a(TurretConfig turretConfig) {
        this.range = turretConfig.range;
        this.minRange = turretConfig.minRange;
        this.shootDelay = turretConfig.shootDelay;
        this.shootSpeed = turretConfig.shootSpeed;
        this.shootAccuracy = turretConfig.shootAccuracy;
        this.canTargetAir = turretConfig.canTargetAir;
        this.m = turretConfig.m;
        this.n = turretConfig.n;
        this.o = turretConfig.o;
        this.u = turretConfig.u;
        this.shootEffect = turretConfig.shootEffect;
        this.rotationSpeed5 = turretConfig.rotationSpeed5;
        this.animationSet4 = turretConfig.animationSet4;
        this.s = turretConfig.s;
        this.shootAngle = turretConfig.shootAngle;
        this.p = turretConfig.p;
        this.q = turretConfig.q;
        this.r = turretConfig.r;
        this.parentTurret = turretConfig.parentTurret;
        this.autoTarget = turretConfig.autoTarget;
        this.B = turretConfig.B;
        this.logicCondition = turretConfig.logicCondition;
        this.sound = turretConfig.sound;
        this.spawnList = turretConfig.spawnList;
        this.spawnCount = turretConfig.spawnCount;
        this.spawnList2 = turretConfig.spawnList2;
        this.spawnLogicCondition = turretConfig.spawnLogicCondition;
        this.spawnLogicCondition2 = turretConfig.spawnLogicCondition2;
        this.spawnLogicCondition3 = turretConfig.spawnLogicCondition3;
        this.spawnLogicCondition4 = turretConfig.spawnLogicCondition4;
        this.spawnLogicCondition5 = turretConfig.spawnLogicCondition5;
        this.animationSet = turretConfig.animationSet;
        this.animationSet2 = turretConfig.animationSet2;
        this.Q = turretConfig.Q;
        this.texture = turretConfig.texture;
        this.textureArray = turretConfig.textureArray;
        this.texture2 = turretConfig.texture2;
        this.R = turretConfig.R;
        this.S = turretConfig.S;
        this.logicCondition2 = turretConfig.logicCondition2;
        this.rotationSpeed2 = turretConfig.rotationSpeed2;
        this.V = turretConfig.V;
        this.W = turretConfig.W;
        this.shootSpeed2 = turretConfig.shootSpeed2;
        this.shootAccuracy2 = turretConfig.shootAccuracy2;
        this.Z = turretConfig.Z;
        this.shootAngle2 = turretConfig.shootAngle2;
        this.linkedTurret = turretConfig.linkedTurret;
        this.aq = turretConfig.aq;
        this.as = turretConfig.as;
        this.at = turretConfig.at;
        this.rotationSpeed3 = turretConfig.rotationSpeed3;
        this.logicCondition3 = turretConfig.logicCondition3;
        this.aw = turretConfig.aw;
        this.shootSpeed4 = turretConfig.shootSpeed4;
        this.ab = turretConfig.ab;
        this.ag = turretConfig.ag;
        this.ai = turretConfig.ai;
        this.shootDelay2 = turretConfig.shootDelay2;
        this.canTargetAir3 = turretConfig.canTargetAir3;
        this.rotationSpeed4 = turretConfig.rotationSpeed4;
        this.animationSet3 = turretConfig.animationSet3;
        this.al = turretConfig.al;
        this.am = turretConfig.am;
        this.an = turretConfig.an;
        this.animationReference = turretConfig.animationReference;
        this.shootSpeed6 = turretConfig.shootSpeed6;
        this.shootAccuracy3 = turretConfig.shootAccuracy3;
    }

    public static void a(TurretConfig turretConfig, CustomUnitConfig customUnitConfig, IniFile iniFile, String str) throws ConfigParseException {
        turretConfig.turretMultiTargeting = true;
        if (turretConfig.canRotate) {
            return;
        }
        Float fValueOf = Float.valueOf(iniFile.getFloatStrictRaw(str, "x"));
        Float fValueOf2 = Float.valueOf(iniFile.getFloatStrictRaw(str, "y"));
        String string = iniFile.getString(str, "copyFrom", (String) null);
        if (string != null) {
            TurretConfig turretConfigFindProjectileConfigByName = customUnitConfig.findProjectileConfigByName(string);
            if (turretConfigFindProjectileConfigByName == null) {
                throw new RuntimeException("[" + str + "] Could not find copy turret target with name:" + string);
            }
            if (turretConfigFindProjectileConfigByName.turretMultiTargeting && !turretConfigFindProjectileConfigByName.canRotate) {
                throw new RuntimeException("[" + str + "] Infinite loop detected with turret copies:" + string);
            }
            if (!turretConfigFindProjectileConfigByName.canRotate) {
                a(turretConfigFindProjectileConfigByName, customUnitConfig, iniFile, turretConfigFindProjectileConfigByName.copyFrom);
            }
            turretConfig.a(turretConfigFindProjectileConfigByName);
        } else {
            turretConfig.shootDelay2 = customUnitConfig.aimOffsetSpread;
            turretConfig.shootSpeed2 = customUnitConfig.turretSize;
        }
        turretConfig.range = fValueOf.floatValue();
        turretConfig.minRange = fValueOf2.floatValue();
        turretConfig.shootDelay = iniFile.getFloat(str, "height", Float.valueOf(0.0f)).floatValue();
        turretConfig.i = iniFile.getFloat(str, "yAxisScaling", Float.valueOf(1.0f)).floatValue();
        String string2 = iniFile.getString(str, "linkDelayWithTurret", (String) null);
        if (string2 != null) {
            turretConfig.linkedTurret = customUnitConfig.findProjectileConfigByName(string2);
            if (turretConfig.linkedTurret == null) {
                throw new RuntimeException("[" + str + "] Could not find 'linkDelayWithTurret' turret target with name:" + string2);
            }
            turretConfig.m = 9000.0f;
        }
        Float time = iniFile.getTime(str, "delay", (Float) null);
        if (time != null) {
            turretConfig.m = time.floatValue();
        }
        if (turretConfig.m == -1.0f) {
            turretConfig.m = customUnitConfig.shootDelay;
        }
        Float time2 = iniFile.getTime(str, "warmup", (Float) null);
        if (time2 != null) {
            turretConfig.n = time2.floatValue();
        }
        Float f = iniFile.getFloat(str, "warmupCallDownRate", (Float) null);
        if (f != null) {
            turretConfig.o = f.floatValue();
        }
        Boolean bool = iniFile.getBoolean(str, "warmupNoReset", (Boolean) null);
        if (bool != null) {
            turretConfig.s = bool.booleanValue();
        }
        Float f2 = iniFile.getFloat(str, "warmupShootDelayTransfer", (Float) null);
        if (f2 != null) {
            turretConfig.shootAngle = f2.floatValue();
        }
        turretConfig.p = iniFile.getFloat(str, "recoilOffset", Float.valueOf(turretConfig.p)).floatValue();
        turretConfig.q = iniFile.getFloat(str, "recoilOutTime", Float.valueOf(turretConfig.q)).floatValue();
        turretConfig.r = iniFile.getTime(str, "recoilReturnTime", Float.valueOf(turretConfig.r)).floatValue();
        Float f3 = iniFile.getFloat(str, "energyUsage", (Float) null);
        if (f3 != null) {
            turretConfig.u = f3.floatValue();
        }
        turretConfig.rotationSpeed5 = iniFile.getLogicBooleanUnit(str, "unloadUpToXUnitsAndGiveAttackOrder", Integer.valueOf(turretConfig.rotationSpeed5)).intValue();
        UnitPrice unitPriceA = UnitPrice.a(customUnitConfig, iniFile, str, "resourceUsage", true);
        if (unitPriceA != null && unitPriceA.d()) {
            turretConfig.shootEffect = unitPriceA;
            customUnitConfig.a(unitPriceA);
        }
        String string3 = iniFile.getString(str, "attachedTo", (String) null);
        if (string3 != null) {
            turretConfig.parentTurret = customUnitConfig.findProjectileConfigByName(string3);
            if (turretConfig.parentTurret == null) {
                throw new RuntimeException("[" + str + "] Could not find attachedTo turret target:" + string3);
            }
            if (turretConfig.parentTurret == turretConfig) {
                throw new RuntimeException("Turret cannot be attachedTo self");
            }
            customUnitConfig.f51fU = true;
        }
        Float f4 = iniFile.getFloat(str, "idleDir", (Float) null);
        if (f4 != null) {
            turretConfig.shootSpeed = f4.floatValue();
        }
        Float f5 = iniFile.getFloat(str, "idleDirReversing", (Float) null);
        if (f5 != null) {
            turretConfig.shootAccuracy = f5.floatValue();
            turretConfig.canTargetAir = true;
        } else if (!turretConfig.canTargetAir) {
            if (turretConfig.parentTurret != null) {
                turretConfig.shootAccuracy = 0.0f;
            } else {
                turretConfig.shootAccuracy = turretConfig.shootSpeed + 180.0f;
            }
        }
        Boolean bool2 = iniFile.getBoolean(str, "canShoot", (Boolean) null);
        Boolean bool3 = iniFile.getBoolean(str, "canAttack", (Boolean) null);
        if (bool2 != null && bool3 != null) {
            throw new RuntimeException("[" + str + "] Cannot use canShoot and canAttack at the same time, they have the same meaning");
        }
        if (bool2 != null) {
            turretConfig.B = bool2.booleanValue();
        } else if (bool3 != null) {
            turretConfig.B = bool3.booleanValue();
        }
        turretConfig.sound = SoundList.a(customUnitConfig, iniFile.getString(str, "shoot_sound", (String) null), turretConfig.sound);
        Float f6 = iniFile.getFloat(str, "shoot_sound_vol", (Float) null);
        if (f6 != null) {
            turretConfig.sound.a(f6.floatValue());
        }
        turretConfig.spawnList = customUnitConfig.addConfigExtension(iniFile.getString(str, "shoot_flame", (String) null), turretConfig.spawnList);
        turretConfig.spawnCount = iniFile.getColorAsInt(str, "shoot_light", turretConfig.spawnCount);
        turretConfig.spawnList2 = customUnitConfig.addConfigExtension(iniFile.getString(str, "warmupStartEffect", (String) null), turretConfig.spawnList2);
        turretConfig.autoTarget = iniFile.getBoolean(str, "slave", Boolean.valueOf(turretConfig.autoTarget)).booleanValue();
        if (turretConfig.autoTarget) {
            if (turretConfig.parentTurret == null) {
                throw new RuntimeException("Turret cannot be a slave without being 'attachedTo' to another turret");
            }
            turretConfig.childTurret = turretConfig.parentTurret;
        }
        turretConfig.logicCondition = iniFile.getLogicBoolean(customUnitConfig, str, "invisible", turretConfig.logicCondition);
        turretConfig.spawnLogicCondition = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackFlyingUnits", turretConfig.spawnLogicCondition);
        turretConfig.spawnLogicCondition2 = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackLandUnits", turretConfig.spawnLogicCondition2);
        turretConfig.spawnLogicCondition3 = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackUnderwaterUnits", turretConfig.spawnLogicCondition3);
        turretConfig.spawnLogicCondition4 = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackNotTouchingWaterUnits", turretConfig.spawnLogicCondition4);
        turretConfig.spawnLogicCondition5 = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackCondition", turretConfig.spawnLogicCondition5);
        turretConfig.animationSet = iniFile.getAnimationSet(customUnitConfig, str, "canOnlyAttackUnitsWithTags", turretConfig.animationSet);
        turretConfig.animationSet2 = iniFile.getAnimationSet(customUnitConfig, str, "canOnlyAttackUnitsWithoutTags", turretConfig.animationSet2);
        String string4 = iniFile.getString(str, "projectile", (String) null);
        if (string4 != null) {
            CustomProjectileTemplate customProjectileTemplateFindProjectileTemplateByName = customUnitConfig.findProjectileTemplateByName(string4);
            if (customProjectileTemplateFindProjectileTemplateByName == null) {
                if ("0".equals(string4) && customUnitConfig.projectileTemplates.size() == 1 && customUnitConfig.findProjectileTemplateByName("1") != null) {
                    turretConfig.R = 0;
                } else {
                    throw new RuntimeException("[" + str + "] Could not find projectile with name:" + string4);
                }
            } else {
                turretConfig.R = customProjectileTemplateFindProjectileTemplateByName.projectileId;
            }
        }
        String string5 = iniFile.getString(str, "altProjectile", (String) null);
        if (string5 != null) {
            CustomProjectileTemplate customProjectileTemplateFindProjectileTemplateByName2 = customUnitConfig.findProjectileTemplateByName(string5);
            if (customProjectileTemplateFindProjectileTemplateByName2 == null) {
                throw new RuntimeException("[" + str + "]altProjectile: Could not find projectile with name:" + string5);
            }
            turretConfig.S = customProjectileTemplateFindProjectileTemplateByName2.projectileId;
        }
        turretConfig.logicCondition2 = iniFile.getLogicBoolean(customUnitConfig, str, "altProjectileCondition", turretConfig.logicCondition2);
        if (turretConfig.S >= 0 && turretConfig.logicCondition2 == null) {
            throw new RuntimeException("[" + str + "]altProjectileCondition is required with altProjectile");
        }
        turretConfig.Q = iniFile.getFloat(str, "canAttackMaxAngle", Float.valueOf(turretConfig.Q)).floatValue();
        turretConfig.rotationSpeed2 = iniFile.getFloat(str, "turnSpeed", turretConfig.rotationSpeed2);
        turretConfig.V = iniFile.getFloat(str, "turnSpeedAcceleration", Float.valueOf(turretConfig.V)).floatValue();
        turretConfig.W = iniFile.getFloat(str, "turnSpeedDeceleration", Float.valueOf(turretConfig.W)).floatValue();
        Float f7 = iniFile.getFloat(str, "barrelY", (Float) null);
        Float f8 = iniFile.getFloat(str, "size", (Float) null);
        if (f7 != null && f8 != null) {
            throw new RuntimeException("Turret [" + str + "]: barrelY and size can not both be used at the same time as they have the same meaning");
        }
        if (f7 != null) {
            turretConfig.shootSpeed2 = f7.floatValue();
        }
        if (f8 != null) {
            turretConfig.shootSpeed2 = f8.floatValue();
        }
        turretConfig.shootAccuracy2 = iniFile.getFloat(str, "barrelX", Float.valueOf(turretConfig.shootAccuracy2)).floatValue();
        turretConfig.Z = iniFile.getFloat(str, "barrelOffsetX_onOddShots", Float.valueOf(turretConfig.Z)).floatValue();
        turretConfig.shootAngle2 = iniFile.getFloat(str, "barrelHeight", Float.valueOf(turretConfig.shootAngle2)).floatValue();
        turretConfig.ab = iniFile.getFloat(str, "limitingRange", Float.valueOf(turretConfig.ab)).floatValue();
        turretConfig.ai = iniFile.getFloat(str, "limitingAngle", Float.valueOf(turretConfig.ai)).floatValue();
        turretConfig.ag = iniFile.getFloat(str, "limitingMinRange", Float.valueOf(turretConfig.ag)).floatValue();
        turretConfig.shootDelay2 = iniFile.getFloat(str, "aimOffsetSpread", Float.valueOf(turretConfig.shootDelay2)).floatValue();
        if (turretConfig.ai >= 0.0f) {
            customUnitConfig.targetProjectiles = true;
        }
        if (turretConfig.ab < 99999.0f) {
            turretConfig.range2 = turretConfig.ab;
        } else {
            turretConfig.range2 = customUnitConfig.unitStats.maxAttackRange;
        }
        turretConfig.minRange2 = turretConfig.range2 * turretConfig.range2;
        if (turretConfig.ag > 0.0f) {
            turretConfig.ah = turretConfig.ag * turretConfig.ag;
        } else {
            turretConfig.ah = -1.0f;
        }
        turretConfig.canTargetAir2 = iniFile.getBoolean(str, "showRangeUIGuide", (Boolean) null);
        turretConfig.shootSpeed3 = iniFile.getFloat(str, "laserDefenceEnergyUse", turretConfig.shootSpeed3);
        if (turretConfig.shootSpeed3 != null) {
            customUnitConfig.targetAir = true;
            customUnitConfig.a(ProjectileInterceptorHook.a);
        }
        turretConfig.animationSet3 = AnimationTag.a(iniFile.getString(str, "interceptProjectiles_withTags", (String) null), turretConfig.animationSet3);
        if (turretConfig.animationSet3 != null) {
            customUnitConfig.targetBuildings = true;
            customUnitConfig.a(ProjectileInterceptorHook.a);
            turretConfig.al = iniFile.getFloat(str, "interceptProjectiles_andTargetingGroundUnderDistance", Float.valueOf(turretConfig.al)).floatValue();
            turretConfig.am = iniFile.getFloat(str, "interceptProjectiles_andUnderDistance", Float.valueOf(turretConfig.am)).floatValue();
            turretConfig.an = iniFile.getFloat(str, "interceptProjectiles_andOverHeight", Float.valueOf(turretConfig.an)).floatValue();
        }
        turretConfig.aq = iniFile.getBoolean(str, "shouldResetTurret", Boolean.valueOf(turretConfig.aq)).booleanValue();
        turretConfig.ar = iniFile.getFloat(str, "idleSpin", Float.valueOf(turretConfig.ar)).floatValue();
        turretConfig.as = iniFile.getFloat(str, "idleSweepAngle", Float.valueOf(turretConfig.as)).floatValue();
        turretConfig.at = iniFile.getFloat(str, "idleSweepDelay", Float.valueOf(turretConfig.at)).floatValue();
        turretConfig.rotationSpeed3 = iniFile.getFloat(str, "idleSweepSpeed", Float.valueOf(turretConfig.rotationSpeed3)).floatValue();
        turretConfig.logicCondition3 = iniFile.getLogicBoolean(customUnitConfig, str, "idleSweepCondition", turretConfig.logicCondition3);
        turretConfig.logicCondition3 = LogicBoolean.convertAlwaysTrueToNull(turretConfig.logicCondition3);
        turretConfig.aw = iniFile.getFloat(str, "idleSweepAddRandomDelay", Float.valueOf(turretConfig.aw)).floatValue();
        if (turretConfig.aw < 0.0f) {
            float f9 = 1.0f;
            if (turretConfig.at > 200.0f) {
                f9 = 20.0f;
            } else if (turretConfig.at > 50.0f) {
                f9 = 5.0f;
            }
            turretConfig.aw = f9;
        }
        turretConfig.shootSpeed4 = iniFile.getFloat(str, "idleSweepAddRandomAngle", Float.valueOf(turretConfig.shootSpeed4)).floatValue();
        if (turretConfig.shootSpeed4 < 0.0f) {
            throw new RuntimeException("Turret [" + str + "]: idleSweepAddRandomAngle must be >= 0");
        }
        if (turretConfig.as < 0.0f) {
            throw new RuntimeException("Turret [" + str + "]: idleSweepAngle must be >= 0");
        }
        turretConfig.canTargetAir3 = iniFile.getBoolean(str, "clearTurretTargetAfterFiring", Boolean.valueOf(turretConfig.canTargetAir3)).booleanValue();
        turretConfig.animationReference = customUnitConfig.loadCore(iniFile.getString(str, "onShoot_playAnimation", (String) null), turretConfig.animationReference);
        turretConfig.shootSpeed5 = iniFile.getTime(str, "onShoot_freezeBodyMovementFor", Float.valueOf(turretConfig.shootSpeed5)).floatValue();
        turretConfig.actionHandler = iniFile.getCustomUnitAction(customUnitConfig, str, "onShoot_triggerActions", turretConfig.actionHandler);
        if (iniFile.getBoolean(str, "isMainNanoTurret", (Boolean) false).booleanValue()) {
            customUnitConfig.defaultTurret = turretConfig;
        }
        Texture textureA = customUnitConfig.a(iniFile, str, "image");
        if (textureA != null) {
            turretConfig.texture = textureA;
            boolean zBooleanValue = customUnitConfig.teamColorsOnTurret;
            Boolean bool4 = iniFile.getBoolean(str, "image_applyTeamColors", (Boolean) null);
            if (bool4 != null) {
                zBooleanValue = bool4.booleanValue();
            }
            if (zBooleanValue) {
                turretConfig.textureArray = customUnitConfig.a(turretConfig.texture, customUnitConfig.baseDamage);
            } else {
                turretConfig.textureArray = null;
            }
        }
        turretConfig.shootSpeed6 = iniFile.getFloat(str, "image_drawOffsetX", Float.valueOf(turretConfig.shootSpeed6)).floatValue();
        turretConfig.shootAccuracy3 = iniFile.getFloat(str, "image_drawOffsetY", Float.valueOf(turretConfig.shootAccuracy3)).floatValue();
        Texture textureA2 = customUnitConfig.a(iniFile, str, "chargeEffectImage");
        if (textureA2 != null) {
            turretConfig.texture2 = textureA2;
            customUnitConfig.f49fP = true;
        }
        if (customUnitConfig.f50fR[turretConfig.R] == null) {
            throw new RuntimeException("Turret [" + str + "]: cannot find linked projectile:" + turretConfig.R);
        }
        if (turretConfig.S >= 0 && customUnitConfig.f50fR[turretConfig.S] == null) {
            throw new RuntimeException("Turret [" + str + "]altProjectile: cannot find linked projectile");
        }
        turretConfig.spawnLogicCondition = LogicBoolean.convertAlwaysTrueToNull(turretConfig.spawnLogicCondition);
        turretConfig.spawnLogicCondition2 = LogicBoolean.convertAlwaysTrueToNull(turretConfig.spawnLogicCondition2);
        turretConfig.spawnLogicCondition3 = LogicBoolean.convertAlwaysTrueToNull(turretConfig.spawnLogicCondition3);
        turretConfig.spawnLogicCondition4 = LogicBoolean.convertAlwaysTrueToNull(turretConfig.spawnLogicCondition4);
        turretConfig.spawnLogicCondition5 = LogicBoolean.convertAlwaysTrueToNull(turretConfig.spawnLogicCondition5);
        if (turretConfig.ai != -1.0f || turretConfig.spawnLogicCondition != null || turretConfig.spawnLogicCondition2 != null || turretConfig.spawnLogicCondition3 != null || turretConfig.spawnLogicCondition4 != null || turretConfig.spawnLogicCondition5 != null) {
            turretConfig.canSpawn = true;
        }
        if (turretConfig.ab < 99999.0f || turretConfig.ag > 0.0f) {
            turretConfig.canSpawn = true;
            turretConfig.canSpawn2 = true;
        }
        if (turretConfig.animationSet != null || turretConfig.animationSet2 != null) {
            turretConfig.canSpawn = true;
        }
        turretConfig.canRotate = true;
    }
}
