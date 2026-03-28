package com.metacontent.cobblenav.command

import com.metacontent.cobblenav.spawndata.SpawnDataHelper
import com.metacontent.cobblenav.storage.SpawnDataCatalogue
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.argument
import net.minecraft.commands.Commands.literal
import net.minecraft.commands.arguments.EntityArgument

object CatalogueCommand : PokenavCommand() {
    const val CATALOGUE = "catalogue"
    const val PLAYER = "player"
    const val GRANT = "grant"
    const val REVOKE = "revoke"
    const val ID = "id"
    const val SPECIES = "species"
    const val ALL = "all"

    override fun afterBase(): ArgumentBuilder<CommandSourceStack, *> {
        val grant = literal(GRANT)
            .then(literal(ID).then(argument(ID, StringArgumentType.word()).executes(::grantById)))
            .then(literal(SPECIES).then(argument(SPECIES, StringArgumentType.word()).executes(::grantByPokemon)))
            .then(literal(ALL).executes(::grantAll))

        val revoke = literal(REVOKE)
            .then(literal(ID).then(argument(ID, StringArgumentType.word()).executes(::revokeById)))
            .then(literal(SPECIES).then(argument(SPECIES, StringArgumentType.word()).executes(::revokeByPokemon)))
            .then(literal(ALL).executes(::revokeAll))

        return literal(CATALOGUE).then(
            argument(PLAYER, EntityArgument.player())
                .then(grant)
                .then(revoke)
        )
    }

    private fun grantById(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYER)
        val id = StringArgumentType.getString(context, ID)
        players.forEach { player ->
            SpawnDataCatalogue.executeAndSave(player) {
                SpawnDataHelper.spawnDetailIds.contains(id) && it.catalogue(id)
            }
        }
        return Command.SINGLE_SUCCESS
    }

    private fun grantByPokemon(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYER)
        val species = StringArgumentType.getString(context, SPECIES)
        players.forEach { player ->
            SpawnDataCatalogue.executeAndSave(player) { data ->
                var shouldSave = false
                SpawnDataHelper.spawnDetailIdBySpecies[species]?.let { ids ->
                    ids.forEach { id ->
                        data.catalogue(id).also {
                            if (it && !shouldSave) {
                                shouldSave = true
                            }
                        }
                    }
                }
                return@executeAndSave shouldSave
            }
        }
        return Command.SINGLE_SUCCESS
    }

    private fun grantAll(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYER)
        players.forEach { player ->
            SpawnDataCatalogue.executeAndSave(player) { data ->
                var shouldSave = false
                SpawnDataHelper.spawnDetailIds.forEach { id ->
                    data.catalogue(id).also {
                        if (it && !shouldSave) {
                            shouldSave = true
                        }
                    }
                }
                return@executeAndSave shouldSave
            }
        }
        return Command.SINGLE_SUCCESS
    }

    private fun revokeById(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYER)
        val id = StringArgumentType.getString(context, ID)
        players.forEach { player ->
            SpawnDataCatalogue.executeAndSave(player) { it.remove(id) }
        }
        return Command.SINGLE_SUCCESS
    }

    private fun revokeByPokemon(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYER)
        val species = StringArgumentType.getString(context, SPECIES)
        players.forEach { player ->
            SpawnDataCatalogue.executeAndSave(player) { data ->
                var shouldSave = false
                SpawnDataHelper.spawnDetailIdBySpecies[species]?.let { ids ->
                    ids.forEach { id ->
                        data.remove(id).also {
                            if (it && !shouldSave) {
                                shouldSave = true
                            }
                        }
                    }
                }
                return@executeAndSave shouldSave
            }
        }
        return Command.SINGLE_SUCCESS
    }

    private fun revokeAll(context: CommandContext<CommandSourceStack>): Int {
        val players = EntityArgument.getPlayers(context, PLAYER)
        players.forEach { player ->
            SpawnDataCatalogue.executeAndSave(player) { data ->
                var shouldSave = false
                SpawnDataHelper.spawnDetailIds.forEach { id ->
                    data.remove(id).also {
                        if (it && !shouldSave) {
                            shouldSave = true
                        }
                    }
                }
                return@executeAndSave shouldSave
            }
        }
        return Command.SINGLE_SUCCESS
    }
}