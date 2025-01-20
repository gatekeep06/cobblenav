package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.api.spawning.IntRanges
import com.cobblemon.mod.common.util.splitMap
import com.metacontent.cobblenav.Cobblenav
import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

class PokefinderSettings : Settings<PokefinderSettings>() {
    companion object {
        const val NAME = "pokefinder"
        const val DELIMITER = " "
        const val ASSIGNER = "="
        val SETTINGS = listOf("species", "shinyonly", "aspects")
        val CODEC: Codec<PokefinderSettings> = Codec.STRING.xmap(
            { parse(it, DELIMITER, ASSIGNER) },
            { it.asString() }
        )
        val BUF_CODEC: StreamCodec<ByteBuf, PokefinderSettings> = ByteBufCodecs.fromCodec(CODEC)
//        val CODEC: Codec<PokefinderSettings> = RecordCodecBuilder.create { instance ->
//            instance.group(
//                Codec.DOUBLE.fieldOf("radius").forGetter(PokefinderSettings::radius),
//                Codec.list(Codec.STRING).fieldOf("species").xmap(
//                    { list -> list?.toSet() },
//                    { set -> set?.toList() }
//                ).forGetter(PokefinderSettings::species),
//                Codec.BOOL.fieldOf("shinyOnly").forGetter(PokefinderSettings::shinyOnly),
//                Codec.list(Codec.STRING).fieldOf("aspects").xmap(
//                    { list -> list?.toSet() },
//                    { set -> set?.toList() }
//                ).forGetter(PokefinderSettings::aspects),
//                Codec.list(Codec.STRING.xmap(
//                    { string ->
//                        try {
//                            val split = string.split("..")
//                            if (split.size != 2) throw IllegalStateException()
//                            return@xmap IntRange(split[0].toInt(), split[1].toInt())
//                        }
//                        catch (e: Exception) {
//                            return@xmap null
//                        }
//                    },
//                    { range -> range?.toString() }
//                )).fieldOf("level").xmap(
//                    { list ->
//                        val ranges = IntRanges()
//                        list.forEach { if (it != null) ranges.ranges.add(it) }
//                        if (ranges.ranges.isEmpty()) return@xmap null
//                        return@xmap ranges
//                    },
//                    { ranges -> ranges.ranges as List<IntRange?>? }
//                ).forGetter(PokefinderSettings::level)
//            ).apply(instance) { radius, species, shinyOnly, aspects, level ->
//                val settings = PokefinderSettings()
//                settings.radius = radius
//                settings.species = species
//                return@apply settings
//            }
//        }
        fun parse(string: String, delimiter: String, assigner: String): PokefinderSettings {
            val settings = PokefinderSettings()
            val settingPairList = string.splitMap(delimiter, assigner)

            settings.species = matchingPair(settingPairList, "species")?.second
                ?.split(",", " ", ", ")
                ?.map { it.trim().lowercase() }
                ?.filter { it.isNotBlank() }
                ?.toSet()
            settings.shinyOnly = matchingPair(settingPairList, "shinyonly")?.second?.toBooleanStrictOrNull()
            Cobblenav.LOGGER.error(matchingPair(settingPairList, "shinyonly").toString())
            settings.aspects = matchingPair(settingPairList, "aspects")?.second
                ?.split(",", " ", ", ")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.toSet()

            return settings
        }

        private fun matchingPair(pairs: List<Pair<String, String?>>, key: String) = pairs.findLast { it.first == key }
    }

    @Transient
    override val name = NAME

//    var radius: Double? = null
//        private set
    var species: Set<String>? = null
        private set
    var shinyOnly: Boolean? = null
        private set
    var aspects: Set<String>? = null
        private set
//    var level: IntRanges? = null
//        private set

    fun merge(
//        radius: Double? = null,
        species: Set<String>? = null,
        shinyOnly: Boolean? = null,
        aspects: Set<String>? = null,
//        level: IntRanges? = null
    ) {
        changed = true
        species?.let { this.species = it }
        shinyOnly?.let { this.shinyOnly = it }
        aspects?.let { this.aspects = it }
    }

    fun apply(settings: PokefinderSettings) {
        changed = true
        species = settings.species
        shinyOnly = settings.shinyOnly
        aspects = settings.aspects
    }

    fun asString(): String {
        val pieces = mutableListOf<String>()
//        radius?.let { pieces.add("radius${ASSIGNER}${it}") }
        species?.let { pieces.add("species${ASSIGNER}${it.joinToString(separator = ",")}") }
        shinyOnly?.let { pieces.add("shinyOnly${ASSIGNER}${it}") }
        aspects?.let { pieces.add("aspects${ASSIGNER}${it.joinToString(separator = ",")}") }
//        level?.let { pieces.add("level${ASSIGNER}${it.ranges.joinToString(separator = ",") { range -> "${range.first}-${range.last}" }}") }
        return pieces.joinToString(DELIMITER)
    }
}