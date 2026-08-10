package io.github.rwx.app

import io.github.rwx.map.PortalTransferMessage
import io.github.rwx.session.MapSnapshot

internal class MapRuntimeState {
    var graphRootPath: String? = null
    val runtimeLinkedMapNames: LinkedHashMap<String, String> = linkedMapOf()
    var appliedP2PAssignedMapPath: String? = null

    private val snapshots = mutableMapOf<String, MapSnapshot>()
    private val pendingPortalTransfers = mutableMapOf<String, MutableList<PortalTransferMessage>>()

    fun cacheKey(mapPath: String): String =
        mapPath.replace('\\', '/').trim().lowercase()

    fun samePath(left: String?, right: String?): Boolean {
        if (left.isNullOrBlank() || right.isNullOrBlank()) {
            return false
        }
        return cacheKey(left) == cacheKey(right)
    }

    fun putSnapshot(mapPath: String, snapshot: MapSnapshot) {
        snapshots[cacheKey(mapPath)] = snapshot
    }

    fun snapshotFor(mapPath: String): MapSnapshot? =
        snapshots[cacheKey(mapPath)]

    fun queuePortalTransfer(targetMapPath: String, transfer: PortalTransferMessage) {
        pendingPortalTransfers.getOrPut(cacheKey(targetMapPath)) { mutableListOf() } += transfer
    }

    fun removePortalTransfersFor(mapPath: String): List<PortalTransferMessage>? =
        pendingPortalTransfers.remove(cacheKey(mapPath))

    fun hasKnownLinkedMaps(): Boolean =
        graphRootPath != null || runtimeLinkedMapNames.size > 1
}
