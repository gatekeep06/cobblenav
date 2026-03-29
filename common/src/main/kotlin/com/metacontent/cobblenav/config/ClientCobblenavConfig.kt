package com.metacontent.cobblenav.config

class ClientCobblenavConfig : Config<ClientCobblenavConfig>() {
    @Transient
    override val fileName = "client-config.json"

    val screenScale = 1f
    val enableBlurEffect = true
    val sendErrorMessagesToChat = true
    val pokefinderScreenScale = 1f
    val pokefinderOverlayScale = 1f
    val pokefinderOverlayOffsetX = 10
    val pokefinderOverlayOffsetY = 10
    val trackArrowYOffset = 80
    val maxCloudNumber = 8
    val maxCloudVelocity = 6

    override fun applyToLoadedConfig(default: ClientCobblenavConfig) {}
}