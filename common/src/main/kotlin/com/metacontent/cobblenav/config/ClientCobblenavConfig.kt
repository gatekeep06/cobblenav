package com.metacontent.cobblenav.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.metacontent.cobblenav.Cobblenav
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class ClientCobblenavConfig {
    companion object {
        private const val PATH = "config/cobblenav/client-config.json"
        private val GSON: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        fun load(): ClientCobblenavConfig {
            val configFile = File(PATH)
            configFile.parentFile.mkdirs()

            var config: ClientCobblenavConfig
            try {
                if (!configFile.exists()) {
                    configFile.createNewFile()
                }
                val fileReader = FileReader(configFile)
                config = GSON.fromJson(fileReader, ClientCobblenavConfig::class.java) ?: ClientCobblenavConfig()
                fileReader.close()
            }
            catch (e: Exception) {
                Cobblenav.LOGGER.error(e.message, e)
                config = ClientCobblenavConfig()
            }

            config.save()

            return config
        }
    }

    val screenScale = 1f
    val sendErrorMessagesToChat = true
    val obscureUnknownPokemon = true
    val useSwimmingAnimationIfSubmerged = true
    val pokefinderOverlayOffset = 10
    val trackArrowYOffset = 80

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