package com.metacontent.cobblenav.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.metacontent.cobblenav.Cobblenav
import java.io.File
import java.io.FileReader
import java.io.FileWriter

abstract class Config<T : Config<T>> {
    companion object {
        private const val PATH = "config/cobblenav"
        private val GSON: Gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        fun <C : Config<C>> load(clazz: Class<C>): C {
            val default = clazz.getConstructor().newInstance()

            val configFile = File("$PATH/${default.fileName}")
            configFile.parentFile.mkdirs()

            val config = runCatching {
                if (!configFile.exists()) {
                    configFile.createNewFile()
                }
                FileReader(configFile).use {
                    GSON.fromJson(it, clazz) ?: default
                }
            }.onFailure {
                Cobblenav.LOGGER.error(it.message, it)
            }.getOrDefault(default)

            config.applyToLoadedConfig(default)

            config.save()

            return config
        }
    }

    abstract val fileName: String

    fun save() {
        val configFile = File("$PATH/$fileName")
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

    protected open fun applyToLoadedConfig(default: T) {}
}