package com.metacontent.cobblenav.command

import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*

object TitleCommand : Command {
    private const val BASE = "trainertitle"
    private const val LIST = "list"

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val command = literal(BASE)
        val commandList = literal(LIST)
            .then(literal("all").executes(::executeListAll))

        command
            .requires { it.player?.hasPermissions(1) == true }
            .then(commandList)

        dispatcher.register(command)
    }

    private fun executeListAll(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException
        TrainerTitles.getAll().forEach {
            player.sendSystemMessage(it.value.name())
        }
        return 1
    }
}