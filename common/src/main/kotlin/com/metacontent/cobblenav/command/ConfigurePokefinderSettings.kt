package com.metacontent.cobblenav.command

import com.cobblemon.mod.common.util.player
import com.metacontent.cobblenav.client.gui.util.PokefinderSettings
import com.metacontent.cobblenav.command.argument.PokefidnerSettingsArgumentType
import com.metacontent.cobblenav.networking.packet.client.UpdatePokefinderSettingsPacket
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*

object ConfigurePokefinderSettings : Command {
    const val BASE = "configure"
    const val NAME = "pokefinder"
    const val SETTINGS = "settings"

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal(BASE)
            .then(literal(NAME)
                .then(argument(SETTINGS, PokefidnerSettingsArgumentType.settings())
                    .executes(::execute))))
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        if (!context.source.isPlayer) return -1

        val settings = context.getArgument(SETTINGS, PokefinderSettings::class.java)
        UpdatePokefinderSettingsPacket(settings).sendToPlayer(context.source.playerOrException)
        return 1
    }
}