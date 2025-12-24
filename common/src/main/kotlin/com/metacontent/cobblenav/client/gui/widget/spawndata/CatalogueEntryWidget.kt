package com.metacontent.cobblenav.client.gui.widget.spawndata

import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class CatalogueEntryWidget(
    x: Int = 0,
    y: Int = 0,
    val spawnData: SpawnData
) : SoundlessWidget(x, y, WIDTH, HEIGHT, Component.literal("${spawnData.result.getResultName()} Entry")) {
    companion object {
        const val WIDTH = 100
        const val HEIGHT = 60
    }

    override fun renderWidget(guiGraphics: GuiGraphics, i: Int, j: Int, f: Float) {

    }
}