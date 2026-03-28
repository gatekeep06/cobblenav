package com.metacontent.cobblenav.properties

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.pokemon.aspect.AspectProvider
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature
import com.cobblemon.mod.common.api.pokemon.feature.SynchronizedSpeciesFeatureProvider
import com.cobblemon.mod.common.api.properties.CustomPokemonPropertyType
import com.cobblemon.mod.common.client.gui.summary.featurerenderers.SummarySpeciesFeatureRenderer
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.readString
import com.cobblemon.mod.common.util.writeString
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf

object BucketSpeciesFeatureProvider : SynchronizedSpeciesFeatureProvider<StringSpeciesFeature>,
    CustomPokemonPropertyType<StringSpeciesFeature>, AspectProvider {
    override var keys = listOf("spawn_bucket")
    override var needsKey = true
    override var visible = false

    override fun fromString(value: String?): StringSpeciesFeature? {
        return value?.lowercase()?.let { StringSpeciesFeature(keys.first(), it) }
    }

    override fun examples(): Collection<String> {
        return emptyList()
    }

    override fun invoke(
        buffer: RegistryFriendlyByteBuf,
        name: String
    ): StringSpeciesFeature? {
        return if (name in keys) {
            StringSpeciesFeature(name, "").also { it.loadFromBuffer(buffer) }
        } else {
            null
        }
    }

    override fun get(pokemon: Pokemon): StringSpeciesFeature? {
        return pokemon.features.filterIsInstance<StringSpeciesFeature>().find { it.name in keys }
    }

    override fun getRenderer(pokemon: Pokemon): SummarySpeciesFeatureRenderer<StringSpeciesFeature>? {
        return null
    }

    override fun invoke(pokemon: Pokemon): StringSpeciesFeature {
        return get(pokemon) ?: StringSpeciesFeature(keys.first(), "")
    }

    override fun invoke(nbt: CompoundTag): StringSpeciesFeature? {
        val key = keys.find { nbt.contains(it) }
        if (key == null) return null
        return StringSpeciesFeature(key, "").also { it.loadFromNBT(nbt) }
    }

    override fun invoke(json: JsonObject): StringSpeciesFeature? {
        val key = keys.find { json.has(it) }
        if (key == null) return null
        return StringSpeciesFeature(key, "").also { it.loadFromJSON(json) }
    }

    override fun saveToBuffer(buffer: RegistryFriendlyByteBuf, toClient: Boolean) {
        buffer.writeCollection(keys) { _, value -> buffer.writeString(value) }
        buffer.writeBoolean(needsKey)
    }

    override fun loadFromBuffer(buffer: RegistryFriendlyByteBuf) {
        keys = buffer.readList { buffer.readString() }
        needsKey = buffer.readBoolean()
    }

    override fun provide(pokemon: Pokemon): Set<String> {
        return get(pokemon)?.let { setOf(it.toAspect()) } ?: emptySet()
    }

    override fun provide(properties: PokemonProperties): Set<String> {
        return properties.customProperties.filterIsInstance<StringSpeciesFeature>()
            .find { it.name in keys }
            ?.let {
                setOf(it.toAspect())
            } ?: emptySet()
    }

    private fun StringSpeciesFeature.toAspect(): String = "bucket-${this.value}"
}