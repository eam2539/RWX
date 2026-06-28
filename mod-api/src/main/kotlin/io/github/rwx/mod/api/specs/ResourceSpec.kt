package io.github.rwx.mod.api.specs


/**
 * Properties for [resource_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class ResourceSpec(
    // ===== Define a new resource local to unit. Works like build-in ammo resource =====
    // Example: displayName: Sauce
    /** Name of this resource in UI (e.g. hovering over unit info) */
    var displayName: String? = null,
    // Example: displayNameShort: Sus
    /** Shorter version of resource name used on some text ui */
    var displayNameShort: String? = null,
    // Example: hidden: true
    /** Hide this resource from the player */
    var hidden: Boolean? = null,
    // Example: equivalentGlobalResourceForAI: universal_sauce
    /**
     * Used to hint to the AI that a resource node with a local resources could be used to get a different global resource. Eg when a harvester unloads the resource
     */
    var equivalentGlobalResourceForAI: ResourceBundle? = null,
    // Example: displayRoundedDown: true
    /** Rounds off resource values with decimals */
    var displayRoundedDown: Boolean? = null
)
