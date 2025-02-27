package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.os.PokenavOS
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class FishingnavScreen(
    os: PokenavOS
) : PokenavScreen(os, true, true, Component.literal("Fishing")) {
    override val color = FastColor.ARGB32.color(255, 117, 230, 218)

    override fun initScreen() {

    }
}