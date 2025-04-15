package com.metacontent.cobblenav.command

import com.cobblemon.mod.common.Cobblemon
import com.metacontent.cobblenav.api.contact.title.TrainerTitle
import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import com.metacontent.cobblenav.command.argument.TrainerTitleArgument
import com.metacontent.cobblenav.storage.ProfilePlayerData
import com.metacontent.cobblenav.util.getProfileData
import com.metacontent.cobblenav.util.join
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component

object TitleCommand : Command {
    private const val BASE = "trainertitle"
    private const val LIST = "list"
    private const val GRANT = "grant"
    private const val REMOVE = "remove"
    private const val ALL = "all"

    private const val PLAYER_ARGUMENT = "player"
    private const val PLAYERS_ARGUMENT = "players"
    private const val TITLE_ARGUMENT = "title"

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val command = literal(BASE)
        val commandList = literal(LIST)
            .then(literal(ALL).executes(::executeListAll))
            .then(argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(::executeListForPlayer))
        val commandGrant = literal(GRANT)
            .then(
                argument(PLAYERS_ARGUMENT, EntityArgument.players())
                    .then(argument(TITLE_ARGUMENT, TrainerTitleArgument.title()).executes(::executeGrant))
                    .then(literal(ALL).executes(::executeGrantAll))
            )
        val commandRemove = literal(REMOVE)
            .then(
                argument(PLAYERS_ARGUMENT, EntityArgument.players())
                    .then(argument(TITLE_ARGUMENT, TrainerTitleArgument.title()).executes(::executeRemove))
                    .then(literal(ALL).executes(::executeRemoveAll))
            )

        command
            .requires { it.player?.hasPermissions(1) == true }
            .then(commandList)
            .then(commandGrant)
            .then(commandRemove)

        dispatcher.register(command)
    }

    private fun executeListAll(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source.playerOrException
        val titles = TrainerTitles.getAll().joinToString { it.id.toString() }
        source.sendSystemMessage(Component.literal(titles))
        return 1
    }

    private fun executeListForPlayer(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source.playerOrException
        val player = EntityArgument.getPlayer(context, PLAYER_ARGUMENT)
        val granted = Cobblemon.playerDataManager.getProfileData(player).grantedTitles
        val titles = TrainerTitles.getAllowed(granted).joinToString { it.id.toString() }
        source.sendSystemMessage(Component.literal(titles))
        return 1
    }

    private fun executeGrant(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYERS_ARGUMENT)
        val title = TrainerTitleArgument.getTitle(context, TITLE_ARGUMENT)
        players.forEach {
            ProfilePlayerData.executeAndSafe(it) { data ->
                if (data.grantTitle(title.id)) {
                    it.sendSystemMessage(Component.translatable("message.cobblenav.title_granted").append(title.name()))
                    return@executeAndSafe true
                }
                return@executeAndSafe false
            }
        }
        return 1
    }

    private fun executeGrantAll(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYERS_ARGUMENT)
        val strictUseTitles = TrainerTitles.getStrict()
        players.forEach {
            ProfilePlayerData.executeAndSafe(it) { data ->
                val addedTitles = strictUseTitles.filter { title -> data.grantTitle(title.id, false) }
                if (addedTitles.isNotEmpty()) {
                    data.onTitleListUpdated()
                    val message = addedTitles.map(TrainerTitle::name).join(Component.translatable("message.cobblenav.title_granted"))
                    it.sendSystemMessage(message)
                    return@executeAndSafe true
                }
                return@executeAndSafe false
            }
        }
        return 1
    }

    private fun executeRemove(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYERS_ARGUMENT)
        val title = TrainerTitleArgument.getTitle(context, TITLE_ARGUMENT)
        players.forEach {
            ProfilePlayerData.executeAndSafe(it) { data ->
                if (data.removeTitle(title.id)) {
                    it.sendSystemMessage(Component.translatable("message.cobblenav.title_removed").append(title.name()))
                    return@executeAndSafe true
                }
                return@executeAndSafe false
            }
        }
        return 1
    }

    private fun executeRemoveAll(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYERS_ARGUMENT)
        players.forEach {
            ProfilePlayerData.executeAndSafe(it) { data ->
                return@executeAndSafe if (data.grantedTitles.isNotEmpty()) {
                    val message = data.grantedTitles.mapNotNull {
                            id -> TrainerTitles.getTitle(id)?.name()
                    }.join(Component.translatable("message.cobblenav.title_removed"))
                    data.clearTitles()
                    it.sendSystemMessage(message)
                    true
                }
                else {
                    false
                }
            }
        }
        return 1
    }
}