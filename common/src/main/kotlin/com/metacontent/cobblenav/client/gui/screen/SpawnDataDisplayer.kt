package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.spawndata.SpawnData

interface SpawnDataDisplayer {
    var displayedData: Collection<SpawnData>?
    var hoveredData: SpawnData?
    var selectedData: SpawnData?

    fun isBlockingTooltip(): Boolean
}