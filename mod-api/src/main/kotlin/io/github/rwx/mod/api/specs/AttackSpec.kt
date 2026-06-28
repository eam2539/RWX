package io.github.rwx.mod.api.specs

import io.github.rwx.mod.api.RequiredIniKey


/**
 * Properties for [attack] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class AttackSpec(
    // Example: canAttack: true
    /**
     * If set to false, can not attack any unit. Regards of other canAttack options below.
     */
    @field:RequiredIniKey
    var canAttack: Boolean = true,
    // Example: canAttackFlyingUnits: false
    /** can also be narrowed per turret. Note: not required if canAttack is false. */
    @field:RequiredIniKey
    var canAttackFlyingUnits: LogicBoolean = "false",
    // Example: canAttackLandUnits: true
    /** can also be narrowed per turret */
    @field:RequiredIniKey
    var canAttackLandUnits: LogicBoolean = "true",
    // Example: canAttackUnderwaterUnits: false
    /** can also be narrowed per turret */
    @field:RequiredIniKey
    var canAttackUnderwaterUnits: LogicBoolean = "false",
    // Example: maxAttackRange: 300
    /** (multiplied by globalScale) */
    var maxAttackRange: Float = 100f,
    // Example: canAttackNotTouchingWaterUnits: false
    /**
     * Default true. If false unit can only attack units in contact with the water. Used for units with torpedos. (can also be set per turret)
     */
    var canAttackNotTouchingWaterUnits: LogicBoolean = "true",
    // Example: canOnlyAttackUnitsWithTags: metallic, ceramic
    /** Will only attack units that has the specified tags. */
    var canOnlyAttackUnitsWithTags: List<String> = listOf(),
    // Example: canOnlyAttackUnitsWithoutTags: corrosive, magnetic
    /** Can only attack units without the specified tags. */
    var canOnlyAttackUnitsWithoutTags: List<String> = listOf(),
    // Example: turretMultiTargeting: true
    /**
     * Allow each turrets to fire at a different target at the same time. Very useful if [turret]limitingAngle is used
     */
    var turretMultiTargeting: Boolean? = null,
    // Example: isMelee: true
    /**
     * Used with a low attack range (like maxAttackRange: 9) makes src and target radius get added to range, and effects AI.
     */
    var isMelee: Boolean = false,
    // Example: meleeEngangementDistance: 400
    /**
     * Makes unit move to attack nearby units. Defaults to 250 for melee, and 0 for non melee (Works even if non-melee, but might be unexpected to players)
     */
    var meleeEngangementDistance: Float = 250f,
    // Example: turretRotateWithBody: true
    /** Are all turrets rotated when body rotates. Defaults to true */
    var turretRotateWithBody: Boolean? = null,
    // Example: attackMovement: true
    /** normal/bomber. bomber attack movement will retreat when energy runs out */
    var attackMovement: AttackMovementType = AttackMovementType.normal,
    // Example: dieOnAttack: true
    /** Will die when it attacks. */
    var dieOnAttack: Boolean = false,
    // Example: isFixedFiring: true
    /**
     * Must aim body at target to shoot. Will often make the unit need to stop before it can aim and shoot.
     */
    var isFixedFiring: Boolean = false,
    // Example: aimOffsetSpread:0
    /**
     * Offset each shot multiplied by target radius. Defaults to 0.6. aimOffsetSpread:0 will make unit always attack center
     */
    var aimOffsetSpread: Float? = null,
    // Example: stopTargetingAfterFiring: true
    /** Unit stops targeting after firing a shot. Rarely used or needed. */
    var stopTargetingAfterFiring: Boolean = false,
    // Example: disablePassiveTargeting: true
    /** Unit only attacks manually ordered target. Rarely used or needed. */
    var disablePassiveTargeting: Boolean = false,
    // Example: showRangeUIGuide: true
    /**
     * Will it show the range indicator. Useful for showing ranges in radar and related structures.
     */
    var showRangeUIGuide: Boolean? = null,
    // Example: shootDelayMultiplier: 0.75
    /** Defaults to 1. Can be dynamically changed with setUnitStats */
    var shootDelayMultiplier: Float? = null,
    // Example: shootDamageMultiplier: 2.43
    /** Defaults to 1. Can be dynamically changed with setUnitStats */
    var shootDamageMultiplier: Float? = null,

    // ===== Deprecated Keys - can be used but better to set these per turret =====
    /** (multiplied by globalScale) */
    var turretSize: Float? = null,
    var turretTurnSpeed: Float? = null,
    /** Global delay, can also use delay on each turret */
    var shootDelay: Float? = null
)

enum class AttackMovementType {
    normal,
    strafing,
    bomber
}
