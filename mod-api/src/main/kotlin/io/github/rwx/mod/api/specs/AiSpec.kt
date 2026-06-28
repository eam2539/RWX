package io.github.rwx.mod.api.specs


/**
 * Properties for [ai] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class AiSpec(
    // Example: useAsBuilder:
    /** Set to true if unit can build or repair buildings. Defaults to [core]isBuilder. */
    var useAsBuilder: Boolean? = null,
    // Example: useAsTransport
    /** Defaults to true if unit can transport units */
    var useAsTransport: Boolean? = null,
    // Example: useAsHarvester
    /** Defaults to true if unit can reclaim resources */
    var useAsHarvester: Boolean? = null,
    // Example: disableUse:
    /** Disallow AI building this unit or building */
    var disableUse: Boolean? = null,
    // Example: ai_upgradePriority
    /**
     * Defaults to 0.06. Set between 0-1, higher means AI is more likely to upgrade this unit before others
     */
    var aiUpgradePriority: Float? = null,

    // ===== Buildings only =====
    // Example: maxGlobal: 50
    /** Maximum amount allowed for a specific structure for an AI team per map */
    var maxGlobal: Int? = null,
    // Example: maxEachBase: 10
    /**
     * Maximum amount allowed for a specific structure for an AI team per "base", usually around an extractor or spawn point. To check for an AI base in sandbox, enable Debug Mode and Click Shift + F3
     */
    var maxEachBase: Int? = null,
    // Example: buildPriority: 1
    /**
     * 0-1. AI uses 0.8 for first land factory, 0.48 for air factory, 0.47 for first turret.
     */
    var buildPriority: Float? = null,
    // Example: noneInBaseExtraPriority: 2
    /** Adds to buildPriority, if this unit doesn't exist in the AIs base */
    var noneInBaseExtraPriority: Float? = null,
    // Example: noneGlobalExtraPriority: 4
    /** Adds to buildPriority, if this unit doesn't exist in the any where on the map */
    var noneGlobalExtraPriority: Float? = null,
    // Example: recommendedInEachBaseNum: 2
    /** Defaults to 0 */
    var recommendedInEachBaseNum: Float? = null,
    // Example: recommendedInEachBasePriorityIfUnmet: 4
    /** Defaults to 0.5. Overrides buildPriority if recommended in base is too low. */
    var recommendedInEachBasePriorityIfUnmet: Float? = null,
    // Example: upgradedFrom: builderLevel1
    /**
     * Create link to another unit to preserve max counts for upgraded and non-upgraded types in same base.
     */
    var upgradedFrom: String? = null,
    // Example: notPassivelyTargetedByOtherUnits: true
    /** Useful for walls, etc */
    var notPassivelyTargetedByOtherUnits: Boolean? = null,
    // Example: lowPriorityTargetForOtherUnits: true
    /** Useful for units that cannot attack back. Eg walls */
    var lowPriorityTargetForOtherUnits: Boolean? = null,
    // Example: whenUsingAsHarvester_recommendedInEachBase: 3
    /** Assigns unit of the same name with certain amount on an AI base */
    var whenUsingAsHarvesterRecommendedInEachBase: Int? = null,
    // Example: whenUsingAsHarvester_recommendedGlobal: 20
    /** Assigns unit of the same name with certain amount on map */
    var whenUsingAsHarvesterRecommendedGlobal: Int? = null,
    // Example: whenUsingAsHarvester_includeOtherHarvesterCounts: true
    /** Includes all harvester types on a single counter */
    var whenUsingAsHarvesterIncludeOtherHarvesterCounts: Boolean? = null,
    // Example: onlyUseAsHarvester_ifBaseHasUnitTagged: mineral, wood, stones
    /**
     * Only sets the harvester unit as a harvester if an AI base has a unit with particular tag(s)
     */
    var onlyUseAsHarvesterIfBaseHasUnitTagged: String? = null
)
