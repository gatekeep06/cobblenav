package com.metacontent.cobblenav.client.settings.pokefinder

import com.metacontent.cobblenav.client.gui.widget.TextFieldWidget
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.util.FastColor

abstract class EditableTextFilter() : RadarFilter<TextFieldWidget> {
    companion object {
        @JvmStatic
        val COLOR = FastColor.ARGB32.color(255, 1, 235, 95)
        val FIELD = cobblenavResource("pokefinder/text")
    }
}