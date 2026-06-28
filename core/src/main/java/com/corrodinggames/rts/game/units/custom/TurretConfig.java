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
    public int turretIndex;

    /* JADX INFO: renamed from: f */
    public float offsetX;

    /* JADX INFO: renamed from: g */
    public float offsetY;

    /* JADX INFO: renamed from: h */
    public float offsetHeight;

    /* JADX INFO: renamed from: j */
    public float idleDir;

    /* JADX INFO: renamed from: k */
    public float idleDirReversing;

    /* JADX INFO: renamed from: l */
    public boolean hasIdleDirReversing;

    /* JADX INFO: renamed from: t */
    public float warmupShootDelayTransfer;

    /* JADX INFO: renamed from: v */
    public UnitPrice resourceUsage;

    /* JADX INFO: renamed from: y */
    public TurretConfig parentTurret;

    /* JADX INFO: renamed from: z */
    public TurretConfig childTurret;

    /* JADX INFO: renamed from: A */
    public boolean isSlave;

    /* JADX INFO: renamed from: C */
    public LogicBoolean invisibilityCondition;

    /* JADX INFO: renamed from: D */
    public SoundList shootSound;

    /* JADX INFO: renamed from: E */
    public CustomUnitSpawnList shootFlameEffect;

    /* JADX INFO: renamed from: F */
    public CustomUnitSpawnList warmupStartEffect;

    public float preFireDuration;
    public String preFireRendererId;
    public String preFireRendererVariant;
    public String preFireObserverId;
    public String preFireObserverVariant;

    /* JADX INFO: renamed from: G */
    public Integer shootLightColor;

    /* JADX INFO: renamed from: H */
    public boolean canSpawn;

    /* JADX INFO: renamed from: I */
    public boolean canSpawn2;

    /* JADX INFO: renamed from: J */
    public LogicBoolean canAttackFlyingUnits;

    /* JADX INFO: renamed from: K */
    public LogicBoolean canAttackLandUnits;

    /* JADX INFO: renamed from: L */
    public LogicBoolean canAttackUnderwaterUnits;

    /* JADX INFO: renamed from: M */
    public LogicBoolean canAttackNotTouchingWaterUnits;

    /* JADX INFO: renamed from: N */
    public LogicBoolean canAttackCondition;

    /* JADX INFO: renamed from: O */
    public AnimationSet canOnlyAttackUnitsWithTags;

    /* JADX INFO: renamed from: P */
    public AnimationSet canOnlyAttackUnitsWithoutTags;

    /* JADX INFO: renamed from: T */
    public LogicBoolean altProjectileCondition;

    /* JADX INFO: renamed from: U */
    public Float turnSpeed;

    /* JADX INFO: renamed from: X */
    public float barrelY;

    /* JADX INFO: renamed from: Y */
    public float barrelX;

    /* JADX INFO: renamed from: aa */
    public float barrelHeight;

    /* JADX INFO: renamed from: ac */
    public Boolean showRangeUIGuide;

    /* JADX INFO: renamed from: ad */
    public float effectiveRange;

    /* JADX INFO: renamed from: ae */
    public float effectiveRangeSquared;

    /* JADX INFO: renamed from: af */
    public float aimOffsetSpread;

    /* JADX INFO: renamed from: aj */
    public Float laserDefenceEnergyUse;

    /* JADX INFO: renamed from: ak */
    public AnimationSet interceptProjectilesWithTags;

    /* JADX INFO: renamed from: ao */
    public TurretConfig linkedTurret;

    /* JADX INFO: renamed from: au */
    public float idleSweepSpeed;

    /* JADX INFO: renamed from: av */
    public LogicBoolean idleSweepCondition;

    /* JADX INFO: renamed from: ax */
    public float idleSweepAddRandomAngle;

    /* JADX INFO: renamed from: ay */
    public boolean clearTurretTargetAfterFiring;

    /* JADX INFO: renamed from: az */
    public int rotationSpeed4;

    /* JADX INFO: renamed from: aA */
    public CustomUnitAnimationReference onShootPlayAnimation;

    /* JADX INFO: renamed from: aB */
    public float onShootFreezeBodyMovementFor;

    /* JADX INFO: renamed from: aC */
    public CustomUnitActionHandler onShootTriggerActions;

    /* JADX INFO: renamed from: aD */
    public Texture image;

    /* JADX INFO: renamed from: aE */
    public Texture[] teamColoredImages;

    /* JADX INFO: renamed from: aF */
    public Texture chargeEffectImage;

    /* JADX INFO: renamed from: aG */
    public float imageDrawOffsetX;

    /* JADX INFO: renamed from: aH */
    public float imageDrawOffsetY;

    /* JADX INFO: renamed from: aI */
    public int unloadUnitsAndGiveAttackOrderCount;

    /* JADX INFO: renamed from: aJ */
    public AnimationSet animationSet4;
    public float i = 1.0f;
    public float m = -1.0f;
    public float warmup = 0.0f;
    public float warmupCallDownRate = 4.0f;
    public float p = 0.0f;
    public float q = 4.0f;
    public float r = 7.0f;
    public boolean warmupNoReset = false;
    public float u = 0.0f;
    public int linkedTurretIndex = -1;
    public int x = -1;
    public boolean canShoot = true;
    public float canAttackMaxAngle = 5.0f;
    public int projectileId = 0;
    public int altProjectileId = -1;
    public float turnSpeedAcceleration = -1.0f;
    public float turnSpeedDeceleration = -1.0f;
    public float barrelOffsetXOnOddShots = 0.0f;
    public float limitingRange = 99999.0f;
    public float limitingMinRange = -1.0f;
    public float limitingMinRangeSquared = -1.0f;
    public float limitingAngle = -1.0f;
    public float interceptProjectilesAndTargetingGroundUnderDistance = -1.0f;
    public float interceptProjectilesAndUnderDistance = 2000.0f;
    public float interceptProjectilesAndOverHeight = -999.0f;
    public int ap = -1;
    public boolean shouldResetTurret = true;
    public float idleSpin = 0.0f;
    public float idleSweepAngle = 0.0f;
    public float idleSweepDelay = 10.0f;
    public float idleSweepAddRandomDelay = -1.0f;

    public int a(CustomUnit customUnit) {
        if (this.altProjectileId >= 0 && this.altProjectileCondition.read(customUnit)) {
            return this.altProjectileId;
        }
        return this.projectileId;
    }

    public void a(TurretConfig turretConfig) {
        this.offsetX = turretConfig.offsetX;
        this.offsetY = turretConfig.offsetY;
        this.offsetHeight = turretConfig.offsetHeight;
        this.idleDir = turretConfig.idleDir;
        this.idleDirReversing = turretConfig.idleDirReversing;
        this.hasIdleDirReversing = turretConfig.hasIdleDirReversing;
        this.m = turretConfig.m;
        this.warmup = turretConfig.warmup;
        this.warmupCallDownRate = turretConfig.warmupCallDownRate;
        this.u = turretConfig.u;
        this.resourceUsage = turretConfig.resourceUsage;
        this.unloadUnitsAndGiveAttackOrderCount = turretConfig.unloadUnitsAndGiveAttackOrderCount;
        this.animationSet4 = turretConfig.animationSet4;
        this.warmupNoReset = turretConfig.warmupNoReset;
        this.warmupShootDelayTransfer = turretConfig.warmupShootDelayTransfer;
        this.p = turretConfig.p;
        this.q = turretConfig.q;
        this.r = turretConfig.r;
        this.parentTurret = turretConfig.parentTurret;
        this.isSlave = turretConfig.isSlave;
        this.canShoot = turretConfig.canShoot;
        this.invisibilityCondition = turretConfig.invisibilityCondition;
        this.shootSound = turretConfig.shootSound;
        this.shootFlameEffect = turretConfig.shootFlameEffect;
        this.shootLightColor = turretConfig.shootLightColor;
        this.warmupStartEffect = turretConfig.warmupStartEffect;
        this.preFireDuration = turretConfig.preFireDuration;
        this.preFireRendererId = turretConfig.preFireRendererId;
        this.preFireRendererVariant = turretConfig.preFireRendererVariant;
        this.preFireObserverId = turretConfig.preFireObserverId;
        this.preFireObserverVariant = turretConfig.preFireObserverVariant;
        this.canAttackFlyingUnits = turretConfig.canAttackFlyingUnits;
        this.canAttackLandUnits = turretConfig.canAttackLandUnits;
        this.canAttackUnderwaterUnits = turretConfig.canAttackUnderwaterUnits;
        this.canAttackNotTouchingWaterUnits = turretConfig.canAttackNotTouchingWaterUnits;
        this.canAttackCondition = turretConfig.canAttackCondition;
        this.canOnlyAttackUnitsWithTags = turretConfig.canOnlyAttackUnitsWithTags;
        this.canOnlyAttackUnitsWithoutTags = turretConfig.canOnlyAttackUnitsWithoutTags;
        this.canAttackMaxAngle = turretConfig.canAttackMaxAngle;
        this.image = turretConfig.image;
        this.teamColoredImages = turretConfig.teamColoredImages;
        this.chargeEffectImage = turretConfig.chargeEffectImage;
        this.projectileId = turretConfig.projectileId;
        this.altProjectileId = turretConfig.altProjectileId;
        this.altProjectileCondition = turretConfig.altProjectileCondition;
        this.turnSpeed = turretConfig.turnSpeed;
        this.turnSpeedAcceleration = turretConfig.turnSpeedAcceleration;
        this.turnSpeedDeceleration = turretConfig.turnSpeedDeceleration;
        this.barrelY = turretConfig.barrelY;
        this.barrelX = turretConfig.barrelX;
        this.barrelOffsetXOnOddShots = turretConfig.barrelOffsetXOnOddShots;
        this.barrelHeight = turretConfig.barrelHeight;
        this.linkedTurret = turretConfig.linkedTurret;
        this.shouldResetTurret = turretConfig.shouldResetTurret;
        this.idleSweepAngle = turretConfig.idleSweepAngle;
        this.idleSweepDelay = turretConfig.idleSweepDelay;
        this.idleSweepSpeed = turretConfig.idleSweepSpeed;
        this.idleSweepCondition = turretConfig.idleSweepCondition;
        this.idleSweepAddRandomDelay = turretConfig.idleSweepAddRandomDelay;
        this.idleSweepAddRandomAngle = turretConfig.idleSweepAddRandomAngle;
        this.limitingRange = turretConfig.limitingRange;
        this.limitingMinRange = turretConfig.limitingMinRange;
        this.limitingAngle = turretConfig.limitingAngle;
        this.aimOffsetSpread = turretConfig.aimOffsetSpread;
        this.clearTurretTargetAfterFiring = turretConfig.clearTurretTargetAfterFiring;
        this.rotationSpeed4 = turretConfig.rotationSpeed4;
        this.interceptProjectilesWithTags = turretConfig.interceptProjectilesWithTags;
        this.interceptProjectilesAndTargetingGroundUnderDistance = turretConfig.interceptProjectilesAndTargetingGroundUnderDistance;
        this.interceptProjectilesAndUnderDistance = turretConfig.interceptProjectilesAndUnderDistance;
        this.interceptProjectilesAndOverHeight = turretConfig.interceptProjectilesAndOverHeight;
        this.onShootPlayAnimation = turretConfig.onShootPlayAnimation;
        this.imageDrawOffsetX = turretConfig.imageDrawOffsetX;
        this.imageDrawOffsetY = turretConfig.imageDrawOffsetY;
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
            turretConfig.aimOffsetSpread = customUnitConfig.aimOffsetSpread;
            turretConfig.barrelY = customUnitConfig.turretSize;
        }
        turretConfig.offsetX = fValueOf.floatValue();
        turretConfig.offsetY = fValueOf2.floatValue();
        turretConfig.offsetHeight = iniFile.getFloat(str, "height", Float.valueOf(0.0f)).floatValue();
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
            turretConfig.warmup = time2.floatValue();
        }
        Float f = iniFile.getFloat(str, "warmupCallDownRate", (Float) null);
        if (f != null) {
            turretConfig.warmupCallDownRate = f.floatValue();
        }
        Boolean bool = iniFile.getBoolean(str, "warmupNoReset", (Boolean) null);
        if (bool != null) {
            turretConfig.warmupNoReset = bool.booleanValue();
        }
        Float f2 = iniFile.getFloat(str, "warmupShootDelayTransfer", (Float) null);
        if (f2 != null) {
            turretConfig.warmupShootDelayTransfer = f2.floatValue();
        }
        turretConfig.p = iniFile.getFloat(str, "recoilOffset", Float.valueOf(turretConfig.p)).floatValue();
        turretConfig.q = iniFile.getFloat(str, "recoilOutTime", Float.valueOf(turretConfig.q)).floatValue();
        turretConfig.r = iniFile.getTime(str, "recoilReturnTime", Float.valueOf(turretConfig.r)).floatValue();
        Float f3 = iniFile.getFloat(str, "energyUsage", (Float) null);
        if (f3 != null) {
            turretConfig.u = f3.floatValue();
        }
        turretConfig.unloadUnitsAndGiveAttackOrderCount = iniFile.getLogicBooleanUnit(str, "unloadUpToXUnitsAndGiveAttackOrder", Integer.valueOf(turretConfig.unloadUnitsAndGiveAttackOrderCount)).intValue();
        UnitPrice unitPriceA = UnitPrice.a(customUnitConfig, iniFile, str, "resourceUsage", true);
        if (unitPriceA != null && unitPriceA.d()) {
            turretConfig.resourceUsage = unitPriceA;
            customUnitConfig.a(unitPriceA);
        }
        String string3 = iniFile.getString(str, "attachedTo", (String) null);
        if (string3 != null) {
            turretConfig.parentTurret = customUnitConfig.findProjectileConfigByName(string3);
            if (turretConfig.parentTurret == null) {
                throw new RuntimeException("[" + str + "] Could not find attachedTo turret target:" + string3);
            }
            if (turretConfig.parentTurret == turretConfig) {
                throw new RuntimeException("Turret cannot FastArrayList attachedTo self");
            }
            customUnitConfig.hasAttachedTurrets = true;
        }
        Float f4 = iniFile.getFloat(str, "idleDir", (Float) null);
        if (f4 != null) {
            turretConfig.idleDir = f4.floatValue();
        }
        Float f5 = iniFile.getFloat(str, "idleDirReversing", (Float) null);
        if (f5 != null) {
            turretConfig.idleDirReversing = f5.floatValue();
            turretConfig.hasIdleDirReversing = true;
        } else if (!turretConfig.hasIdleDirReversing) {
            if (turretConfig.parentTurret != null) {
                turretConfig.idleDirReversing = 0.0f;
            } else {
                turretConfig.idleDirReversing = turretConfig.idleDir + 180.0f;
            }
        }
        Boolean bool2 = iniFile.getBoolean(str, "canShoot", (Boolean) null);
        Boolean bool3 = iniFile.getBoolean(str, "canAttack", (Boolean) null);
        if (bool2 != null && bool3 != null) {
            throw new RuntimeException("[" + str + "] Cannot use canShoot and canAttack at the same time, they have the same meaning");
        }
        if (bool2 != null) {
            turretConfig.canShoot = bool2.booleanValue();
        } else if (bool3 != null) {
            turretConfig.canShoot = bool3.booleanValue();
        }
        turretConfig.shootSound = SoundList.a(customUnitConfig, iniFile.getString(str, "shoot_sound", (String) null), turretConfig.shootSound);
        Float f6 = iniFile.getFloat(str, "shoot_sound_vol", (Float) null);
        if (f6 != null) {
            turretConfig.shootSound.a(f6.floatValue());
        }
        turretConfig.shootFlameEffect = customUnitConfig.addConfigExtension(iniFile.getString(str, "shoot_flame", (String) null), turretConfig.shootFlameEffect);
        turretConfig.shootLightColor = iniFile.getColorAsInt(str, "shoot_light", turretConfig.shootLightColor);
        turretConfig.warmupStartEffect = customUnitConfig.addConfigExtension(iniFile.getString(str, "warmupStartEffect", (String) null), turretConfig.warmupStartEffect);
        turretConfig.isSlave = iniFile.getBoolean(str, "slave", Boolean.valueOf(turretConfig.isSlave)).booleanValue();
        if (turretConfig.isSlave) {
            if (turretConfig.parentTurret == null) {
                throw new RuntimeException("Turret cannot FastArrayList a slave without being 'attachedTo' to another turret");
            }
            turretConfig.childTurret = turretConfig.parentTurret;
        }
        turretConfig.invisibilityCondition = iniFile.getLogicBoolean(customUnitConfig, str, "invisible", turretConfig.invisibilityCondition);
        turretConfig.canAttackFlyingUnits = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackFlyingUnits", turretConfig.canAttackFlyingUnits);
        turretConfig.canAttackLandUnits = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackLandUnits", turretConfig.canAttackLandUnits);
        turretConfig.canAttackUnderwaterUnits = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackUnderwaterUnits", turretConfig.canAttackUnderwaterUnits);
        turretConfig.canAttackNotTouchingWaterUnits = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackNotTouchingWaterUnits", turretConfig.canAttackNotTouchingWaterUnits);
        turretConfig.canAttackCondition = iniFile.getLogicBoolean(customUnitConfig, str, "canAttackCondition", turretConfig.canAttackCondition);
        turretConfig.canOnlyAttackUnitsWithTags = iniFile.getAnimationSet(customUnitConfig, str, "canOnlyAttackUnitsWithTags", turretConfig.canOnlyAttackUnitsWithTags);
        turretConfig.canOnlyAttackUnitsWithoutTags = iniFile.getAnimationSet(customUnitConfig, str, "canOnlyAttackUnitsWithoutTags", turretConfig.canOnlyAttackUnitsWithoutTags);
        String string4 = iniFile.getString(str, "projectile", (String) null);
        if (string4 != null) {
            CustomProjectileTemplate customProjectileTemplateFindProjectileTemplateByName = customUnitConfig.findProjectileTemplateByName(string4);
            if (customProjectileTemplateFindProjectileTemplateByName == null) {
                if ("0".equals(string4) && customUnitConfig.projectileTemplates.size() == 1 && customUnitConfig.findProjectileTemplateByName("1") != null) {
                    turretConfig.projectileId = 0;
                } else {
                    throw new RuntimeException("[" + str + "] Could not find projectile with name:" + string4);
                }
            } else {
                turretConfig.projectileId = customProjectileTemplateFindProjectileTemplateByName.projectileId;
            }
        }
        String string5 = iniFile.getString(str, "altProjectile", (String) null);
        if (string5 != null) {
            CustomProjectileTemplate customProjectileTemplateFindProjectileTemplateByName2 = customUnitConfig.findProjectileTemplateByName(string5);
            if (customProjectileTemplateFindProjectileTemplateByName2 == null) {
                throw new RuntimeException("[" + str + "]altProjectile: Could not find projectile with name:" + string5);
            }
            turretConfig.altProjectileId = customProjectileTemplateFindProjectileTemplateByName2.projectileId;
        }
        turretConfig.altProjectileCondition = iniFile.getLogicBoolean(customUnitConfig, str, "altProjectileCondition", turretConfig.altProjectileCondition);
        if (turretConfig.altProjectileId >= 0 && turretConfig.altProjectileCondition == null) {
            throw new RuntimeException("[" + str + "]altProjectileCondition is required with altProjectile");
        }
        turretConfig.canAttackMaxAngle = iniFile.getFloat(str, "canAttackMaxAngle", Float.valueOf(turretConfig.canAttackMaxAngle)).floatValue();
        turretConfig.turnSpeed = iniFile.getFloat(str, "turnSpeed", turretConfig.turnSpeed);
        turretConfig.turnSpeedAcceleration = iniFile.getFloat(str, "turnSpeedAcceleration", Float.valueOf(turretConfig.turnSpeedAcceleration)).floatValue();
        turretConfig.turnSpeedDeceleration = iniFile.getFloat(str, "turnSpeedDeceleration", Float.valueOf(turretConfig.turnSpeedDeceleration)).floatValue();
        Float f7 = iniFile.getFloat(str, "barrelY", (Float) null);
        Float f8 = iniFile.getFloat(str, "size", (Float) null);
        if (f7 != null && f8 != null) {
            throw new RuntimeException("Turret [" + str + "]: barrelY and size can not both FastArrayList used at the same time as they have the same meaning");
        }
        if (f7 != null) {
            turretConfig.barrelY = f7.floatValue();
        }
        if (f8 != null) {
            turretConfig.barrelY = f8.floatValue();
        }
        turretConfig.barrelX = iniFile.getFloat(str, "barrelX", Float.valueOf(turretConfig.barrelX)).floatValue();
        turretConfig.barrelOffsetXOnOddShots = iniFile.getFloat(str, "barrelOffsetX_onOddShots", Float.valueOf(turretConfig.barrelOffsetXOnOddShots)).floatValue();
        turretConfig.barrelHeight = iniFile.getFloat(str, "barrelHeight", Float.valueOf(turretConfig.barrelHeight)).floatValue();
        turretConfig.limitingRange = iniFile.getFloat(str, "limitingRange", Float.valueOf(turretConfig.limitingRange)).floatValue();
        turretConfig.limitingAngle = iniFile.getFloat(str, "limitingAngle", Float.valueOf(turretConfig.limitingAngle)).floatValue();
        turretConfig.limitingMinRange = iniFile.getFloat(str, "limitingMinRange", Float.valueOf(turretConfig.limitingMinRange)).floatValue();
        turretConfig.aimOffsetSpread = iniFile.getFloat(str, "aimOffsetSpread", Float.valueOf(turretConfig.aimOffsetSpread)).floatValue();
        if (turretConfig.limitingAngle >= 0.0f) {
            customUnitConfig.hasTurretWithLimitingAngle = true;
        }
        if (turretConfig.limitingRange < 99999.0f) {
            turretConfig.effectiveRange = turretConfig.limitingRange;
        } else {
            turretConfig.effectiveRange = customUnitConfig.unitStats.maxAttackRange;
        }
        turretConfig.effectiveRangeSquared = turretConfig.effectiveRange * turretConfig.effectiveRange;
        if (turretConfig.limitingMinRange > 0.0f) {
            turretConfig.limitingMinRangeSquared = turretConfig.limitingMinRange * turretConfig.limitingMinRange;
        } else {
            turretConfig.limitingMinRangeSquared = -1.0f;
        }
        turretConfig.showRangeUIGuide = iniFile.getBoolean(str, "showRangeUIGuide", (Boolean) null);
        turretConfig.laserDefenceEnergyUse = iniFile.getFloat(str, "laserDefenceEnergyUse", turretConfig.laserDefenceEnergyUse);
        if (turretConfig.laserDefenceEnergyUse != null) {
            customUnitConfig.hasLaserDefenceTurret = true;
            customUnitConfig.a(ProjectileInterceptorHook.a);
        }
        turretConfig.interceptProjectilesWithTags = AnimationTag.a(iniFile.getString(str, "interceptProjectiles_withTags", (String) null), turretConfig.interceptProjectilesWithTags);
        if (turretConfig.interceptProjectilesWithTags != null) {
            customUnitConfig.hasProjectileInterceptorTurret = true;
            customUnitConfig.a(ProjectileInterceptorHook.a);
            turretConfig.interceptProjectilesAndTargetingGroundUnderDistance = iniFile.getFloat(str, "interceptProjectiles_andTargetingGroundUnderDistance", Float.valueOf(turretConfig.interceptProjectilesAndTargetingGroundUnderDistance)).floatValue();
            turretConfig.interceptProjectilesAndUnderDistance = iniFile.getFloat(str, "interceptProjectiles_andUnderDistance", Float.valueOf(turretConfig.interceptProjectilesAndUnderDistance)).floatValue();
            turretConfig.interceptProjectilesAndOverHeight = iniFile.getFloat(str, "interceptProjectiles_andOverHeight", Float.valueOf(turretConfig.interceptProjectilesAndOverHeight)).floatValue();
        }
        turretConfig.shouldResetTurret = iniFile.getBoolean(str, "shouldResetTurret", Boolean.valueOf(turretConfig.shouldResetTurret)).booleanValue();
        turretConfig.idleSpin = iniFile.getFloat(str, "idleSpin", Float.valueOf(turretConfig.idleSpin)).floatValue();
        turretConfig.idleSweepAngle = iniFile.getFloat(str, "idleSweepAngle", Float.valueOf(turretConfig.idleSweepAngle)).floatValue();
        turretConfig.idleSweepDelay = iniFile.getFloat(str, "idleSweepDelay", Float.valueOf(turretConfig.idleSweepDelay)).floatValue();
        turretConfig.idleSweepSpeed = iniFile.getFloat(str, "idleSweepSpeed", Float.valueOf(turretConfig.idleSweepSpeed)).floatValue();
        turretConfig.idleSweepCondition = iniFile.getLogicBoolean(customUnitConfig, str, "idleSweepCondition", turretConfig.idleSweepCondition);
        turretConfig.idleSweepCondition = LogicBoolean.convertAlwaysTrueToNull(turretConfig.idleSweepCondition);
        turretConfig.idleSweepAddRandomDelay = iniFile.getFloat(str, "idleSweepAddRandomDelay", Float.valueOf(turretConfig.idleSweepAddRandomDelay)).floatValue();
        if (turretConfig.idleSweepAddRandomDelay < 0.0f) {
            float f9 = 1.0f;
            if (turretConfig.idleSweepDelay > 200.0f) {
                f9 = 20.0f;
            } else if (turretConfig.idleSweepDelay > 50.0f) {
                f9 = 5.0f;
            }
            turretConfig.idleSweepAddRandomDelay = f9;
        }
        turretConfig.idleSweepAddRandomAngle = iniFile.getFloat(str, "idleSweepAddRandomAngle", Float.valueOf(turretConfig.idleSweepAddRandomAngle)).floatValue();
        if (turretConfig.idleSweepAddRandomAngle < 0.0f) {
            throw new RuntimeException("Turret [" + str + "]: idleSweepAddRandomAngle must FastArrayList >= 0");
        }
        if (turretConfig.idleSweepAngle < 0.0f) {
            throw new RuntimeException("Turret [" + str + "]: idleSweepAngle must FastArrayList >= 0");
        }
        turretConfig.clearTurretTargetAfterFiring = iniFile.getBoolean(str, "clearTurretTargetAfterFiring", Boolean.valueOf(turretConfig.clearTurretTargetAfterFiring)).booleanValue();
        turretConfig.onShootPlayAnimation = customUnitConfig.loadCore(iniFile.getString(str, "onShoot_playAnimation", (String) null), turretConfig.onShootPlayAnimation);
        turretConfig.onShootFreezeBodyMovementFor = iniFile.getTime(str, "onShoot_freezeBodyMovementFor", Float.valueOf(turretConfig.onShootFreezeBodyMovementFor)).floatValue();
        turretConfig.onShootTriggerActions = iniFile.getCustomUnitAction(customUnitConfig, str, "onShoot_triggerActions", turretConfig.onShootTriggerActions);
        if (iniFile.getBoolean(str, "isMainNanoTurret", (Boolean) false).booleanValue()) {
            customUnitConfig.mainNanoTurret = turretConfig;
        }
        Texture textureA = customUnitConfig.a(iniFile, str, "image");
        if (textureA != null) {
            turretConfig.image = textureA;
            boolean zBooleanValue = customUnitConfig.teamColorsOnTurret;
            Boolean bool4 = iniFile.getBoolean(str, "image_applyTeamColors", (Boolean) null);
            if (bool4 != null) {
                zBooleanValue = bool4.booleanValue();
            }
            if (zBooleanValue) {
                turretConfig.teamColoredImages = customUnitConfig.a(turretConfig.image, customUnitConfig.teamColoringMode);
            } else {
                turretConfig.teamColoredImages = null;
            }
        }
        turretConfig.imageDrawOffsetX = iniFile.getFloat(str, "image_drawOffsetX", Float.valueOf(turretConfig.imageDrawOffsetX)).floatValue();
        turretConfig.imageDrawOffsetY = iniFile.getFloat(str, "image_drawOffsetY", Float.valueOf(turretConfig.imageDrawOffsetY)).floatValue();
        Texture textureA2 = customUnitConfig.a(iniFile, str, "chargeEffectImage");
        if (textureA2 != null) {
            turretConfig.chargeEffectImage = textureA2;
            customUnitConfig.hasTurretChargeEffectImage = true;
        }
        if (customUnitConfig.projectileTemplatesById[turretConfig.projectileId] == null) {
            throw new RuntimeException("Turret [" + str + "]: cannot find linked projectile:" + turretConfig.projectileId);
        }
        if (turretConfig.altProjectileId >= 0 && customUnitConfig.projectileTemplatesById[turretConfig.altProjectileId] == null) {
            throw new RuntimeException("Turret [" + str + "]altProjectile: cannot find linked projectile");
        }
        turretConfig.canAttackFlyingUnits = LogicBoolean.convertAlwaysTrueToNull(turretConfig.canAttackFlyingUnits);
        turretConfig.canAttackLandUnits = LogicBoolean.convertAlwaysTrueToNull(turretConfig.canAttackLandUnits);
        turretConfig.canAttackUnderwaterUnits = LogicBoolean.convertAlwaysTrueToNull(turretConfig.canAttackUnderwaterUnits);
        turretConfig.canAttackNotTouchingWaterUnits = LogicBoolean.convertAlwaysTrueToNull(turretConfig.canAttackNotTouchingWaterUnits);
        turretConfig.canAttackCondition = LogicBoolean.convertAlwaysTrueToNull(turretConfig.canAttackCondition);
        if (turretConfig.limitingAngle != -1.0f || turretConfig.canAttackFlyingUnits != null || turretConfig.canAttackLandUnits != null || turretConfig.canAttackUnderwaterUnits != null || turretConfig.canAttackNotTouchingWaterUnits != null || turretConfig.canAttackCondition != null) {
            turretConfig.canSpawn = true;
        }
        if (turretConfig.limitingRange < 99999.0f || turretConfig.limitingMinRange > 0.0f) {
            turretConfig.canSpawn = true;
            turretConfig.canSpawn2 = true;
        }
        if (turretConfig.canOnlyAttackUnitsWithTags != null || turretConfig.canOnlyAttackUnitsWithoutTags != null) {
            turretConfig.canSpawn = true;
        }
        turretConfig.canRotate = true;
    }
}
