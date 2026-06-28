package io.github.rwx.mod.api.specs


/**
 * Properties for [template_NAME] section
 * 
 * Generated from Rusted_Warfare__Beta_Modding_Reference.xlsx
 */
data class TemplateSpec(
    // ===== --All these below features can be used with any section not just templates-- =====
    // Example: @copyFromSection: template_name/action_name/projectile_name
    // Directive: @copyFromSection - Template preprocessor command
    /**
     * Use in any section to include keys from a section or template. (Comma separated for multiple)
     */
    var copyFromSection: String? = null,
    // Example: @copyFrom_skipThisSection
    // Directive: @copyFrom_skipThisSection - Template preprocessor command
    /**
     * Use in any section to make [core]copyFrom not copy into it. Eg not copy an action when overriding
     */
    var copyFromSkipThisSection: String? = null,
    // Example: @define targetEffect: boom
    // Directive: @define X - Template preprocessor command
    /** Define a local variable within a section (best outside of template) */
    var defineX: String? = null,
    // Example: @global targetEffect: pop
    // Directive: @global X - Template preprocessor command
    /**
     * Define a global variable used in all sections. Local variables have a higher priority
     */
    var globalX: String? = null,
    // Example: @memory transportCount: float
    // Directive: @memory X - Template preprocessor command
    /** Define a memory for this unit, its type must be defined as well. */
    var memoryX: String? = null,
    // Example: copyFrom:ROOT:templates\cruiser.template, copyFrom:ROOT:templates\infantry.template, copyFrom:ROOT:templates\hq.template, 
    /**
     * A template that can be used to share many parts across units, used with copyFrom:                                       However, cannot define variables
     */
    var file: String? = null,
    // Example: Example
    /** Description */
    var key: String? = null
)
