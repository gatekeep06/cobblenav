package com.metacontent.cobblenav.networking.handler.server

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.api.spawning.CobblemonWorldSpawnerManager
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.api.spawning.WorldSlice
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail
import com.cobblemon.mod.common.api.spawning.spawner.SpawningArea
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.networking.packet.client.SpawnMapPacket
import com.metacontent.cobblenav.networking.packet.server.RequestSpawnMapPacket
import com.metacontent.cobblenav.util.SpawnData
import com.metacontent.cobblenav.util.SpawnDataHelper
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
                val spawner = CobblemonWorldSpawnerManager.spawnersForPlayers[player.uuid] ?: throw NullPointerException("For some reason player spawner is null")
                val bucket = Cobblemon.bestSpawner.config.buckets.firstOrNull { it.name == packet.bucket } ?: throw NullPointerException("For some reason bucket is null")

                val cause = SpawnCause(spawner, bucket, spawner.getCauseEntity())
                val slice: WorldSlice
                try {
                    slice = spawner.prospector.prospect(spawner, SpawningArea(
                        cause, player.serverLevel(),
                        ceil(player.x - config.checkSpawnWidth / 2f).toInt(),
                        ceil(player.y - config.checkSpawnHeight / 2f).toInt(),
                        ceil(player.z - config.checkSpawnWidth / 2f).toInt(),
                        config.checkSpawnWidth,
                        config.checkSpawnHeight,
                        config.checkSpawnWidth
                    ))
                }
                catch (e: IllegalStateException) {
                    SpawnMapPacket(packet.bucket, emptyList()).sendToPlayer(player)
                    return@execute
                }

                val contexts = Cobblenav.contextResolver.resolve(spawner, spawner.contextCalculators, slice)
                val spawnProbabilities = spawner.getSpawningSelector().getProbabilities(spawner, contexts)

                spawnProbabilities.forEach { (detail, spawnChance) ->
                    if (detail is PokemonSpawnDetail && detail.isValid()) {
                        SpawnDataHelper.collect(detail, spawnChance, contexts, player)?.let { spawnDataList.add(it) }
                    }
                }
            }
            SpawnMapPacket(packet.bucket, spawnDataList).sendToPlayer(player)
        }
    }
}