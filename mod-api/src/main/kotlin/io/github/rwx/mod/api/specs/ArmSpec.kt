package io.github.rwx.mod.api.specs


/**
 * Properties for [arm_#] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class ArmSpec(
    // Example: x: 10
    /** Sets position of the foot on the X axis. */
    var x: Float? = null,
    // Example: y: 20
    /** Sets position of the foot on the Y axis. */
    var y: Float? = null,
    // Example: copyFrom: 1
    /** Copy from another leg. Useful to only need to set leg values once */
    var copyFrom: Int? = null,
    // Example: attach_x: 10
    /** Sets the leg's attach point on the X axis. */
    var attachX: Float? = null,
    // Example: attach_y: 0
    /** Sets the leg's attach point on the Y axis. */
    var attachY: Float? = null,
    // Example: rotateSpeed: 2
    /** Sets the leg's rotation speed on movement */
    var rotateSpeed: Float? = null,
    // Example: endDirOffset: 45
    /** Target foot/end rotation relative to body */
    var endDirOffset: Int? = null,
    // Example: lockMovement: true
    /** Lock to unit body. Useful if walking unit converted to a flying unit. */
    var lockMovement: Boolean? = null,
    // Example: heightSpeed: 2
    /** Sets how fast the leg rises while walking */
    var heightSpeed: Float? = null,
    // Example: moveSpeed: 3
    /** Sets how fast the leg moves while walking */
    var moveSpeed: Float? = null,
    // Example: moveWarmUp: 2
    /** Delay before the leg moved */
    var moveWarmUp: String? = null,
    // Example: holdDisMin: 10
    /**
     * Defaults to 7. Reposition leg at this distance if neighbor legs are not already repositioning.
     */
    var holdDisMin: Float? = null,
    // Example: holdDisMax: 20
    /**
     * Defaults to 16. Force reposition of leg at this distance. Repositions leg at this distance even if a neighboring leg is moving
     */
    var holdDisMax: Float? = null,
    // Example: holdDisMin_maxMovingLegs: 4
    /** Sets maximum amount of legs to check before applying distance holding */
    var holdDisMinMaxMovingLegs: Int? = null,
    // Example: hold_moveOnlyIfFurthest: true
    /**
     * Defaults to true. When true, starting moving leg only if it is currently the furthest leg from where it should be.
     */
    var holdMoveOnlyIfFurthest: Boolean? = null,
    // Example: holdDisMin_checkNeighbours: true
    /** Checks neighbors before applying distance holding */
    var holdDisMinCheckNeighbours: Boolean? = null,
    // Example: hardLimit: 60
    /** Defaults to 50. Force leg to never go this far. Better to not be reached. */
    var hardLimit: Float? = null,
    // Example: estimatingPositionMultiplier: 2
    /**
     * defaults to 1. Predicts were unit will be for leg placement based on unit speed.
     */
    var estimatingPositionMultiplier: Float? = null,

    // ===== Graphics and effects =====
    // Example: hidden: true
    /** When true, hides the arm/leg */
    var hidden: LogicBoolean? = null,
    // Example: image_end: rotor.png
    /** Sets the main arm end sprite */
    var imageEnd: String? = null,
    // Example: image_end_shadow: AUTO
    /** Sets the shadow for the arm */
    var imageEndShadow: String? = null,
    // Example: image_end_teamColors: true
    /** When true, the arm end will apply team colors */
    var imageEndTeamColors: Boolean? = null,
    // Example: image_foot: footR.png
    /** same as image_end, but acts as the foot for the leg */
    var imageFoot: String? = null,
    // Example: image_foot_shadow: AUTO
    /** Sets the shadow for the foot */
    var imageFootShadow: String? = null,
    // Example: image_middle: NONE
    /** Sets the arm image */
    var imageMiddle: String? = null,
    // Example: image_leg: legR.png
    /** Sets the leg image */
    var imageLeg: String? = null,
    // Example: draw_foot_on_top: true
    /** Renders foot above leg */
    var drawFootOnTop: Boolean? = null,
    // Example: drawOverBody: false
    /** Draw over body */
    var drawOverBody: Boolean? = null,
    // Example: drawUnderAllUnits: true
    /** Draw over all units */
    var drawUnderAllUnits: Boolean? = null,
    // Example: drawDirOffset: 73
    /** Rotates the foot/arm sprite to a specified direction */
    var drawDirOffset: Float? = null,
    // Example: dust_effect: true
    /** Spawns dust particles on each step. */
    var dustEffect: Boolean? = null,
    // Example: spinRate: 2
    /** Makes arm/leg spin, like idleSpin for turrets. Great for helicopters */
    var spinRate: Float? = null,
    // Example: favourOppositeSideNeighbours: true
    /** calculate neighbours with X 10 times closer than Y */
    var favourOppositeSideNeighbours: Boolean? = null,
    // Example: drawLegWhenZoomedOut: false
    /** For performance, defaults changes based on unit size */
    var drawLegWhenZoomedOut: Boolean? = null,
    // Example: drawFootWhenZoomedOut: false
    /** For performance, defaults changes based on unit size */
    var drawFootWhenZoomedOut: Boolean? = null,
    // Example: liftingHeightOffset: 20
    /** Often used with decals */
    var liftingHeightOffset: Int? = null,
    // Example: targetHeight: 10
    /** Sets height of the leg */
    var targetHeight: Int? = null,
    // Example: targetHeightRelative: true
    /** Apply height relative to unit's height */
    var targetHeightRelative: Boolean? = null,
    /** Unused */
    var resetAngle: Float? = null
)
