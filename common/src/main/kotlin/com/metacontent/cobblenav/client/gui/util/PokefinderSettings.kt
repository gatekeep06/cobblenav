package com.metacontent.cobblenav.client.gui.util

import com.cobblemon.mod.common.api.spawning.IntRanges
import com.cobblemon.mod.common.util.fromJson
import com.cobblemon.mod.common.util.splitMap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.metacontent.cobblenav.Cobblenav
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class PokefinderSettings {
    companion object {
        const val MAX_RADIUS: Double = 200.0
        const val PATH = "cobblenav/settings/pokefinder.json"
        const val DELIMITER = " "
        const val ASSIGNER = "="
        val SETTINGS = listOf("radius", "species", "shinyOnly", "aspects", "level")
        val GSON: Gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create()
        val CODEC: Codec<PokefinderSettings> = Codec.STRING.xmap(
            { parse(it, DELIMITER, ASSIGNER) },
            { it.asString() }
        )
        val BUF_CODEC: StreamCodec<ByteBuf, PokefinderSettings> = ByteBufCodecs.fromCodec(CODEC)

        fun get(): PokefinderSettings {
            val file = File(PATH)
            var settings: PokefinderSettings
            try {
                val fileReader = FileReader(file)
                settings = CODEC.parse(JsonOps.INSTANCE, GSON.fromJson(fileReader)).orThrow
                fileReader.close()
            }
            catch (e: Exception) {
                settings = PokefinderSettings()
                settings.changed = true
            }

            return settings
        }

        fun parse(string: String, delimiter: String, assigner: String): PokefinderSettings {
            val settings = PokefinderSettings()
            val settingPairList = string.splitMap(delimiter, assigner)

            settings.radius = matchingPair(settingPairList, "radius")?.second?.toDouble()
            settings.species = matchingPair(settingPairList, "species")?.second
                ?.split(",", " ", ", ")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.toSet()
            settings.shinyOnly = matchingPair(settingPairList, "shinyOnly")?.second?.toBooleanStrictOrNull()
            settings.aspects = matchingPair(settingPairList, "aspects")?.second
                ?.split(",", " ", ", ")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.toSet()
            matchingPair(settingPairList, "level")?.second
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.map {
                    val range = it.split("-", limit = 2)
                    IntRange(range[0].toInt(), range[1].toInt())
                }?.let {
                    settings.level = IntRanges().also { ranges -> ranges.ranges.addAll(it) }
                }
            return settings
        }

        private fun matchingPair(pairs: List<Pair<String, String?>>, key: String) = pairs.findLast { it.first == key }
    }

    @Transient
    var changed = false

    var radius: Double? = null
    var species: Set<String>? = null
    var shinyOnly: Boolean? = null
    var aspects: Set<String>? = null
    var level: IntRanges? = null

    fun merge(settings: PokefinderSettings) {
        changed = true

        radius = settings.radius ?: radius
        species = settings.species ?: species
        shinyOnly = settings.shinyOnly ?: shinyOnly
        aspects = settings.aspects ?: aspects
        level = settings.level ?: level
    }

    fun save() {
        val file = File(PATH)
        file.parentFile.mkdirs()
        if (!file.exists()) file.createNewFile()
        try {
            val fileWriter = FileWriter(file)
            GSON.toJson(CODEC.encodeStart(JsonOps.INSTANCE, this).orThrow, fileWriter)
            fileWriter.close()
        }
        catch (e: Exception) {
            Cobblenav.LOGGER.error(e.message, e)
        }
    }

    fun asString(): String {
        val pieces = mutableListOf<String>()
        radius?.let { pieces.add("radius${ASSIGNER}${it}") }
        species?.let { pieces.add("species${ASSIGNER}${it.joinToString(separator = ",")}") }
        shinyOnly?.let { pieces.add("shinyOnly${ASSIGNER}${it}") }
        aspects?.let { pieces.add("aspects${ASSIGNER}${it.joinToString(separator = ",")}") }
        level?.let { pieces.add("level${ASSIGNER}${it.ranges.joinToString(separator = ",") { range -> "${range.first}-${range.last}" }}") }
        return pieces.joinToString(DELIMITER)
    }
}
