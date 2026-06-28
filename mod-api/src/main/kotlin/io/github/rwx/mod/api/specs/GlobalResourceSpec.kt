package io.github.rwx.mod.api.specs


/**
 * Properties for [global_resource_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class GlobalResourceSpec(
    // ===== Define a new resource shared with all units in a team, works just like the built-in credits resource. Add to 'all-units.template' (at mod root) for easy use in all of your mods =====
    // Example: displayName: Crude Oil
    /** Name of this resource in UI */
    var displayName: String? = null,
    // Example: displayNameShort: Oil
    /**
     * Resource name on smaller UI elements like action hovertext (Defaults to displayName)
     */
    var displayNameShort: String? = null,
    // Example: hidden: false
    /** Hide this resource from the player */
    var hidden: Boolean? = null,
    // Example: priority: 0.5
    /**
     * If 2 or mods/units define a resource with the same NAME, the displayName/displayColor with the highest priority is used
     */
    var priority: Float? = null,
    // Example: displayColor: #FF0000
    /** Color, can be hex with optional alpha */
    var displayColor: Color? = null,
    // Example: displayRoundedDown
    /** Don't show decimal places to the player */
    var displayRoundedDown: Boolean? = null,
    // Example: displayTextPrefix: -
    /** Adds a string before the resource value */
    var displayTextPrefix: String? = null,
    // Example: displayTextPostfix: x
    /** Adds a string after the resource value */
    var displayTextPostfix: String? = null,
    // Example: displayTextAppendResource: sauce
    /**
     * Appends another resource after this resource. It's recommended to give a "hidden" key on the appended resource to hide duplicate.
     */
    var displayTextAppendResource: ResourceBundle? = null,
    // Example: displayWhenZero: true
    /** Displays the resource even when empty. Set as false by default */
    var displayWhenZero: Boolean? = null,
    // Example: displayPos: 1
    /** Sets the position of the globa resource in the screen */
    var displayPos: Int? = null,
    // Example: iconImage: icon_oil.png
    /** Shows a custom icon for the specific resource in the HUD and in text */
    var iconImage: String? = null,
    // Example: iconImageUseInText: false
    /** Default as true; Shows resource icon in action description */
    var iconImageUseInText: Boolean? = null,
    // Example: displayNameHideWhenIconShownInText: true
    /** Default as false */
    var displayNameHideWhenIconShownInText: Boolean? = null,
    // Example: displayNameHideWhenIconShownInHUD: true
    /** Hides the icon in menus referring to resource when true. Default as false */
    var displayNameHideWhenIconShownInHUD: Boolean? = null,
    // Example: displayColorUseInText: false
    /** Shows color in action description */
    var displayColorUseInText: Boolean? = null,
    // Example: displayInHud: false
    /**
     * For resources used in appendResourceInHUD that shouldn't be hidden: true. Defaults true.
     */
    var displayInHud: Boolean? = null,
    // Example: appendResourceInHUD: sauce
    /** stacks another resource specified after this resource on the HUD. */
    var appendResourceInHUD: ResourceBundle? = null,
    // Example: appendResourceInHUD_whenThisZero: true
    /**
     * Defaults as true. When set as false, allows appended resource to be hidden with the parent resource.
     */
    var appendResourceInHUDWhenThisZero: Boolean? = null,
    // Example: displayPrefixInHUD: -
    /**
     * Displays text to show before resource value - replaces resource name and removes the colon separator
     */
    var displayPrefixInHUD: String? = null,
    // Example: displayPostfixInHUD: x
    /** Similar to the prefix counterpart, but is displayed after the resource value */
    var displayPostfixInHUD: String? = null,
    // Example: valueInStats: 0.5
    /** Affects post game stats and replay leaderboard. Defaults to 1. */
    var valueInStats: Float? = null,
    // Example: displayTextAppendResourceWithGap: true
    /** Adds a space between this and the appended resource. Defaults as false. */
    var displayTextAppendResourceWithGap: Boolean? = null,
    // Example: displayDigitGrouping: comma
    /** Sets the symbol for separating place units in: none, comma, space */
    var displayDigitGrouping: String? = null
)
