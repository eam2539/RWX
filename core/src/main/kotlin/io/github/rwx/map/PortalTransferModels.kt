package io.github.rwx.map

import kotlinx.serialization.Serializable

@Serializable
data class TransferredUnit(
    val unitTypeId: String,
    val teamId: Int,
    val healthFraction: Float,
    val direction: Float,
)

@Serializable
data class PortalTransferMessage(
    val sourceMapPath: String?,
    val targetMapId: String,
    val targetPortalId: String?,
    val units: List<TransferredUnit>,
)
