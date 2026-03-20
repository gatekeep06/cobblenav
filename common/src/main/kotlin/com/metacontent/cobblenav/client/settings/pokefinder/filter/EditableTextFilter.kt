package com.metacontent.cobblenav.client.settings.pokefinder.filter

abstract class EditableTextFilter : RadarFilter {
    abstract fun update(value: String)

    abstract fun asString(): String
}