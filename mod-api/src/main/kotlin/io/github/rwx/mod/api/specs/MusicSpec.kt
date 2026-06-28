package io.github.rwx.mod.api.specs


/**
 * Properties for [music] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class MusicSpec(
    // Example: sourceFolder: assets/audio/music
    /** Sets where the custom music will be played for the mod */
    var sourceFolder: String? = null,
    // Example: whenUsingUnitsFromThisMod_playExclusively: true
    /** Only plays music from this mod */
    var whenUsingUnitsFromThisModPlayExclusively: Boolean? = null,
    /** image (with frame numbers) */
    var name: String? = null,

    var addToNormalPlaylist: Boolean? = null,
)
