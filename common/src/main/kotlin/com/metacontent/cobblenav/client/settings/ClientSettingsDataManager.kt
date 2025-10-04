package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.adapters.IntRangeAdapter
import com.cobblemon.mod.common.util.adapters.PokemonPropertiesAdapter
import com.google.gson.GsonBuilder
import com.metacontent.cobblenav.Cobblenav
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object ClientSettingsDataManager {
    const val DIRECTORY = "cobblenav/settings/"
    val GSON = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .registerTypeAdapter(ResourceLocation::class.java, IdentifierAdapter)
        .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
        .registerTypeAdapter(PokemonProperties::class.java, PokemonPropertiesAdapter(false))
        .create()

    private fun String.toPath() = "$DIRECTORY$this.json"

    fun <T : Settings<T>> save(settings: T) {
        val file = File(settings.name.toPath())
        try {
            val fileWriter = FileWriter(file)
            GSON.toJson(settings, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
        }
    }

    fun <T : Settings<T>> load(name: String, clazz: Class<T>): Settings<T> {
        val file = File(name.toPath())
        try {
            file.parentFile.mkdirs()
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileReader = FileReader(file)
            return GSON.fromJson(fileReader, clazz) ?: clazz.getConstructor().newInstance()
        } catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
            return clazz.getConstructor().newInstance()
        }
    }
}