package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.spawndata.CheckedSpawnData
import com.metacontent.cobblenav.spawndata.SpawnData

interface SpawnDataDisplayer {
    val displayedData: List<SpawnData>?
    var hoveredData: CheckedSpawnData?
    var selectedData: SpawnData?

    fun isBlockingTooltip(): Boolean

    fun selectedCanBeTracked(): Boolean

    fun isDataSelected(): Boolean = selectedData != null

    fun switchData(step: Int): Int {
        val index = displayedData?.indexOf(selectedData)?.let { it + step } ?: -1
        displayedData?.getOrNull(index)?.let { selectedData = it }
        return index
    }
}