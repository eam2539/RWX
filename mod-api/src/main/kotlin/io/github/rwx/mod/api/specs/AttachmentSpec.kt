package io.github.rwx.mod.api.specs

import io.github.rwx.mod.api.RequiredIniKey

/**
 * Properties for [attachment_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class AttachmentSpec(
    // Example: x
    /** Sets the horiztontal position of the attachment */
    @field:RequiredIniKey
    var x: Float = 0f,
    // Example: y
    /** Sets the vertical position of the attachment */
    @field:RequiredIniKey
    var y: Float = 0f,
    // Example: height
    /** Sets the elevation of the attachment */
    var height: Float? = null,
    // Example: idleDir
    /** Sets the direction of the attachment when idle */
    var idleDir: Int? = null,
    // Example: idleDirReversing
    /** Sets the direction of the attachment when the base unit is moving in reverse */
    var idleDirReversing: Int? = null,
    // Example: isVisible
    /** Defaults to true. If false, the attachment is hidden */
    var isVisible: Boolean = true,
    // Example: onCreateSpawnUnitOf
    /** Upon spawning the unit, specified attached unit is also spawned as attachment */
    var onCreateSpawnUnitOf: String? = null,
    // Example: isUnselectable
    /** Defaults to false. When true, the player cannot click the attachment. */
    var isUnselectable: Boolean = false,
    // Example: isUnselectableAsTarget
    /** When true, the attachment cannot be selected as an attack or ability target. */
    var isUnselectableAsTarget: Boolean? = null,
    // Example: canAttack
    /** Defaults to true. Set to false to stop this attachment attacking. */
    var canAttack: Boolean = true,
    // Example: canBeAttackedAndDamaged
    /** When true, the attachment is vulnerable to attacks */
    var canBeAttackedAndDamaged: Boolean? = null,
    // Example: deattachIfWantingToMove
    /**
     * If the unit is ordered to move, it will detach. This includes waypoints from actions.
     */
    var deattachIfWantingToMove: Boolean? = null,
    // Example: lockLegMovement
    /** Locks the leg movement while attached. */
    var lockLegMovement: Boolean? = null,
    // Example: keepAliveWhenParentDies
    /** Defaults to false */
    var keepAliveWhenParentDies: Boolean = false,
    // Example: setDrawLayerOnTop
    /** Renders the attachment above the base unit */
    var setDrawLayerOnTop: Boolean? = null,
    // Example: setDrawLayerOnBottom
    /** Renders the attachment below the base unit */
    var setDrawLayerOnBottom: Boolean? = null,
    // Example: addTransportedUnits
    /**
     * Often used with transport units, when true, attaches one of the unit passenger to this attachment slot
     */
    var addTransportedUnits: Boolean? = null,
    // Example: lockRotation
    /** When true, stops the attachment  from rotating */
    var lockRotation: Boolean? = null,
    // Example: rotateWithParent
    /** When true, the attachment rotates with the parent unit */
    var rotateWithParent: Boolean? = null,
    // Example: resetRotationWhenNotAttacking
    /** Similar to shouldResetTurret:for turrets. */
    var resetRotationWhenNotAttacking: Boolean? = null,
    // Example: prioritizeParentsMainTarget
    /** It will priotize targeting the main target. Defaults to true. */
    var prioritizeParentsMainTarget: Boolean? = null,
    // Example: onlyAttackParentsMainTarget
    /** Prevents the attachment from attacking targets other than the parent's main target. */
    var onlyAttackParentsMainTarget: Boolean? = null,
    // Example: alwaysAllowedToAttackParentsMainTarget
    /** Will always attack the parents main target. */
    var alwaysAllowedToAttackParentsMainTarget: Boolean? = null,
    // Example: onParentTeamChangeKeepCurrentTeam
    /**
     * Defaults false. If true attached units are not converted when parent changes team. Eg from [projectile]convertHitToSourceTeam
     */
    var onParentTeamChangeKeepCurrentTeam: Boolean = false,
    // Example: onConvertKeepExistingUnitInSameSlot
    /**
     * When true, the attachment is retained on the same attachment slot when the parent is converted to another unit
     */
    var onConvertKeepExistingUnitInSameSlot: Boolean? = null,
    // Example: unloadInCurrentPosition
    /**
     * Defaults false. If true transported attached units are kept current attached location when unloading
     */
    var unloadInCurrentPosition: Boolean = false,
    // Example: keepWaypointsNeedingMovement
    /**
     * Defaults false. If true attached units keep waypoints with movement even while they cannot move. Useful if they will be automatically deattached soon.
     */
    var keepWaypointsNeedingMovement: Boolean = false,
    // Example: smoothlyBlendPositionWhenExistingUnitAdded
    var smoothlyBlendPositionWhenExistingUnitAdded: Boolean? = null,
    // Example: showAllActionsFrom
    /** Show all actions of the units attached in the parent unit list when selected */
    var showAllActionsFrom: LogicBoolean? = null,
    // Example: createIncompleteIfParentIs:
    /**
     * If parent hasn't been built, create attachment with the same built value. Links built values till attachment is complete. Useful for buildings built with nano.
     */
    var createIncompleteIfParentIs: Boolean? = null,
    // Example: redirectDamageToParent:
    /**
     * Redirects damage done to this attachment to the parent instead of damaging itself directly
     */
    var redirectDamageToParent: Boolean? = null,
    // Example: redirectDamageToParent_shieldOnly:
    /**
     * When enemies attack the attachment, all damage are redirected to the parent's shield
     */
    var redirectDamageToParentShieldOnly: Boolean? = null
)
