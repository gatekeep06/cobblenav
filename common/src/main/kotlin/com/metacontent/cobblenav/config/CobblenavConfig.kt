package com.metacontent.cobblenav.config

class CobblenavConfig : Config<CobblenavConfig>() {
    @Transient
    override val fileName = "server-config.json"

    val hideUnknownSpawns = false
    val hideConditionsOfUnknownSpawns = true
    val hideNaturalBlockConditions = true
    val percentageForKnownHerd = 0.5f
    val syncLabelsWithClient = true
    val searchAreaWidth = 200.0
    val searchAreaHeight = 200.0
    val pokemonFeatureWeights = FeatureWeights.BASE
    val collectableConditions = mutableMapOf(
        "biomes"                to true,
        "coordinates"           to true,
        "light"                 to true,
        "moon_phase"            to true,
        "sky_light"             to true,
        "slime_chunk"           to true,
        "structures"            to true,
        "time_range"            to true,
        "under_open_sky"        to true,
        "weather"               to true,
        "y_height"              to true,
        "depth_submerged"       to true,
        "depth_surface"         to true,
        "fluid_submerged"       to true,
        "fluid_surface"         to true,
        "bait"                  to true,
        "lure_level"            to true,
        "rod"                   to true,
        "rod_type"              to true,
        "area_type_block"       to true,
        "grounded_type_block"   to true,
        "seafloor_type_block"   to true,
        "fishing_block"         to true,
        "count"                 to true,
        "streak"                to true,
        "key_item"              to false,
        "items"                 to false,
        "pokemon"               to false,
        "zygarde_cube_charge"   to false
    )

    override fun applyToLoadedConfig(default: CobblenavConfig) {
        default.collectableConditions.forEach { this.collectableConditions.putIfAbsent(it.key, it.value) }
    }
}