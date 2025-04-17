package com.metacontent.cobblenav.api.event.profile

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

data class TitlesRemoved(
    val player: ServerPlayer?,
    val titleIds: List<ResourceLocation>
)
