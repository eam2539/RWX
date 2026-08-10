package io.github.rwx.mod

import io.github.rwx.mod.api.*

internal class UnitApiImpl : Units {
    private val lock = Any()
    private val unitDefinitions = linkedMapOf<UnitId, UnitDefinition>()

    override fun registerUnit(definition: UnitDefinition) {
        validate(definition)
        synchronized(lock) {
            check(definition.id !in unitDefinitions) { "Unit is already registered: ${definition.id}" }
            unitDefinitions[definition.id] = definition
        }
    }

    fun units(): List<UnitDefinition> = synchronized(lock) { unitDefinitions.values.toList() }

    private fun validate(definition: UnitDefinition) {
        definition.extension.shaderId?.let { shaderId ->
            require(shaderId.isNotBlank() && shaderId == shaderId.trim()) {
                "Unit shaderId must be non-blank and trimmed: ${definition.id}"
            }
        }
        val eventHandlerIds = hashSetOf<UnitEventBindingId>()
        definition.eventHandlers.forEach { binding ->
            require(eventHandlerIds.add(binding.id)) {
                "Duplicate event handler '${binding.id}' on unit ${definition.id}"
            }
            require(binding.recursionLimit in 0..MAX_EVENT_RECURSION_LIMIT) {
                "Unit event recursionLimit must be between zero and $MAX_EVENT_RECURSION_LIMIT: ${definition.id}"
            }
            require(binding.actions.size <= MAX_EVENT_ACTIONS) {
                "Unit event handler cannot contain more than $MAX_EVENT_ACTIONS actions: ${definition.id}"
            }
            binding.actions.forEach { action ->
                when (action) {
                    is UnitEventAction.SpawnEffect -> Unit
                }
            }
            require(binding.actions.isNotEmpty() || binding.listener != null) {
                "Unit event handler must contain an action or listener: ${definition.id} (${binding.id.value})"
            }
            validate(binding.condition)
        }
    }

    private fun validate(condition: UnitCondition, depth: Int = 0) {
        require(depth <= MAX_CONDITION_DEPTH) { "Unit condition exceeds maximum depth of $MAX_CONDITION_DEPTH" }
        when (condition) {
            UnitCondition.Always,
            UnitCondition.Never,
            is UnitCondition.SubjectExists,
            is UnitCondition.BooleanProperty,
            is UnitCondition.HasTag -> Unit

            is UnitCondition.Compare -> {
                validate(condition.left, depth + 1)
                validate(condition.right, depth + 1)
            }

            is UnitCondition.All -> condition.conditions.forEach { validate(it, depth + 1) }
            is UnitCondition.Any -> condition.conditions.forEach { validate(it, depth + 1) }
            is UnitCondition.Not -> validate(condition.condition, depth + 1)
        }
    }

    private fun validate(expression: UnitNumberExpression, depth: Int) {
        require(depth <= MAX_CONDITION_DEPTH) { "Unit expression exceeds maximum depth of $MAX_CONDITION_DEPTH" }
        when (expression) {
            is UnitNumberExpression.Constant -> require(expression.value.isFinite()) {
                "Unit expression constants must be finite"
            }

            is UnitNumberExpression.Property -> Unit
            is UnitNumberExpression.Arithmetic -> {
                validate(expression.left, depth + 1)
                validate(expression.right, depth + 1)
                val right = expression.right
                if (
                    expression.operator == UnitArithmeticOperator.DIVIDE &&
                    right is UnitNumberExpression.Constant
                ) {
                    require(right.value != 0f) { "Unit expression cannot divide by zero" }
                }
            }
        }
    }

    private companion object {
        const val MAX_EVENT_RECURSION_LIMIT = 50
        const val MAX_EVENT_ACTIONS = 256
        const val MAX_CONDITION_DEPTH = 64
    }
}
