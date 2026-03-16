package com.metacontent.cobblenav.client.settings.pokefinder

import com.cobblemon.mod.common.pokemon.Pokemon
import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget

class TranslatedNameFilter(
    private var names: List<String>
) : EditableTextFilter() {
    companion object {
        const val TYPE = "name"
    }

    override val type = TYPE

    override val widget by lazy {
        TextFieldWidget(
            x = 0,
            y = 0,
            width = RadarFilter.WIDGET_WIDTH,
            height = RadarFilter.WIDGET_HEIGHT,
            default = names.joinToString(),
            textureSheet = FIELD,
            onChange = {}
        )
    }

    override fun test(pokemon: Pokemon): Boolean = names.any {
        it.equals(pokemon.getDisplayName().string, true)
    }

    override fun onFinishEditing() {
        names = widget.value.split(",").map(String::trim)
    }
}