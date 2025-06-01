package com.metacontent.cobblenav.config

class ClientCobblenavConfig : Config<ClientCobblenavConfig>() {
    @Transient
    override val fileName = "client-config.json"

    val screenScale = 1f
    val sendErrorMessagesToChat = true
    val obscureUnknownPokemon = true
    val useSwimmingAnimationIfSubmerged = true
    val pokefinderOverlayOffset = 10
    val trackArrowYOffset = 80
    val maxCloudNumber = 12
    val maxCloudVelocity = 6
}