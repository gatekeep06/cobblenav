package com.metacontent.cobblenav.client.gui.screen

import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialMenuState
import com.metacontent.cobblenav.client.gui.widget.radialmenu.RadialPopupMenu
import com.metacontent.cobblenav.networking.packet.server.RequestCataloguePacket
import com.metacontent.cobblenav.os.PokenavOS
import com.metacontent.cobblenav.spawndata.SpawnData
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor

class CatalogueScreen(
    os: PokenavOS
) : PokenavScreen(os, false, false, Component.literal("Catalogue")) {
    companion object {

    }

    override val color = FastColor.ARGB32.color(255, 242, 242, 242)

    override fun initScreen() {
        RadialPopupMenu(
            this,
            screenX + (WIDTH - RadialMenuState.MENU_DIAMETER) / 2,
            screenY + HEIGHT - HORIZONTAL_BORDER_DEPTH - RadialMenuState.MENU_DIAMETER / 2
        ).also { addUnblockableWidget(it) }

        RequestCataloguePacket().sendToServer()
    }

    fun receiveCatalogue(spawnDataList: List<SpawnData>) {
        player?.sendSystemMessage(Component.literal(spawnDataList.joinToString { it.result.getResultName().string }))
    }
}