package com.metacontent.cobblenav

import com.metacontent.cobblenav.util.cobblenavResource
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.BuiltInLootTables
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.NestedLootTable
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator

object CobblenavLootInjector {
    private val injectableTables = setOf(
        BuiltInLootTables.FISHING_TREASURE.location()
    )

    fun inject(id: ResourceLocation, consumer: (LootPool.Builder) -> Unit) {
        if (!injectableTables.contains(id)) return
        val table = cobblenavResource("injection/${id.path}")
        val pool = LootPool.lootPool().add(
            NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, table)).setWeight(1)
        ).setBonusRolls(UniformGenerator.between(0f, 1f))
        consumer(pool)
    }
}