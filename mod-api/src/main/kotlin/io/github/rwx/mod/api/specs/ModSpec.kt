package io.github.rwx.mod.api.specs


/**
 * Properties for [mod] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class ModSpec(
    // Example: title: Sample mod
    /** Sets the title of the mod package */
    var title: String = "Unnamed Mod",
    // Example: description: This is a sample of mod that samples a unit to the game
    /** Sets the description of the mod package. Doesn't support line breaks. */
    var description: String = "",
    // Example: tags: units, sample, demo
    /**
     * Sets varied tags depending on what is specified, can be multiple with comma as separator
     */
    var tags: String? = null,
    // Example: minVersion: 1.15p9
    /**
     * Declares the minimum version compatible for the mod. It is important to declare one when using decals and other modding keys starting on version 1.15p9 beta
     */
    var minVersion: String = "1.15",
    // Example: thumbnail: assets/images/other/thumb.png
    /** Sets the mod's thumbnail in Steam Workshop */
    var thumbnail: String? = null
)
