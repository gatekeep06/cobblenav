package com.metacontent.cobblenav.command

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.text.red
import com.metacontent.cobblenav.api.contact.ContactType
import com.metacontent.cobblenav.api.contact.PokenavContact
import com.metacontent.cobblenav.storage.ContactPlayerData
import com.metacontent.cobblenav.util.getContactData
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component

object ContactCommand : Command {
    private const val BASE = "navcontacts"
    private const val LIST = "list"
    private const val CHECK = "check"
    private const val BY_NAME = "byname"
    private const val ADD = "add"
    private const val REMOVE = "remove"
    private const val CLEAR = "clear"

    private const val PLAYER_ARGUMENT = "player"
    private const val PLAYERS_ARGUMENT = "players"
    private const val CONTACT_PLAYER_ARGUMENT = "contact"
    private const val CONTACT_NAME_ARGUMENT = "contact"

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val command = literal(BASE)
        val commandList = literal(LIST)
            .then(argument(PLAYER_ARGUMENT, EntityArgument.player()).executes(::executeList))
        val commandCheck = literal(CHECK)
            .then(
                argument(PLAYER_ARGUMENT, EntityArgument.player())
                    .then(argument(CONTACT_PLAYER_ARGUMENT, EntityArgument.player()).executes(::executeCheck))
                    .then(
                        literal(BY_NAME).then(
                            argument(CONTACT_NAME_ARGUMENT, StringArgumentType.word()).executes(::executeCheckByName)
                        )
                    )
            )
        val commandAdd = literal(ADD)
            .then(
                argument(PLAYERS_ARGUMENT, EntityArgument.players())
                    .then(argument(CONTACT_PLAYER_ARGUMENT, EntityArgument.player()).executes(::executeAdd))
            )
        val commandRemove = literal(REMOVE)
            .then(
                argument(PLAYERS_ARGUMENT, EntityArgument.players())
                    .then(argument(CONTACT_PLAYER_ARGUMENT, EntityArgument.player()).executes(::executeRemove))
                    .then(
                        literal(BY_NAME).then(
                            argument(CONTACT_NAME_ARGUMENT, StringArgumentType.word()).executes(::executeRemoveByName)
                        )
                    )
            )
        val commandClear = literal(CLEAR)
            .then(argument(PLAYERS_ARGUMENT, EntityArgument.players()).executes(::executeClear))

        command
            .requires { it.player?.hasPermissions(1) == true }
            .then(commandList)
            .then(commandCheck)
            .then(commandAdd)
            .then(commandRemove)
            .then(commandClear)

        dispatcher.register(command)
    }

    private fun executeList(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source.playerOrException
        val player = EntityArgument.getPlayer(context, PLAYER_ARGUMENT)
        val data = Cobblemon.playerDataManager.getContactData(player)
        val names = data.contacts.values.joinToString { it.name }
        if (names.isNotBlank()) source.sendSystemMessage(Component.literal(names))
        return 1
    }

    private fun executeCheck(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source.playerOrException
        val player = EntityArgument.getPlayer(context, PLAYER_ARGUMENT)
        val contactUuid = EntityArgument.getPlayer(context, CONTACT_PLAYER_ARGUMENT).uuid
        val data = Cobblemon.playerDataManager.getContactData(player)
        data.find(contactUuid)?.let {
            source.sendSystemMessage(Component.literal(it.summarizeBattles()))
            return 1
        }
        source.sendSystemMessage(Component.translatable("message.cobblenav.contact_not_found").red())
        return 0
    }

    private fun executeCheckByName(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source.playerOrException
        val player = EntityArgument.getPlayer(context, PLAYER_ARGUMENT)
        val name = StringArgumentType.getString(context, CONTACT_NAME_ARGUMENT)
        val contacts = Cobblemon.playerDataManager.getContactData(player).findByName(name)
        if (contacts.isNotEmpty()) {
            contacts.forEach {
                source.sendSystemMessage(Component.literal(it.summarizeBattles()))
            }
            return 1
        }
        source.sendSystemMessage(Component.translatable("message.cobblenav.contact_not_found").red())
        return 0
    }

    private fun executeAdd(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYERS_ARGUMENT)
        val contactPlayer = EntityArgument.getPlayer(context, CONTACT_PLAYER_ARGUMENT)
        players.forEach {
            val contact = PokenavContact(
                id = contactPlayer.uuid.toString(),
                type = ContactType.PLAYER,
                name = contactPlayer.name.string,
                battles = hashMapOf()
            )
            ContactPlayerData.executeAndSave(it) { data -> data.updateContacts(contact) }
        }
        return 1
    }

    private fun executeRemove(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYERS_ARGUMENT)
        val contactUuid = EntityArgument.getPlayer(context, CONTACT_PLAYER_ARGUMENT).uuid.toString()
        players.forEach {
            ContactPlayerData.executeAndSave(it) { data -> data.removeContact(contactUuid) }
        }
        return 1
    }

    private fun executeRemoveByName(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYER_ARGUMENT)
        val name = StringArgumentType.getString(context, CONTACT_NAME_ARGUMENT)
        players.forEach { player ->
            ContactPlayerData.executeAndSave(player) { data ->
                val contacts = data.findByName(name)
                return@executeAndSave if (contacts.isNotEmpty()) {
                    contacts.map { data.removeContact(it.id) }.any { it }
                } else {
                    false
                }
            }
        }
        return 1
    }

    private fun executeClear(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYERS_ARGUMENT)
        players.forEach {
            ContactPlayerData.executeAndSave(it) { data ->
                return@executeAndSave if (data.contacts.isNotEmpty()) {
                    data.clearContacts()
                    true
                } else {
                    false
                }
            }
        }
        return 1
    }
}