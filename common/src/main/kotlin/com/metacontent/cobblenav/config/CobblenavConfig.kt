package com.metacontent.cobblenav.config

import com.cobblemon.mod.common.Cobblemon
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

            var config: CobblenavConfig
            try {
                if (!configFile.exists()) {
                    configFile.createNewFile()
                }
                val fileReader = FileReader(configFile)
                config = GSON.fromJson(fileReader, CobblenavConfig::class.java) ?: CobblenavConfig()
                fileReader.close()
            }
            catch (e: Exception) {
                Cobblenav.LOGGER.error(e.message, e)
                config = CobblenavConfig()
            }

            config.save()

            return config
        }
    }

    val hideUnknownPokemon = false
    val showPokemonTooltips = true
    val showUnknownPokemonTooltips = true
    val checkSpawnWidth = Cobblemon.config.worldSliceDiameter
    val checkSpawnHeight = Cobblemon.config.worldSliceHeight
    val searchAreaWidth = 200.0
    val searchAreaHeight = 200.0
    val pokemonFeatureWeights = FeatureWeights.BASE

    fun save() {
        val configFile = File(PATH)
        try {
            val fileWriter = FileWriter(configFile)
            GSON.toJson(this, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        }
        catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
        }
    }
}