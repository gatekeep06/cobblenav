package com.metacontent.cobblenav.event

import net.minecraft.server.level.ServerPlayer

data class FishTravelingStartedEvent(
    val player: ServerPlayer
)