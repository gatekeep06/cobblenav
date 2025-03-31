package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.util.enchantmentRegistry
import com.metacontent.cobblenav.networking.packet.client.FishingnavScreenInitDataPacket
import com.metacontent.cobblenav.networking.packet.server.RequestLocationScreenInitDataPacket
import com.metacontent.cobblenav.util.PreferencesSaver
import com.metacontent.cobblenav.util.WeightedBucket
import com.metacontent.cobblenav.util.savedPreferences
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments

object RequestFishingnavScreenInitDataHandler : ServerNetworkPacketHandler<RequestLocationScreenInitDataPacket> {
    override fun handle(packet: RequestLocationScreenInitDataPacket, server: MinecraftServer, player: ServerPlayer) {
        server.execute {
            val applyBuckets = player.savedPreferences().getBoolean(PreferencesSaver.APPLY_BUCKET_KEY)

            var pokeBall = ResourceLocation.withDefaultNamespace("air")
            var luckOfTheSeaLevel = 0
            var lineColor = ""
            var bait = ItemStack.EMPTY
            (player.fishing as? PokeRodFishingBobberEntity)?.let { bobber ->
                pokeBall = bobber.pokeRodId?.let { PokeRods.getPokeRod(it)?.pokeBallId } ?: pokeBall
                bobber.rodStack?.let { stack ->
                    luckOfTheSeaLevel = player.level().enchantmentRegistry.getHolder(Enchantments.LUCK_OF_THE_SEA).map {
                        EnchantmentHelper.getItemEnchantmentLevel(it, stack)
                    }.orElse(0)
                }
                lineColor = bobber.lineColor
                bait = bobber.bobberBait
            }

            // Hardcoded, because cobblemon is also hardcoded
            val baseValues = listOf(94.3F, 5.0F, 0.5F, 0.2F)
            val adjustments = listOf(-4.1F, 2.5F, 1.0F, 0.6F)

            val adjustedWeights = Cobblemon.bestSpawner.config.buckets.mapIndexed { index, bucket ->
                if (index >= baseValues.size) {
                    return@mapIndexed bucket to bucket.weight
                }
                val base = baseValues[index]
                val adjustment = adjustments[index]
                bucket to (base + adjustment * luckOfTheSeaLevel)
            }.toMap()

            val weightSum = adjustedWeights.values.sum()
            val buckets = adjustedWeights.map { WeightedBucket(it.key.name, it.value / weightSum) }

            FishingnavScreenInitDataPacket(buckets, applyBuckets, pokeBall, lineColor, bait).sendToPlayer(player)
        }
    }
}