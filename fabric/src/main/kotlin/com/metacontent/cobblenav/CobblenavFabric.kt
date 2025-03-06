package com.metacontent.cobblenav

import com.metacontent.cobblenav.CobblenavItems
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.brigadier.arguments.ArgumentType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.loot.v3.LootTableEvents
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.NestedLootTable
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import kotlin.reflect.KClass

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
        CommandRegistrationCallback.EVENT.register(CobblenavCommands::register)
    }

    override fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(
        identifier: ResourceLocation,
        argumentClass: KClass<A>,
        serializer: ArgumentTypeInfo<A, T>
    ) {
        ArgumentTypeRegistry.registerArgumentType(identifier, argumentClass.java, serializer)
    }

    override fun injectLootTables() {
        LootTableEvents.MODIFY.register { id, tableBuilder, source, provider ->
            if (id == BuiltInLootTables.FISHING_TREASURE) {
                val table = cobblenavResource("injection/${id.location().path}")
                val pool = LootPool.lootPool().add(
                    NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, table)).setWeight(1)
                ).setBonusRolls(UniformGenerator.between(0f, 1f))
                tableBuilder.withPool(pool)
            }
        }
    }
}