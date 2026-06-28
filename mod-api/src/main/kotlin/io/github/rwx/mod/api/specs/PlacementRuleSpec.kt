package io.github.rwx.mod.api.specs


/**
 * Properties for [placementRule_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class PlacementRuleSpec(
    // Example: Example
    /** Description */
    var code: String? = null,
    // Example: anyRuleInGroup: struct
    /**
     * (Only require 1 of the rules in this group pass, instead of all. Use the same group name on other placement rules to create a group.)
     */
    var anyRuleInGroup: String? = null,
    // Example: searchTags: factory
    /** Search for any unit with at least one of these tags */
    var searchTags: List<String>? = null,
    // Example: searchTeam: own
    /** Teams to include in search, can be: own|neutral|allyNotOwn|ally|enemy|any */
    var searchTeam: String? = null,
    // Example: searchOffsetX: 100
    /** defaults to 0 */
    var searchOffsetX: Float? = null,
    // Example: searchOffsetY: 200
    /** defaults to 0 */
    var searchOffsetY: Float? = null,
    // Example: searchDistance: 500
    /** Required */
    var searchDistance: Float? = null,
    // Example: excludeIncompleteBuildings: true
    /**
     * defaults to false. Might want to set to true depending on the requirement reason
     */
    var excludeIncompleteBuildings: Boolean? = null,
    // Example: excludeNonBuildings:true
    /** defaults to false */
    var excludeNonBuildings: Boolean? = null,
    // Example: minCount: 0
    /**
     * Set min amount of units that need to be found in search. (eg needs to be near something). Defaults to 0
     */
    var minCount: Int? = null,
    // Example: maxCount: 1
    /**
     * Set max amount of units before match fails (eg cannot be close to something). Defaults to unlimited
     */
    var maxCount: Int? = null,
    // Example: blocksPlacement: false
    /** Defaults to true. */
    var blocksPlacement: Boolean? = null,
    // Example: cannotPlaceMessage: "No factory is nearby"
    /**
     * Highly Recommended. Message shown to player if this rule fails (will be first failing rule if using anyRuleInGroup).
     */
    var cannotPlaceMessage: String? = null,
    // Example: checkEachTile: false
    /**
     * defaults to true (set to false to only test unit center, true checks each tile under the unit which shows up on the placement grid. Can be easier to see requirements with true)
     */
    var checkEachTile: Boolean? = null,
    /** Prices/Resources lines - used by addResources, price, etc */
    var type: String? = null,
    // Example: price: 100
    /** Global resource */
    var credits: String? = null,
    // Example: resourceUsage: energy=1
    /** Energy used for laser shield and ammunition */
    var energy: String? = null,
    // Example: addResources: hp += 100
    /** Unit hitpoints */
    var hp: String? = null,
    // Example: price: hp=-100, shield=100
    /** Shielding for units */
    var shield: String? = null,
    // Example: price: hp=-100, shield=101
    /** Hidden value on each unit for use by mods */
    var ammo: String? = null,
    // Example: addResources: setFlag=1
    /**
     * use with addResources, resourceUsage or price. 0-31. Flags are stored in each unit
     */
    var setFlag: String? = null,
    /** use with addResources, resourceUsage or price. 0-31 */
    var unsetFlag: String? = null,
    /** use with price or resourceUsage */
    var hasFlag: String? = null,
    /** use with price or resourceUsage */
    var hasMissingFlag: String? = null,
    // Example: gold=5, stone=20
    /** Any resource defined in [global_resource_x] or [resource_x] sections */
    var x: String? = null
)
