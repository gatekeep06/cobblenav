package com.metacontent.cobblenav.client.settings.pokefinder.filter

import com.metacontent.cobblenav.client.gui.util.gui

abstract class EditableTextFilter : RadarFilter {
    companion object {
        val FIELD = gui("pokefinder/text")
    }

    abstract fun update(value: String)

    abstract fun asString(): String
}