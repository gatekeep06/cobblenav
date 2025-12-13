package com.metacontent.cobblenav.command

import com.cobblemon.mod.common.api.spawning.BestSpawner
import com.cobblemon.mod.common.api.spawning.SpawnCause
import com.cobblemon.mod.common.util.spawner
import com.metacontent.cobblenav.Cobblenav
import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object TestSpawnsCommand : Command {
    const val BUCKET = "bucket"

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("testspawns")
                .requires { it.player?.hasPermissions(1) == true }
                .executes(::run)
        )
    }

    fun run(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException
        val checkedSpawns = BestSpawner.config.buckets.associateWith { b ->
            SpawnDataHelper.checkPlayerSpawns(player, b.name).map { it.data.id }.toMutableList()
        }

        checkedSpawns.forEach { (bucket, spawns) ->
            player.sendSystemMessage(Component.literal("${bucket.name}: ${spawns.joinToString()}"))
        }

        val cause = SpawnCause(player.spawner, player)
        val zoneInput = player.spawner.getZoneInput(cause) ?: return 0
        while (checkedSpawns.any { it.value.isNotEmpty() }) {
            val actions = player.spawner.calculateSpawnActionsForArea(zoneInput, 100)
            actions.forEach { action ->
                Cobblenav.LOGGER.error(action.detail.id)
                checkedSpawns[action.bucket]?.remove(action.detail.id)?.let { if (it) Cobblenav.LOGGER.error("- ${action.detail.id}") }
            }
        }

        return 1
    }
}