package com.metacontent.cobblenav.client.gui.util

import net.minecraft.util.FastColor

data class RGB(
    val r: Int,
    val g: Int,
    val b: Int
) {
    companion object {
        const val MAX_VALUE = 255
    }

    fun toColor(opacity: Int = MAX_VALUE) = FastColor.ARGB32.color(opacity, r, g, b)

    fun red() = r.toFloat() / MAX_VALUE

    fun green() = g.toFloat() / MAX_VALUE

    fun blue() = b.toFloat() / MAX_VALUE
}
