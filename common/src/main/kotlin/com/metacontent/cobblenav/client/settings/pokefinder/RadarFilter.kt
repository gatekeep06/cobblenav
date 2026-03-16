package com.metacontent.cobblenav.client.settings.pokefinder

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.gui.components.AbstractWidget

interface RadarFilter<T : AbstractWidget> {
    companion object {
        const val WIDGET_WIDTH = 218
        const val WIDGET_HEIGHT = 26
    }

    val type: String

    val widget: T

    fun test(pokemon: Pokemon): Boolean

    fun onFinishEditing()
}