package com.metacontent.cobblenav.neoforge

import com.metacontent.cobblenav.*
import com.metacontent.cobblenav.neoforge.client.CobblenavNeoForgeClient
import com.metacontent.cobblenav.util.ModDependency
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.commands.synchronization.ArgumentTypeInfos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.village.WandererTradesEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.RegisterEvent
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import kotlin.reflect.KClass

@Mod(Cobblenav.ID)
class CobblenavNeoForge : Implementation {
    private val commandArgumentTypes = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Cobblenav.ID)
    override val networkManager = CobblenavNeoForgeNetworkManager

    init {
        with(MOD_BUS) {
            this@CobblenavNeoForge.commandArgumentTypes.register(this)
            Cobblenav.init(this@CobblenavNeoForge)
            addListener(networkManager::registerMessages)
        }
        with(NeoForge.EVENT_BUS) {
            addListener(::onWanderingTraderRegistry)
        }
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CobblenavNeoForgeClient.init()
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
                        ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), cobblenavResource("cobblenav")),
                        CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.cobblenav.pokenav_group"))
                            .icon { ItemStack(CobblenavItems.POKENAV) }
                            .displayItems(CobblenavItems::addToGroup)
                            .build()
                    )
                }
            }
        }
    }

    override fun registerCommands() {
        with(NeoForge.EVENT_BUS) {
            addListener<RegisterCommandsEvent> { event ->
                CobblenavCommands.register(event.dispatcher, event.buildContext, event.commandSelection)
            }
        }
    }

    override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(
        identifier: ResourceLocation,
        argumentClass: KClass<A>,
        serializer: ArgumentTypeInfo<A, T>
    ) {
        commandArgumentTypes.register(identifier.path) { _ ->
            ArgumentTypeInfos.registerByClass(argumentClass.java, serializer)
        }
    }

    override fun injectLootTables() {
        with(NeoForge.EVENT_BUS) {
            addListener<LootTableLoadEvent> { event ->
                CobblenavLootInjector.inject(event.name) { builder -> event.table.addPool(builder.build()) }
            }
        }
    }

    fun onWanderingTraderRegistry(event: WandererTradesEvent) {
        event.rareTrades.addAll(Cobblenav.resolveWandererTrades())
    }

    override fun isModInstalled(mod: ModDependency): Boolean {
        return ModList
            .get()
            .getModContainerById(mod.id)
            .map { it.modInfo.version.compareTo(DefaultArtifactVersion(mod.version)) }
            .orElse(-1) >= 0
    }
}