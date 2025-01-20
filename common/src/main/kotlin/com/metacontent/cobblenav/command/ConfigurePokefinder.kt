package com.metacontent.cobblenav.command

import com.metacontent.cobblenav.client.settings.PokefinderSettings
import com.metacontent.cobblenav.command.argument.PokefinderSettingsArgumentType
import com.metacontent.cobblenav.networking.packet.client.UpdatePokefinderSettingsPacket
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*

object ConfigurePokefinder : Command {
    const val BASE = "configure"
    const val NAME = "pokefinder"
    const val SETTINGS = "settings"

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal(BASE)
            .then(literal(NAME)
                .then(argument(SETTINGS, PokefinderSettingsArgumentType.settings())
                    .executes(::execute))))
    }

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        if (!context.source.isPlayer) return -1

        val settings = context.getArgument(SETTINGS, PokefinderSettings::class.java)
        UpdatePokefinderSettingsPacket(settings).sendToPlayer(context.source.playerOrException)
        return 1
    }
}