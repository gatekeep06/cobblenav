package com.metacontent.neoforge

import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.Implementation
import com.metacontent.cobblenav.registry.CobblenavItems
import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.registries.RegisterEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(Cobblenav.ID)
class CobblenavNeoForge : Implementation {
    override val networkManager = CobblenavNeoForgeNetworkManager

    init {
        Cobblenav.init(this)
        with(MOD_BUS) {
            addListener(networkManager::registerMessages)
        }
    }

    override fun registerItems() {
        with(MOD_BUS) {
            addListener<RegisterEvent> { event ->
                event.register(CobblenavItems.resourceKey) { helper ->
                    CobblenavItems.register { resourceLocation, item -> helper.register(resourceLocation, item) }
                }
            }
            addListener<RegisterEvent> { event ->
                event.register(Registries.CREATIVE_MODE_TAB) { helper ->
                    helper.register(
                        ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), cobblenavResource("com/metacontent/cobblenav")),
                        CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.cobblenav"))
                            .icon { ItemStack(CobblenavItems.POKENAV) }
                            .displayItems(CobblenavItems::addToGroup)
                            .build()
                    )
                }
            }
        }
    }

    override fun registerCommands() {
    }
}