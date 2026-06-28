package io.github.rwx.mod.api.specs


/**
 * Properties for [movement] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class MovementSpec(
    // Example: movementType: LAND
    /**
     * Defines what kind of terrain the unit will be able to move, along with other properties. Can use only one: NONE, LAND, AIR, WATER, HOVER, BUILDING, OVER_CLIFF, OVER_CLIFF_WATER
     */
    var movementType: MovementType = MovementType.NONE,
    // Example: slowDeathFall: true
    /**
     * Used with large aircraft. Makes the unit fall slowly while maintaining its speed at the time of death.
     */
    var slowDeathFall: Boolean = false,
    // Example: moveSpeed: 1.2
    /** Maximum movement speed of the unit. */
    var moveSpeed: Float = 1.0f,
    // Example: moveAccelerationSpeed: 0.07
    /** Defines how fast units accelerate to max speed. */
    var moveAccelerationSpeed: Float? = null,
    // Example: moveDecelerationSpeed: 0.17
    /** Don't make this too low or units will have trouble stopping at waypoints */
    var moveDecelerationSpeed: Float? = null,
    // Example: reverseSpeedPercentage: 0
    /**
     * 0.6 default. Over 0.4 will reverse for short distances (at 40% speed). If set to 1 will drive in reverse same as forwards. Useful if slow turning
     */
    var reverseSpeedPercentage: Float = 0.6f,
    // Example: landOnGround: false
    /** Should flying unit land when idle. */
    var landOnGround: Boolean = false,
    // Example: targetHeight: 25
    /** Defaults to 0 but if AIR movementType default is 35 */
    var targetHeight: Float? = null,
    // Example: targetHeightDrift: 1
    /**
     * Smooth animated height change. Defaults to 0 but if AIR movementType default is 1.5
     */
    var targetHeightDrift: Float? = null,
    // Example: startingHeightOffset: 40
    /** Sets the initial height on spawn. Defaults at 0. */
    var startingHeightOffset: Float = 0f,
    // Example: heightChangeRate: 3
    /** Rate at which the unit changes height, either from converting or drifting */
    var heightChangeRate: Float = 0F,
    // Example: fallingAcceleration:
    /** The acceleration in which a unit drops */
    var fallingAcceleration: Float? = null,
    // Example: fallingAccelerationDead:
    /** fallingAcceleration but when destroyed */
    var fallingAccelerationDead: Float? = null,
    // Example: maxTurnSpeed: 4
    /** Sets the top turning speed of a unit */
    var maxTurnSpeed: Float = 4f,
    // Example: turnAcceleration: 1
    /** Defines how fast units accelerate to max turn speed. */
    var turnAcceleration: Float? = null,
    // Example: moveSlidingMode: true
    /**
     * Makes the unit slide when moveDecelerationSpeed is lower, making them drift and feel natural
     */
    var moveSlidingMode: Boolean = false,
    // Example: moveIgnoringBody: true
    /**
     * Allows the unit to move without fully turning in the direction its moving, useful for ships and air units
     */
    var moveIgnoringBody: Boolean = false,
    // Example: moveSlidingDir: 180
    /** Sets direction when sliding */
    var moveSlidingDir: Int? = null, //Note: not found handling logic in RW
    // Example: joinsGroupFormations: false
    /**
     * Defaults to true. Changing not recommended. When false, the unit will directly move to the assigned waypoint without taking space consideration from neighboring units.
     */
    var joinsGroupFormations: Boolean = true
)
