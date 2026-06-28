package io.github.rwx.mod.api.specs


/**
 * Properties for [core] section
 *
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class CoreSpec(
    // ===== Necessary Code: will cause error if these are not included =====
    // Example: name: customTank1
    /**
     * Defines the unit raw name, game uses it to identify as a unique name. (This is not displayed in-game)
     */
    var name: String = "customUnit",
    // Example: mass: 3000
    /**
     * The 'weight' of the unit, defines how it collides with other units, a greater value means it's tougher to push.
     */
    var mass: Int? = null,
    // Example: radius: 20
    /**
     * Circular area around the unit that makes it selectable. (mouse click/screen touch)
     */
    var radius: Int = 20,
    // Example: price: 500, gold=5, stone=10
    /**
     * The unit cost from builders/buildings. Defaults to credits if no resource type is used
     */
    var price: Int? = null,
    // Example: maxHp: 200
    /** The max health for the unit. (will spawn with this value). */
    var maxHp: Int = 0,

    // ===== Common Keys =====
    // Example: buildSpeed: 3s
    /** Time it takes to build the unit. (may multiply with builder speed) */
    var buildSpeed: String = "5s",
    // Example: class: CustomUnitMetadata
    /** Reserved for future use, must be CustomUnitMetadata by default. */
    var `class`: String? = null,
    // Example: techLevel: 1
    /**
     * Defines the Tech Level of the unit, there're 3 levels and each will appear in a different color in the GUI.
     */
    var techLevel: Int? = null,
    // Example: altNames: custTank1, customTank1, cTank1
    /**
     * Comma separated list of names. Like name but lower priority, useful for multiple optional mods.
     */
    var altNames: List<String>? = null,
    // Example: strictLevel: 1
    /**
     * Defaults to 0. 1 = Errors if keys are duplicated. Add to "all-units.template" in root to apply to all units.
     */
    var strictLevel: Float? = null,
    // Example: isBio: true
    /**
     * Choose whether the unit is bioligical or not, affects sound and splat (unless hideScorchMark:true)
     */
    var isBio: Boolean = false,
    // Example: isBug: false
    /** Changes some death defaults, and sort order in Sandbox. */
    var isBug: Boolean? = null,
    // Example: isBuilder: true
    /** Normally required if this unit places buildings. Defaults to [ai]useAsBuilder. */
    var isBuilder: Boolean? = null,
    // Example: streamingCost: gem=420
    /**
     * Like price but paid for overtime while this unit is being queued or built. Construction or queue is paused if resources run out while building.
     */
    var streamingCost: ResourceBundle? = null,
    // Example: switchPriceWithStreamingCost: true
    /**
     * Shortcut to set streamingCost to price value and clear price, add to all-units.template to quickly switch a mod over to streaming resources.
     */
    var switchPriceWithStreamingCost: Boolean? = null,

    // ===== Unit Stats Keys =====
    // Example: selfRegenRate: 0.01
    /** Passive self repair rate. */
    var selfRegenRate: Float? = null,
    // Example: maxShield: 500
    /**
     * The max shield hitpoints of the unit. Can start with 0 hitpoints if startShieldAtZero:true.
     */
    var maxShield: Int? = null,
    // Example: startShieldAtZero: true
    /** Unit starts with a 0 hitpoints shield on created if true. */
    var startShieldAtZero: Boolean? = null,
    // Example: shieldRegen: 0.15
    /** Passive shield regen rate. */
    var shieldRegen: Float? = null,
    // Example: energyMax: 1
    /**
     * Defaults to 0. Energy that can be used as ammo for turrets, laser defense and actions.
     */
    var energyMax: Float? = null,
    // Example: energyRegen: 0.001
    /** Passive energy regen rate. */
    var energyRegen: Float? = null,
    // Example: energyStartingPercentage: 0.5
    /** Sets the percentage of charged energy when the unit is first built. */
    var energyStartingPercentage: Float? = null,
    // Example: energyNeedsToRechargeToFull: true
    /** Disables weapons using energy after reaching zero till fully recharged if true. */
    var energyNeedsToRechargeToFull: Float? = null,
    /** Regen rate while recharging. */
    var energyRegenWhenRecharging: Float? = null,
    // Example: armour: 6
    /** Damage taken away from each hit. (not currently used in any vanilla units) */
    var armour: Int? = null,
    // Example: armourMinDamageToKeep: 2
    /** Min damage to keep from received damage. Defaults to 1. */
    var armourMinDamageToKeep: Int? = null,
    // Example: borrowResourcesWhileAlive: gold=10
    /** Takes these resources when created and returns them when removed or destroyed. */
    var borrowResourcesWhileAlive: ResourceBundle? = null,
    // Example: borrowResourcesWhileBuilt: supplyCap = -10
    /**
     * Like borrowResourcesWhileAlive but doesn't take affect till built. Mostly useful for buildings like houses that have negative resources to add to the unit cap, etc.
     */
    var borrowResourcesWhileBuilt: ResourceBundle? = null,
    // Example: generation_resources: credits=5, gold=20
    /** Income unit creates. (custom resource version) */
    var generationResources: ResourceBundle? = null,
    // Example: generation_active: if not self.hp(lessThan=100)
    /** Disables generation_resources/credits when false. (logic_boolean) */
    var generationActive: LogicBoolean? = null,
    // Example: generation_credits: 2
    /** Income unit creates. (credits only) */
    var generationCredits: Int? = null,
    // Example: generation_delay: 40
    /**
     * How often generation_resources/credits is added. Defaults to 40. (changing not recommended)
     */
    var generationDelay: Int? = null,

    // ===== UI and Graphics Keys =====
    // Example: showInEditor: false
    /** Set to false to hide unit in Sandbox editor. (Defaults to true) */
    var showInEditor: Boolean? = null,
    // Example: displayText: Custom Tank
    /** The unit name that the game shows to the player. */
    var displayText: String? = null,
    // Example: displayText_es: Tanque Personalizado
    // Template: displayText_{LANG}: - Use ISO 639-1 language codes as keys (en, es, fr...)
    /** LANG = ISO 639-1 Code to show this text instead when game is in this language. */
    var displayTextLang: Map<String, String>? = null,
    // Example: displayDescription: -Fast movement\n-Light damage
    /** Unit description that the game shows to the player. */
    var displayDescription: String? = null,
    // Example: displayDescription_es: -Movimiento rápido\n-Daño ligero
    // Template: displayDescription_{LANG}: - Use ISO 639-1 language codes as keys (en, es, fr...)
    /** LANG = ISO 639-1 Code to show this text instead when game is in this language. */
    var displayDescriptionLang: Map<String, String>? = null,
    // Example: displayLocaleKey: units.mechArtillery
    /** Translation file key for unit name and description. */
    var displayLocaleKey: String? = null,
    // Example: displayRadius: 20
    /**
     * Defaults to radius value. Set to show a larger or smaller selection circle UI on units.
     */
    var displayRadius: Int? = null,
    // Example: uiTargetRadius: 10
    /**
     * Defaults to displayRadius value. Radius used when attacking/reclaiming/etc this unit
     */
    var uiTargetRadius: Int? = null,
    // Example: shieldRenderRadius: 12
    /**
     * Defaults is a little bigger than radius. Set to show a larger or smaller shield circle on units.
     */
    var shieldRenderRadius: Int? = null,
    // Example: shieldDisplayOnlyDeflection: true
    /** Hide shield unless deflecting shot if true. */
    var shieldDisplayOnlyDeflection: Boolean? = null,
    // Example: shieldDeflectionDisplayRate: 3
    /** Defaults to 4. High value causes shield deflection to fade disappear faster. */
    var shieldDeflectionDisplayRate: Float? = null,
    // Example: showOnMinimap: false
    /** Defaults to true. Hide units on minimap if false. */
    var showOnMinimap: Boolean? = null,
    // Example: showActionsWithMixedSelectionIfOtherUnitsHaveTag: true
    /**
     * Shows a merged action list if all units selected includes one of these tags. Useful for converted units.
     */
    var showActionsWithMixedSelectionIfOtherUnitsHaveTag: Boolean? = null,
    // Example: showOnMinimapToEnemies: false
    /** Useful for stealth units */
    var showOnMinimapToEnemies: Boolean? = null,

    // ===== Building Only Keys =====
    // Example: isBuilding: true
    /** Defines if the unit is a building. */
    var isBuilding: Boolean = false,
    // Example: footprint: 0,0,1,1
    /**
     * Left, up, right, down. Tiles taken up which block unit movement. Defaults to 0,0,0,0  = 1 center tile.
     */
    var footprint: String? = null,
    // Example: constructionFootprint: -1,-1,1,3
    /**
     * Tiles taken up for placement of other buildings. Defaults to 0,0,0,0 = 1 center tile.
     */
    var constructionFootprint: String? = null,
    // Example: displayFootprint: 0,0,1,1
    /**
     * Left, up, right, down. Only applies to buildings, just used for GUI. Defaults to footprint.
     */
    var displayFootprint: String? = null,
    // Example: buildingSelectionOffset: 4
    /** Defaults to 0. Adds or removes padding on the drawn selection rect in UI. */
    var buildingSelectionOffset: Int = 0,
    // Example: buildingToFootprintOffsetX: 4
    /** Defaults to 10. Change the building position in the footprint on the X-axis. */
    var buildingToFootprintOffsetX: Float? = 10f,
    // Example: buildingToFootprintOffsetY: 6
    /** Defaults to 10. Change the building position in the footprint on the Y-axis. */
    var buildingToFootprintOffsetY: Float? = 10f,
    // Example: placeOnlyOnResPool: true
    /** Normally used for extractors, forces building construction in a resource pool. */
    var placeOnlyOnResPool: Boolean = false,
    // Example: selfBuildRate: 0.0008
    /** Rate unit builds itself when placed without a builder. */
    var selfBuildRate: Float = 0f,
    // Example: ignoreInUnitCapCalculation: true
    /**
     * defaults to true for buildings otherwise false. Set to true to not count this unit in unit cap.
     */
    var ignoreInUnitCapCalculation: String? = null,

    // ===== Misc Keys =====
    // Example: copyFrom: ROOT:defaultTanks.template, tankT1.ini
    /**
     * Uses unit data from another ini file as default for this unit, supports multiple files.
     */
    var copyFrom: String? = null,
    // Example: dont_load: true
    /**
     * Do not load unit, and don't error on missing data. Can be useful when used with copyFrom.
     */
    var dontLoad: Boolean? = null,
    // Example: overrideAndReplace: builder, combatEngineer
    /**
     * Overrides another unit with this unit. Build links and map positions to target unit will be replaced.
     */
    var overrideAndReplace: List<String>? = null,
    // Example: onNewMapSpawn: spawnPoint_eachActiveTeam
    /**
     * Values: emptyResourcePools_asNeutral, emptyOrOccupiedResourcePools_asNeutral, mapCenter_asNeutral, mapCenter_eachActiveTeam, spawnPoint_eachActiveTeam
     */
    var onNewMapSpawn: String? = null,
    // Example: globalScale: 2
    /** Defaults to 1. Changing not recommended. */
    var globalScale: Float? = null,
    // Example: isLocked: true
    /**
     * Disallow building of this unit. Can be used with overrideAndReplace to restrict units player can build.
     */
    var isLocked: Boolean? = null,
    // Example: isLockedIfGameModeNoNuke: true
    /** Disallows building of this unit if nukes are disabled during match setup. */
    var isLockedIfGameModeNoNuke: Boolean? = null,
    // Example: experimental: true
    /** Tag unit as experimental. Affects zoomed out icon and end game stats. */
    var experimental: Boolean? = null,
    // Example: stayNeutral: false
    /** Set to false to disable capture when unit is on the neutral team. */
    var stayNeutral: Boolean? = null,
    // Example: createNeutral: true
    /** Set to true to always spawn the unit on the neutral team. */
    var createNeutral: Boolean? = null,
    // Example: createOnAggressiveTeam: true
    /**
     * Set to true to always spawn the unit on aggressive teams on single player matches.
     */
    var createOnAggressiveTeam: Boolean? = null,
    // Example: tags: tank, smallTank, piercingDamage
    /**
     * List of comma separated strings. Used to classify units, create special actions and balances.
     */
    var tags: List<String>? = null,
    /*
     * Example:
     * defineUnitMemory: boolean nukeActive, boolean laserReady, float experience, unit nextTarget, unit homeBase, string customText
     * 
     */
    /**
     * Can define several variables for custom storage, unique for each unit. Allowed types: boolean, float/number, unit, string.
     */
    var defineUnitMemory: String? = null,

    //@Transient var parsedVariableMapping: VariableMapping? = null,
    // Example: fogOfWarSightRange: 35
    /** Sets number of tiles this unit can see through the fog of war. Defaults to 15. */
    var fogOfWarSightRange: Int? = null,
    // Example: fogOfWarSightRangeWhileNotBuilt: 10
    /**
     * Fog of War range when unit/building is incomplete. Defaults to fogOfWarSightRange
     */
    var fogOfWarSightRangeWhileNotBuilt: Int? = null,
    // Example: softCollisionOnAll: 3
    /** Creates a soft collision effect when touching other units. */
    var softCollisionOnAll: Int? = null,
    // Example: disableAllUnitCollisions: true
    /** Unit cannot collide with others if true. */
    var disableAllUnitCollisions: Boolean? = null,
    // Example: isUnrepairableUnit: true
    /** No unit can repair this unit if true. */
    var isUnrepairableUnit: Boolean? = null,
    // Example: isUnselectable: true
    /** If true unit cannot be selected. (includes AI players) */
    var isUnselectable: Boolean? = null,
    // Example: isUnselectableAsTarget: false
    /**
     * Defaults to isUnselectable. Can be used to create units that cannot be selected but can be targeted for attack, reclaim, etc
     */
    var isUnselectableAsTarget: Boolean? = null,
    // Example: isPickableStartingUnit: true
    /** If true, unit is added to dropdowns for starting unit in game setup menus. */
    var isPickableStartingUnit: Boolean? = null,
    // Example: startFallingWhenStartingUnit: true
    /** Unit will appear falling from skies when starting unit if true. */
    var startFallingWhenStartingUnit: Boolean? = null,
    // Example: soundOnAttackOrder: tankAttackOrder1.ogg, tankAttackOrder2.ogg 
    /**
     * List of sound names. Only one will be played on each attack order. Only .ogg and .wav formats.
     */
    var soundOnAttackOrder: String? = null,
    // Example: soundOnMoveOrder: tankMoveOrder1.ogg, tankMoveOrder2.ogg
    /**
     * List of sound names. Only one will be played on each move order. Only .ogg and .wav formats.
     */
    var soundOnMoveOrder: String? = null,
    // Example: soundOnNewSelection: tankSelection1.ogg, tankSelection2.ogg
    /**
     * List of sound names. Only one will be played on each unit selection. Only .ogg and .wav formats.
     */
    var soundOnNewSelection: String? = null,
    // Example: canNotBeDirectlyAttacked: true
    /**
     * No unit can directly target this unit. If true this will also skip this unit in victory/defeat checks.
     */
    var canNotBeDirectlyAttacked: Boolean? = null,
    // Example: canNotBeDamaged: true
    /**
     * Defaults to value of canNotBeDirectlyAttacked (be careful setting this without canNotBeDirectlyAttacked, as AI will attack forever)
     */
    var canNotBeDamaged: Boolean? = null,
    // Example: canNotBeGivenOrdersByPlayer: true
    /** If true unit will not take player or AI orders. */
    var canNotBeGivenOrdersByPlayer: Boolean? = null,
    // Example: canOnlyBeAttackedByUnitsWithTags: piercingTank, powerfulTank
    /** List of tag strings, only units with these tags can directly target this unit. */
    var canOnlyBeAttackedByUnitsWithTags: List<String>? = null,
    // Example: disableDeathOnZeroHp: true
    /**
     * Setting to true allows unit to continue living even at 0 HP, useful for custom "death" action. Warning: If not used with an autoTrigger, etc units will attack this unit forever.
     */
    var disableDeathOnZeroHp: Boolean? = null,
    // Example: allowCaptureWhenNeutralByAI: true
    /** When true, it lets to be captured on contact by AI as well. Defaults as false */
    var allowCaptureWhenNeutralByAI: Boolean? = null,

    // ===== Transport Keys =====
    // Example: transportSlotsNeeded: 2
    /**
     * Defaults to 1. Number of slots this unit uses up in a transport, experimentals are often set to 5.
     */
    var transportSlotsNeeded: Int? = null,
    // Example: maxTransportingUnits: 5
    /** Number of slots this units has for transporting other units. */
    var maxTransportingUnits: Int? = null,
    // Example: transportUnitsRequireTag: smallTank, soldier
    /** Only allows trasport of units that have one of these tags. */
    var transportUnitsRequireTag: List<String>? = null,
    // Example: transportUnitsRequireMovementType: AIR, WATER
    /** Only allows trasport of units that have one of these movement types. */
    var transportUnitsRequireMovementType: String? = null,
    // Example: transportUnitsBlockAirAndWaterUnits: false
    /** Defaults to true. This unit can only transport LAND units if true. */
    var transportUnitsBlockAirAndWaterUnits: Boolean? = null,
    // Example: transportUnitsEachUnitAlwaysUsesSingleSlot: true
    /**
     * Defaults to false. Units in this transport occupy 1 slot always if true, ignoring transportSlotsNeeded.
     */
    var transportUnitsEachUnitAlwaysUsesSingleSlot: Boolean? = null,
    // Example: transportUnitsKeepBuiltUnits: true
    /**
     * Makes built units stay inside transport instead of exiting it once ready if true.
     */
    var transportUnitsKeepBuiltUnits: Boolean? = null,
    // Example: transportUnitsCanUnloadUnits: false
    /**
     * Defaults to: if not self.isOverLiquid() and not self.isMoving(). This unit cannot unload units if false.
     */
    var transportUnitsCanUnloadUnits: LogicBoolean? = null,
    // Example: transportUnitsAddUnloadOption: false
    /** Defines if unload button should be added to the unit menu */
    var transportUnitsAddUnloadOption: Boolean? = null,
    // Example: transportUnitsUnloadDelayBetweenEachUnit: 12
    /** Changes the delay it takes between each unit getting unloaded. */
    var transportUnitsUnloadDelayBetweenEachUnit: Float? = null,
    // Example: transportUnitsKillOnDeath: if self.isOverLiquid()
    /** Defaults to true. If false transported units don't die when transport dies. */
    var transportUnitsKillOnDeath: LogicBoolean? = null,
    // Example: transportUnitsHealBy: 0.1
    /** Rate to heal units that are being transported. */
    var transportUnitsHealBy: Float? = null,
    // Example: transportUnitsBlockOtherTransports: false
    /** Defaults to true, if false this transports can hold other transports. */
    var transportUnitsBlockOtherTransports: Boolean? = null,
    // Example: whileNeutralTransportAnyTeam: true
    /** This unit can transport units of any team while neutral if true. */
    var whileNeutralTransportAnyTeam: Boolean? = null,
    // Example: whileNeutralConvertToTransportedTeam: true
    /**
     * Converts this unit to transported team while neutral. Useful with whileNeutralTransportAnyTeam.
     */
    var whileNeutralConvertToTransportedTeam: Boolean? = null,
    // Example: convertToNeutralIfNotTransporting: true
    /**
     * Reverts back this unit to neutral when unloaded. Useful with whileNeutralTransportAnyTeam.
     */
    var convertToNeutralIfNotTransporting: Boolean? = null,
    // Example: transportUnitsOnTeamChangeKeepCurrentTeam: true
    /**
     * Keeps transported units on their orginal team when this unit is converted if true.
     */
    var transportUnitsOnTeamChangeKeepCurrentTeam: Boolean? = null,

    // ===== Resource Node Keys =====
    // Example: resourceRate: 100
    /**
     * Used with canReclaimResources. Allows other teams to reclaim this unit. Normally used with neutral team. Use price to set what resources are gained.
     */
    var resourceRate: Float? = null,
    // Example: similarResourcesHaveTag: goldResource
    /**
     * When this has been reclaimed harvester unit moves on to another resource with these tags.
     */
    var similarResourcesHaveTag: List<String>? = null,
    // Example: resourceMaxConcurrentReclaimingThis: 3
    /**
     * Defaults to unlimited. Set to restict how many units can reclaim this resource at the same time.
     */
    var resourceMaxConcurrentReclaimingThis: Int? = null,
    // Example: reclaimPrice: gold=1000
    /** Like price but for resources. Useful for buildable resources. */
    var reclaimPrice: Int? = null,

    // ===== Resource Harvester Keys =====
    // Example: canReclaimResources: true
    /** If true this unit can gather resources, useful with resourceRate. */
    var canReclaimResources: Boolean? = null,
    // Example: canReclaimResourcesNextSearchRange: 100
    /**
     * Defines the resource search range of this unit when its main gathered resource runs out.
     */
    var canReclaimResourcesNextSearchRange: Int? = null,
    // Example: canReclaimResourcesOnlyWithTags: foodResource, goldResource
    /** This unit is only allowed to gather resources with these tags. */
    var canReclaimResourcesOnlyWithTags: List<String>? = null,
    // Example: canReclaimUnitsOnlyWithTags: reclaimable
    /**
     * This is for reclaiming units, not for resources. See canReclaimResourcesOnlyWithTags
     */
    var canReclaimUnitsOnlyWithTags: List<String>? = null,
    // Example: resourceReclaimMultiplier: 1.5
    /**
     * Multiplies the builder's reclaim speed. Different from the related key "nanoUnbuildSpeed"
     */
    var resourceReclaimMultiplier: Float? = null,

    // ===== Construction and Factory Keys =====
    // Example: canRepairUnitsOnlyWithTags: vulnerable
    /** Repairs units with the specified tags */
    var canRepairUnitsOnlyWithTags: List<String>? = null,
    // Example: canRepairBuildings: true
    /** Can this can heal ally buildings (isBuilder:true is required) */
    var canRepairBuildings: Boolean? = null,
    // Example: canRepairUnits: true
    /**
     * Can this can heal ally units. (isBuilder:true is required), canRepairBuildings required for buildings.
     */
    var canRepairUnits: Boolean? = null,
    // Example: autoRepair: true
    /**
     * Automatically try and repair damaged units in nano range. (isBuilder:true is required)
     */
    var autoRepair: Boolean? = null,
    // Example: nanoRange: 110
    /** Defaults to 85. Defines the unit building/repair/reclaim range. */
    var nanoRange: Int? = null,
    // Example: nanoRepairSpeed: 0.01
    /** Defaults to 0.2. Defines the unit nano repair/reclaim speed. */
    var nanoRepairSpeed: Float? = null,
    // Example: nanoBuildSpeed: 0.9
    /**
     * Defaults to 1. Defines the unit nano building speed. (May multiply with target's buildSpeed)
     */
    var nanoBuildSpeed: Float = 1f,
    // Example: nanoUnbuildSpeed: 1.4
    /** How fast a builder reclaims an incomplete building (defaults to 1) */
    var nanoUnbuildSpeed: Float? = null,
    // Example: nanoReclaimSpeed: 0.23
    /** How fast a builder reclaims a normal unit (not a resource unit) */
    var nanoReclaimSpeed: Float? = null,
    // Example: nanoRangeForRepairIsMelee: true
    /** Defines if this unit must touch its target to repair it. */
    var nanoRangeForRepairIsMelee: Boolean? = null,
    // Example: nanoRangeForReclaimIsMelee: true
    /** Defines if this unit must touch its target to reclaim it. */
    var nanoRangeForReclaimIsMelee: Boolean? = null,
    // Example: nanoRangeForRepair: 60
    /** Defines a specific range for the repair action of this unit. */
    var nanoRangeForRepair: Int? = null,
    // Example: nanoRangeForReclaim: 60
    /** Defines a specific range for the reclaim action of this unit. */
    var nanoRangeForReclaim: Int? = null,
    // Example: nanoFactorySpeed: 1.2
    /**
     * Defaults to 1. Multiplies the buildSpeed value of the created unit if this unit is a factory.
     */
    var nanoFactorySpeed: Float? = null,
    // Example: extraBuildRangeWhenBuildingThis: 90
    /**
     * Temporarily adds extra build range to builders to build this unit. Useful for water based buildings.
     */
    var extraBuildRangeWhenBuildingThis: Int? = null,
    // Example: builtFrom_1_name: landFactory, airFactory
    // Template: builtFrom_{NUM}_name: - Use integer keys (1, 2, 3...)
    /**
     * Useful if adding this unit to build to existing buildings. Like canBuild but in opposite direction.
     */
    var builtFromNName: Map<Int, List<String>>? = null,
    // Example: builtFrom_1_pos: 0.1
    // Template: builtFrom_{NUM}_pos: - Use integer keys (1, 2, 3...)
    /**
     * Order this build link appears in UI. Using canBuild instead is more recommended.
     */
    var builtFromNPos: Map<Int, Float>? = null,
    // Example: builtFrom_1_forceNano: true
    // Template: builtFrom_{NUM}_forceNano: - Use integer keys (1, 2, 3...)
    /** Build as if this is a building if true. (even if it's a unit) */
    var builtFromNForceNano: Map<Int, Boolean>? = null,
    // Example: builtFrom_1_isLocked: if self.hp(lessThan=100)
    // Template: builtFrom_{NUM}_isLocked: - Use integer keys (1, 2, 3...)
    /**
     * If true this unit cannot be built in this build link. (can be conditioned if logicBooleans are used)
     */
    var builtFromNIsLocked: Map<Int, LogicBoolean>? = null,
    // Example: builtFrom_1_isLockedMessage: -Needs more population
    // Template: builtFrom_{NUM}_isLockedMessage: - Use integer keys (1, 2, 3...)
    /** Message shown when this build link is locked. */
    var builtFromNIsLockedMessage: Map<Int, String>? = null,
    // Example: exit_x: 0
    /**
     * Where created or unloaded units appears from the transport or building. Defaults to 0.
     */
    var exitX: Float? = null,
    // Example: exit_x: 5
    /**
     * Where created or unloaded units appears from the transport or building. Defaults to 5.
     */
    var exitY: Float? = null,
    // Example: exit_dirOffset: 140
    /**
     * Defaults to 180 for units and 0 for buildings. Defines the exit direction of created or unloaded units.
     */
    var exitDirOffset: Float? = null,
    // Example: exit_heightOffset: 16
    /** Defaults to 0. Defines the height where created or unloaded units appears. */
    var exitHeightOffset: Float? = null,
    // Example: exit_moveAwayAmount: 10
    /**
     * Defaults to 70. Defines the distance that created or unloaded units moves from this unit.
     */
    var exitMoveAwayAmount: Float? = null,
    // Example: exitHeightIgnoreParent: true
    /**
     * Ignores parent height for exit height; useful for separating attachments with their parents for building
     */
    var exitHeightIgnoreParent: Boolean? = null,

    // ===== Death Keys =====
    // Example: dieOnConstruct: true
    /**
     * Deletes this unit when it starts to build if true. (target building/unit likely will need selfBuildRate set)
     */
    var dieOnConstruct: Boolean? = null,
    // Example: dieOnZeroEnergy: true
    /** Kills this unit if energy level reaches zero when true. */
    var dieOnZeroEnergy: Boolean? = null,
    // Example: numBitsOnDeath: 20
    /** Defines the number of scattered bit fragments when this unit dies. */
    var numBitsOnDeath: Int? = null,
    // Example: nukeOnDeath: true
    /** Unit will spawn a nuke detonation built-in effect when dies if true. */
    var nukeOnDeath: Boolean? = null,
    // Example: nukeOnDeathRange: 140
    /** Defines the nuke effect range when using nukeOnDeath. */
    var nukeOnDeathRange: Float? = null,
    // Example: nukeOnDeathDamage: 2000
    /** Defines the nuke effect area damage when using nukeOnDeath. */
    var nukeOnDeathDamage: Float? = null,
    // Example: nukeOnDeathDisableWhenNoNuke: true
    /**
     * Defaults to false. If true this unit will not explode with nuke when nukes are disabled in skirmish maps.
     */
    var nukeOnDeathDisableWhenNoNuke: Boolean? = null,
    // Example: fireTurretXAtSelfOnDeath: turret_1
    /** Auto-shoot a specific turret when this unit dies. */
    var fireTurretXAtSelfOnDeath: String? = null,
    // Example: explodeOnDeath: false
    /** Defaults to true. Disables the unit death explode built-in effect if false. */
    var explodeOnDeath: Boolean? = null,
    // Example: explodeOnDeathGroundCollision: false
    /**
     * Defaults to true. Disables the explode built-in effect on death when unit touches ground if false.
     */
    var explodeOnDeathGroundCollision: Boolean? = null,
    /**
     * options: verysmall, small, normal, large, largeUnit, building, buildingNoShockwaveOrSmoke, verylargeBuilding
     */
    var explodeTypeOnDeath: String? = null,
    // Example: effectOnDeath: shockwave, CUSTOM:pieces*3, CUSTOM:deathSound
    /** Spawns built-in or custom effects when unit dies. */
    var effectOnDeath: String? = null,
    // Example: effectOnDeathGroundCollision: CUSTOM:bigExplosion
    /** Like effectOnDeath but when unit touches ground. Useful for flying units. */
    var effectOnDeathGroundCollision: String? = null,
    // Example: unitsSpawnedOnDeath: tank*5, hoverTank
    /** Spawns these units when dies. Comma separated unit identifiers. */
    var unitsSpawnedOnDeath: List<String>? = null,
    // Example: unitsSpawnedOnDeath_setToTeamOfLastAttacker: true
    /** Units spawned on death will appear on the last attacker team if true. */
    var unitsSpawnedOnDeathSetToTeamOfLastAttacker: Boolean? = null,
    // Example: hideScorchMark: true
    /** Disables the death scorch mark leaved when unit dies if true. */
    var hideScorchMark: Boolean? = null,
    // Example: soundOnDeath: tankExplosion1.ogg, tankExplosion2.ogg
    /** Sets a custom sound for this unit death. */
    var soundOnDeath: List<String>? = null,
    // Example: effectOnDeathIfUnbuilt: CUSTOM:implode
    /**
     * If the unit was not completed, and is destroyed, play this effect. Defaults to effectOnDeath
     */
    var effectOnDeathIfUnbuilt: String? = null,

    // ===== Action Keys =====
    // Example: autoTriggerCooldownTime: 0.05s
    /**
     * Post automatic action cooldown (Not detection cooldown). Defaults to 1s. Warning: Setting this too low for many units might effect performance depending on the action effects.
     */
    var autoTriggerCooldownTime: String? = null,
    // Example: autoTriggerCooldownTime_allowDangerousHighCPU: true
    /**
     * Allows for auto action cooldown lower than 0.2s. Default to false. Not recommended.
     */
    var autoTriggerCooldownTimeAllowDangerousHighCPU: Boolean? = null,
    // Example: autoTriggerCheckRate:every8Frames
    /**
     * options: everyFrame (default), every4Frames, every8Frames. Note: all triggers regardless of check rate are checked when first created and after an auto trigger cooldown. Note: Adding [core]autoTriggerCheckRate:every8Frames to all-units.template could have a large performance boost for mods with complex autoTriggers.
     */
    var autoTriggerCheckRate: String? = null,
    // Example: autoTriggerCheckWhileNotBuilt: true
    /**
     * Defaults to false. autoTrigger of unit actions check even when not completely built if true
     */
    var autoTriggerCheckWhileNotBuilt: Boolean? = null,
    // Example: updateUnitMemory: timeCount += 1
    /**
     * Faster memory update than [action_#] setUnitMemory. Useful on many applications.
     */
    var updateUnitMemory: String? = null,
    // Example: updateUnitMemoryRate: 0
    /**
     * Sets how often the memory is updated. Defaults at 1s. settings it to 0 will update memory every frame.
     */
    var updateUnitMemoryRate: Int? = null,
    // Example: @memory fish:unit
    // Directive: @memory - Template preprocessor command
    /**
     * A template-friendly method of defineUnitMemory. Declare name followed by type, separated by a colon
     */
    var memory: String? = null,

    // ===== Deprecated Keys (can be used but there are better ways) =====
    // Example: action_1_convertTo: customTank_2
    /** Deprecated in 1.13, use [action_x] sections instead */
    var actionNConvertTo: String? = null,
    // Example: action_1_pos: 0.1
    /** Order action appears in UI */
    var actionNPos: Float? = null,
    // Example: action_1_price: 1000
    /**
     * The price of your action for the unit. (All your sub actions will be linked to the # you use)
     */
    var actionNPrice: Int? = null,
    // Example: action_1_text: Upgrade to Custom Tank 2
    /**
     * A display text when you select your unit's action, used to explain it's purpose.
     */
    var actionNText: String? = null,
    // Example: action_1_description: -Converts the tank
    /** The action description. */
    var actionNDescription: String? = null,
    // Example: action_1_addEnergy: 10
    /** Adds energy to unit. Has no effect unless energyMax is set */
    var actionNAddEnergy: Float? = null,
    // Example: action_1_whenBuilding_cannotMove: true
    /**
     * Stops unit moving while action is being applied. Useful for deploy like actions.
     */
    var actionNWhenBuildingCannotMove: Boolean? = null

)
