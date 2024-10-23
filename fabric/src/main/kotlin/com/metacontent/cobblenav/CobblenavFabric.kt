package com.metacontent.cobblenav

import com.metacontent.cobblenav.registry.CobblenavItems
import com.metacontent.cobblenav.util.cobblenavResource
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

class CobblenavFabric : ModInitializer, Implementation {
    override val networkManager = CobblenavFabricNetworkManager

    override fun onInitialize() {
        Cobblenav.init(this)
        networkManager.registerMessages()
        networkManager.registerServerHandlers()
    }

    override fun registerItems() {
        CobblenavItems.register { resourceLocation, item -> Registry.register(CobblenavItems.registry, resourceLocation, item) }
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            cobblenavResource("cobblenav"),
            FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.cobblenav.pokenav_group"))
                .icon { ItemStack(CobblenavItems.POKENAV) }
                .displayItems(CobblenavItems::addToGroup)
                .build()
        )
    }

    override fun registerCommands() {
    }
}