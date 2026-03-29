package com.metacontent.cobblenav.storage

import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail

abstract class AbstractSpawnDataCatalogue(
    val spawnDetailIds: MutableSet<String>
) {
    fun contains(detailId: String) = spawnDetailIds.contains(detailId)

    fun contains(detail: SpawnDetail) = contains(detail.id)
}