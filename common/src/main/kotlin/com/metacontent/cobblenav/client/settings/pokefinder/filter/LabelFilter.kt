package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.CobblenavClient
import net.minecraft.util.StringUtil.isBlank

class LabelFilter(
    private var labels: List<String> = emptyList()
) : EditableTextFilter() {
    companion object {
        const val TYPE = "label"
    }

    override val type = TYPE

    override fun test(pokemon: Pokemon): Boolean = labels.isEmpty() || labels.all { label ->
        pokemon.form.labels.any { it.equals(label, true) }
    }

    override fun update(value: String) {
        labels = value.split(",").mapNotNull { it.trim().takeUnless(::isBlank) }
        CobblenavClient.pokefinderSettings?.changed = true
    }

    override fun asString(): String = labels.joinToString()

    override fun clear() {
        labels = emptyList()
    }
}