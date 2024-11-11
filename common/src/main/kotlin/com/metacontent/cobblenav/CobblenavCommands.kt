package com.metacontent.cobblenav

import com.metacontent.cobblenav.command.ConfigurePokefinderSettings
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CobblenavCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, registry: CommandBuildContext, selection: Commands.CommandSelection) {
        ConfigurePokefinderSettings.register(dispatcher)
    }
}