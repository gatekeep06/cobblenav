package com.metacontent.cobblenav.config

class ClientCobblenavConfig : Config<ClientCobblenavConfig>() {
    override val fileName = "client-config.json"

    val screenScale = 1f
    val sendErrorMessagesToChat = true
    val obscureUnknownPokemon = true
    val useSwimmingAnimationIfSubmerged = true
    val pokefinderOverlayOffset = 10
    val trackArrowYOffset = 80
}