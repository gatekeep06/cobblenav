package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.spawndata.SpawnData

interface SpawnDataTooltipDisplayer {
    var hoveredSpawnData: SpawnData?

    fun isBlockingTooltip(): Boolean
}