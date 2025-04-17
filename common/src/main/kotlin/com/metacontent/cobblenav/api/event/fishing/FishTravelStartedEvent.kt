package com.metacontent.cobblenav.api.event.fishing

import net.minecraft.server.level.ServerPlayer

data class FishTravelStartedEvent(
    val player: ServerPlayer
)