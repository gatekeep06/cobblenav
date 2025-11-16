package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.position.calculators.SpawnablePositionCalculator
import com.cobblemon.mod.common.api.spawning.spawner.SpawningZoneInput
import com.cobblemon.mod.common.util.spawner
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.networking.packet.client.SpawnMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestSpawnMapPacket
import com.metacontent.cobblenav.spawndata.SpawnData
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import kotlin.math.ceil

object RequestSpawnMapHandler : ServerNetworkPacketHandler<RequestSpawnMapPacket> {
    override fun handle(
        packet: RequestSpawnMapPacket,
        server: MinecraftServer,
        player: ServerPlayer
    ) {
        val cobblemonConfig = Cobblemon.config
        val config = Cobblenav.config
        val spawnDataList = mutableListOf<SpawnData>()

        server.execute {
            if (cobblemonConfig.enableSpawning) {
                val spawner = player.spawner
                val bucket = Cobblemon.bestSpawner.config.buckets.firstOrNull { it.name == packet.bucket } ?: run {
                    Cobblenav.LOGGER.error("For some reason bucket is null")
                    SpawnMapPacket(packet.bucket, emptyList()).sendToPlayer(player)
                    return@execute
                }

                val cause = SpawnCause(spawner, player)
                val zone = Cobblemon.spawningZoneGenerator.generate(
                    spawner = spawner,
                    input = SpawningZoneInput(
                        cause, player.serverLevel(),
                        ceil(player.x - config.checkSpawnWidth / 2f).toInt(),
                        ceil(player.y - config.checkSpawnHeight / 2f).toInt(),
                        ceil(player.z - config.checkSpawnWidth / 2f).toInt(),
                        config.checkSpawnWidth,
                        config.checkSpawnHeight,
                        config.checkSpawnWidth
                    )
                ) //?: run {
//                    SpawnMapPacket(packet.bucket, emptyList()).sendToPlayer(player)
//                    return@execute
//                }

                val spawnablePositions = Cobblenav.resolver.resolve(
                    spawner = spawner,
                    spawnablePositionCalculators = SpawnablePositionCalculator.prioritizedAreaCalculators,
                    zone = zone
                )
                val spawnProbabilities = spawner.selector.getProbabilities(spawner, bucket, spawnablePositions)

                spawnProbabilities.forEach { (detail, spawnChance) ->
                    if (detail is PokemonSpawnDetail && detail.isValid()) {
                        SpawnDataHelper.collect(detail, spawnChance, spawnablePositions, player)
                            ?.let { spawnDataList.add(it) }
                    }
                }
            }
            SpawnMapPacket(packet.bucket, spawnDataList).sendToPlayer(player)
        }
    }
}