package com.metacontent.cobblenav.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.metacontent.cobblenav.Cobblenav
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class CobblenavConfig {
    companion object {
        private const val PATH = "config/cobblenav/server-config.json"
        private val GSON: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        fun load(): CobblenavConfig {
            val configFile = File(PATH)
            configFile.parentFile.mkdirs()

            val default = CobblenavConfig()
            val config = runCatching {
                if (!configFile.exists()) {
                    configFile.createNewFile()
                }
                FileReader(configFile).use {
                    GSON.fromJson(it, CobblenavConfig::class.java) ?: default
                }
            }.onFailure {
                Cobblenav.LOGGER.error(it.message, it)
            }.getOrDefault(default)

            default.collectableConditions.forEach { config.collectableConditions.putIfAbsent(it.key, it.value) }

            config.save()

            return config
        }
    }

    val hideUnknownPokemon = false
    val showPokemonTooltips = true
    val hideUnknownPokemonTooltips = false
    val hideNaturalBlockConditions = true
    val checkSpawnWidth = 8
    val checkSpawnHeight = 16
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
        "fishing_block"         to true
    )

    fun save() {
        val configFile = File(PATH)
        runCatching{
            FileWriter(configFile).use {
                GSON.toJson(this, it)
            }
        }.onFailure{ Cobblenav.LOGGER.error(it.message ?: "", it) }
    }
}