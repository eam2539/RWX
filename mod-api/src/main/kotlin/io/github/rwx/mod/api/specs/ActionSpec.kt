package io.github.rwx.mod.api.specs


/**
 * Properties for [action_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class ActionSpec(
    // Example: text: Catch Fish, text: Fire: %{self.resource.ammo}
    /** Labels the action. Supports dynamic text */
    var text: String? = null,
    // Example: text: [  textPostFix: ]  textAddUnitName: unitRef self.attachment(slot="${slotId}")
    /** Text shown as suffix, useful with textAddUnitName to create text UI */
    var textPostFix: String? = null,
    // Example: text_es: Fuego %{self.resource.ammo}
    // Template: text_{LANG} - Use ISO 639-1 language codes as keys (en, es, fr...)
    /**
     * Alternative text for different language. Use ISO 639-1 Language code on the {LANG} prefix. Supports dynamic text
     */
    var textLang: Map<String, String>? = null,
    // Example: description: Fires shot on target area
    /**
     * A display text when you select your unit's action, used to explain it's purpose. Supports dynamic text
     */
    var description: String? = null,
    // Example: description_es: Disparos en el área objetivo
    // Template: description_{LANG} - Use ISO 639-1 language codes as keys (en, es, fr...)
    /**
     * Alternative description for different language. Use ISO 639-1 Language code on the {LANG} prefix. Supports dynamic text
     */
    var descriptionLang: Map<String, String>? = null,
    // Example: displayType: upgrade
    /**
     * Affects how the action button and text is displayed. Options: none, rally, upgrade, queueUnit, building, action, infoOnly, infoOnlyNoBox, infoOnlyStockpile
     */
    var displayType: List<String>? = null,
    // Example: displayRemainingStockpile: true
    /**
     * Queue is shown as number of times action can be triggered based on price. Use dynamic text on text as alternative.
     */
    var displayRemainingStockpile: Boolean? = null,
    // Example: pos: 1
    /** Order action appears in UI. Merges with positions from canBuild buttons */
    var pos: Float? = null,
    // Example: iconImage: fireShot.png
    /** Sets a thumbnail image for the action button */
    var iconImage: String? = null,
    // Example: iconExtraImage: fireShotNo.png
    /** Drawn over top of icon image. Useful for upgrade icons, etc */
    var iconExtraImage: String? = null,
    // Example: iconExtraColor: #ff0000
    /** Defaults to #64FFFFFF */
    var iconExtraColor: Color? = null,
    // Example: iconExtraIsVisible: if self.resource.ammo < 1
    /** When the condition is met, the extras for icon is visible. */
    var iconExtraIsVisible: LogicBoolean? = null,
    // Example: eg: unitShownInUI: unitRef self.transporting(slot=0)  or  unitShownInUI: heavyTank
    /** Display this unit. (as if this action built this unit) */
    var unitShownInUI: String? = null,
    // Example: guiBuildUnit: placeholderUnit
    /**
     * Uses the UI similar when building structures. An alternative to fireTurretX on some cases
     */
    var guiBuildUnit: String? = null,
    // Example: setBuilt: 0.5
    /**
     * Designates how built the unit is from a percentage of 0-100% with a number between 0 and 1.
     */
    var setBuilt: Float? = null,
    // Example: tags: actionFire
    /**
     * To be used with the withTag parameter for self.queueSize(withTag=x) and queueItemAdded and queueItemCanceled events
     */
    var tags: List<String>? = null,
    // Example: id: fireShot
    /**
     * Allow same/equivalent actions to be more easily connected when converting between units, to preserve queues, calldowns, etc. (Normally action order is used but can be unstable.)
     */
    var id: String? = null,

    // ===== Unit Reference - Dynamically parts from already existing units, useful w/ isAlsoViewableByEnemies =====
    // Example:  eg: textAddUnitName: unitRef self.attachment(slot="1")
    /** Add this unit's name to this action's text */
    var textAddUnitName: String? = null,
    // Example: descriptionAddFromUnit: builder
    /** Add this unit's description to this action's description */
    var descriptionAddFromUnit: String? = null,
    // Example: descriptionAddUnitStats: unitRef self.parent()
    /** Add this unit's stats (eg HP, energy, resources) to this action's description */
    var descriptionAddUnitStats: String? = null,
    // Example: unitShownInUIWithHpBar: true
    /** default true, Only used when unitShownInUI is a unitRef */
    var unitShownInUIWithHpBar: Boolean? = null,
    // Example: unitShownInUIWithProgressBar: false
    /**
     * default true, Only used when unitShownInUI is a unitRef. Replaces HP bar if active
     */
    var unitShownInUIWithProgressBar: Boolean? = null,

    // ===== Requirements for player/AI to use in UI =====
    // Example: alwaysSinglePress: true
    /**
     * Defaults false. When true no confirmation needed on mobile, when used with canPlayerCancel:false and allowMultipleInQueue:false will also hide the queue interface.
     */
    var alwaysSinglePress: Boolean? = null,
    // Example: price: credits=5, energy=5, hp=100, shield=5, ammo=1
    /**
     * The price of your action for the unit. Disables action if not available. Defaults to credits if unlabelled
     */
    var price: ResourceBundle? = null,
    // Example: isActive: true
    /** Defaults true. If false then action is disabled and shown in red in UI. */
    var isActive: LogicBoolean? = null,
    // Example: isVisible: true
    /** Defaults true. If false action is hidden from UI and disabled. */
    var isVisible: LogicBoolean? = null,
    // Example: isLocked: if self.resource.ammo < 1
    /**
     * Defaults false. If true action is disabled, and a lock icon is shown. Mostly used for no nuke game modes
     */
    var isLocked: LogicBoolean? = null,
    // Example: isLockedMessage: Not enough ammunition
    /** Shows the message when the isLocked's condition is met */
    var isLockedMessage: String? = null,
    // Example: isLockedAlt: if numberOfUnitsInGame(withTag="factory") < 2
    /**
     * Another reason for this to be locked. Can just use OR on isLocked, but this allows a different message to be shown
     */
    var isLockedAlt: LogicBoolean? = null,
    // Example: isLockedAltMessage: Not enough factories!
    /** Message for isLockedAlt */
    var isLockedAltMessage: String? = null,
    // Example: isLockedAlt2: if nearestUnit(withTag="explosive", withinRange="500", relation="own").hp() < 10
    /** Second isLocked alternative */
    var isLockedAlt2: LogicBoolean? = null,
    // Example: isLockedAlt2Message: Explosive stockpile is heavily damaged.
    /** Message for isLockedAlt2 */
    var isLockedAlt2Message: String? = null,
    // Example: allowMultipleInQueue: true
    /**
     * When false makes it so only one action can be queued of this type (useful for keeping actions with conditions from being spammed)
     */
    var allowMultipleInQueue: Boolean? = null,
    // Example: onlyOneUnitAtATime: true
    /**
     * When action is picked in UI, only one unit selected with get this action. Defaults to false.
     */
    var onlyOneUnitAtATime: Boolean? = null,
    // Example: isGuiBlinking: true
    /**
     * Flashes in UI to draw attention to it. Might be annoying if used often, recommended only for temporarily states/messages
     */
    var isGuiBlinking: LogicBoolean? = null,
    // Example: isAlsoViewableByAllies: true
    /**
     * Allows ally players to see actions from this unit, useful for showing stats to other players (eg missile count, items collected)
     */
    var isAlsoViewableByAllies: Boolean? = null,
    // Example: isAlsoViewableByEnemies: true
    /**
     * Allows enemy players to see actions from this unit, useful for showing stats to other players (eg missile count, items collected)
     */
    var isAlsoViewableByEnemies: Boolean? = null,

    // ===== AI - How the AI uses this action =====
    // Example: ai_isHighPriority: true
    /**
     * Use this for faction selection actions or other high priority actions such as building high priority units
     */
    var aiIsHighPriority: LogicBoolean? = null,
    // Example: ai_isDisabled: false
    /**
     * Defaults false. Stop AI using this action. (Note when ai_isHighPriority is true this might be ignored)
     */
    var aiIsDisabled: LogicBoolean? = null,
    // Example: ai_considerSameAsBuilding: true
    /** Be careful with */
    var aiConsiderSameAsBuilding: Boolean? = null,

    // ===== Triggers - These skip the queue and do not use price, ignores isLocked, buildTime, etc =====
    // Example: autoTriggerOnEvent: queueItemCancelled(withActionTag="actionFire")
    /**
     * Action will be triggered when an event is happening on a particular unit. Options: created, completeAndActive, destroyed, killedAnyUnit, queuedUnitFinished, queueItemAdded(withActionTag="#"), queueItemCancelled(withActionTag="#"), teleported, touchTargetSuccess, newWaypointGivenByPlayer, teamChanged, transportingNewUnit, transportUnloadedOrRemovedUnit, tookDamage(withTag="#"), newMessage(withTag="#"), enteredTransport, leftTransport, attachmentRemoved. withTag parameter for tookDamage uses tag from projectile and it is optional. withTag parameter for newMessage uses message tags.
     */
    var autoTriggerOnEvent: String? = null,
    // Example: autoTriggerOnEventRecursionLimit: 4
    /**
     * Defaults to 1. Prevents loops, useful with triggering itself so it doesn't infinitely triggers, good for repeating action effect on event
     */
    var autoTriggerOnEventRecursionLimit: Int? = null,
    // Example: autoTrigger: if self.overWater(), autoTrigger: if self.customTimer(laterThanSeconds=5)
    /**
     * When true triggers the effects of this action instantly (ignoring price, isActive, isVisible, buildSpeed, etc)
     */
    var autoTrigger: LogicBoolean? = null,
    // Example: autoTriggerCheckRate:every8Frames
    /**
     * options: everyFrame (default), every4Frames, every8Frames. This overrides autoTriggerCheckRate set on [core] Note: all triggers regardless of check rate are checked when first created and after an auto trigger cooldown. Note: Adding [core]autoTriggerCheckRate:every8Frames to all-units.template could have a large performance boost for mods with complex autoTriggers.
     */
    var autoTriggerCheckRate: String? = null,

    // ===== While action is queued =====
    // Example: buildSpeed: 5s
    /** Sets how fast the action has to be queued before doing the action behaviors */
    var buildSpeed: Duration? = null,
    // Example: highPriorityQueue: true
    /**
     * Defaults to false. If true this action skips all other low priority actions in queue. Useful for fireTurret actions.
     */
    var highPriorityQueue: Boolean? = null,
    // Example: canPlayerCancel: false
    /** Defaults to true. When false, players cannot cancel this particular action. */
    var canPlayerCancel: Boolean? = null,
    // Example: whenBuilding_cannotMove: true
    /**
     * Stops unit moving while action is being applied. Useful for deploy like actions.
     */
    var whenBuildingCannotMove: Boolean? = null,
    // Example: whenBuilding_playAnimation: firePrepare
    /** Plays a specified animation while the action is queued. */
    var whenBuildingPlayAnimation: String? = null,
    // Example: whenBuilding_rotateTo: 45
    /** Rotate unit body to this direction when action is in active queue. */
    var whenBuildingRotateTo: Float? = null,
    // Example: whenBuilding_rotateTo_orBackwards: true
    /**
     * If true allow rotation in 180 degrees from whenBuilding_rotateTo when this is a smaller angle
     */
    var whenBuildingRotateToOrBackwards: Boolean? = null,
    // Example: whenBuilding_rotateTo_waitTillRotated: true
    /** Pause action queue till rotation is finished */
    var whenBuildingRotateToWaitTillRotated: Boolean? = null,
    // Example: whenBuilding_temporarilyConvertTo: cannon_uberState
    /**
     * Convert to another unit while action is in active queue. Note: actions from the original unit will be kept
     */
    var whenBuildingTemporarilyConvertTo: String? = null,
    // Example: whenBuilding_temporarilyConvertTo_keepFields: maxHp, maxEnergy, moveSpeed
    /**
     * Don't change these fields when using whenBuilding_temporarilyConvertTo (both to and from)
     */
    var whenBuildingTemporarilyConvertToKeepFields: String? = null,
    // Example: whenBuilding_triggerAction: spawnMinions
    /** While action is queued, another action is triggered. */
    var whenBuildingTriggerAction: String? = null,
    // Example: whenBuilding_rotateTo_aimAtActionTarget: true
    /**
     * While action is queued, the unit is rotated to the target. Often used with fireTurretX actions
     */
    var whenBuildingRotateToAimAtActionTarget: Boolean? = null,
    // Example: whenBuilding_rotateTo_rotateTurretX: cannon
    /**
     * While action is queued, a specified turret is aimed at the target. Often used with fireTurretX actions
     */
    var whenBuildingRotateToRotateTurretX: String? = null,
    // Example: spawnEffectsOnQueue: CUSTOM:steam
    /** Effects to spawn at unit when action is first added to queue */
    var spawnEffectsOnQueue: String? = null,
    // Example: playSoundToPlayerOnQueue: eva_building.ogg
    /** Global sound to play to unit's player only when action is first added to queue */
    var playSoundToPlayerOnQueue: String? = null,

    // ===== Misc outcomes / Results (What happens) (Note: Must be at least one outcome for an action to show) =====
    // Example: requireConditional: if self.resource.mass < 300
    /** Skip all effects of this action if this evaluates to false */
    var requireConditional: LogicBoolean? = null,
    // Example: convertTo: fishLevel2
    /** Convert your unit into another unit. properties are preserved. */
    var convertTo: String? = null,
    // Example: convertTo_keepCurrentTags: true
    /** Keep current and temporarily tags and ignores default tags on convertTo target. */
    var convertToKeepCurrentTags: Boolean? = null,
    // Example: convertTo_keepCurrentFields: armour, maxEnergy, maxHp
    /**
     * Don't change these fields when converting, useful with setUnitStats (Allowed fields: maxHp, maxShield, shieldRegen, maxEnergy, armour, mass, shootDelayMultiplier, moveSpeed, maxAttackRange.)
     */
    var convertToKeepCurrentFields: String? = null,
    // Example: addEnergy: 10
    /**
     * Adds energy to unit. Has no effect unless energyMax is set. (Same as addResources: energy=X)
     */
    var addEnergy: Float? = null,
    // Example: addResources: credits=5, energy=-5, hp=-100, shield=5, ammo=1
    /** Add these resources when action finishes. */
    var addResources: ResourceBundle? = null,
    // Example: addResourcesScaledByAIHandicaps: true
    /**
     * Same as addResources, but increased or decreased depending on AI difficulty level
     */
    var addResourcesScaledByAIHandicaps: String? = null,
    // Example: addResourcesWithLogic: hp = select( self.parent.energy>5, 10, 20 ) 
    /** Like addResources but allows logic to be used for the resource value */
    var addResourcesWithLogic: ResourceBundle? = null,
    // Example: setResourcesWithLogic: hp=self.parent.hp - 10, energy = self.energy / 2
    /**
     * Sets target resources to this value instead of adding. Becareful with global resources.
     */
    var setResourcesWithLogic: ResourceBundle? = null,
    // Example: deleteSelf: true
    /** Remove self with no explosions or sounds */
    var deleteSelf: Boolean? = null,
    // Example: resetCustomTimer: true
    /** Reset timer used with self.customTimer() */
    var resetCustomTimer: LogicBoolean? = null,
    // Example: setBodyRotation: 270, setBodyRotation: directionBetween(self, customTarget1)
    /** Rotates the unit to a particular direction. Supports dynamic values */
    var setBodyRotation: Int? = null,
    // Example: setUnitStats: maxShield += 20, moveSpeed += 0.1
    /**
     * Allows changing of a select number of fields dynamically without converting. Supports =/+=/-=, with dynamic maths/logic. Changeable fields: maxHp, hp, maxShield, shield, shieldRegen, maxEnergy, energy, armour, mass, shootDelayMultiplier, shootDamageMultiplier, moveSpeed, maxTurnSpeed, maxAttackRange, fogOfWarSightRange, nanoRange, selfRegenRate
     */
    var setUnitStats: String? = null,
    // Example: resetUnitStats: true
    /** Reset changes made by setUnitStats to base values */
    var resetUnitStats: Boolean? = null,
    /*
     * Example:
     * setUnitMemory: """
     *   customText=memory.customText+'hello',
     *   nukeActive=true, 
     *   nextTarget=self.attacking.nearestUnit(withinRange=300, withTag='x', relation='enemy')
     * """
     */
    /**
     * Change this unit's memory, values can be set with logic. Memory must first be defined with defineUnitMemory
     */
    var setUnitMemory: String? = null,
    // @Transient var parsedSetUnitMemory: List<LogicAction.Assignment>? = null,
    // Example: setHeight: parent.height+5
    /** Changes unit height based on this height value */
    var setHeight: LogicBoolean? = null,
    // Example: setCustomTarget1: self.parent
    /**
     * Used for unit linking without requiring unit memories or markers. It is the unit that built this by default.
     */
    var setCustomTarget1: String? = null,
    // Example: setCustomTarget2: lastDamagedBy
    /** Like setCustomTarget1 and with the same use. It is none by default. */
    var setCustomTarget2: String? = null,
    // Example: sendMessageTo: unitref nearestUnit(withinRange=100, withTag="fish").
    /** Sends a message to a targeted unit */
    var sendMessageTo: String? = null,
    // Example: sendMessageWithTags: hitZone
    /**
     * Useful for message detection in an autoTriggerOnEvent event, eg autoTriggerOnEvent:newMessage(withTag=xyz)
     */
    var sendMessageWithTags: String? = null,
    // Example: sendMessageWithData: fish="nice!", cat=self.activeWaypointTarget, amount=4, xyz=memory.something
    /**
     * The data that will be sent to the targeted unit. Allows multiple key-value pairs with any dynamic data type, use eventData() to read this data in the event
     */
    var sendMessageWithData: String? = null,
    // Example: refundAllQueuedItems: true
    /**
     * Refunds the spent price in the queue of a specific action; Includes set flags in price.
     */
    var refundAllQueuedItems: Boolean? = null,
    // Example: removeAllQueuedItemsWithoutRefund: true
    /** Clears the queue without refunding */
    var removeAllQueuedItemsWithoutRefund: Boolean? = null,

    // ===== Outcome - Chaining Actions =====
    // Example: alsoTriggerAction: addCredits, playSound
    /** Trigger to results of another action as well. Ignores action's requirements. */
    var alsoTriggerAction: String? = null,
    // Example: alsoQueueAction: spawnMinions
    /** Adds another action into the normal unit's queue. Ignores action's requirements */
    var alsoQueueAction: String? = null,
    // Example: alsoTriggerOrQueueActionConditional: false
    /**
     * Defaults true. alsoTriggerAction and alsoQueueAction are ignored if this works out to be false.
     */
    var alsoTriggerOrQueueActionConditional: LogicBoolean? = null,
    // Example: alsoTriggerOrQueueActionWithTarget: lastDamagedBy
    /**
     * Changes the target of the triggered action, normally defaults to the current action target. Effects things like fireTurretXAtGround, spawnUnits, thisActionTarget(), etc
     */
    var alsoTriggerOrQueueActionWithTarget: String? = null,
    // Example: alsoTriggerActionRepeat: 5
    /**
     * Repeats the alsoTriggerAction call, thisActionIndex changed on each repeat - Useful to create loops or work with arrays
     */
    var alsoTriggerActionRepeat: LogicBoolean? = null,

    // ===== Outcome - Sounds =====
    // Example: playSoundAtUnit: engineStart.ogg
    /** Local sound to play when action finishes */
    var playSoundAtUnit: String? = null,
    // Example: playSoundGlobally: hornWarn.ogg
    /** Global sound to play to all players in game */
    var playSoundGlobally: String? = null,
    // Example: playSoundToPlayer: confirm.wav
    /** Global sound to play to unit's player only */
    var playSoundToPlayer: String? = null,

    // ===== Outcome - Fire projectile from turret =====
    // Example: fireTurretXAtGround: nukeSilo
    /**
     * When action finishes fire target turret at point on ground, bypasses canShoot rules in turret.
     */
    var fireTurretXAtGround: String? = null,
    // Example: fireTurretXAtGround_withOffset: 0,0
    /**
     * If not set player targets the ground with GUI, if a point is set this step is skipped
     */
    var fireTurretXAtGroundWithOffset: String? = null,
    // Example: fireTurretXAtGround_withProjectile: nuke
    /** Used with fireTurretXAtGround. Defaults to target turret's normal projectile. */
    var fireTurretXAtGroundWithProjectile: String? = null,
    // Example: fireTurretXAtGround_withTarget: lastDamagedBy
    /** Fires a turret aimed at the location of the indicated unit or marker */
    var fireTurretXAtGroundWithTarget: String? = null,
    // Example: fireTurretXAtGround_count: 3
    /** Number of projectiles to fire. Defaults to 1 */
    var fireTurretXAtGroundCount: String? = null,
    // Example: fireTurretXAtGround_onlyOverPassableTileOf: HOVER
    /**
     * Only allow tiles crossable by this movement type to be selected (e.g., LAND,BUILDING,WATER,HOVER)
     */
    var fireTurretXAtGroundOnlyOverPassableTileOf: String? = null,
    // Example: fireTurretXAtGround_showGuideDecals: strikeZone300
    /**
     * Draws decals at the target location. Recommend setting up decals with layer: inactive
     */
    var fireTurretXAtGroundShowGuideDecals: String? = null,

    // ===== Outcome - Spawning =====
    // Example: eg: spawnUnits: heavyTank, tank*5, hoverTank(offsetX=10)
    /**
     * Spawn units at action's target. See 'Spawn units line' section in this doc for details.
     */
    var spawnUnits: String? = null,
    // Example: produceUnits: builder*4, plasmaTank*8
    /**
     * Like spawnUnits but unit exits as if it was produced normally, and gets a move away waypoint
     */
    var produceUnits: String? = null,
    // Example: spawnEffects: CUSTOM:puff
    /** Effects to spawn at unit */
    var spawnEffects: String? = null,
    /** Effects to spawn at the action target */
    var spawnEffectsAtActionTarget: String? = null,

    // ===== Outcome - Position =====
    // Example: offsetSelfAbsolute: 0, 0, 40 
    /** Offsets unit position absolutely by this point. Format: [x,y,height] */
    var offsetSelfAbsolute: String? = null,
    // Example: teleportTo: memory.lastLocation
    /**
     * Changes unit position to this position. Great alternative to fireTurretX with teleporting projectile
     */
    var teleportTo: String? = null,
    /** Teleports the unit to the selected action target */
    var teleportToActionTarget: Boolean? = null,

    // ===== Outcome - Transport Changes =====
    // Example: addUnitsIntoTransport: tank*3, heavyTank(neutralTeam=true)
    /**
     * Creates and add units into transport, use self.transportingCount() to check for space before adding
     */
    var addUnitsIntoTransport: String? = null,
    /**
     * {Currently broken in 1.15 - don't use} Instantly tries to transports existing units on the map into this transport. Might fail if rules don't allow this unit to be transported.
     */
    var transportTargetNow: String? = null,
    // Example: deleteNumUnitsFromTransport: 2
    /** Removes a specified amount of cargo units */
    var deleteNumUnitsFromTransport: Int? = null,
    // Example: deleteNumUnitsFromTransport_onlyWithTags: cheapStuff
    /** Removes a specified amount of cargo units, but only those with specified tags */
    var deleteNumUnitsFromTransportOnlyWithTags: List<String>? = null,
    // Example: startUnloadingTransport: true
    /** Unloads all cargo units normally */
    var startUnloadingTransport: Boolean? = null,
    // Example: forceUnloadTransportNow: true
    /**
     * For unload all units, or slot targeted by forceUnloadTransportNow_onlyOnSlot. Unloads even if no space or overwater, etc
     */
    var forceUnloadTransportNow: Boolean? = null,
    // Example: forceUnloadTransportNow_onlyOnSlot: drop1
    /** Focuses the force unload to a specific slot */
    var forceUnloadTransportNowOnlyOnSlot: Int? = null,

    // ===== Outcome - Waypoint Changes =====
    // Example: clearAllWaypoints: true
    /**
     * Clears all waypoints, be careful not to annoy players by removing their orders, prepending waypoints is often better
     */
    var clearAllWaypoints: Boolean? = null,
    // Example: clearActiveWaypoint: true
    /** Clears only the current waypoint */
    var clearActiveWaypoint: Boolean? = null,
    // Example: addWaypoint_type: move
    /**
     * Adds a waypoint with a specific purpose. Options: move, attackMove, guard, loadInto, loadUp, attack, reclaim, repair, touchTarget, build, follow, setPassiveTarget
     */
    var addWaypointType: String? = null,
    // Example: addWaypoint_unitType: turret
    /** Only for use with addWaypoint_type:build */
    var addWaypointUnitType: String? = null,
    // Example: addWaypoint_prepend: false
    /** Add to the start of the waypoint queue or the end */
    var addWaypointPrepend: Boolean? = null,
    // Example: addWaypoint_triggerActionIfFailed: retreat
    /**
     * If target_nearestUnit fails to find a match so waypoint cannot be added then trigger this action
     */
    var addWaypointTriggerActionIfFailed: String? = null,
    // Example: addWaypoint_triggerActionIfMatched: VIP_target
    /** Triggers an action if a waypoint is legal */
    var addWaypointTriggerActionIfMatched: String? = null,
    // Example: addWaypoint_maxTime: 20s
    /**
     * Automatically remove this waypoint if it has been active for longer than this time.
     */
    var addWaypointMaxTime: Float? = null,
    // Example: addWaypoint_target_nearestUnit_tagged: assault, mechanized
    /** Puts the waypoint to a nearest unit with specific tags */
    var addWaypointTargetNearestUnitTagged: List<String>? = null,
    // Example: addWaypoint_target_nearestUnit_team: ally
    /**
     * Puts the waypoint to a nearest unit with a specific relation to the player's unit. Options: own, neutral, allyNotOwn, ally, enemy, any, notOwn
     */
    var addWaypointTargetNearestUnitTeam: String? = null,
    // Example: addWaypoint_target_nearestUnit_maxRange: 2000
    /** Puts the waypoint to a nearest unit within the maximum range specified. */
    var addWaypointTargetNearestUnitMaxRange: Float? = null,
    // Example: addWaypoint_target_mapMustBeReachable: true
    /**
     * Puts the waypoint only if the waypoint target is reachable by the unit (e.g., if a unit cannot cross two islands, the waypoint is not placed.)
     */
    var addWaypointTargetMapMustBeReachable: Boolean? = null,
    // Example: addWaypoint_target_fromReference: self.memory.lastDock
    /** Puts the waypoint to a marker or a reference unit */
    var addWaypointTargetFromReference: String? = null,
    // Example: addWaypoint_position_offsetFromSelf:
    /** Puts the waypoint to an absolute coordinate */
    var addWaypointPositionOffsetFromSelf: String? = null,
    // Example: addWaypoint_position_fromAction: fireShot
    /** Puts the waypoint taken from an action that triggered this action */
    var addWaypointPositionFromAction: Boolean? = null,
    // Example: addWaypoint_position_randomOffsetFromSelf: 
    /** Puts the waypoint to a random relative coordinate */
    var addWaypointPositionRandomOffsetFromSelf: String? = null,
    // Example: addWaypoint_position_relativeOffsetFromSelf: 10, 20
    /** Puts the waypoint to a relative coordinate */
    var addWaypointPositionRelativeOffsetFromSelf: String? = null,
    // Example: addWaypoint_target_randomUnit_tagged: chargers, strikers
    /** Puts the waypoint to a random unit with specific tags */
    var addWaypointTargetRandomUnitTagged: List<String>? = null,
    // Example: addWaypoint_target_randomUnit_team: any
    /**
     * Puts the waypoint to a random unit with a specific relation to the player's unit. Options: own, neutral, allyNotOwn, ally, enemy, any, notOwn
     */
    var addWaypointTargetRandomUnitTeam: String? = null,
    // Example: addWaypoint_target_randomUnit_maxRange: 1000
    /** Puts the waypoint to a random unit within the maximum range specified. */
    var addWaypointTargetRandomUnitMaxRange: Int? = null,

    // ===== Outcome - Cooldown =====
    // Example: addAllActionCooldownsTime: 4s
    /** Adds cooldown on all actions, including canBuilds */
    var addAllActionCooldownsTime: Float? = null,
    // Example: addActionCooldownTime: 20s
    /** Player cannot use action again for this amount of time */
    var addActionCooldownTime: Duration? = null,
    // Example: addActionCooldownApplyToActions: retreat, transformFins
    /** Sets addActionCooldownTime's target. Defaults to this action. */
    var addActionCooldownApplyToActions: String? = null,
    // Example: clearAllActionCooldowns: true
    /** Removes all existing cooldowns on all buttons */
    var clearAllActionCooldowns: Boolean? = null,

    // ===== Outcome - Animation =====
    // Example: playAnimation: engageThrusters
    /** Plays animation when the action is triggered */
    var playAnimation: String? = null,
    // Example: playAnimationIfNotPlaying: true
    /** Don't restart animation if this animation is already playing */
    var playAnimationIfNotPlaying: Boolean? = null,
    // Example: finishPlayingLastAnimation: true
    /** Finish last animation, including blend out */
    var finishPlayingLastAnimation: Boolean? = null,
    // Example: stopLastAnimation: true
    /** Stop last animation, skipping blend out */
    var stopLastAnimation: Boolean? = null,
    // Example: switchToNeutralTeam: false
    /**
     * Change team to neutral. This team is allied to all other teams. Will be captured by nearby units unless [core]stayNeutral:true is used
     */
    var switchToNeutralTeam: Boolean? = null,
    // Example: switchToAggressiveTeam: true
    /**
     * Change to a built-in team that is aggressive to all other teams. Does not get captured.
     */
    var switchToAggressiveTeam: Boolean? = null,
    // Example: switchToTeam: 2, switchToTeam: lastDamagedBy
    /**
     * Team id to switch to. Starts at 0. (but -1 for a neutral team, -2 for aggressive Team). Also supports dynamic values
     */
    var switchToTeam: LogicBoolean? = null,

    // ===== Outcome - Take Resources from other units =====
    // Example: takeResources: hp=5, gold=10
    /**
     * Resources to take (required to use take resources). And at-least 1 include key is needed.
     */
    var takeResources: ResourceBundle? = null,
    // Example: takeResources_includeUnitsInTransport: true
    /** Includes cargo units in taking resources */
    var takeResourcesIncludeUnitsInTransport: Boolean? = null,
    // Example: takeResources_includeParent: true
    /** Include attachment parent or transport parent */
    var takeResourcesIncludeParent: Boolean? = null,
    // Example: takeResources_includeReference: self.lastDamagedBy
    /** Incudes referenced units in taking resources */
    var takeResourcesIncludeReference: String? = null,
    // Example: takeResources_includeUnitsWithinRange: 300
    /** Includes all units within the specified range */
    var takeResourcesIncludeUnitsWithinRange: Float? = null,
    // Example: takeResources_includeUnitsWithinRange_team: own
    /**
     * Used with includeUnitsWithinRange, defaults to own. Can be:  own|ally|allyNotOwn|enemy|neutral|any
     */
    var takeResourcesIncludeUnitsWithinRangeTeam: String? = null,
    // Example: takeResources_excludeUnitsWithoutTags: truck, tanker
    /** Includes all units without the specified tags in taking resources */
    var takeResourcesExcludeUnitsWithoutTags: List<String>? = null,
    // Example: takeResources_excludeUnitsWithTheseResources: oil, juice, sauce
    /** Excludes all units with custom prices in them */
    var takeResourcesExcludeUnitsWithTheseResources: ResourceBundle? = null,
    // Example: takeResources_excludeUnitsWithoutAllResources: true
    /** Defaults to true. */
    var takeResourcesExcludeUnitsWithoutAllResources: Boolean? = null,
    // Example: takeResources_triggerActionIfAnyCollected: purify
    /**
     * Triggers an action if any amount of resources is collected in the current collection action
     */
    var takeResourcesTriggerActionIfAnyCollected: String? = null,
    // Example: takeResources_triggerActionIfNoneCollected: callOtherTanker
    /**
     * Triggers an action if no amount of resources is collected in the current collection action
     */
    var takeResourcesTriggerActionIfNoneCollected: String? = null,
    // Example: takeResources_triggerActionForEach: manufacture
    /**
     * Calls this action for each unit found by takeResource with the unit as thisActionTarget, and thisActionIndex counting up from zero.
     */
    var takeResourcesTriggerActionForEach: String? = null,
    // Example: takeResources_discardCollected: false
    /** Just take resources from targets, don't add(or remove) to self */
    var takeResourcesDiscardCollected: Boolean? = null,
    // Example: takeResources_keepResourcesOnTarget: true
    /**
     * Don't add/remove resource from target. This clones resources. Use with takeResources_discardCollected and takeResources_triggerActionIfAnyCollected to make a resource detector.
     */
    var takeResourcesKeepResourcesOnTarget: Boolean? = null,
    // Example: takeResources_maxUnits: 10
    /** Defaults to 1. Takes resources from specified amount of units */
    var takeResourcesMaxUnits: Int? = null,
    // Example: takeResources_searchOnly: true
    /**
     * Shortcut for maxUnits: 200, discardCollected: true, keepResourcesOnTarget: true to detect resources only
     */
    var takeResourcesSearchOnly: Boolean? = null,
    // Example: takeResources_directTransferStoppingAtZero: true
    /**
     * If less resources on target than transfer amount, only remaining resources will be transfered. Doesn't support use with some other takeResources_* keys
     */
    var takeResourcesDirectTransferStoppingAtZero: Boolean? = null,

    // ===== Outcome - Convert Resources =====
    // Example: convertResource_from: juice
    /** Name of custom resource to take from */
    var convertResourceFrom: ResourceBundle? = null,
    // Example: convertResource_to: sauce
    /** Name of custom resource to give to */
    var convertResourceTo: ResourceBundle? = null,
    // Example: convertResource_minAmount: 10
    /**
     * Skip if less than this amount in 'from'. Defaults to 0. Likely not needed for most use cases
     */
    var convertResourceMinAmount: Float? = null,
    // Example: convertResource_maxAmount: 100
    /** Max amount to transfer between 'from' and 'to' */
    var convertResourceMaxAmount: Float? = null,
    // Example: convertResource_multiplyAmountBy: 1.5
    /**
     * Defaults to 1. Amount to multiply when adding on 'to' (does not effect amount taken on 'from')
     */
    var convertResourceMultiplyAmountBy: Float? = null,

    // ===== Outcome - Set Resources =====
    // Example: resourceAmount: oil
    /**
     * Name of custom resource to set with the below 3 keys. All keys are optional, and can be used together.
     */
    var resourceAmount: ResourceBundle? = null,
    // Example: resourceAmount_setValue: 20
    /**
     * Absolute value to set this resource to, ignores current value of resource. Skipped by default
     */
    var resourceAmountSetValue: Float? = null,
    // Example: resourceAmount_addOtherResource: juice
    /**
     * Name of another custom resource to add to this on. Can be used without resourceAmount_setValue, to just add resources. Or with resourceAmount_setValue:0 to copy a resource value.
     */
    var resourceAmountAddOtherResource: ResourceBundle? = null,
    // Example: resourceAmount_multiplyBy: 2
    /** Defaults to 1. Multiple the current or new value by */
    var resourceAmountMultiplyBy: Float? = null,

    // ===== Outcome - Attachment changes =====
    // Example: attachments_addNewUnits: coreDefense*3
    /** Adds specified units to attachments */
    var attachmentsAddNewUnits: String? = null,
    // Example: attachments_deleteNumUnits: 3
    /** Removes a specified amount of units attached */
    var attachmentsDeleteNumUnits: Int? = null,
    // Example: attachments_onlyOnSlots: drop1, drop2, drop3
    /** Restrict attachments_* actions to these attachments */
    var attachmentsOnlyOnSlots: String? = null,
    // Example: attachments_unload: true
    /**
     * Unload all attachments. Can be used with attachments_onlyOnSlots. Same as unloading transported units
     */
    var attachmentsUnload: Boolean? = null,
    // Example: attachments_disconnect: true
    /**
     * Disconnect all attachments in the place they are right now. Can be used with attachments_onlyOnSlots.
     */
    var attachmentsDisconnect: Boolean? = null,
    // Example: disconnectFromParent: true
    /** Disconnects this unit from parent's attachment slot */
    var disconnectFromParent: Boolean? = null,

    // ===== Outcome - Tag changes =====
    // Example: temporarilyAddTags: emptyJuice, fullSauce
    /**
     * Add tag to this unit until it is converted or reset (unless convertTo_keepCurrentTags is used)
     */
    var temporarilyAddTags: List<String>? = null,
    // Example: temporarilyRemoveTags: fullJuice, emptySauce
    /**
     * Remove tag from this unit until it is converted or reset (unless convertTo_keepCurrentTags is used)
     */
    var temporarilyRemoveTags: List<String>? = null,
    // Example: resetToDefaultTags: true
    /** Reset to standard tags */
    var resetToDefaultTags: Boolean? = null,
    // Example: addGlobalTeamTags: upgrade_energized, upgrade_research2
    /**
     * Add a tag to player's team. Use with self.globalTeamTags() to create unlocks and upgrades. Unique tags are best to not conflict with other mods.
     */
    var addGlobalTeamTags: List<String>? = null,
    // Example: removeGlobalTeamTags: buff_immune
    /** Remove a tag from player's team. */
    var removeGlobalTeamTags: List<String>? = null,

    // ===== Outcome - Show Message =====
    // Example: showMessageToPlayer: There is a hidden enemy near your defenses
    /** Sends a message to the player controlling the unit */
    var showMessageToPlayer: String? = null,
    // Example: showMessageToPlayer_fil: May nakatagong kalaban na malapit sa mga depensa mo
    // Template: showMessageToPlayer_{LANG} - Use ISO 639-1 language codes as keys (en, es, fr...)
    /**
     * Note: This format is support on nearly all strings that show to player even when reference doesn't show it. Use ISO 639-1 Language Code on the {LANG} placeholder
     */
    var showMessageToPlayerLang: Map<String, String>? = null,
    // Example: showMessageToAllPlayers: %{self.playerName} has captured a point
    /** Sends a message to all players */
    var showMessageToAllPlayers: String? = null,
    // Example: showMessageToAllEnemyPlayers: Team %{self.playerName} has %{self.resource.gold}
    /** Sends a message to all enemy players only */
    var showMessageToAllEnemyPlayers: String? = null,
    // Example: showQuickWarLogToPlayer: Unit decloaked
    /**
     * Sends a Quick War Log message to the player controlling the unit (in the lower lef)
     */
    var showQuickWarLogToPlayer: String? = null,
    // Example: showQuickWarLogToAllPlayers: 500 oil sold to allied market
    /** Sends a Quick War Log message to all players (in the lower lef) */
    var showQuickWarLogToAllPlayers: String? = null,
    // Example: debugMessage: [action log] unit launch on %{thisActionTarget.x}, %{thisActionTarget.y}
    /** Only shows in Sandbox with Debug mode on. */
    var debugMessage: String? = null
)
