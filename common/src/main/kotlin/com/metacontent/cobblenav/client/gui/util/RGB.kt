package com.metacontent.cobblenav.client.gui.util

import net.minecraft.util.FastColor

data class RGB(
    val r: Int,
    val g: Int,
    val b: Int
) {
    fun toColor(opacity: Int = 255) = FastColor.ARGB32.color(opacity, r, g, b)
}
