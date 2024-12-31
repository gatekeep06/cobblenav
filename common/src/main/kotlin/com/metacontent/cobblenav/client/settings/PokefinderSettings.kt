package com.metacontent.cobblenav.client.settings

import com.cobblemon.mod.common.api.spawning.IntRanges
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

class PokefinderSettings : Settings<PokefinderSettings>() {
    companion object {
        const val NAME = "pokefinder"
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
    }

//    override val codec: Codec<Settings<PokefinderSettings>> = CODEC

    @Transient
    override val name = NAME

    var radius: Double? = null
        private set
    var species: Set<String>? = null
        private set
    var shinyOnly: Boolean? = null
        private set
    var aspects: Set<String>? = null
        private set
    var level: IntRanges? = null
        private set

    fun apply(
        radius: Double? = null,
        species: Set<String>? = null,
        shinyOnly: Boolean? = null,
        aspects: Set<String>? = null,
        level: IntRanges? = null
    ) {
        changed = true
        radius?.let { this.radius = it }
        species?.let { this.species = it }
        shinyOnly?.let { this.shinyOnly = it }
        aspects?.let { this.aspects = it }
        level?.let { this.level = it }
    }
}