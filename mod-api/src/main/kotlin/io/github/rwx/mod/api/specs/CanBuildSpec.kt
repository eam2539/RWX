package io.github.rwx.mod.api.specs


/**
 * Properties for [canBuild_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class CanBuildSpec(
    // Example: name: setRally, tank, hoverTank, heavyTank
    /**
     * List of unit identifiers this unit can create. Can be buildings or units. Add "setRally" to create a rally button
     */
    var name: List<String> = listOf(),
    // Example: pos: 0.1
    /** Order build link appears in this unit UI. */
    var pos: Float? = null,
    // Example: tech: 2
    /**
     * Tech level. Mostly just affects build link colour in this unit UI. Defaults to 1.
     */
    var tech: Int? = null,
    // Example: forceNano: true
    /** Builds target as if it was a building if true. (even if it's a unit) */
    var forceNano: Boolean? = null,
    // Example: isVisible: if not self.energy(greaterThan=100)
    /** Hide this build link if true in this unit UI. */
    var isVisible: LogicBoolean = "true",
    // Example: isLocked: if self.hp(lessThan=100)
    /** Dynamically locks this build option and shows isLockedMessage if true. */
    var isLocked: LogicBoolean? = null,
    // Example: isLockedMessage: -Needs 2 Barracks
    /** Set to tell to players why a unit is locked. */
    var isLockedMessage: String? = null,
    // Example: isLockedMessage_es: -Necesita 2 Cuarteles
    // Template: isLockedMessage_{LANG}: - Use ISO 639-1 language codes as keys (en, es, fr...)
    /** LANG = ISO 639-1 Code to show this text instead when game is in this language. */
    var isLockedMessageLang: Map<String, String>? = null,
    // Example: isLockedAlt: if self.energy(greaterThan=90)
    /**
     * Another reason for this to be locked. Just allows a different message to be shown.
     */
    var isLockedAlt: LogicBoolean? = null,
    // Example: isLockedAltMessage: -Needs less energy
    /** Message for isLockedAlt. */
    var isLockedAltMessage: String? = null,
    // Example: isLockedAlt2: if self.isMoving()
    /** Like isLockedAlt but to show one more message. */
    var isLockedAlt2: LogicBoolean? = null,
    // Example: isLockedAlt2Message: -Needs to be quiet
    /** Message for isLockedAlt2. */
    var isLockedAlt2Message: String? = null,
    // Example: addResources: ammo=5, setFlag=1
    /** Adds these resources to self when placing the building or producing the unit. */
    var addResources: ResourceBundle? = null,
    // Example: price: credits=1000, ammo=5
    /** Overrides builded units/buildings price. Defaults to target unit prices. */
    var price: ResourceBundle? = null,
    // Example: isGuiBlinking: true
    /** Generates a blinking effect in UI if true. */
    var isGuiBlinking: LogicBoolean? = null
)
