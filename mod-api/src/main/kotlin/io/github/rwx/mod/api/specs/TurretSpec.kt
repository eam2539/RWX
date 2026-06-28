package io.github.rwx.mod.api.specs

import io.github.rwx.mod.api.RequiredIniKey

/**
 * Properties for [turret_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class TurretSpec(

    // ===== Positioning/Stats =====
    // Example: x: 0
    /**
     * Sets the horizontal position of the turret. 0 is center, negatives toward left, positives toward right.
     */
    @field:RequiredIniKey
    var x: Float = 0f,
    // Example: y:  40
    /**
     * Sets the vertical position of the turret.  0 is center, negatives toward bottom, positives toward top.
     */
    @field:RequiredIniKey
    var y: Float = 0f,
    // Example: copyFrom: 1
    /** Copy all values from another turret as defaut values for this turret */
    var copyFrom: String? = null,
    // Example: projectile: torpedo
    /** Projectile fired from this turret. */
    var projectile: ProjectileRef? = null,
    // Example: altProjectile: spear
    /**
     * Alternative projectile fired from this turret when altProjectileCondition is true
     */
    var altProjectile: String? = null,
    // Example: altProjectileCondition: if self.hp() < 100
    /** Used with altProjectile */
    var altProjectileCondition: LogicBoolean? = null,
    // Example: barrelX: 0
    /** Defaults to 0. Controls horizontal position for projectile spawn. */
    var barrelX: Float? = null,
    // Example: barrelY: 5
    /** Defaults to size. Note: size and barrelY have the same meaning */
    var barrelY: Float? = null,
    // Example: barrelHeight: 10
    /** Height of barrel in 3d. Effect projectile and shoot flame starting height */
    var barrelHeight: Float? = null,
    // Example: height: 30
    /** Height of the unit in 3D, to be used with decals */
    var height: Float? = null,
    // Example: size: 5
    /**
     * Controls the distance between the center of the turret and the point from where projectiles spawn.
     */
    var size: Float? = null,
    // Example: turnSpeed: 2
    /** Max turn speed of the turret */
    var turnSpeed: Float? = null,
    // Example: turnSpeedAcceleration: 0.4
    /** Defaults to disabled, and full turn speed is used. */
    var turnSpeedAcceleration: Float? = null,
    // Example: turnSpeedDeceleration: 0.2
    /**
     * Defaults to turnSpeedAcceleration. Setting this higher than turn acceleration might allow faster targets to be hit
     */
    var turnSpeedDeceleration: Float? = null,
    // Example: idleDir: 180
    /** Defaults to 0 */
    var idleDir: Float = 0f,
    // Example: idleDirReversing: 30
    /**
     * Defaults to idleDir+180 unless attached to another turret (as attached turret will often be rotating when reversing)
     */
    var idleDirReversing: Float? = null,
    // Example: shouldResetTurret: false
    /** Defaults true. False to disable the reseting turret angle when idle */
    var shouldResetTurret: Boolean = true,
    // Example: idleSweepAngle: 35
    /** Disabled by default. Controls how far the turret will "look" left and right */
    var idleSweepAngle: Int? = null,
    // Example: idleSweepDelay: 20
    /** Controls the delay between idleSweep movements */
    var idleSweepDelay: Float? = null,
    // Example: idleSweepSpeed: 0.3
    /** Controls the speed with which the turret sweeps when idle */
    var idleSweepSpeed: Float? = null,
    // Example: idleSweepCondition: if self.hp() > 400
    /** Disable idle sweep if false */
    var idleSweepCondition: LogicBoolean = "true",
    // Example: idleSweepAddRandomDelay: 30
    /**
     * Default 1-20 depends on idleSweepDelay, used to stop sweep syncing up with other units
     */
    var idleSweepAddRandomDelay: Float? = null,
    // Example: idleSweepAddRandomAngle: 15
    /** Default 0 */
    var idleSweepAddRandomAngle: Int? = null,
    // Example: attachedTo: base
    /**
     * Id of another turret to attach to, will be positioned relative to it, and rotate with it.
     */
    var attachedTo: String? = null,
    // Example: slave: true
    /**
     * Locks this turret's direction and shot cooldown to attached turret. Often used with warmup for multiple barrel guns
     */
    var slave: Boolean = false,
    // Example: isMainNanoTurret: true
    /**
     * Defaults to false. Turret to use for creating buildings, etc. should only be true on one turret, and should have canShoot set to false
     */
    var isMainNanoTurret: Boolean = false,
    // Example: energyUsage: 1
    /** Required energy to fire weapon. Same as resourceUsage: energy=X */
    var energyUsage: Float = 0f,
    // Example: resourceUsage: credits=5, energy=5, hp=100, shield=5, ammo=1
    /** can be in credits/energy/hp/shield/ammo. Stops firing if not met */
    var resourceUsage: ResourceBundle? = null,

    // ===== Timing =====
    // Example: delay: 2s
    /** Override global shootDelay for this turret */
    var delay: Float? = null,
    // Example: linkDelayWithTurret: 1
    /**
     * When this other turret fires the cooldown delay on this turret will be reset/removed
     */
    var linkDelayWithTurret: String? = null,
    // Example: warmup: 5s
    /** Delay before firing a shot. */
    var warmup: Float? = null,
    // Example: warmupCallDownRate: 10
    /** Rate to reduce warmup when turret is not ready to fire at any targets */
    var warmupCallDownRate: Float? = null,
    // Example: warmupNoReset: true
    /**
     * Defaults to false. When true warmup is not reset after firing a shot and turret doesn't wait for warnup. Used with warmupCallDownRate and warmupShootDelayTransfer.
     */
    var warmupNoReset: Boolean = false,
    // Example: warmupShootDelayTransfer: 20
    /**
     * Defaults to 0, amount to reduces the next shot delay depending on warmup. When used with warmupNoReset, can make a each shot faster.
     */
    var warmupShootDelayTransfer: Float? = null,

    // ===== On Shoot =====
    // Example: onShoot_freezeBodyMovementFor: 2s
    /** Freezes body movement while shooting. */
    var onShootFreezeBodyMovementFor: Float? = null,
    // Example: barrelOffsetX_onOddShots: -10
    /**
     * 0 by default. Sets a barrelX offset only during odd numbered shots, useful for twin-barreled units. Use with barrelX. Use the opposite value of barrelX for most cases.
     */
    var barrelOffsetXOnOddShots: Float? = null,

    // ===== Targeting control =====
    // Example: aimOffsetSpread: 14
    /** Sets the shot inaccuracy. 0 will make all shots land on target's center */
    var aimOffsetSpread: String? = null,
    // Example: canShoot: true
    /** Defaults to true */
    var canShoot: Boolean = true,
    // Example: canAttackFlyingUnits: false
    /**
     * Narrows targeting for this turret, note targeting for the whole unit in [attack] is applied first. (so you can only use this to target less not more)
     */
    var canAttackFlyingUnits: LogicBoolean? = null,
    // Example: canAttackLandUnits: true
    /** Default true. If false unit cannot attack surface units. */
    var canAttackLandUnits: LogicBoolean? = null,
    // Example: canAttackUnderwaterUnits: true
    /** Default true. If false unit cannot attack underwaterunits. */
    var canAttackUnderwaterUnits: LogicBoolean? = null,
    // Example: canAttackNotTouchingWaterUnits: false
    /**
     * Default true. If false unit can only attack units in contact with the water. Used for units with torpedos.
     */
    var canAttackNotTouchingWaterUnits: LogicBoolean? = null,
    // Example: canOnlyAttackUnitsWithTags: shelled, armoured
    /** Only attacks potential targets with specific tags */
    var canOnlyAttackUnitsWithTags: List<String>? = null,
    // Example: canOnlyAttackUnitsWithoutTags: corrosive, fleshy
    /** Only attacks potential targets without specific tags */
    var canOnlyAttackUnitsWithoutTags: List<String>? = null,
    // Example: canAttackCondition: if not self.flying
    /**
     * Normally, used to optionally disable a turret based on a LogicBoolean. Eg: this unit's height
     */
    var canAttackCondition: LogicBoolean? = null,
    // Example: canAttackMaxAngle: 181
    /**
     * Max angle to target for turret to be allowed for fire. Defaults to 5, don't set lower. Can be set to 181 for turrets that don't need to turn to fire missiles.
     */
    var canAttackMaxAngle: Float = 5f,
    // Example: clearTurretTargetAfterFiring:
    /** Clears the turrets sub-target when using multi-targeting */
    var clearTurretTargetAfterFiring: Boolean? = null,
    // Example: limitingRange: 100
    /**
     * Make this turret have less range than the maxAttackRange. Do not apply this to all turrets change maxAttackRange instead.
     */
    var limitingRange: Float? = null,
    // Example: limitingAngle: 60
    /** Linked with idleDir. Turret will only be able to fire at units +/- this angle. */
    var limitingAngle: Int? = null,
    // Example: limitingMinRange: 200
    /** Sets minimum range for turret. */
    var limitingMinRange: Int? = null,
    // Example: interceptProjectiles_withTags: nuke
    /** Currently used with anti-nuke units. */
    var interceptProjectilesWithTags: List<String>? = null,
    // Example: interceptProjectiles_andTargetingGroundUnderDistance: 100
    /** Minimum distance required for intercepting projectile */
    var interceptProjectilesAndTargetingGroundUnderDistance: Int? = null,
    // Example: interceptProjectiles_andUnderDistance: 300
    /** Defaults to 2000, distance inflight before firing */
    var interceptProjectilesAndUnderDistance: Int? = null,
    // Example: interceptProjectiles_andOverHeight: 10
    /** Defaults to 0. Sets the projectile target height limit */
    var interceptProjectilesAndOverHeight: Int? = null,
    // Example: laserDefenceEnergyUse: 0.25
    /**
     * Set to enable a projectile laser defence from this turret. Should also set the energyMax in core.
     */
    var laserDefenceEnergyUse: Float? = null,

    // ===== Graphics and effects =====
    // Example: invisible: true
    /** Don't render this turret, but still can shoot, etc. */
    var invisible: LogicBoolean = "false",
    // Example: image: gun.png
    /** Use custom image. Overrides unit's main turret image */
    var image: Texture? = null,
    // Example: image_applyTeamColors: true
    /**
     * When true, the specific turret applies team colors over the special greens in the sprite.
     */
    var imageApplyTeamColors: Boolean? = null,
    // Example: image_drawOffsetX: 0
    /** Moves the image horizontally from the turret center. */
    var imageDrawOffsetX: Float? = null,
    // Example: image_drawOffsetY: 30
    /** Moves the image verticallly from the turret center. */
    var imageDrawOffsetY: Float? = null,
    /*
     * Example:
     * chargeEffectImage: glow.png
     * 
     */
    /** Used with warmup. Shows a scaling effect image on turret barrel when charging. */
    var chargeEffectImage: String? = null,
    // Example: warmupStartEffect: CUSTOM:abosrb
    /** Spawns specified effects when the specific turret is charging/warming up */
    var warmupStartEffect: String? = null,
    // Example: shoot_sound: tank_firing | shoot_sound: missile.wav | shoot_sound: ROOT:audio/shoot.ogg
    /**
     * Can be linked to an .ogg or .wav file, or one of the default game sounds (list at bottom of reference)
     */
    var shootSound: String? = null,
    // Example: shoot_sound_vol: 0.4
    /** Sets the volume of the shoot sound */
    var shootSoundVol: Float? = null,
    // Example: eg: shoot_flame: smoke, CUSTOM:lightFade, CUSTOM:pop*5
    /** Current types are: small, large, smoke, shockwave, or CUSTOM: effectSectionName */
    var shoot_flame: String? = null,
    // Example: shoot_light: #fafafa
    /** Produces a colored light upon shooting */
    var shootLight: Color? = null,
    // Example: idleSpin: 2
    /** Spin rate when idle, used on missile turrets */
    var idleSpin: Float? = null,
    // Example: onShoot_playAnimation: heatUp
    /** Play a custom animation from an [animation] section after firing this turret */
    var onShootPlayAnimation: String? = null,
    // Example: onShoot_triggerActions: spawnShells
    /** Trigger these actions each time this turret fires */
    var onShootTriggerActions: String? = null,
    // Example: unloadUpToXUnitsAndGiveAttackOrder: 1
    /**
     * Unloads X units at turret barrel locations and gives them the attack order of turret target
     */
    var unloadUpToXUnitsAndGiveAttackOrder: Int? = null,
    // Example: recoilOffset: 10
    /** Push turret forward or back after firing for a recoil effect. Value in pixels. */
    var recoilOffset: Float? = null,
    // Example: recoilOutTime: 1s
    /** Time to get to offset position after firing */
    var recoilOutTime: Float? = null,
    // Example: recoilReturnTime: 0.5s
    /** Time to return to default position */
    var recoilReturnTime: Float? = null,
    // Example: showRangeUIGuide: true
    /** Defaults to true. Displays the attack range for this turret */
    var showRangeUIGuide: Boolean? = null
)
