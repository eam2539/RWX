package io.github.rwx.mod.api.specs


/**
 * Properties for [animation_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class AnimationSpec(
    // Example: onActions: move
    /**
     * Automatically plays the animation on specified events. Options: move, attack, idle, underConstruction, underConstructionWithLinkedBuiltTime, queuedUnits
     */
    var onActions: String? = null,
    // Example: onActionsQueuedUnitPlayAt: 0
    /**
     * For onAction: queuedUnits. Amount queue needs to reach before starting, set between 0-1
     */
    var onActionsQueuedUnitPlayAt: Float? = null,
    // Example: blendIn: 2s
    /** Blend with last animation for this time */
    var blendIn: Float? = null,
    // Example: blendOut: 3s
    /** Blend with next animation for this time */
    var blendOut: Float? = null,
    // Example: pingPong: true
    /** Play animation in reverse after it ends */
    var pingPong: Boolean? = null,
    // Example: KeyframeTimeScale: 1
    /**
     * Scales all keyframe times, useful to make an animation faster/slower without changing everything
     */
    var keyframeTimeScale: Float? = null,

    // ===== Keyframes - create as many as needed =====
    // Example: arm1_5s: {x: 5, dir: 90 }
    // Keyframe Template: arm#_[time]
    // Pattern: Replace # with limb number (1,2,3...), [time] with duration (e.g., 5s, 100ms)
    /** Adds a keyframe at time. Use multiple times to create animation. */
    var armNKeyframes: Map<String, Map<String, Any>>? = null,
    // Example: leg1_3s: {dir: 300}
    // Keyframe Template: leg#_[time]
    // Pattern: Replace # with limb number (1,2,3...), [time] with duration (e.g., 5s, 100ms)
    /** Adds a keyframe at time. Use multiple times to create animation. */
    var legNKeyframes: Map<String, Map<String, Any>>? = null,
    // Example: body_4s: {frame: 4, scale: 0.5}
    // Keyframe Template: body_[time]
    // Pattern: Replace # with limb number (1,2,3...), [time] with duration (e.g., 5s, 100ms)
    /** Adds a keyframe at time for body. Only frame and scale allowed on body */
    var bodyKeyframes: Map<String, Map<String, Any>>? = null,
    // Example: effect_2s: {name:CUSTOM|myExplode,  x: 0,y: 5}
    // Keyframe Template: effect_[time]
    // Pattern: Replace # with limb number (1,2,3...), [time] with duration (e.g., 5s, 100ms)
    /** Spawn effects while playing an animation */
    var effectKeyframes: Map<String, Map<String, Any>>? = null,
    // Example: direction_units: 45
    /** Overrides [graphics]animation_direction_units while this animation is playing */
    var directionUnits: Float? = null,
    // Example: direction_strideX: 20
    /** Overrides [graphics]animation_direction_strideX */
    var directionStrideX: Int? = null,
    // Example: direction_strideY: 50
    /** Overrides [graphics]animation_direction_strideY */
    var directionStrideY: Int? = null,
    // Example: direction_starting: 0
    /** Overrides [graphics]animation_direction_starting */
    var directionStarting: Float? = null,

    // ===== Deprecated Keys (can be used but there are better ways) =====
    // DEPRECATED: start : int - Consider using newer alternatives
    /** Start image frame. deprecated */
    //var start: String? = null,
    // DEPRECATED: end : int - Consider using newer alternatives
    /** End image frame. deprecated */
    //var end: String? = null,
    // DEPRECATED: scale_start : float - Consider using newer alternatives
    /** Start scale. Deprecated, use body keyframes instead. */
    //var scaleStart: String? = null,
    // DEPRECATED: scale_end : float - Consider using newer alternatives
    /** End scale. Deprecated, use body keyframes instead. */
    //var scaleEnd: String? = null,
    // DEPRECATED: speed : float - Consider using newer alternatives
    /** Speed, smaller is faster. Only effects start, end, scale_start, scale_end */
    //var speed: String? = null,
    // Example: Spawn lines specifically for units, used with "unit ref" value types
    /** spawnUnits:LIST */
    //var type: String? = null,
    // Example: Example
    /** Description */
    //var code: String? = null,

    // ===== Most units spawning keys support multiple units with parameters =====
    // Example: spawnUnits: tank*3(neutralTeam=true, offsetRandomX=20, offsetRandomY=20, gridAlign=true)
    /** Spawn the unit on the neutral team instead of the same team as source */
    var neutralTeam: String? = null,
    // Example: spawnUnits: egg(setToTeamOfLastAttacker=true)
    /**
     * Spawn the unit on the last attacker of source (useful on [core]unitsSpawnedOnDeath)
     */
    var setToTeamOfLastAttacker: String? = null,
    // Example: spawnUnits: shards(spawnChance=0.3)
    /** Chance this unit will spawn. Defaults to 1. */
    var spawnChance: String? = null,
    // Example: spawnUnits: tank(spawnSource=memory.lastLocation)
    /** Changes spawn location and team of spawned units to this unit ref. */
    var spawnSource: String? = null,
    // Example: spawnUnits: treeA(spawnChance=0.5, maxSpawnLimit=1), treeB(maxSpawnLimit=1)
    /** Useful with spawnChance, max number of units to spawn in total */
    var maxSpawnLimit: String? = null,
    // Example: spawnUnits: hovertank(gridAlign: true)
    /** Align spawn location to grid, useful for buildings */
    var gridAlign: String? = null,
    // Example: spawnUnits: crates*10(skipIfOverlapping=true, offsetRandomX=40, offsetRandomY=40, gridAlign=true)
    /**
     * Don't spawn this unit if spawn in an invalid location. Eg on units or over water when LAND based
     */
    var skipIfOverlapping: Boolean? = null,
    // Example: spawnUnits: jet(offsetX=20), jet(offsetX=-20)
    /** Sets horizontal position relative to source */
    var offsetX: Float? = null,
    // Example: spawnUnits: scout(offsetY=40), scout(offsetY=-40)
    /** Sets vertical position relative to source */
    var offsetY: Float? = null,
    // Example: spawnUnits: bike(offsetDir=-45)
    /** Set the facing direction of spawned units */
    var offsetDir: Float? = null,
    // Example: spawnUnits: drone(offsetHeight=20)
    /** Sets the height for spawned units */
    var offsetHeight: Float? = null,
    // Example: spawnUnits: k9*5(offsetRandomX=45)
    /** Sets random horizontal position relative to source */
    var offsetRandomX: Float? = null,
    // Example: spawnUnits: scanners*10(offsetRandomY=60)
    /** Sets random vertical position relative to source */
    var offsetRandomY: Float? = null,
    // Example: spawnUnits: chickens*20(offsetRandomDir=360)
    /** Set random facing direction of spawned units */
    var offsetRandomDir: Float? = null,
    // Example: spawnUnits: crates(addResources=gold:30|stone:10, spawnChance=0.5)
    /** Give spawn unit those resources, can be used to set flags that trigger actions */
    var addResources: ResourceBundle? = null,
    // Example: spawnUnits: transporter(transportedUnitsToTransfer=5)
    /**
     * Puts the designated amount of transported units into the transport of the spawned unit.
     */
    var transportedUnitsToTransfer: Int? = null,
    // Example: spawnUnits: tank(copyWaypointsFrom=self)
    /** Copies all waypoints on target to created units. */
    var copyWaypointsFrom: String? = null,
    // Example: spawnUnits: builder(alwayStartDirAtZero=true)
    /** Sets the direction of spawned unit to 0 degrees. */
    var alwayStartDirAtZero: Boolean? = null,
    // Example: spawnUnits: damagingBorder(techLevel=100), zoneMarker(techLevel=40)
    /**
     * Usually used in BR maps, controls the size of safe zone markers and damaging borders. 1 techLevel = 10x10 tiles = 200 pixels in diameter
     */
    var techLevel: Int? = null,

    // ===== Most projectile spawning keys used for projectile ref =====
    // Example: spawnProjectileOnEndOfLife: flamingBits*4(spawnChance=0.35, recursionLimit=2)
    /**
     * Prevents loops, useful with spawning itself so it doesn't infinitely spawn, good for chain exploding. (Recommended no more than 4 if spawning more than 3 projectiles)
     */
    var recursionLimit: Int? = null,
    // Example: spawnProjectileOnCreate: homingEnergy(xOffsetRelative=5, yOffsetRelative=10)
    /**
     * Similar to offsetX, but the offset is relative to the position of the projectile
     */
    var xOffsetRelative: Float? = null,
    // Example: spawnProjectileOnCreate: homingEnergy(xOffsetRelative=5, yOffsetRelative=10)
    /** Similar to xOffsetRelative, but for Y axis */
    var yOffsetRelative: Float? = null,
    // Example: spawnProjectileOnEndOfLife: shrapnels*20(spawnChance=0.2, offsetRandomXY=100)
    /**
     * The offset in both directions to randomly spawn, makes truly random spawning within an area
     */
    var offsetRandomXY: Float? = null,

    // ===== Unit references =====
    /*
     * Example:
     * fireTurretXAtGround: mainGun (thisActionTarget==Marker with ground location)
     * alsoTriggerAction: x (thisActionTarget==Same as original action)
     * [turret]onShoot_triggerActions: x  (thisActionTarget==Target that was shot at)
     * takeResources_triggerActionIfAnyCollected: x (thisActionTarget==Target with resources)
     * addWaypoint_triggerActionIfMatched: x (thisActionTarget == Marker for move/Target for attack, etc. Note: use addWaypoint_maxTime:0 if you want to search only)
     */
    /** Current target or location targeted. */
    var thisActionTarget: String? = null,
    // Example: alsoTriggerActionRepeat: thisActionIndex
    /** Used with alsoTriggerActionRepeat and takeResources_triggerActionForEach */
    var thisActionIndex: Int? = null,
    /*
     * Example:
     * autoTriggerOnEvent: tookDamage (eventSource==Unit that caused damage)
     * autoTriggerOnEvent: killedAnyUnit (eventSource==Unit that was killed)
     * autoTriggerOnEvent: transportingNewUnit (eventSource==Unit that was transported)
     * autoTriggerOnEvent: transportUnloadedOrRemovedUnit (eventSource==Unit unloaded)
     * autoTriggerOnEvent: queuedUnitFinished (eventSource==New unit made)
     * autoTriggerOnEvent: touchTargetSuccess (eventSource==Target touched) 
     */
    /** Current trigger from an autoTriggerOnEvent, otherwise null */
    var eventSource: String? = null,
    // Example: setCustomTarget2: self.attachment(withTag='x').lastDamagedBy.getAsMarker()
    /** Returns unit attachment as reference (parameters: [slot], [withTag]) */
    var attachment: String? = null,
    // Example: autoTrigger: if self.transporting(slot=0).hasResources(gold=100)
    /** Returns unit passenger as reference (parameters: [slot]) */
    var transporting: String? = null,
    // Example: isLocked: if attacking.tags(includes='bug') and attacking.hp < 20
    /** Current target this is attacking, might not be the current waypoint target. */
    var attacking: String? = null,
    // Example: teleportTo: lastDamagedBy
    /** Last unit that attacked this. */
    var lastDamagedBy: String? = null,
    // Example: autoTrigger: if parent.energy > 100
    /**
     * The transporter or attachment parent. (Note: units are suspended state when transported without attachment slot)
     */
    var parent: String? = null,
    // Example: isActive: if distanceBetween(self, activeWaypointTarget) < 100
    /**
     * Current active waypoint target. Includes attacking, transporting, repairing, etc.
     */
    var activeWaypointTarget: String? = null,
    // Example: isLocked: if parent.customTarget1 == self
    /** Custom memory, defaults to the unit that created this unit. */
    var customTarget1: String? = null,
    // Example: fireTurretXAtGround_withPosition: customTarget2
    /** Custom memory, defaults to null */
    var customTarget2: String? = null,
    // Example: setCustomTarget1: nearestUnit(withTag="derrick")
    /**
     * (withinRange=500, withTag='x', relation='any') Search for a unit (not recommended in autoTrigger check for perfomance)
     */
    var nearestUnit: String? = null,
    // Example: self.globalSearchForFirstUnit(withTag='gameController', relation='neutral')
    /**
     * (withTag=x, relation) - Returns first (and oldest) unit found matching the filter. Slow, avoid using in autoTrigger checks
     */
    var globalSearchForFirstUnit: String? = null,
    // Example: isLockedAlt2: if self.parent == nullUnit
    /** returns a null unit reference, useful for comparisons */
    var nullUnit: String? = null,

    // ===== Marker functions =====
    // Example: lastDamagedBy.getAsMarker()
    /**
     * creates a temporary marker at the position a unit is right now. Markers are very fast to create and automatically removed when no longer needed. Is not linked to any unit and still exists when the unit dies, and stays the same when source moves.
     */
    var getAsMarker: String? = null,
    // Example: addWaypoint_target_fromReference: unitref getOffsetAbsolute(self.x, self.y+1000, self.z)
    /**
     * ([x],[y],[height]) Returns marker with absolute offset (-y is north, +x is east)
     */
    var getOffsetAbsolute: String? = null,
    // Example: self.getOffsetRelative(y=100).nearestUnit(withinRange=70, withTag='mouse') != null
    /**
     * ([x],[y],[height],[dirOffset]) Returns marker with relative offset. (y+ is forwards)
     */
    var getOffsetRelative: String? = null,

    // ===== Global functions =====
    // Example: self.readUnitMemory('ammoType', type='string'), parent.readUnitMemory(''attachmentArray", type="unit[ ]", index=5), attacking.readUnitMemory("wishlist", type="string[ ]")[12]
    /** Reads memory from the unit reference  (e.g. self, parent) */
    var readUnitMemory: String? = null
)
