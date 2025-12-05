package com.metacontent.cobblenav.event

import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.gui.components.AbstractWidget

data class SpawnDataWidgetsCreatedEvent(
    val spawnData: SpawnData,
    val widgets: MutableList<AbstractWidget>
)
