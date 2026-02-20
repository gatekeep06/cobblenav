package com.metacontent.cobblenav.config

class ClientCobblenavConfig : Config<ClientCobblenavConfig>() {
    @Transient
    override val fileName = "client-config.json"

    val screenScale = 1f
    val enableBlurEffect = true
    val sendErrorMessagesToChat = true
    val pokefinderOverlayOffset = 10
    val trackArrowYOffset = 80
    val maxCloudNumber = 8
    val maxCloudVelocity = 6
    val collectableClientConditions = mutableMapOf(
        "encounter"         to true,
        "overall_counts"    to true,
        "streak_counts"     to true,
        "fishing_count"     to true
    )

    override fun applyToLoadedConfig(default: ClientCobblenavConfig) {
        default.collectableClientConditions.forEach { this.collectableClientConditions.putIfAbsent(it.key, it.value) }
    }
}