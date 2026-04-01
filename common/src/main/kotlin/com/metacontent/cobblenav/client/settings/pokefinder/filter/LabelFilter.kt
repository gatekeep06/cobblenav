package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.CobblenavClient
import net.minecraft.resources.ResourceLocation

class LabelFilter(
    private var labels: List<String> = emptyList()
) : EditableTextFilter() {
    companion object {
        const val TYPE = "label"
    }

    override val type = TYPE

    override var dot: ResourceLocation? = null

    override fun test(pokemon: Pokemon): Boolean = pokemon.form.labels.containsAll(labels)

    override fun update(value: String) {
        labels = value.split(",").map(String::trim)
        CobblenavClient.pokefinderSettings?.changed = true
    }

    override fun asString(): String = labels.joinToString()
}